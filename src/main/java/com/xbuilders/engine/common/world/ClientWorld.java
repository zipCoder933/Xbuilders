package com.xbuilders.engine.common.world;

import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.settings.ClientSettings;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.ChunkShader;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.mesh.CompactOcclusionMesh;
import com.xbuilders.engine.common.math.AABB;
import com.xbuilders.engine.common.math.MathUtils;
import com.xbuilders.engine.common.players.localPlayer.camera.Camera;
import com.xbuilders.engine.common.progress.ProgressData;
import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.common.world.chunk.ClientChunk;
import com.xbuilders.engine.common.world.chunk.FutureChunk;
import com.xbuilders.engine.server.block.BlockArrayTexture;
import com.xbuilders.engine.server.entity.ChunkEntitySet;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import static com.xbuilders.Main.LOGGER;
import static com.xbuilders.Main.game;
import static com.xbuilders.engine.client.Client.userPlayer;
import static com.xbuilders.engine.client.Client.world;
import static com.xbuilders.engine.common.math.MathUtils.positiveMod;
import static com.xbuilders.engine.common.world.wcc.WCCi.chunkDiv;

/**
 * The world is the main class that manages the chunks and allEntities in the game.
 * It is the model for the game world and everything in it.
 */
public class ClientWorld extends World<ClientChunk> {

    public ChunkShader chunkShader;
    private final AtomicBoolean needsSorting = new AtomicBoolean(true);
    private final Vector3f lastPlayerPosition = new Vector3f();
    private SortByDistanceToPlayer sortByDistance;
    private final List<Chunk> sortedChunksToRender = new ArrayList<>();
    public int blockTextureID;


    @Override
    protected ClientChunk createChunk(Chunk recycleChunk, final Vector3i coords, boolean isTopLevel, FutureChunk futureChunk, float distToPlayer) {
        if (recycleChunk != null) return new ClientChunk(coords, isTopLevel, futureChunk, distToPlayer, recycleChunk);
        else return new ClientChunk(coords, isTopLevel, futureChunk, distToPlayer, data, terrain);
    }


    public ClientWorld() {
        super();
    }


    public void setViewDistance(ClientSettings settings, int viewDistance2) {
        super.setViewDistance(settings, viewDistance2);
        chunkShader.setViewDistance(viewDistance.get() - Chunk.WIDTH);
    }


    public void init(BlockArrayTexture textures) throws IOException {
        blockTextureID = textures.getTexture().id;
        // Prepare for game
        chunkShader = new ChunkShader(ChunkShader.FRAG_MODE_CHUNK);

        setViewDistance(ClientWindow.settings, ClientWindow.settings.internal_viewDistance.value);
        sortByDistance = new SortByDistanceToPlayer(Client.userPlayer.worldPosition);
        allEntities.clear();
    }

    public Chunk addChunk(final Vector3i coords, boolean isTopLevel) {
        //Return an existing chunk if it exists
        Chunk chunk = getChunk(coords);
        if (chunk != null) return chunk;

        //make the chunk
        float distToPlayer = MathUtils.dist(coords.x, coords.y, coords.z,
                lastPlayerPosition.x, lastPlayerPosition.y, lastPlayerPosition.z);

        if (!unusedChunks.isEmpty()) { //Recycle from unused chunk pool
            Chunk recycleChunk = unusedChunks.remove(unusedChunks.size() - 1);
            chunk = createChunk(recycleChunk, coords, isTopLevel, futureChunks.remove(coords), distToPlayer);
        } else if (chunks.size() < maxChunksForViewDistance) { //Create a new chunk from scratch
            chunk = createChunk(null, coords, isTopLevel, futureChunks.remove(coords), distToPlayer);
        }

        if (chunk != null) {
            chunk.load();
            this.chunks.put(coords, chunk);
            this.sortedChunksToRender.remove(chunk);
            needsSorting.set(true);
        }
        return chunk;
    }


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
        super.stopGameEvent();
        sortedChunksToRender.clear();
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
            //This is used for 1) chunk task prioritization and 2) chunk ticking distance
            chunk.client_distToPlayer = MathUtils.dist(
                    coords.x * Chunk.WIDTH,
                    coords.y * Chunk.HEIGHT,
                    coords.z * Chunk.WIDTH,
                    playerPosition.x,
                    playerPosition.y,
                    playerPosition.z);

