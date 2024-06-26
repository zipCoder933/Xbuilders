package com.xbuilders.engine.world;

import com.xbuilders.engine.player.camera.Camera;
import com.xbuilders.engine.rendering.chunk.mesh.CompactOcclusionMesh;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.progress.ProgressData;
import com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor.ExecutorServiceUtils;
import com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor.PriorityThreadPoolExecutor;
import com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor.comparator.LowValueComparator;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.FutureChunk;

import java.io.IOException;

import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.rendering.chunk.BlockShader;
import com.xbuilders.engine.gameScene.GameScene;

import static com.xbuilders.engine.gameScene.GameScene.world;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.BlockArrayTexture;
import com.xbuilders.engine.utils.ErrorHandler;

import static com.xbuilders.engine.utils.math.MathUtils.positiveMod;

import static com.xbuilders.engine.world.wcc.WCCi.chunkDiv;

import com.xbuilders.engine.world.chunk.pillar.PillarInformation;
import com.xbuilders.game.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.xbuilders.window.developmentTools.FrameTester;
import org.joml.*;

public class World {
    public static FrameTester frameTester = Main.frameTester;

    /*
     * CHUNK GENERATION PERFORMANCE
     *
     * Valkyre:
     * Chunk Load distance: 5
     * Load distance in voxels: 160
     * (Minecraft has a default chunk distance of 160 blocks.)
     *
     * In valkyre,With the standard chunk distanceThe FPS sometimes dips when
     * loading lots of stuff, but not very often.
     * (But This issue is resolved when vsync is disabled.)
     *
     * COMPARING PERFORMANCE
     * - When comparing the two side by side, with the same chunk distance, my
     * preformance with that chunk distance is actually
     * at the exact same level as valkyre.
     * - Also note that valkyre has half the chunk height as my game.
     * - When valkyre loads chunks at 288 voxels away, things start getting choppy
     * just like my game does.
     */
    public static final int CHUNK_LOAD_THREADS = 12; //Redicing the number of threads helps performance
    public static final int CHUNK_LIGHT_THREADS = 1;
    public static final int CHUNK_MESH_THREADS = 1;

    public static int VIEW_DIST_MIN = Chunk.WIDTH * 2;
    public static int VIEW_DIST_MAX = Chunk.WIDTH * 7;
    public static int DEFAULT_VIEW_DISTANCE = (int) (Chunk.WIDTH * 3);// 13
    private int maxChunksForViewDistance;
    private final AtomicInteger viewDistance = new AtomicInteger(VIEW_DIST_MIN);
    public final static AtomicInteger newGameTasks = new AtomicInteger(0);
    public BlockShader chunkShader;

    public void setViewDistance(int viewDistance2) {
        viewDistance.set(MathUtils.clamp(viewDistance2, VIEW_DIST_MIN, VIEW_DIST_MAX));
        // Settings
        Main.settings.viewDistance = viewDistance.get();
        Main.saveSettings();

        chunkShader.setViewDistance(viewDistance.get() - Chunk.WIDTH);
        // maxChunksForViewDistance = (int) Math.pow(viewDistance.get() * 2, 2) *
        // WORLD_CHUNK_HEIGHT;
        maxChunksForViewDistance = Integer.MAX_VALUE;
    }

    public int getViewDistance() {
        return viewDistance.get();
    } //The view distance reffers to rendering distance.

    public int getCreationViewDistance() {
        return viewDistance.get() + Chunk.WIDTH;
    }

    public int getDeletionViewDistance() {
        return viewDistance.get() + (Chunk.WIDTH * 6);
    }

    private final AtomicBoolean needsSorting; // Atomic variables are thread update
    private final Vector3f lastPlayerPosition = new Vector3f();

    // World boundaries
    // chunk boundaries
    public final static int TOP_Y_CHUNK = 0; // These are the boundaries of the world. We can set int.min and int.max if
    // we want them to be infinite
    public final static int BOTTOM_Y_CHUNK = (16 * 16) / Chunk.WIDTH;
    public final static int WORLD_CHUNK_HEIGHT = BOTTOM_Y_CHUNK - TOP_Y_CHUNK;

