/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.world.chunk;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.math.MathUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 * <br>
 * <h2>all Information about the format of the chunk files are saved in the "Chunk saving and loading.md" file</h2>
 */
class ChunkSavingLoadingUtils {

    public static final byte NEWLINE_BYTE = Byte.MIN_VALUE;
    protected static final byte VOXEL_BYTE = -127;
    protected static final byte ENTITY_BYTE = -126;
    protected static final byte BYTE_SKIP_ALL_VOXELS = -125;
    protected static final int METADATA_BYTES = 10;


    private static String printSubList(byte[] bytes, int target, int radius) {
        int start = MathUtils.clamp(target - radius, 0, bytes.length - 1);
        int end = MathUtils.clamp(target + radius, 0, bytes.length - 1);
        String str = "🎯=" + target + "(";
        for (int i = start; i < end; i++) {
            if (i == target) {
                str += ("<" + bytes[i] + "> ");
            } else {
                str += (bytes[i] + " ");
            }
        }
        return str + ")";
    }

    private static String printBytesFormatted(byte[] bytes) {
        String str = "";
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == NEWLINE_BYTE) str += "(-128\n)";
            else if (bytes[i] == VOXEL_BYTE) str += "VOXEL ";
            else if (bytes[i] == BYTE_SKIP_ALL_VOXELS) str += "SKIP ";
            else if (bytes[i] == ENTITY_BYTE) str += "ENTITY ";
            else str += bytes[i] + " ";
        }
        return str;
    }

    private static void writeAndVerifyBuffer(final OutputStream out, final ByteBuffer buffer) throws IOException {
        for (int i = 0; i < buffer.capacity(); i++) {
            byte b = buffer.get(i);
            if (b == NEWLINE_BYTE) {
                throw new IllegalArgumentException("The byte [" + NEWLINE_BYTE + "] is forbidden for use.");
            }
            out.write(b);
        }
    }

    private static void writeShort(OutputStream out, final short x) throws IOException {
        out.write((byte) (x & 0xFF));
        out.write((byte) ((x >> 8) & 0xFF));
    }

    private static void writeUnsignedShort(OutputStream out, final int x) throws IOException {
        out.write((byte) (x & 0xFF));
        out.write((byte) ((x >> 8) & 0xFF));
    }

    private static int readShort(final byte b1, final byte b2) {
        return (b2 << 8 | (b1 & 0xFF));
    }

    private final static float maxMult16bits = (float) ((Math.pow(2, 10) / Chunk.WIDTH) - 1);

    private static void writeChunkVoxelCoords(OutputStream out, Vector3f vec) throws IOException {
//        System.out.println("Writing as " + vec.x + ", " + vec.y + ", " + vec.z);
        writeUnsignedShort(out, (int) (vec.x * maxMult16bits));
        writeUnsignedShort(out, (int) (vec.y * maxMult16bits));
        writeUnsignedShort(out, (int) (vec.z * maxMult16bits));
    }

    private static Vector3f readChunkVoxelCoords(AtomicInteger start, byte[] bytes) {
        float x = readShort(bytes[start.get()], bytes[start.get() + 1]);
        float y = readShort(bytes[start.get() + 2], bytes[start.get() + 3]);
        float z = readShort(bytes[start.get() + 4], bytes[start.get() + 5]);
        x = x / maxMult16bits;
        y = y / maxMult16bits;
        z = z / maxMult16bits;
//        System.out.println("Reading as " + x + ", " + y + ", " + z);
        start.set(start.get() + 6);
        return new Vector3f(x, y, z);
    }

    private static void writeEntity(final XBFilterOutputStream filteredOut, final OutputStream out2, Entity entity) throws IOException {
        out2.write(ENTITY_BYTE);
        writeShort(out2, entity.link.id);

        //Write position
        entity.updatePosition();
        writeChunkVoxelCoords(out2, entity.chunkPosition.chunkVoxel);

        entity.toBytes(filteredOut);
        out2.write(NEWLINE_BYTE);
    }

    private static Entity readEntity(Chunk chunk, final byte[] bytes, AtomicInteger start) {
//        System.out.println("\nStarting to read entity: " + printSubList(bytes, start.get(), 5));
        final short entityID = (short) readShort(bytes[start.get() + 1], bytes[start.get() + 2]);
        EntityLink link = ItemList.getEntity(entityID);
        start.set(start.get() + 3);

        //Read position
        Vector3f chunkVox = readChunkVoxelCoords(start, bytes);

        //Read entity data
        ArrayList<Byte> entityBytes = new ArrayList<>();
        while (true) {
            final byte b = bytes[start.get()];
            start.set(start.get() + 1);
            if (b == NEWLINE_BYTE) {
                break;
            } else {
                entityBytes.add(b);
            }
        }

        if (bytes[start.get()] != ENTITY_BYTE) {
            start.set(start.get() - 1);
        }
//        System.out.println("Ending value: " + printSubList(bytes, start.get(), 5));
        return link.makeNew(chunk,
                chunkVox.x + chunk.position.x * Chunk.WIDTH,
                chunkVox.y + chunk.position.y * Chunk.WIDTH,
                chunkVox.z + chunk.position.z * Chunk.WIDTH,
                entityBytes);
    }

    protected static boolean writeChunkToFile(final Chunk chunk, final File f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            try (FileOutputStream fos = new FileOutputStream(f); GZIPOutputStream out = new GZIPOutputStream(fos)) {
                out.write(new byte[METADATA_BYTES]);
                boolean iterateOverVoxels = true;


                //Write entities
                XBFilterOutputStream fout = new XBFilterOutputStream(out);
                for (int i = 0; i < chunk.entities.list.size(); i++) {
                    Entity entity = chunk.entities.list.get(i);
                    writeEntity(fout, out, entity);
                }

                //Write voxels
                if (iterateOverVoxels) {
                    for (int y = chunk.data.size.y - 1; y >= 0; y--) {
                        for (int x = 0; x < chunk.data.size.x; ++x) {
                            for (int z = 0; z < chunk.data.size.z; ++z) {
                                writeVoxel(chunk, out, fout, x, y, z);
                            }
                        }
                    }
                } else {
                    out.write(BYTE_SKIP_ALL_VOXELS);
                }

            } catch (FileNotFoundException ex) {
                ErrorHandler.handleFatalError(ex);
                return false;
            } catch (IOException ex) {
                ErrorHandler.handleFatalError(ex);
                return false;
            } catch (Exception ex) {
                ErrorHandler.handleFatalError(ex);
                return false;
            }
        }
        return true;
    }


    protected static boolean readChunkFromFile(final Chunk chunk, final File f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            AtomicInteger start = new AtomicInteger(0);
            start.set(0);

            try (FileInputStream fis = new FileInputStream(f); GZIPInputStream input = new GZIPInputStream(fis)) {
                byte[] metadata = input.readNBytes(METADATA_BYTES);
                final byte[] bytes = input.readAllBytes();

//                //Print bytes formatted
//                String str = printBytesFormatted(bytes);
//                Files.write(new File(f.getParent() + "/" + f.getName() + ".formatted").toPath(), str.getBytes());
//                System.out.println("SAVED FORMATTED BYTES");

                //Load the entities
                boolean hasEntities = false;
                while (bytes[start.get()] == ENTITY_BYTE) {
                    Entity entity = readEntity(chunk, bytes, start);
                    chunk.entities.list.add(entity);
                    hasEntities = true;
                }
                if (hasEntities) {
                    start.set(start.get() + 1);  //We have to move 1 byte past the entity newline byte to start reading voxels
                }
//                System.out.println("Voxels start byte: " + printSubList(bytes, start.get(), 10));

                //Load the voxels
                chunkVoxels:
                for (int y = chunk.data.size.y - 1; y >= 0; y--) {
                    for (int x = 0; x < chunk.data.size.x; ++x) {
                        for (int z = 0; z < chunk.data.size.z; ++z) {
                            final byte startByte = bytes[start.get()];
                            if (startByte == BYTE_SKIP_ALL_VOXELS) {
                                start.set(start.get() + 1);
                                continue chunkVoxels;
                            } else if (startByte == NEWLINE_BYTE) { //Newline means end of voxel
                                start.set(start.get() + 1);
                            } else { //Every non air voxel starts with pipe byte
                                readVoxel(bytes, chunk, x, y, z, start);
                            }
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                ErrorHandler.handleFatalError("An error occurred reading chunk " + chunk, "", ex, false);
                return false;
            } catch (IOException ex) {
                ErrorHandler.handleFatalError("An error occurred reading chunk " + chunk, "", ex, false);
                return false;
            } catch (Exception ex) {
                ErrorHandler.handleFatalError("An error occurred reading chunk " + chunk, "", ex, false);
                return false;
            }
        }
        return true;
    }

    private static void writeVoxel(Chunk chunk, GZIPOutputStream out, XBFilterOutputStream fout, int x, int y, int z) throws IOException {
        //VOXEL, LIGHT, BLOCK ID, BLOCK ID, BLOCK DATA...
        out.write(VOXEL_BYTE);
        out.write(chunk.data.getPackedLight(x, y, z));

        short block = chunk.data.getBlock(x, y, z);
        writeShort(out, block);
        final BlockData blockData = chunk.data.getBlockData(x, y, z);
        if (block != BlockList.BLOCK_AIR.id && blockData != null) {
            writeAndVerifyBuffer(out, blockData.buff);
        }


        out.write(NEWLINE_BYTE);
    }

    private static void readVoxel(
            final byte[] bytes,
            final Chunk chunk, final int x, final int y,
            final int z, AtomicInteger start) {

        //Read light
        chunk.data.setPackedLight(x, y, z, bytes[start.get() + 1]);
        //Read block id
        final short blockID = (short) readShort(bytes[start.get() + 2], bytes[start.get() + 3]);
        chunk.data.setBlock(x, y, z, blockID);
        start.set(start.get() + 4);

        final ArrayList<Byte> blockDataBytes = new ArrayList<>();
        while (true) {
            final byte b3 = bytes[start.get()];
            if (b3 == NEWLINE_BYTE) {
                break;
            }
            blockDataBytes.add(b3);
            start.set(start.get() + 1);
        }
        if (!blockDataBytes.isEmpty()) {
            BlockData data = new BlockData(blockDataBytes);
            chunk.data.setBlockData(x, y, z, data);
        }
        start.set(start.get() + 1);
    }
}