            if (!chunkIsWithinRange_XZ(playerPosition, coords, removalViewDistance)) {
                chunksToUnload.add(chunk);
                sortedChunksToRender.remove(chunk);
            } else {
                // frameTester.startProcess();
                if (needsSorting.get()) {
                    //Dont add chunk unless it is within the view distance
                    if (chunkIsWithinRange_XYZ(playerPosition, chunk.position, viewDistance.get() + Chunk.HALF_WIDTH)) {
                        sortedChunksToRender.add(chunk);
                    }
                }
                chunk.inFrustum = Camera.frustum.isChunkInside(chunk.position);
                // frameTester.endProcess("UCTRL: sorting and frustum check");
                chunk.prepare(terrain, ClientWindow.frameCount, false);
            }
        });
        chunksToUnload.forEach(chunk -> {
            removeChunk(chunk.position);
        });
        Client.frameTester.set("all chunks", unusedChunks.size() + chunks.size());
        Client.frameTester.set("in-use chunks", chunks.size());
        Client.frameTester.set("chunksToRender", sortedChunksToRender.size());
        Client.frameTester.set("unused chunks", unusedChunks.size());
        Client.frameTester.set("world allEntities", world.allEntities.size());
    }

    final Vector3f chunkShader_cursorMin = new Vector3f();
    final Vector3f chunkShader_cursorMax = new Vector3f();

    public void client_updateAndRenderChunks(Matrix4f projection, Matrix4f view, Vector3f playerPosition) {
        // <editor-fold defaultstate="collapsed" desc="chunk updating">
        if (!lastPlayerPosition.equals(playerPosition)) {
            needsSorting.set(true);
            lastPlayerPosition.set(playerPosition);
        }

        if (ClientWindow.frameCount % 10 == 0) {
            Client.frameTester.startProcess();
            world.fillChunksAroundPlayer(playerPosition, false);
            Client.frameTester.endProcess("Fill chunks around player");
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
                save();
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
        Client.frameTester.endProcess("Sort chunks if needed");
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


        if (userPlayer.camera.cursorRay.hitTarget()) {
            chunkShader_cursorMin.set(userPlayer.camera.cursorRay.getHitPos());
            chunkShader_cursorMax.set(chunkShader_cursorMin).add(1, 1, 1);

            List<AABB> cursorBoxes = userPlayer.camera.cursorRay.cursorRay.cursorBoxes;
            if (cursorBoxes != null && !cursorBoxes.isEmpty()) {
                chunkShader_cursorMin.set(cursorBoxes.get(0).min);
                chunkShader_cursorMax.set(cursorBoxes.get(0).max);
                for (AABB aabb : cursorBoxes) {
                    chunkShader_cursorMin.set(Math.min(chunkShader_cursorMin.x, aabb.min.x), Math.min(chunkShader_cursorMin.y, aabb.min.y), Math.min(chunkShader_cursorMin.z, aabb.min.z));
                    chunkShader_cursorMax.set(Math.max(chunkShader_cursorMax.x, aabb.max.x), Math.max(chunkShader_cursorMax.y, aabb.max.y), Math.max(chunkShader_cursorMax.z, aabb.max.z));
                }
            }
            chunkShader.setCursorPosition(chunkShader_cursorMin, chunkShader_cursorMax);
            chunkShader.setBlockBreakPercentage(userPlayer.camera.cursorRay.breakPercentage);
        } else {
            chunkShader.setBlockBreakPercentage(0);
            chunkShader_cursorMin.set(0, 0, 0);
            chunkShader.setCursorPosition(chunkShader_cursorMin, chunkShader_cursorMin);
        }

        // Render visible opaque meshes
        chunkShader.bind();
        chunkShader.tickAnimation();
        sortedChunksToRender.forEach(chunk -> {
            if (chunkIsVisible(chunk, playerPosition)) {
                chunk.updateMVP(projection, view); // we must update the MVP within each model;
                initShaderUniforms(chunk);
                chunk.meshes.opaqueMesh.getQueryResult();
                chunk.meshes.opaqueMesh.drawVisible(GameScene.drawWireframe);

                if (GameScene.drawBoundingBoxes) chunk.meshes.opaqueMesh.drawBoundingBoxWithWireframe();

            }
        });
        // Render invisible opaque meshes
        CompactOcclusionMesh.startInvisible();
        sortedChunksToRender.forEach(chunk -> {
            if (chunkIsVisible(chunk, playerPosition)) {
                // chunkShader.setChunkPosition(chunk.position);
                chunk.meshes.opaqueMesh.drawInvisible();
            }
        });
        CompactOcclusionMesh.endInvisible();

        //Draw allEntities
        //The allEntities must be drawn BEFORE the transparent meshes, otherwise they will not be visible over the transparent meshes
        ChunkEntitySet.startDraw(projection, view);
        sortedChunksToRender.forEach(chunk -> {
            if (chunkIsVisible(chunk, playerPosition)) {
                chunk.entities.draw(Camera.frustum, playerPosition);
            }
        });

        //Draw transparent meshes
        sortedChunksToRender.forEach(chunk -> {
            if (!chunk.meshes.transMesh.isEmpty() && chunkIsVisible(chunk, playerPosition)) {
                if (chunk.meshes.opaqueMesh.isVisibleSafe(2) || chunk.meshes.opaqueMesh.isEmpty()) {
                    initShaderUniforms(chunk);
                    chunk.meshes.transMesh.draw(GameScene.drawWireframe);
                }
            }
        });


    }

    private void initShaderUniforms(Chunk chunk) {
        chunk.mvp.sendToShader(chunkShader.getID(), chunkShader.mvpUniform);
        chunkShader.setChunkPosition(chunk.position);
    }

    private boolean chunkIsVisible(Chunk chunk, Vector3f playerPosition) {
        return chunk.inFrustum
                && chunk.getGenerationStatus() == Chunk.GEN_COMPLETE
                && chunkIsWithinRange_XYZ(playerPosition, chunk.position, getViewDistance());
    }


    public Chunk updateMesh(boolean updateAllNeighbors, boolean markAsModified,
                            int worldX, int worldY, int worldZ) {
        int blockX = positiveMod(worldX, Chunk.WIDTH);
        int blockY = positiveMod(worldY, Chunk.WIDTH);
        int blockZ = positiveMod(worldZ, Chunk.WIDTH);

        int chunkX = chunkDiv(worldX);
        int chunkY = chunkDiv(worldY);
        int chunkZ = chunkDiv(worldZ);
        Vector3i pos = new Vector3i(chunkX, chunkY, chunkZ);
        Chunk chunk = getChunk(pos);
        if (chunk != null) {
            if (markAsModified) chunk.markAsModified();
            chunk.updateMesh(updateAllNeighbors, blockX, blockY, blockZ);
        }
        return chunk;
    }

    public long getTimeSinceLastSave() {
        return System.currentTimeMillis() - lastSaveMS;
    }

    public void save() {
        Vector3f playerPos = userPlayer.worldPosition;
        ClientWindow.printlnDev("Saving world...");
        // Save all modified chunks
        Iterator<Chunk> iterator = chunks.values().iterator();
        while (iterator.hasNext()) {
            Chunk chunk = iterator.next();
            chunk.save(data);
        }

        //Save world info
        try {
            data.setSpawnPoint(playerPos);
            data.save();
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "World \"" + data.getName() + "\" could not be saved", ex);
        }

        //Save player info
        userPlayer.saveToWorld(data);
    }

}