    // voxel boundaries
    public static final int WORLD_SIZE_NEG_X = -100000; // -X
    public static final int WORLD_TOP_Y = TOP_Y_CHUNK * Chunk.WIDTH; // up (-Y)
    public static final int WORLD_SIZE_NEG_Z = -100000; // -Z

    public static final int WORLD_SIZE_POS_X = 100000; // +X
    public static final int WORLD_BOTTOM_Y = BOTTOM_Y_CHUNK * Chunk.WIDTH + Chunk.WIDTH; // down (+Y)
    public static final int WORLD_SIZE_POS_Z = 100000; // +Z

    private SortByDistanceToPlayer sortByDistance;

    private final List<Chunk> unusedChunks = new ArrayList<>();
    public final Map<Vector3i, Chunk> chunks = new HashMap();
    private final Map<Vector3i, FutureChunk> futureChunks = new HashMap<>();
    private final List<Chunk> sortedChunksToRender = new ArrayList<>();
    private int blockTextureID;

    public WorldInfo info;
    public Terrain terrain;

    /**
     * = new ScheduledThreadPoolExecutor(1, r -> { ... });: This line creates an
     * instance of ScheduledThreadPoolExecutor. It's a type of
     * ScheduledExecutorService that uses a pool of threads to execute
     * tasks.<br>
     * <br>
     * <p>
     * - 1 specifies that the pool will have one thread. This means it will be
     * capable of executing one task at a time.<br>
     * <br>
     * <p>
     * - r -> { ... } is a lambda expression that provides a ThreadFactory to
     * the executor. It defines how threads are created. In this case, it
     * creates a new thread, sets its name to "Generation Thread", and marks it
     * as a daemon thread (meaning it won't prevent the JVM from exiting).
     */
    public static final PriorityThreadPoolExecutor generationService = new PriorityThreadPoolExecutor(
            CHUNK_LOAD_THREADS, r -> {
                Thread thread = new Thread(r, "Generation Thread");
                thread.setDaemon(true);
                return thread;
            }, new LowValueComparator());

    /**
     * THIS was the ONLY REASON why the chunk meshService.submit() in chunk mesh
     * generation was the performance bottleneck. We have to be careful the
     * settings we put here, because with the wrong settings, a task can take a
     * lot of time to execute() and block the main render thread
     */
    public static final ThreadPoolExecutor meshService = new ThreadPoolExecutor(
            CHUNK_MESH_THREADS, CHUNK_MESH_THREADS,
            3L, TimeUnit.MILLISECONDS, // It really just came down to tuning these settings for performance
            new LinkedBlockingQueue<Runnable>(), r -> {
                frameTester.count("Mesh threads", 1);
                Thread thread = new Thread(r, "Mesh Thread");
                thread.setDaemon(true);
                thread.setPriority(1);
                return thread;
            });

    public static final PriorityThreadPoolExecutor lightService = new PriorityThreadPoolExecutor(CHUNK_LIGHT_THREADS,
            r -> {
                Thread thread = new Thread(r, "Light Thread");
                thread.setDaemon(true);
                return thread;
            }, new LowValueComparator());

    public World() {
        this.needsSorting = new AtomicBoolean(true);

    }

    public void init(BlockArrayTexture textures) throws IOException {

        blockTextureID = textures.getTexture().id;
        // Prepare for game
        chunkShader = new BlockShader(BlockShader.FRAG_MODE_CHUNK);

        setViewDistance(Main.settings.viewDistance);
        chunkShader.setSkyColor(GameScene.backgroundColor);
        sortByDistance = new SortByDistanceToPlayer(GameScene.player.worldPosition);
    }

    // <editor-fold defaultstate="collapsed" desc="Chunk operations">
    public boolean hasChunk(final Vector3i coords) {
        return this.chunks.containsKey(coords);
    }

    public Chunk getChunk(final Vector3i coords) {
        return this.chunks.get(coords);
    }

