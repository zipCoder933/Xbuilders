package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.Main;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.BlockRegistry;
import com.xbuilders.engine.server.entity.ChunkEntitySet;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.entity.EntitySupplier;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.meshers.ChunkMeshBundle;
import com.xbuilders.engine.common.utils.ErrorHandler;
import com.xbuilders.engine.common.math.AABB;
import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.Terrain.GenSession;
import com.xbuilders.engine.common.world.data.WorldData;
import com.xbuilders.engine.common.world.chunk.pillar.PillarInformation;
import com.xbuilders.engine.common.world.chunk.saving.ChunkSavingLoadingUtils;
import com.xbuilders.window.render.MVP;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.io.File;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xbuilders.engine.common.world.World.*;

public class Chunk {

    public long lastModifiedTime;


    /**
     * We dont have to make a needs to be saved call because it wont get saved unless it is owned by the user
     * And we wont ever need to mark as needs to be saved if it is not owned by the user, because it won't be saved
     * <p>
     * Mark the chunk as changed by the user (sets ownedByUser and needsToBeSaved to true)
     */
    public void markAsModified() {
        ownedByUser = true;
        needsToBeSaved = true;
        lastModifiedTime = System.currentTimeMillis();
    }

    private boolean ownedByUser = false;
    protected boolean needsToBeSaved = false;

    /**
     * @return the ownedByUser
     */
    public boolean isOwnedByUser() {
        return ownedByUser;
    }

    /**
     * Having larger chunks means a much greater preformance
     */
    public static final int WIDTH = 32; // The solution was just to clean+build after width change
    public static final int HEIGHT = WIDTH;
    public static final int HALF_WIDTH = WIDTH / 2;

