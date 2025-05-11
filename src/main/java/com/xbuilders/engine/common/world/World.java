package com.xbuilders.engine.common.world;

import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.entity.EntitySupplier;
import com.xbuilders.engine.common.players.localPlayer.camera.Camera;
import com.xbuilders.engine.client.settings.ClientSettings;
import com.xbuilders.engine.common.math.MathUtils;
import com.xbuilders.engine.common.progress.ProgressData;
import com.xbuilders.engine.common.threadPoolExecutor.PriorityExecutor.ExecutorServiceUtils;
import com.xbuilders.engine.common.threadPoolExecutor.PriorityExecutor.PriorityThreadPoolExecutor;
import com.xbuilders.engine.common.threadPoolExecutor.PriorityExecutor.comparator.LowValueComparator;
import com.xbuilders.engine.common.world.chunk.BlockData;
import com.xbuilders.engine.common.world.chunk.FutureChunk;

import com.xbuilders.engine.common.world.chunk.Chunk;

import static com.xbuilders.Main.LOGGER;
import static com.xbuilders.Main.game;

import com.xbuilders.engine.server.block.BlockRegistry;
import com.xbuilders.engine.server.block.Block;

import static com.xbuilders.engine.common.math.MathUtils.positiveMod;

import static com.xbuilders.engine.common.world.wcc.WCCi.chunkDiv;

import com.xbuilders.engine.common.world.chunk.pillar.PillarInformation;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import com.xbuilders.engine.common.world.wcc.WCCi;
import org.joml.*;

