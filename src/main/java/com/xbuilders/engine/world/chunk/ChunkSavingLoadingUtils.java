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
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
class ChunkSavingLoadingUtils {

    public static final byte NEWLINE_BYTE = Byte.MIN_VALUE;
    protected static final byte PIPE_BYTE = -127;
    protected static final byte ENTITY_BYTE = -126;
    protected static final byte SKIP_BYTE = -125;
    protected static final int METADATA_BYTES = 10;


    private static void writeAndVerifyByteData(final OutputStream out, final ByteBuffer buffer) throws IOException {
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

    private static Vector3f readChunkVoxelCoords(IntBuffer start, byte[] bytes) {
        float x = readShort(bytes[start.get(0)], bytes[start.get(0) + 1]);
        float y = readShort(bytes[start.get(0) + 2], bytes[start.get(0) + 3]);
        float z = readShort(bytes[start.get(0) + 4], bytes[start.get(0) + 5]);
        x = x / maxMult16bits;
        y = y / maxMult16bits;
        z = z / maxMult16bits;
//        System.out.println("Reading as " + x + ", " + y + ", " + z);
        start.put(0, start.get(0) + 6);
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

    private static Entity readEntity(Chunk chunk, final byte[] bytes, IntBuffer start) {
        final short entityID = (short) readShort(bytes[start.get(0) + 1], bytes[start.get(0) + 2]);
        EntityLink link = ItemList.getEntity(entityID);
        start.put(0, start.get(0) + 3);

        //Read position
        Vector3f chunkVox = readChunkVoxelCoords(start, bytes);

        //Read entity data
        ArrayList<Byte> entityBytes = new ArrayList<>();
        while (true) {
            final byte b = bytes[start.get(0)];
            start.put(0, start.get(0) + 1);
            if (b == NEWLINE_BYTE) {
                break;
            } else {
                entityBytes.add(b);
            }
        }

        if (bytes[start.get(0)] != ENTITY_BYTE) {
            start.put(0, start.get(0) - 1);
        }

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
                                short block = chunk.data.getBlock(x, y, z);
                                if (block != BlockList.BLOCK_AIR.id) {
                                    out.write(PIPE_BYTE);
                                    writeShort(out, block);
                                    final BlockData blockData = chunk.data.getBlockData(x, y, z);
                                    if (blockData != null) {
                                        writeAndVerifyByteData(out, blockData.buff);
                                    }
                                }
                                out.write(NEWLINE_BYTE);

                            }
                        }
                    }
                } else {
                    out.write(SKIP_BYTE);
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

    private static void printSubList(byte[] bytes, int target, int radius) {
        int start = MathUtils.clamp(target - radius, 0, bytes.length - 1);
        int end = MathUtils.clamp(target + radius, 0, bytes.length - 1);

        for (int i = start; i < end; i++) {
            if (i == target) {
                System.out.print("<" + bytes[i] + "> ");
            } else {
                System.out.print(bytes[i] + " ");
            }
        }
        System.out.println();
    }

    protected static boolean readChunkFromFile(final Chunk chunk, final File f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer start = stack.mallocInt(1);
            start.put(0, 0);

            try (FileInputStream fis = new FileInputStream(f); GZIPInputStream input = new GZIPInputStream(fis)) {
                byte[] metadata = input.readNBytes(METADATA_BYTES);
                final byte[] bytes = input.readAllBytes();

                //Read entities
                while (bytes[start.get(0)] == ENTITY_BYTE) {
//                    System.out.print("\nStarting to read entity: ");
//                    printSubList(bytes, start.get(0), 5);

                    Entity entity = readEntity(chunk, bytes, start);
                    chunk.entities.list.add(entity);

//                    System.out.print("Ending value: ");
//                    printSubList(bytes, start.get(0), 5);
                }
                //Read voxels
                Label_0186:
                for (int y = chunk.data.size.y - 1; y >= 0; y--) {
                    for (int x = 0; x < chunk.data.size.x; ++x) {
                        for (int z = 0; z < chunk.data.size.z; ++z) {
                            final byte startByte = bytes[start.get(0)];
                            if (startByte == SKIP_BYTE) {
                                start.put(0, start.get(0) + 1);
                                continue Label_0186;
                            }
                            if (startByte == NEWLINE_BYTE) {
                                start.put(0, start.get(0) + 1);
                            } else {
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

    private static void readVoxel(
            final byte[] bytes,
            final Chunk chunk, final int x, final int y,
            final int z, IntBuffer start) {

        final short blockID = (short) readShort(bytes[start.get(0) + 1], bytes[start.get(0) + 2]);
        chunk.data.setBlock(x, y, z, blockID);

        start.put(0, start.get(0) + 3);
        final ArrayList<Byte> blockDataBytes = new ArrayList<>();
        while (true) {
            final byte b3 = bytes[start.get(0)];
            if (b3 == NEWLINE_BYTE) {
                break;
            }
            blockDataBytes.add(b3);
            start.put(0, start.get(0) + 1);
        }
        if (!blockDataBytes.isEmpty()) {
            BlockData data = new BlockData(blockDataBytes);
            chunk.data.setBlockData(x, y, z, data);
        }
        start.put(0, start.get(0) + 1);
    }
}