    public Chunk addChunk(final Vector3i coords, boolean isTopLevel) {
        Chunk chunk = null;
        if (!unusedChunks.isEmpty()) {
            chunk = unusedChunks.remove(unusedChunks.size() - 1);
        } else if (chunks.size() < maxChunksForViewDistance) {
            chunk = new Chunk(blockTextureID);
        }
        if (chunk != null) {
            float distToPlayer = MathUtils.dist(
                    coords.x, coords.y, coords.z,
                    lastPlayerPosition.x, lastPlayerPosition.y, lastPlayerPosition.z);
            chunk.init(coords, info, terrain, futureChunks.remove(coords), distToPlayer, isTopLevel);
            this.chunks.put(coords, chunk);
            this.sortedChunksToRender.remove(chunk);
            needsSorting.set(true);
        }
        return chunk;
    }

    public void removeChunk(final Vector3i coords) {
        if (hasChunk(coords)) {
            Chunk chunk = this.chunks.remove(coords);
            chunk.save(info);
            unusedChunks.add(chunk);
        }
    }
    // </editor-fold>

    public void newGame(ProgressData prog, WorldInfo info, Vector3f playerPosition) {
        prog.setTask("Generating chunks");
        this.chunks.clear();
        this.unusedChunks.clear();
        this.futureChunks.clear(); // Important!
        newGameTasks.set(0);
        this.info = info;
        try {
            this.terrain = Main.game.getTerrainFromInfo(info);
            System.out.println("Loaded terrain: " + this.terrain.toString());
            prog.bar.setMax(fillChunksAroundPlayer(playerPosition, true));
        } catch (Exception e) {
            prog.abort();
        }

    }

    public void stopGame(Vector3f playerPos) {
        save(playerPos);

        // We may or may not actually need to shutdown the services, since chunks cancel
        // all tasks when they are disposed
        ExecutorServiceUtils.cancelAllTasks(generationService);
        ExecutorServiceUtils.cancelAllTasks(lightService);
        ExecutorServiceUtils.cancelAllTasks(meshService);

        chunks.forEach((coords, chunk) -> chunk.dispose());
        unusedChunks.forEach((chunk) -> {
            chunk.dispose();
        });

        chunks.clear();
        unusedChunks.clear();
        sortedChunksToRender.clear();
        chunksToUnload.clear();
        futureChunks.clear(); // Important!

        System.gc();
        info = null;
        terrain = null;
    }

    /*
     * ONE VERY IMPORTANT THING is to make sure that the chunks are deleted with the
     * same criteria that they were created with.
     * This is so that the chunks dont end up being deleted and than recreated over
     * and over again
     */
    private boolean chunkIsWithinRange(Vector3f player, Vector3i chunk, float viewDistance) {
        return MathUtils.dist(
                player.x,
                player.z,
                chunk.x * Chunk.WIDTH,
                chunk.z * Chunk.WIDTH) < viewDistance;
    }

    public static final int CHUNK_QUANTITY_Y = 16;

    public int addChunkPillar(int chunkX, int chunkZ, Vector3f player) {
        int chunksGenerated = 0;
        boolean isTopChunk = true;

        Chunk[] chunkPillar = new Chunk[PillarInformation.CHUNKS_IN_PILLAR];
        for (int y = TOP_Y_CHUNK; y <= BOTTOM_Y_CHUNK; ++y) {
            final Vector3i coords = new Vector3i(chunkX, y, chunkZ);
            if (!chunks.containsKey(coords)
                    && chunkIsWithinRange(player, coords, getCreationViewDistance())) {
                chunkPillar[y - TOP_Y_CHUNK] = addChunk(coords, isTopChunk);
                isTopChunk = false;
                chunksGenerated++;
            } else {
                chunkPillar[y - TOP_Y_CHUNK] = getChunk(coords);
            }
        }
        for (Chunk chunk : chunkPillar) {
            chunk.pillarInformation = new PillarInformation(chunkPillar);
        }
        // chunkPillar[0].pillarInformation.loadChunks(terrain, info);

        return chunksGenerated;
    }

