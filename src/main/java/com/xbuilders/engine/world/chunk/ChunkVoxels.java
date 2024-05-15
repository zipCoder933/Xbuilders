// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world.chunk;

import com.xbuilders.engine.items.ItemList;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;

/**
 * If not having vector3i coords to get/set with this, becomes a sufficient
 * annoyance, I will add a vector3i to match every method.
 */
public class ChunkVoxels {
//TODO: Switch out chunk voxels to use arrays instead of off-heap buffers
    final int dataSize;

    public ChunkVoxels(final int sizeX, final int sizeY, final int sizeZ) {
        this.size = new Vector3i(sizeX, sizeY, sizeZ);
        this.blockData = new HashMap<>();
        this.blocks = MemoryUtil.memAllocShort(size.x * size.y * size.z);
        this.light = MemoryUtil.memAlloc(size.x * size.y * size.z);
        dataSize = blocks.capacity();
    }

    public void dispose() {
        MemoryUtil.memFree(blocks);
        MemoryUtil.memFree(light);
        blocks = null;
        light = null;
    }

    public int getIndexOfCoords(final int x, final int y, final int z) {
        int indx = x + this.size.x * (y + this.size.y * z);
        if (indx >= dataSize || indx < 0) {// IMPORTANT: If we set a block out of bounds, it overflows and causes chunk
                                           // blocks to get mangled
            throw new IndexOutOfBoundsException(
                    "Chunk voxel coordinates (" + x + ", " + y + ", " + z + ") out of bounds!");
        }
        return indx;
    }

    public final Vector3i size;
    public boolean blocksAreEmpty = true;

    public void clear() {
        blockData.clear();
        for (int i = 0; i < blocks.capacity(); i++) {
            blocks.put(i, (short) 0);
            light.put(i, (byte) ((15 << 4))); // Set first 4 bits to 15, last 4 bits to 0
        }
        blocksAreEmpty = true;
    }

    // <editor-fold defaultstate="collapsed" desc="Sunlight">
    private ByteBuffer light;

    // DATA STRUCTURE FOR TORCHLIGHT
    // We are going to try doing a single channel of torchlight instead of having
    // multiple channels of torchlight

    public byte getSun(final int x, final int y, final int z) {
        return (byte) ((light.get(getIndexOfCoords(x, y, z)) & 0b11110000) >> 4);
    }

    public void setSun(final int x, final int y, final int z, final int newVal) {
        byte origVal = this.light.get(getIndexOfCoords(x, y, z));
        this.light.put(getIndexOfCoords(x, y, z),
                (byte) ((origVal & 0b00001111) | (newVal << 4)));

    }

    public int getTorch(final int x, final int y, final int z) {
        return (this.light.get(getIndexOfCoords(x, y, z)) & 0b00001111);

    }

    public void setTorch(final int x, final int y, final int z, int newVal) {
        // Set the last 4 bytes to the new value
        byte origVal = this.light.get(getIndexOfCoords(x, y, z));
        this.light.put(getIndexOfCoords(x, y, z),
                (byte) ((origVal & 0b11110000) | (newVal & 0b00001111)));

    }

    public byte getPackedLight(final int x, final int y, final int z) {
        // Returns an 8 bit packed value
        return this.light.get(getIndexOfCoords(x, y, z));
    }

    public void setPackedLight(final int x, final int y, final int z, byte value) {
        this.light.put(getIndexOfCoords(x, y, z), value);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Blocks">
    private ShortBuffer blocks;

    public short getBlock(final int x, final int y, final int z) {
        return blocks.get(getIndexOfCoords(x, y, z));
    }

    public void setBlock(final int x, final int y, final int z, final short value) {
        // try {//TODO: Decide if we should throw an error if we set blocks out of bounds or just ignore it
            this.blocks.put(getIndexOfCoords(x, y, z), value);
            if (value != ItemList.blocks.BLOCK_AIR.id) {
                blocksAreEmpty = false;
            }
        // } catch (IndexOutOfBoundsException e) {
        //     // Do nothing
        // }
    }
    
    public void setBlockData(final int x, final int y, final int z, final BlockData b) {
        // try {
            this.blockData.put(x + this.size.x * (y + this.size.y * z), b);
        // } catch (IndexOutOfBoundsException e) {
        //     // Do nothing
        // }
    }

    // <editor-fold defaultstate="collapsed" desc="Block Data">
    // private BlockData[] blockData;
    // The keys in a hashmap are non eligeble for garbage colleciton unless the
    // whole hashmap is unreachable.
    // Somehow, acessing items from this hashmap has been the main contributor to
    // memory usage in collision handling
    private HashMap<Integer, BlockData> blockData;

    public BlockData getBlockData(final int x, final int y, final int z) {
        int val = getIndexOfCoords(x, y, z); // Causes no overhead
        // TODO: blockData.get() is what is driving up all of the memory usage in
        // collision handler
        return blockData.get(val);// Causes all the overhead
        // return blockData[val];
    }
    // public BlockData getBlockData(Vector3i pos) {
    // return getBlockData(pos.x, pos.y, pos.z);
    // }

    // </editor-fold>
}
