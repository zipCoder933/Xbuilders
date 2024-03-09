// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world.chunk;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.world.chunk.BlockData;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import org.joml.Vector3i;
import org.lwjgl.system.MemoryUtil;

public class ChunkVoxels {

    public ChunkVoxels(final int sizeX, final int sizeY, final int sizeZ) {
        this.size = new Vector3i(sizeX, sizeY, sizeZ);
        this.blockData = new HashMap<>();
        this.blocks = MemoryUtil.memAllocShort(size.x * size.y * size.z);
        this.sun = MemoryUtil.memAlloc(size.x * size.y * size.z);
    }

    public void dispose() {
        MemoryUtil.memFree(blocks);
        MemoryUtil.memFree(sun);
        blocks = null;
        sun = null;
    }

    public int getIndexOfCoords(final int x, final int y, final int z) {
        return x + this.size.x * (y + this.size.y * z);
    }

    public final Vector3i size;

    public void clear() {
        for (int i = 0; i < blocks.capacity(); i++) {
            blocks.put(i, (short) 0);
            sun.put(i, (byte) 15);
        }
        blockData.clear();
    }

    //<editor-fold defaultstate="collapsed" desc="Sunlight">
    private ByteBuffer sun;

    public byte getSun(final int x, final int y, final int z) {
        try {
            return sun.get(x + size.x * (y + size.y * z));
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Block coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }

    public void setSun(final int x, final int y, final int z, final byte value) {
        try {
            this.sun.put(x + size.x * (y + size.y * z), value);
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
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

    public void setBlockData(final int x, final int y, final int z, final BlockData b) {
        this.blockData.put(x + this.size.x * (y + this.size.y * z), b);
    }
    //</editor-fold>
}