    public synchronized int fillChunksAroundPlayer(Vector3f player, boolean generateOutOfFrustum) {
        int centerX = (int) player.x;
        int centerY = (int) player.y;
        int centerZ = (int) player.z;

        int viewDistanceXZ = getCreationViewDistance();
        int viewDistanceY = getCreationViewDistance();

        final int xStart = (centerX - viewDistanceXZ) / Chunk.WIDTH;
        final int xEnd = (centerX + viewDistanceXZ) / Chunk.WIDTH;
        final int zStart = (centerZ - viewDistanceXZ) / Chunk.WIDTH;
        final int zEnd = (centerZ + viewDistanceXZ) / Chunk.WIDTH;
        // final int yStart = (centerY - viewDistanceY) / Chunk.WIDTH;
        // final int yEnd = (centerY + viewDistanceY) / Chunk.WIDTH;

        // Having fixed y bounds makes the chunk generation much faster
        int chunksGenerated = 0;

        for (int chunkX = xStart; chunkX < xEnd; ++chunkX) {
            for (int chunkZ = zStart; chunkZ < zEnd; ++chunkZ) {
                if (MathUtils.dist(
                        player.x,
                        player.z,
                        chunkX * Chunk.WIDTH,
                        chunkZ * Chunk.WIDTH) < viewDistanceXZ
                        && (generateOutOfFrustum
                                || Camera.frustum.isPillarChunkInside(chunkX, chunkZ, TOP_Y_CHUNK, BOTTOM_Y_CHUNK))) {
                    chunksGenerated += addChunkPillar(chunkX, chunkZ, player);
                }
            }
        }
        return chunksGenerated;
    }

    private final List<Chunk> chunksToUnload = new ArrayList<>();
    private long lastSaveMS;

    private void updateChunksToRenderList(Vector3f playerPosition) {
        chunksToUnload.clear();

        int removalViewDistance = getDeletionViewDistance();

        chunks.forEach((coords, chunk) -> {
            if (!chunkIsWithinRange(playerPosition, coords, removalViewDistance)) {
                chunksToUnload.add(chunk);
                sortedChunksToRender.remove(chunk);
            } else {
                // frameTester.startProcess();
                if (needsSorting.get()) {
                    sortedChunksToRender.add(chunk);
                }
                chunk.inFrustum = Camera.frustum.isChunkInside(chunk.position);
                chunk.distToPlayer = MathUtils.dist(
                        coords.x, coords.y, coords.z,
                        lastPlayerPosition.x, lastPlayerPosition.y, lastPlayerPosition.z);
                // frameTester.endProcess("UCTRL: sorting and frustum check");
                chunk.prepare(terrain, frame, false);
            }
        });
        chunksToUnload.forEach(chunk -> {
            removeChunk(chunk.position);
        });
        frameTester.set("all chunks", unusedChunks.size() + chunks.size());
        frameTester.set("in-use chunks", chunks.size());
        frameTester.set("chunksToRender", sortedChunksToRender.size());
        frameTester.set("unused chunks", unusedChunks.size());
    }

    long frame = 0;

