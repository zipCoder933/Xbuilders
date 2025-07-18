package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.engine.common.math.AABB;
import com.xbuilders.engine.common.world.World;
import com.xbuilders.engine.common.world.WorldData;
import com.xbuilders.engine.common.world.chunk.saving.ChunkSavingLoadingUtils;
import com.xbuilders.engine.server.entity.ChunkEntitySet;
import com.xbuilders.window.render.MVP;
import org.joml.Vector3i;

import java.util.Objects;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public ChunkVoxels data;
    public final ChunkEntitySet entities;
    public final Vector3i position;
    public final MVP mvp;
    public boolean inFrustum;
    public final AABB aabb;
    public final NeighborInformation neghbors;
    public final World world;
    public final Chunk otherChunk;


    FutureChunk futureChunk;

    /**
     * The chunk is a reusable class but we have different types of chunk so we have to reuse the most important data
     * and throw away everything else
     */
    public Chunk(Vector3i position, FutureChunk futureChunk, World world) {
        this.position = new Vector3i(position);
        this.mvp = new MVP();
        this.data = new ChunkVoxels(WIDTH, HEIGHT, WIDTH);

        this.aabb = new AABB();
        this.world = world;
        this.neghbors = new NeighborInformation(world);
        this.entities = new ChunkEntitySet(this, world);
        this.otherChunk = null;


        initVariables(futureChunk);
    }

    /**
     * This method is how we reuse chunks
     * We take the data from the other chunk and use that in our new chunk
     */
    public Chunk(Chunk other, Vector3i position, FutureChunk futureChunk, World world) {
        //New variables
        this.position = new Vector3i(position);
        this.mvp = new MVP();

        this.otherChunk = other;
        this.aabb = new AABB();
        this.loadFuture = null;
        this.world = world;
        //Recyclied variables
        this.data = other.data;
        this.data.reset();
        this.neghbors = other.neghbors;
        this.entities = other.entities;
        this.entities.clear();

        initVariables(futureChunk);
    }

    //A unified place for all variables to be initialized
    private void initVariables(FutureChunk futureChunk) {
        this.aabb.setPosAndSize(position.x * WIDTH, position.y * HEIGHT, position.z * WIDTH, WIDTH, HEIGHT, WIDTH);
        neghbors.init(position);
        this.futureChunk = futureChunk;
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


    public boolean gen_terrainLoaded() {
        return true;
    }

    public boolean gen_sunLoaded() {
        return true;
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
}