/*
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
public abstract class World {
    public static final int CHUNK_LOAD_THREADS = 12; //Redicing the number of threads helps performance
    public static final int CHUNK_LIGHT_THREADS = 1;
    public static final int CHUNK_MESH_THREADS = 1;
    public static final int PLAYER_CHUNK_MESH_THREADS = 3; //The number of threads allocated to player based chunk updating

    public static int VIEW_DIST_MIN = Chunk.WIDTH * 2;
    public static int VIEW_DIST_MAX = Chunk.WIDTH * 16; //Allowing higher view distances increases flexibility

    protected int maxChunksForViewDistance;
    protected final AtomicInteger viewDistance = new AtomicInteger(VIEW_DIST_MIN);
    public final static AtomicInteger newGameTasks = new AtomicInteger(0);

    public void setViewDistance(ClientSettings settings, int viewDistance2) {
        viewDistance.set(MathUtils.clamp(viewDistance2, VIEW_DIST_MIN, VIEW_DIST_MAX));
        // Settings
        settings.internal_viewDistance.value = viewDistance.get();
        settings.save();
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


    // World boundaries
    // chunk boundaries
    // These are the boundaries of the world. We can set int.min and int.max if we want them to be infinite
    public final static int TOP_Y_CHUNK = -2;
    public final static int BOTTOM_Y_CHUNK = 7;
    public final static int WORLD_CHUNK_HEIGHT = BOTTOM_Y_CHUNK - TOP_Y_CHUNK;

    // voxel boundaries
    public static final int WORLD_SIZE_NEG_X = -32000; // -X
    public static final int WORLD_TOP_Y = TOP_Y_CHUNK * Chunk.WIDTH; // up (-Y)
    public static final int WORLD_SIZE_NEG_Z = -32000; // -Z

    public static final int WORLD_SIZE_POS_X = 32000; // +X
    public static final int WORLD_BOTTOM_Y = (BOTTOM_Y_CHUNK * Chunk.WIDTH) + Chunk.WIDTH; // down (+Y)
    public static final int WORLD_SIZE_POS_Z = 32000; // +Z

    public boolean inYBounds(int y) {
        return y > WORLD_TOP_Y && y < WORLD_BOTTOM_Y - 1;
    }

    public boolean inBounds(int ix, int iy, int iz) {
        return ix >= WORLD_SIZE_NEG_X && ix < WORLD_SIZE_POS_X
                && iz >= WORLD_SIZE_NEG_Z && iz < WORLD_SIZE_POS_Z
                && inYBounds(iy);
    }

    //Model properties
    public final Map<Vector3i, Chunk> chunks = new ConcurrentHashMap<>(); //Important if we want to use this in multiple threads
    public final WorldEntityMap allEntities = new WorldEntityMap(); // <chunkPos, entity>
    public WorldData data;
    public Terrain terrain;
    private final Vector3f lastPlayerPosition = new Vector3f();
    protected List<Chunk> unusedChunks = new ArrayList<>();
    protected final Map<Vector3i, FutureChunk> futureChunks = new HashMap<>();


    /**
     * = new ScheduledThreadPoolExecutor(1, r -> { ... });: This line creates an
     * instance of ScheduledThreadPoolExecutor. It's a typeReference of
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
        Client.frameTester.count("Mesh threads", 1);
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


    public static final ThreadPoolExecutor playerUpdating_meshService = new ThreadPoolExecutor(
            PLAYER_CHUNK_MESH_THREADS, PLAYER_CHUNK_MESH_THREADS,
            1L, TimeUnit.MILLISECONDS, // It really just came down to tuning these settings for performance
            new LinkedBlockingQueue<Runnable>(), r -> {
        Client.frameTester.count("Player Mesh threads", 1);
        Thread thread = new Thread(r, "Player Mesh Thread");
        thread.setDaemon(true);
        thread.setPriority(10);
        return thread;
    });


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
            chunk = new Chunk(data, terrain);
        }
        if (chunk != null) {
            float distToPlayer = MathUtils.dist(
                    coords.x, coords.y, coords.z,
                    lastPlayerPosition.x, lastPlayerPosition.y, lastPlayerPosition.z);
            //We need to init chunks since we are recycling them
            chunk.init_common(coords, futureChunks.remove(coords), distToPlayer, isTopLevel);
            this.chunks.put(coords, chunk);

        }
        return chunk;
    }

    public void removeChunk(final Vector3i coords) {
        if (hasChunk(coords)) {
            Chunk chunk = this.chunks.remove(coords);
            allEntities.removeAllEntitiesFromChunk(chunk);
            chunk.save(data);
            unusedChunks.add(chunk);
        }
    }
    // </editor-fold>

    public boolean init(ProgressData prog, Vector3f spawnPosition) {
        System.out.println("\n\nStarting new game: " + data.getName());
        prog.setTask("Starting new game");
        this.chunks.clear();
        this.unusedChunks.clear();
        this.futureChunks.clear(); // Important!
        newGameTasks.set(0);
        allEntities.clear();
        //Get the terrain from worldInfo
        this.terrain = game.getTerrainFromInfo(data);
        if (terrain == null) {
            LOGGER.log(Level.SEVERE, "Terrain not found");
            return false;
        } else System.out.println("Terrain: " + this.terrain);

        prog.setTask("Generating chunks");
        prog.bar.setMax(fillChunksAroundPlayer(spawnPosition, true));
        return true;
    }

    public void stopGameEvent() {
        // We may or may not actually need to shutdown the services, since chunks cancel
        // all tasks when they are disposed
        ExecutorServiceUtils.cancelAllTasks(generationService);
        ExecutorServiceUtils.cancelAllTasks(lightService);
        ExecutorServiceUtils.cancelAllTasks(meshService);
        ExecutorServiceUtils.cancelAllTasks(playerUpdating_meshService);

        chunks.forEach((coords, chunk) -> chunk.dispose());
        unusedChunks.forEach((chunk) -> {
            chunk.dispose();
        });

        allEntities.clear();
        chunks.clear();
        unusedChunks.clear();
        futureChunks.clear(); // Important!

        System.gc();
        data = null;
        terrain = null;
    }

    /*
     * ONE VERY IMPORTANT THING is to make sure that the chunks are deleted with the
     * same criteria that they were created with.
     * This is so that the chunks dont end up being deleted and than recreated over
     * and over again
     */
    protected boolean chunkIsWithinRange_XZ(Vector3f player, Vector3i chunk, float viewDistance) {
        return MathUtils.dist(
                player.x,
                player.z,
                chunk.x * Chunk.WIDTH,
                chunk.z * Chunk.WIDTH) < viewDistance;
    }


    protected boolean chunkIsWithinRange_XYZ(Vector3f player, Vector3i chunk, int viewDistance) {
        return MathUtils.dist(
                player.x,
                player.y,
                player.z,
                chunk.x * Chunk.WIDTH,
                chunk.y * Chunk.WIDTH,
                chunk.z * Chunk.WIDTH) < viewDistance;
    }


    public static final int CHUNK_QUANTITY_Y = 16;

    public int addChunkPillar(int chunkX, int chunkZ, Vector3f playerPos) {
        int chunksGenerated = 0;
        boolean isTopChunk = true;

        Chunk[] chunkPillar = new Chunk[PillarInformation.CHUNKS_IN_PILLAR];
        for (int y = TOP_Y_CHUNK; y <= BOTTOM_Y_CHUNK; ++y) {
            final Vector3i chunkCoords = new Vector3i(chunkX, y, chunkZ);
            boolean isWithinReach = playerPos == null || chunkIsWithinRange_XZ(playerPos, chunkCoords, getCreationViewDistance());

            if (!chunks.containsKey(chunkCoords) && isWithinReach) {
                chunkPillar[y - TOP_Y_CHUNK] = addChunk(chunkCoords, isTopChunk);
                isTopChunk = false;
                chunksGenerated++;
            } else {
                chunkPillar[y - TOP_Y_CHUNK] = getChunk(chunkCoords);
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


    // <editor-fold defaultstate="collapsed" desc="block operations">

    /**
     * does NOT notify other players and does not guarantee save of the chunk
     *
     * @param entity
     * @param w
     * @return the entity
     */
    public Entity placeEntity(EntitySupplier entity, Vector3i w, byte[] data) {
        WCCi wcc = new WCCi();
        wcc.set(w);
        Chunk chunk = Client.world.chunks.get(wcc.chunk);
        if (chunk != null) {
            Entity e = chunk.entities.placeNew(w, entity, data);
            e.sendMultiplayer = false;
            return e;
        }
        return null;
    }

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

    public FutureChunk newFutureChunk(Vector3i pos) {
        FutureChunk futureChunk = futureChunks.get(pos);
        if (futureChunk == null) {
            futureChunk = new FutureChunk(pos);
            futureChunks.put(new Vector3i(pos), futureChunk);// We have to create a new vector, because chunk vector can
            // change when it is repurposed
        }
        return futureChunk;
    }

    public Chunk markAsModified(int worldX, int worldY, int worldZ) {
        int chunkX = chunkDiv(worldX);
        int chunkY = chunkDiv(worldY);
        int chunkZ = chunkDiv(worldZ);
        Vector3i pos = new Vector3i(chunkX, chunkY, chunkZ);
        Chunk chunk = getChunk(pos);
        if (chunk != null) chunk.markAsModified();
        return chunk;
    }

    public Block getBlock(int worldX, int worldY, int worldZ) {
        Block block = Registrys.getBlock(getBlockID(worldX, worldY, worldZ));
        return block == null ? BlockRegistry.BLOCK_AIR : block;
    }

    public Block getBlock(Vector3i pos) {
        return getBlock(pos.x, pos.y, pos.z);
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
}