    public void drawChunks(Matrix4f projection, Matrix4f view, Vector3f playerPosition) throws IOException {
        // <editor-fold defaultstate="collapsed" desc="chunk updating">
        frame++;
        if (!lastPlayerPosition.equals(playerPosition)) {
            needsSorting.set(true);
            lastPlayerPosition.set(playerPosition);
        }

        if (frame % 10 == 0) {
            frameTester.startProcess();
            world.fillChunksAroundPlayer(playerPosition, false);
            frameTester.endProcess("Fill chunks around player");
        }

        /*
         * If the chunks need sorting, newGame the render list
         */
        if (needsSorting.get()) {
            sortedChunksToRender.clear();
        }

        if (System.currentTimeMillis() - lastSaveMS > 25000) {
            lastSaveMS = System.currentTimeMillis();
            // Save chunks
            generationService.submit(0.0f, () -> {
                save(playerPosition);
            });
        }

        updateChunksToRenderList(playerPosition);
        if (needsSorting.get()) {
            sortedChunksToRender.sort(sortByDistance);
            needsSorting.set(false);
        }
        // <editor-fold defaultstate="collapsed" desc="For testing sorted chunk distance
        // (KEEP THIS!)">
        // int i = 0; //For testing sorted chunk distance (KEEP THIS!)
        // for (Chunk chunk : sortedChunksToRender) {
        // if (chunk.getGenerationStatus() == Chunk.GEN_COMPLETE) {
        // chunk.updateMVP(projection, view); // we must update the MVP within each
        // model;
        // chunk.mvp.sendToShader(chunkShader.getID(), chunkShader.mvpUniform);
        // chunk.meshes.opaqueMesh.draw(true);
        // chunk.meshes.opaqueMesh.drawBoundingBoxWithWireframe();
        // i++;
        // if (i > 0) break;
        // }
        // }
        // </editor-fold>
        frameTester.endProcess("Sort chunks if needed");
        // </editor-fold>

        /*
         * The basic layout for query occlusion culling is:
         * 
         * 1. Create the query (or queries).
         * 2. Render loop:
         * a. Do AI / physics etc...
         * b. Rendering:
         * i. Check the query result from the previous frame.
         * ii. Issue query begin:
         * 1. If the object was visible in the last frame:
         * a. Enable rendering to screen.
         * b. Enable or disable writing to depth buffer (depends on whether the object
         * is translucent or opaque).
         * c. Render the object itself.
         * 2. If the object wasn't visible in the last frame:
         * a. Disable rendering to screen.
         * b. Disable writing to depth buffer.
         * c. "Render" the object's bounding box.
         * iii. (End query)
         * iv. (Repeat for every object in scene.)
         * c. Swap buffers.
         * (End of render loop)
         */

        // Render visible opaque meshes
        int viewDistance = getViewDistance();

        chunkShader.bind();
        chunkShader.tickAnimation();
        sortedChunksToRender.forEach(chunk -> {// TODO: The chunk in front doesnt have any samples?
            if (chunk.inFrustum
                    && chunk.getGenerationStatus() == Chunk.GEN_COMPLETE
                    && chunkIsWithinRange(playerPosition, chunk.position, viewDistance)) {

                chunk.updateMVP(projection, view); // we must update the MVP within each model;
                chunk.mvp.sendToShader(chunkShader.getID(), chunkShader.mvpUniform);
                chunk.meshes.opaqueMesh.getQueryResult();
                chunk.meshes.opaqueMesh.drawVisible(GameScene.drawWireframe);
            }
        });
        // Render invisible opaque meshes
        CompactOcclusionMesh.startInvisible();
        sortedChunksToRender.forEach(chunk -> {
            if (chunk.inFrustum
                    && chunk.getGenerationStatus() == Chunk.GEN_COMPLETE
                    && chunkIsWithinRange(playerPosition, chunk.position, viewDistance)) {
                chunk.meshes.opaqueMesh.drawInvisible();
            }
        });
        CompactOcclusionMesh.endInvisible();

        // Render transparent meshes
        // TODO: Because the opaque mesh is invisible, the transparent mesh stutters one
        // frame every second.
        sortedChunksToRender.forEach(chunk -> {
            if (chunk.inFrustum
                    && chunk.getGenerationStatus() == Chunk.GEN_COMPLETE
                    && chunkIsWithinRange(playerPosition, chunk.position, viewDistance)) {
                if (!chunk.meshes.transMesh.isEmpty()) {
                    if ((chunk.meshes.opaqueMesh.isVisible() || chunk.meshes.opaqueMesh.isEmpty())) {
                        chunk.mvp.sendToShader(chunkShader.getID(), chunkShader.mvpUniform);
                        chunk.meshes.transMesh.draw(GameScene.drawWireframe);
                    }
                }
                chunk.entities.draw(projection, view, Camera.frustum, playerPosition);
            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="block operations">

    public short getBlockID(int worldX, int worldY, int worldZ) {
        int blockX = positiveMod(worldX, Chunk.WIDTH);
        int blockY = positiveMod(worldY, Chunk.WIDTH);
        int blockZ = positiveMod(worldZ, Chunk.WIDTH);

        int chunkX = chunkDiv(worldX);
        int chunkY = chunkDiv(worldY);
        int chunkZ = chunkDiv(worldZ);

        Chunk chunk = getChunk(new Vector3i(chunkX, chunkY, chunkZ));
        if (chunk == null) { // By default, all get block events should return air otherwise the chunk will
            // be null
            return 0;
        }
        return chunk.data.getBlock(blockX, blockY, blockZ);
    }

    public Chunk setBlockData(BlockData data, int worldX, int worldY, int worldZ) {
        int blockX = positiveMod(worldX, Chunk.WIDTH);
        int blockY = positiveMod(worldY, Chunk.WIDTH);
        int blockZ = positiveMod(worldZ, Chunk.WIDTH);

        int chunkX = chunkDiv(worldX);
        int chunkY = chunkDiv(worldY);
        int chunkZ = chunkDiv(worldZ);

        Vector3i pos = new Vector3i(chunkX, chunkY, chunkZ);
        Chunk chunk = getChunk(pos);
        if (chunk == null) {
            // FutureChunk futureChunk = newFutureChunk(pos);
        } else {
            chunk.data.setBlockData(blockX, blockY, blockZ, data);
        }
        return chunk;
    }

    public Chunk setBlock(short blockID, int worldX, int worldY, int worldZ) {
        int blockX = positiveMod(worldX, Chunk.WIDTH);
        int blockY = positiveMod(worldY, Chunk.WIDTH);
        int blockZ = positiveMod(worldZ, Chunk.WIDTH);

        int chunkX = chunkDiv(worldX);
        int chunkY = chunkDiv(worldY);
        int chunkZ = chunkDiv(worldZ);

        Vector3i pos = new Vector3i(chunkX, chunkY, chunkZ);
        Chunk chunk = getChunk(pos);
        if (chunk == null) { // We automatically set the block on a future chunk
            FutureChunk futureChunk = newFutureChunk(pos);
            futureChunk.addBlock(blockID, blockX, blockY, blockZ);
        } else {
            chunk.data.setBlock(blockX, blockY, blockZ, blockID);
        }
        return chunk;
    }

    public Block getBlock(int worldX, int worldY, int worldZ) {
        Block block = ItemList.getBlock(getBlockID(worldX, worldY, worldZ));
        return block == null ? BlockList.BLOCK_AIR : block;
    }

    public BlockData getBlockData(int worldX, int worldY, int worldZ) {
        int blockX = positiveMod(worldX, Chunk.WIDTH);
        int blockY = positiveMod(worldY, Chunk.WIDTH);
        int blockZ = positiveMod(worldZ, Chunk.WIDTH);

        int chunkX = chunkDiv(worldX);
        int chunkY = chunkDiv(worldY);
        int chunkZ = chunkDiv(worldZ);

        Chunk chunk = getChunk(new Vector3i(chunkX, chunkY, chunkZ));
        if (chunk == null) {
            return null;
        }
        return chunk.data.getBlockData(blockX, blockY, blockZ);
    }
    // </editor-fold>

    public void save(Vector3f playerPos) {
        // System.out.println("Saving...");
        // Save all chunks
        Iterator<Chunk> iterator = chunks.values().iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            chunk.save(info);
        }
        info.setSpawnPoint(playerPos);
        try {
            info.save();
        } catch (IOException ex) {
            ErrorHandler.handleFatalError(ex);
        }
    }

    public FutureChunk newFutureChunk(Vector3i pos) {
        FutureChunk futureChunk = futureChunks.get(pos);
        if (futureChunk == null) {
            futureChunk = new FutureChunk(pos);
            futureChunks.put(new Vector3i(pos), futureChunk);// We have to create a new vector, because chunk vector can
            // change when it is repurposed
        }
        return futureChunk;
    }

}
