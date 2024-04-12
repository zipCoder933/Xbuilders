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
 * If not having vector3i coords to get/set with this, becomes a sufficient annoyance, I will add a vector3i to match every method.
 */
public class ChunkVoxels {

    public ChunkVoxels(final int sizeX, final int sizeY, final int sizeZ) {
        this.size = new Vector3i(sizeX, sizeY, sizeZ);
        this.blockData = new HashMap<>();
        this.blocks = MemoryUtil.memAllocShort(size.x * size.y * size.z);
        this.light = MemoryUtil.memAlloc(size.x * size.y * size.z);
    }

    public void dispose() {
        MemoryUtil.memFree(blocks);
        MemoryUtil.memFree(light);
        blocks = null;
        light = null;
    }

    public int getIndexOfCoords(final int x, final int y, final int z) {
        return x + this.size.x * (y + this.size.y * z);
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

    //<editor-fold defaultstate="collapsed" desc="Sunlight">
    private ByteBuffer light;

    //DATA STRUCTURE FOR TORCHLIGHT
    //We are going to try doing a single channel of torchlight instead of having multiple channels of torchlight

    public byte getSun(final int x, final int y, final int z) {
        try {
            return (byte) ((light.get(x + size.x * (y + size.y * z)) & 0b11110000) >> 4);
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Block coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }

    public void setSun(final int x, final int y, final int z, final int newVal) {
        try {
            byte origVal = this.light.get(x + size.x * (y + size.y * z));
            this.light.put(x + size.x * (y + size.y * z),
                    (byte) ((origVal & 0b00001111) | (newVal << 4)));
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }

    public int getTorch(final int x, final int y, final int z) {
        try {
            return (this.light.get(x + size.x * (y + size.y * z)) & 0b00001111);
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }

    public void setTorch(final int x, final int y, final int z, int newVal) {
        try {
            //Set the last 4 bytes to the new value
            byte origVal = this.light.get(x + size.x * (y + size.y * z));
            this.light.put(x + size.x * (y + size.y * z),
                    (byte) ((origVal & 0b11110000) | (newVal & 0b00001111)));
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }

    public byte getPackedLight(final int x, final int y, final int z) {
        return this.light.get(x + size.x * (y + size.y * z));
    }

    public void setPackedLight(final int x, final int y, final int z, byte value) {
        this.light.put(x + size.x * (y + size.y * z), value);
    }


//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Blocks">
    private ShortBuffer blocks;

    public short getBlock(final int x, final int y, final int z) {
        try {
            return blocks.get(x + size.x * (y + size.y * z));
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Block coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }

    public void setBlock(final int x, final int y, final int z, final short value) {
        try {
            if (value != ItemList.blocks.BLOCK_AIR.id) {
                blocksAreEmpty = false;
            }
            this.blocks.put(x + size.x * (y + size.y * z), value);
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Block Data">
    //    private BlockData[] blockData;
    //The keys in a hashmap are non eligeble for garbage colleciton unless the whole hashmap is unreachable.
    //Somehow, acessing items from this hashmap has been the main contributor to memory usage in collision handling
    private HashMap<Integer, BlockData> blockData;

    public BlockData getBlockData(final int x, final int y, final int z) {
        int val = x + this.size.x * (y + this.size.y * z); //Causes no overhead
        //        TODO: blockData.get() is what is driving up all of the memory usage in collision handler
        return blockData.get(val);//Causes all the overhead
        //        return blockData[val];
    }
//    public BlockData getBlockData(Vector3i pos) {
//        return getBlockData(pos.x, pos.y, pos.z);
//    }

    public void setBlockData(final int x, final int y, final int z, final BlockData b) {
        this.blockData.put(x + this.size.x * (y + this.size.y * z), b);
    }
    //</editor-fold>
}
