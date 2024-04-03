// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world.wcc;

import com.xbuilders.engine.utils.MiscUtils;

import static com.xbuilders.engine.utils.math.MathUtils.positiveMod;

import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.Chunk;

import java.nio.IntBuffer;

import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

public class WCCi {

    public final Vector3i chunk;
    public final Vector3i chunkVoxel;

    public Chunk getChunk(World world) {
        return world.getChunk(chunk);
    }

    public WCCi() {
        this.chunk = new Vector3i();
        this.chunkVoxel = new Vector3i();
    }

    public WCCi(IntBuffer buff1, IntBuffer buff2) {
        this.chunk = new Vector3i(buff1);
        this.chunkVoxel = new Vector3i(buff2);
    }

    public WCCi(MemoryStack stack) {
        this.chunk = new Vector3i(stack.mallocInt(3));
        this.chunkVoxel = new Vector3i(stack.mallocInt(3));
    }

    public WCCi(final Vector3i chunk, final Vector3i block) {
        this.chunk = chunk;
        this.chunkVoxel = block;
    }

    public WCCi set(Vector3i vec) {
        return set(vec.x, vec.y, vec.z);
    }

    /*   public static WCCi getNeighboringWCC(final Vector3i currentChunk, ) {
        return new WCCi(getNeighboringSubChunk(currentChunk, x, y, z), normalizeToChunkSpace(x, y, z));
    }*/

    public WCCi getNeighboring(Vector3i chunkPos, final int x, final int y, final int z) {
        getNeighboringChunk(this.chunk, chunkPos, x, y, z);
        normalizeToChunkSpace(this.chunkVoxel, x, y, z);
        return this;
    }
    public WCCi getNeighboring(Vector3i chunkPos, Vector3i blockPos) {
        getNeighboringChunk(this.chunk, chunkPos, blockPos.x, blockPos.y, blockPos.z);
        normalizeToChunkSpace(this.chunkVoxel, blockPos.x, blockPos.y, blockPos.z);
        return this;
    }

    public WCCi set(final int worldX, final int worldY, final int worldZ) {
        int blockX = positiveMod(worldX, Chunk.WIDTH);
        int blockY = positiveMod(worldY, Chunk.WIDTH);
        int blockZ = positiveMod(worldZ, Chunk.WIDTH);

        this.chunkVoxel.set(blockX, blockY, blockZ);

        int chunkX = chunkDiv(worldX);
        int chunkY = chunkDiv(worldY);
        int chunkZ = chunkDiv(worldZ);

        this.chunk.set(chunkX, chunkY, chunkZ);
        return this;
    }

    @Override
    public String toString() {
        return "WCC{chunk=" + MiscUtils.printVector(chunk)
                + ", voxel=" + MiscUtils.printVector(chunkVoxel) + '}';
    }

    // <editor-fold defaultstate="collapsed" desc="static methods">

    /**
     * The chunk coordinate should START when the chunk-block coordinate is at
     * 0, and END when the block PASSES chunk width-1.
     *
     * @param worldN the world coordinate
     * @return the chunk coordinate
     */
    public static int chunkDiv(int worldN) {
        return worldN < 0
                ? ((worldN + 1) / Chunk.WIDTH) - 1
                : (worldN / Chunk.WIDTH);
    }

    public static float chunkDiv(float worldN) {
        return worldN < 0
                ? ((worldN + 1) / Chunk.WIDTH) - 1
                : (worldN / Chunk.WIDTH);
    }

    public static void testChunkDiv() {
        for (int world = -Chunk.WIDTH * 3; world < Chunk.WIDTH * 3; world++) {
            System.out.println("World: " + world
                    + "\tChunk: " + WCCi.chunkDiv(world)
                    + "\tBlock: " + positiveMod(world, Chunk.WIDTH));
        }
    }

    public static void getNeighboringChunk(Vector3i vec,
                                           final Vector3i chunkPos,
                                           final int x, final int y, final int z) {
        int coordsX = chunkPos.x;
        int coordsY = chunkPos.y;
        int coordsZ = chunkPos.z;
        if (x < 0) {
            --coordsX;
        } else if (x >= Chunk.WIDTH) {
            ++coordsX;
        }
        if (y < 0) {
            --coordsY;
        } else if (y >= Chunk.WIDTH) {
            ++coordsY;
        }
        if (z < 0) {
            --coordsZ;
        } else if (z >= Chunk.WIDTH) {
            ++coordsZ;
        }
        vec.set(coordsX, coordsY, coordsZ);
    }

    public static void getChunkAtWorldPos(final Vector3i vec,
                                          final int worldX, final int worldY, final int worldZ) {

        int chunkX = chunkDiv(worldX);
        int chunkY = chunkDiv(worldY);
        int chunkZ = chunkDiv(worldZ);
        vec.set(chunkX, chunkY, chunkZ);
    }

    public static void normalizeToChunkSpace(Vector3i vec,
                                             final int worldX, final int worldY, final int worldZ) {

        int blockX = positiveMod(worldX, Chunk.WIDTH);
        int blockY = positiveMod(worldY, Chunk.WIDTH);
        int blockZ = positiveMod(worldZ, Chunk.WIDTH);
        vec.set(blockX, blockY, blockZ);
    }

    public static Vector3i chunkSpaceToWorldSpace(
            final Vector3i chunkPos,
            final int worldX, final int worldY, final int worldZ) {
        return new Vector3i(
                worldX + chunkPos.x * Chunk.WIDTH,
                worldY + chunkPos.y * Chunk.WIDTH,
                worldZ + chunkPos.z * Chunk.WIDTH);
    }

    public static Vector3i chunkSpaceToWorldSpace(WCCi wcc) {
        return new Vector3i(
                wcc.chunk.x * Chunk.WIDTH + wcc.chunkVoxel.x,
                wcc.chunk.y * Chunk.WIDTH + wcc.chunkVoxel.y,
                wcc.chunk.z * Chunk.WIDTH + wcc.chunkVoxel.z);
    }
    // </editor-fold>
}
