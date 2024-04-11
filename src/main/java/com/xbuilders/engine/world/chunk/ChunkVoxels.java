// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world.chunk;

import com.xbuilders.engine.items.ItemList;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

import com.xbuilders.engine.world.light.TorchChannelSet;
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
        this.sun = MemoryUtil.memAlloc(size.x * size.y * size.z);
        this.torch = MemoryUtil.memAllocShort(size.x * size.y * size.z);
    }

    public void dispose() {
        MemoryUtil.memFree(blocks);
        MemoryUtil.memFree(sun);
        MemoryUtil.memFree(torch);
        blocks = null;
        sun = null;
        torch = null;
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
            sun.put(i, (byte) 15);
            torch.put(i, (short) 0);
        }
        blocksAreEmpty = true;
    }

    //<editor-fold defaultstate="collapsed" desc="Sunlight">
    private ByteBuffer sun;
    private ShortBuffer torch;

    //DATA STRUCTURE FOR TORCHLIGHT
    //We cant use float datatype for sun and torch because bitwise operations on floats are not supported
    //It also isnt a good idea to put everything on one number because we want sunlight to have faster access

    //We dont need torchChannel set to store torchlight anymore, instead we are using 4 bits for each channel
    //sun:   SSSSS0000
    //torch: 11112222 33334444
    //Where the channel is the falloff / 4


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

    public TorchChannelSet getTorch(final int x, final int y, final int z) {
        try {
            return new TorchChannelSet(torch.get(x + size.x * (y + size.y * z)));
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Block coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }

    public void setTorch(final int x, final int y, final int z, final byte faloff, final byte value) {
        try {
            this.torch.put(x + size.x * (y + size.y * z), value);
        } catch (IndexOutOfBoundsException ex) {
            throw new IndexOutOfBoundsException("Coordinates " + x + ", " + y + ", " + z + " out of bounds!");
        }
    }

    public byte getPackedSunAndTorch(final int x, final int y, final int z) {
        int sun = this.getSun(x, y, z) & 0x0F;
        int torch2 = torch.get(x + size.x * (y + size.y * z)) & 0x0F;
        return (byte) ((sun << 4) | torch2);
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