    public static boolean inBounds(int x, int y, int z) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && z >= 0 && z < WIDTH;
    }

    public static boolean inBoundsXZ(int x) {
        return x >= 0 && x < WIDTH;
    }

    public static boolean inBoundsY(int y) {
        return y >= 0 && y < HEIGHT;
    }

    public final ChunkVoxels data;
    public final ChunkEntitySet entities;
    public final Vector3i position;
    public final Matrix4f modelMatrix;
    public final MVP mvp;
    public boolean inFrustum;
    public float client_distToPlayer;
    public final ChunkMeshBundle meshes;
    public final AABB aabb;
    public final NeighborInformation neghbors;
    public boolean isTopChunk;
    public PillarInformation pillarInformation;
    Terrain terrain;
    WorldData info;
    FutureChunk futureChunk;

    public Chunk(int texture, WorldData info, Terrain terrain) {
        this.position = new Vector3i();
        mvp = new MVP();
        data = new ChunkVoxels(WIDTH, HEIGHT, WIDTH);
        meshes = new ChunkMeshBundle(texture, this, terrain);
        modelMatrix = new Matrix4f();
        aabb = new AABB();
        neghbors = new NeighborInformation();
        entities = new ChunkEntitySet(this);
        this.info = info;
        this.terrain = terrain;
    }

    public void init(Vector3i position, FutureChunk futureChunk, float distToPlayer, boolean isTopChunk) {
        entities.clear();
        data.reset();
        generationStatus = 0;//The only time we can reset the generation status
        loadFuture = null;
        mesherFuture = null;
        pillarInformation = null;
        this.isTopChunk = isTopChunk;
        this.position.set(position);
        modelMatrix.identity().setTranslation(position.x * WIDTH, position.y * HEIGHT, position.z * WIDTH);
        aabb.setPosAndSize(position.x * WIDTH, position.y * HEIGHT, position.z * WIDTH, WIDTH, HEIGHT, WIDTH);
        meshes.init(aabb);
        neghbors.init(position);


        this.client_distToPlayer = distToPlayer;   // Load the chunk
        this.futureChunk = futureChunk;

        Client.frameTester.startProcess();
        load();
        Client.frameTester.endProcess("Load chunk");
    }

    public void load() {
        loadFuture = generationService.submit(client_distToPlayer, () -> {
            try {
                loadChunk(info, terrain, futureChunk);
                return false;
            } finally {
                newGameTasks.incrementAndGet();
            }
        });
    }

    public void loadChunk(WorldData info, Terrain terrain, FutureChunk futureChunk) {
        File f = info.getChunkFile(position);

        try {
            boolean needsSunGeneration = true;
            if (f.exists()) {
                ChunkSavingLoadingUtils.readChunkFromFile(this, f);
                needsSunGeneration = false;
            } else if (terrain.isBelowMinHeight(this.position, 0)) {
                GenSession createTerrainOnChunk = terrain.createTerrainOnChunk(this);
            }
            if (futureChunk != null) {
                futureChunk.setBlocksInChunk(this);
                needsSunGeneration = true;
            }

            //Load all entities to the world
            Client.world.entities.addAllEntitiesFromChunk(this);

            // Loading a chunk includes loading sunlight
            setGenerationStatus(needsSunGeneration ? GEN_TERRAIN_LOADED : GEN_SUN_LOADED); //TODO: The world updates sunlight in pillars, therefore setting light to sun_loaded makes no difference because the pillar doesnt know how if a chunk doesnt have sunlight
        } catch (Exception ex) {//For some reason we have to catch incoming errors otherwise they wont be visible
            ErrorHandler.report("Error loading chunk", ex);
        }
    }

    public void dispose() {
        try {
            data.dispose();
        } catch (Exception ex) {
            Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateMVP(Matrix4f projection, Matrix4f view) {
        mvp.update(projection, view, modelMatrix);
    }

    public void updateMesh(boolean updateAllNeighbors, int x, int y, int z) {
        //TODO: There is a bug where the chunk is not updating the mesh
//        if (updateAllNeighbors) ClientWindow.printlnDev("Reg mesh (all neighbors)");
//        else ClientWindow.printlnDev("Reg mesh (" + x + ", " + y + ", " + z + ")");


        if (!neghbors.allFacingNeghborsLoaded) {
            neghbors.cacheNeighbors();
        }
        generateMesh(true);
        if (neghbors.allFacingNeghborsLoaded) {
            if (x == 0 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.NEG_X_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.NEG_X_NEIGHBOR].generateMesh(true);
            } else if (x == Chunk.WIDTH - 1 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.POS_X_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.POS_X_NEIGHBOR].generateMesh(true);
            }

            if (y == 0 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.NEG_Y_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.NEG_Y_NEIGHBOR].generateMesh(true);
            } else if (y == Chunk.WIDTH - 1 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.POS_Y_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.POS_Y_NEIGHBOR].generateMesh(true);
            }

            if (z == 0 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.NEG_Z_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.NEG_Z_NEIGHBOR].generateMesh(true);
            } else if (z == Chunk.WIDTH - 1 || updateAllNeighbors) {
                if (neghbors.neighbors[neghbors.POS_Z_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.POS_Z_NEIGHBOR].generateMesh(true);
            }
        }
    }

    /*
     * CHUNK GENERATION
     * - We first generate the terrain
     * - Mesh generation is the last step
     */
    private Future<ChunkMeshBundle> mesherFuture;
    // public Future<Boolean> lightFuture;
    public Future<Boolean> loadFuture;

    private int generationStatus = 0;
    public static final int GEN_TERRAIN_LOADED = 1;
    public static final int GEN_SUN_LOADED = 2;
    public static final int GEN_COMPLETE = 3;


    public int getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(int newGenStatus) {
        if (this.generationStatus < newGenStatus) {//Only update if we are PROGRESSING the generation status
            this.generationStatus = newGenStatus;
        }
    }

    public boolean gen_terrainLoaded() {
        return getGenerationStatus() >= Chunk.GEN_TERRAIN_LOADED;
    }

    public boolean gen_sunLoaded() {
        return getGenerationStatus() >= Chunk.GEN_SUN_LOADED;
    }

    public boolean gen_Complete() {
        return getGenerationStatus() >= Chunk.GEN_COMPLETE;
    }

    // public static FrameTester chunkGenFrameTester = new FrameTester("Chunk
    // generation");
    // static {
    // chunkGenFrameTester.setUpdateTimeMS(1000);
    // }


    public void prepare(Terrain terrain, long frame, boolean isSettingUpWorld) {
        if (loadFuture != null && loadFuture.isDone()) {

            if (isTopChunk && pillarInformation != null && !pillarInformation.pillarLightLoaded && pillarInformation.isPillarLoaded()) {
                pillarInformation.initLighting(null, terrain, client_distToPlayer);
                pillarInformation.pillarLightLoaded = true;
            }

            if (getGenerationStatus() >= GEN_SUN_LOADED && !gen_Complete()) {
                if (neghbors.allNeghborsLoaded) {
                    loadFuture = null;
                    Client.frameTester.startProcess();
                    mesherFuture = meshService.submit(() -> {

                        if (Client.world.data == null) return null; // Quick fix. TODO: remove this line

                        meshes.compute();
                        setGenerationStatus(GEN_COMPLETE);
                        return meshes;
                    });
                    Client.frameTester.endProcess("red Compute meshes");
                } else {
                    /**
                     * The cacheNeighbors is still a bottleneck. I have kind of fixed it
                     * by only calling it every 10th frame
                     */
                    Client.frameTester.startProcess();
                    if (frame % 20 == 0 || isSettingUpWorld) {
                        neghbors.cacheNeighbors();
                    }
                    Client.frameTester.endProcess("red Cache Neghbors");
                }
            }
        }

        // send mesh to GPU
        if (inFrustum || isSettingUpWorld) {
            Client.frameTester.startProcess();
            entities.chunkUpdatedMesh = true;
            sendMeshToGPU();
            Client.frameTester.endProcess("Send mesh to GPU");
        }
    }

    /**
     * Queues a task to mesh the chunk
     */
    public void generateMesh(boolean isPlayerUpdate) {
        setGenerationStatus(GEN_COMPLETE);
        if (mesherFuture != null) {
            mesherFuture.cancel(true);
            mesherFuture = null;
        }

        if (isPlayerUpdate) mesherFuture = playerUpdating_meshService.submit(() -> {
            meshes.compute();
            return meshes;
        });
        else mesherFuture = meshService.submit(() -> {
            meshes.compute();
            return meshes;
        });
    }

    /**
     * sends the mesh to the GPU after meshing
     */
    public void sendMeshToGPU() {
        // Send mesh to GPU if mesh thread is finished
        if (mesherFuture != null && mesherFuture.isDone() && gen_Complete()) {
            try {
                mesherFuture.get().sendToGPU();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mesherFuture = null;
            }
        }
    }

    Object saveLock = new Object();

    /**
     * Only saves the chunk if it is owned by the user and has changed since it
     * was last saved.
     *
     * @param info
     * @return if the chunk was really saved
     */
    public boolean save(WorldData info) {
        if (isOwnedByUser() && needsToBeSaved) {
            synchronized (saveLock) {
                needsToBeSaved = false;
                return ChunkSavingLoadingUtils.writeChunkToFile(this, info.getChunkFile(position));
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.position);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Chunk other = (Chunk) obj;
        return Objects.equals(this.position, other.position);
    }

    @Override
    public String toString() {
        return "Chunk{" + position.x + "," + position.y + "," + position.z + '}';
    }

    private static Random randomTick_random = new Random();
    public static float randomTickLikelyhoodMultiplier = 1;

    public static float getRandomTickLikelihood() {
        return randomTickLikelyhoodMultiplier * 0.005f;
    }

    /**
     * Ticks the chunk
     *
     * @param spawnEntities
     * @return if the chunk mesh was updated
     */
    public boolean tick(boolean spawnEntities) {
        boolean updatedChunkMesh = false;
        int wx = position.x * WIDTH;
        int wy = position.y * HEIGHT;
        int wz = position.z * WIDTH;
        float spawnLikelyhood = 0;

        EntitySupplier entityToSpawn = null;
        if (spawnEntities && Registrys.entities.autonomousList.size() > 0) {
            entityToSpawn = Registrys.entities.autonomousList.get(randomTick_random.nextInt(Registrys.entities.autonomousList.size()));
            spawnLikelyhood = entityToSpawn.spawnLikelyhood.get();
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int z = 0; z < WIDTH; z++) {

                    if (randomTick_random.nextFloat() <= getRandomTickLikelihood()) {
                        short blockID = data.getBlock(x, y, z);
                        if (blockID != BlockRegistry.BLOCK_AIR.id) {
                            Block block = Registrys.getBlock(blockID);
                            if (block.randomTickEvent != null) {
                                if (block.randomTickEvent.run(wx + x, wy + y, wz + z)) updatedChunkMesh = true;
                            }
                        }
                    }

                    if (spawnEntities && entityToSpawn != null
                            && client_distToPlayer > 10
                            && randomTick_random.nextFloat() <= spawnLikelyhood &&
                            entityToSpawn.spawnCondition.get(wx + x, wy + y, wz + z)) {
                        Vector3f pos = new Vector3f(wx + x, wy + y, wz + z);
                        Entity e = Main.getServer().placeEntity(entityToSpawn, pos, null);
                        e.spawnedNaturally = true;
                    }

                }
            }
        }
        return updatedChunkMesh;
    }
}
