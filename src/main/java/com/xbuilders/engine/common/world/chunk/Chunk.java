package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.Main;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.common.world.World;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.BlockRegistry;
import com.xbuilders.engine.server.entity.ChunkEntitySet;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.entity.EntitySupplier;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.meshers.ChunkMeshBundle;
import com.xbuilders.engine.common.math.AABB;
import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.Terrain.GenSession;
import com.xbuilders.engine.common.world.WorldData;
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

import static com.xbuilders.Main.LOGGER;
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
    public final MVP mvp;
    public boolean inFrustum;
    public float client_distToPlayer;
    public final AABB aabb;
    public final NeighborInformation neghbors;
    public final boolean isTopChunk;
    public final World world;

    public PillarInformation pillarInformation;

    FutureChunk futureChunk;

    /**
     * The chunk is a reusable class but we have different types of chunk so we have to reuse the most important data
     * and throw away everything else
     */
    public Chunk(Vector3i position, boolean isTopChunk, FutureChunk futureChunk, float distToPlayer, World world) {
        this.position = new Vector3i(position);
        this.mvp = new MVP();
        this.isTopChunk = isTopChunk;
        this.data = new ChunkVoxels(WIDTH, HEIGHT, WIDTH);

        this.aabb = new AABB();
        this.world = world;
        this.neghbors = new NeighborInformation(world);
        this.entities = new ChunkEntitySet(this);


        initVariables(futureChunk, distToPlayer);
    }

    /**
     * This method is how we reuse chunks
     * We take the data from the other chunk and use that in our new chunk
     */
    public Chunk(Chunk other, Vector3i position, boolean isTopChunk, FutureChunk futureChunk, float distToPlayer) {
        //New variables
        this.position = new Vector3i(position);
        this.mvp = new MVP();
        this.isTopChunk = isTopChunk;

        this.aabb = new AABB();
        this.loadFuture = null;
        this.world = other.world;
        this.pillarInformation = null;
        this.generationStatus = 0;

        //Recyclied variables
        this.data = other.data;
        this.data.reset();
        this.neghbors = other.neghbors;
        this.entities = other.entities;
        this.entities.clear();

        initVariables(futureChunk, distToPlayer);
    }

    //A unified place for all variables to be initialized
    private void initVariables(FutureChunk futureChunk, float distToPlayer) {
        this.aabb.setPosAndSize(position.x * WIDTH, position.y * HEIGHT, position.z * WIDTH, WIDTH, HEIGHT, WIDTH);

        neghbors.init(position);


        this.client_distToPlayer = distToPlayer;   // Load the chunk
        this.futureChunk = futureChunk;
    }


    public void load() {
        Client.frameTester.startProcess();
        loadFuture = generationService.submit(client_distToPlayer, () -> {
            try {
                loadChunk(futureChunk);
                return false;
            } finally {
                newGameTasks.incrementAndGet();
            }
        });
        Client.frameTester.endProcess("Load chunk");
    }

    public void loadChunk(FutureChunk futureChunk) {
        File f = world.data.getChunkFile(position);
        try {
            boolean needsSunGeneration = true;
            if (f.exists()) {
                ChunkSavingLoadingUtils.readChunkFromFile(this, f);
                needsSunGeneration = false;
            } else if (world.terrain.isBelowMinHeight(this.position, 0)) {
                GenSession createTerrainOnChunk = world.terrain.createTerrainOnChunk(this);
            }
            if (futureChunk != null) {
                futureChunk.setBlocksInChunk(this);
                needsSunGeneration = true;
            }

            //Load all allEntities to the world
            Client.world.allEntities.addAllEntitiesFromChunk(this);

            // Loading a chunk includes loading sunlight
            setGenerationStatus(needsSunGeneration ? GEN_TERRAIN_LOADED : GEN_SUN_LOADED); //TODO: The world updates sunlight in pillars, therefore setting light to sun_loaded makes no difference because the pillar doesnt know how if a chunk doesnt have sunlight
        } catch (Exception ex) {//For some reason we have to catch incoming errors otherwise they wont be visible
            LOGGER.log(Level.WARNING, "error loading chunk", ex);
        }
    }

    public void dispose() {
        try {
            data.dispose();
        } catch (Exception ex) {
            Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



    /*
     * CHUNK_DATA GENERATION
     * - We first generate the terrain
     * - Mesh generation is the last step
     */
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
