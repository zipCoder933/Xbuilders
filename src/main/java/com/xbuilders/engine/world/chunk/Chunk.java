package com.xbuilders.engine.world.chunk;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.rendering.chunk.ChunkMeshBundle;
import com.xbuilders.engine.items.ChunkEntitySet;
import com.xbuilders.engine.utils.BFS.HashQueue;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.Terrain.GenSession;
import com.xbuilders.engine.world.World;

import static com.xbuilders.engine.world.World.newGameTasks;
import static com.xbuilders.engine.world.World.generationService;
import static com.xbuilders.engine.world.World.meshService;

import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.chunk.pillar.PillarInformation;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.BFS.ChunkNode;
import com.xbuilders.window.render.MVP;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joml.Matrix4f;
import org.joml.Vector3i;

public class Chunk {

    /**
     * Mark the chunk as changed by the user (sets ownedByUser and
     * needsToBeSaved to true)
     */
    public void markAsModifiedByUser() {
        ownedByUser = true;
        needsToBeSaved = true;
        // We dont have to make a needs to be saved call because it wont get saved
        // unless it is owned by the user
        // And we wont ever need to mark as needs to be saved if it is not owned by the
        // user, because it won't be saved
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
    public float distToPlayer;
    public final ChunkMeshBundle meshes;
    public final AABB aabb;
    public final NeighborInformation neghbors;
    public boolean isTopChunk;
    public PillarInformation pillarInformation;
    Terrain terrain;
    WorldInfo info;
    FutureChunk futureChunk;

    public Chunk(int texture) {
        this.position = new Vector3i();
        mvp = new MVP();
        data = new ChunkVoxels(WIDTH, HEIGHT, WIDTH);
        meshes = new ChunkMeshBundle(texture, this);
        modelMatrix = new Matrix4f();
        aabb = new AABB();
        neghbors = new NeighborInformation();
        entities = new ChunkEntitySet(this);
    }

    public void init(Vector3i position, WorldInfo info,
                     Terrain terrain, FutureChunk futureChunk,
                     float distToPlayer, boolean isTopChunk) {
        entities.clear();
        data.clear();
        generationStatus = 0;//The only time we can reset the generation status
        loadFuture = null;
        mesherFuture = null;
        lightQueue.clear();
        pillarInformation = null;
        this.isTopChunk = isTopChunk;
        this.position.set(position);
        modelMatrix.identity().setTranslation(
                position.x * WIDTH,
                position.y * HEIGHT,
                position.z * WIDTH);
        aabb.setPosAndSize(position.x * WIDTH, position.y * HEIGHT, position.z * WIDTH,
                WIDTH, HEIGHT, WIDTH);
        meshes.init();
        neghbors.init(position);
        // Load the chunk

        this.info = info;
        this.terrain = terrain;
        this.distToPlayer = distToPlayer;
        this.futureChunk = futureChunk;

        World.frameTester.startProcess();
        load();
        World.frameTester.endProcess("Load chunk");
    }

    public void load() {
        loadFuture = generationService.submit(distToPlayer, () -> {
            try {
                loadChunk(info, terrain, futureChunk);
                return false;
            } finally {
                newGameTasks.incrementAndGet();
            }
        });
    }

    public void loadChunk(WorldInfo info, Terrain terrain, FutureChunk futureChunk) {
        File f = info.getChunkFile(position);

        try {
            boolean needsSunGeneration = true;
            if (f.exists()) {
                ChunkSavingLoadingUtils.readChunkFromFile(this, f);
                needsSunGeneration = false;
            } else {
                GenSession createTerrainOnChunk = terrain.createTerrainOnChunk(this);
            }
            if (futureChunk != null) {
                futureChunk.setBlocksInChunk(this);
                needsSunGeneration = true;
            }
            // Loading a chunk includes loading sunlight
            setGenerationStatus(needsSunGeneration ? GEN_TERRAIN_LOADED : GEN_SUN_LOADED); //TODO: The world updates sunlight in pillars, therefore setting light to sun_loaded makes no difference because the pillar doesnt know how if a chunk doesnt have sunlight
        } catch (Exception ex) {//For some reason we have to catch incoming errors otherwise they wont be visible
            ErrorHandler.handleFatalError("Error loading chunk", ex);
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

    public void updateMesh(int x, int y, int z) {
        if (!neghbors.allFacingNeghborsLoaded) {
            neghbors.cacheNeighbors();
        }
        generateMesh();
        if (neghbors.allFacingNeghborsLoaded) {
            if (x == 0) {
                if (neghbors.neighbors[neghbors.NEG_X_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.NEG_X_NEIGHBOR].generateMesh();
            } else if (x == Chunk.WIDTH - 1) {
                if (neghbors.neighbors[neghbors.POS_X_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.POS_X_NEIGHBOR].generateMesh();
            }

            if (y == 0) {
                if (neghbors.neighbors[neghbors.NEG_Y_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.NEG_Y_NEIGHBOR].generateMesh();
            } else if (y == Chunk.WIDTH - 1) {
                if (neghbors.neighbors[neghbors.POS_Y_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.POS_Y_NEIGHBOR].generateMesh();
            }

            if (z == 0) {
                if (neghbors.neighbors[neghbors.NEG_Z_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.NEG_Z_NEIGHBOR].generateMesh();
            } else if (z == Chunk.WIDTH - 1) {
                if (neghbors.neighbors[neghbors.POS_Z_NEIGHBOR] != null)
                    neghbors.neighbors[neghbors.POS_Z_NEIGHBOR].generateMesh();
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

    final HashQueue<ChunkNode> lightQueue = new HashQueue<>();

    public void prepare(Terrain terrain, long frame, boolean isSettingUpWorld) {
        if (loadFuture != null && loadFuture.isDone()) {

            if (isTopChunk && pillarInformation != null
                    && pillarInformation.isPillarLoaded()) {
                loadFuture = null;
                pillarInformation.initLighting(lightQueue, terrain, distToPlayer);
            }

            if (getGenerationStatus() >= GEN_SUN_LOADED && !gen_Complete()) {
                if (neghbors.allNeghborsLoaded) {
                    loadFuture = null;
                    World.frameTester.startProcess();
                    mesherFuture = meshService.submit(() -> {
                        if (GameScene.world.info == null)
                            return null; // Quick fix. TODO: remove this line
                        meshes.compute();
                        setGenerationStatus(GEN_COMPLETE);
                        // if(!meshes.isEmpty()) System.out.println("Mesh computed!");
                        return meshes;
                    });
                    World.frameTester.endProcess("red Compute meshes");
                }else{
                    /**
                     * The cacheNeighbors is still a bottleneck. I have kind of fixed it
                     * by only calling it every 10th frame
                     */
                    World.frameTester.startProcess();
                    if (frame % 20 == 0 || isSettingUpWorld) {
                        neghbors.cacheNeighbors();
                    }
                    World.frameTester.endProcess("red Cache Neghbors");
                }
            }
        }

        // send mesh to GPU
        if (inFrustum || isSettingUpWorld) {
            World.frameTester.startProcess();
            entities.chunkUpdatedMesh = true;
            sendMeshToGPU();
            World.frameTester.endProcess("Send mesh to GPU");
        }
    }

    /**
     * Queues a task to mesh the chunk
     */
    public void generateMesh() {
        setGenerationStatus(GEN_COMPLETE);
        if (mesherFuture != null) {
            mesherFuture.cancel(true);
            mesherFuture = null;
        }
        mesherFuture = meshService.submit(() -> {
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
    public boolean save(WorldInfo info) {
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

}
