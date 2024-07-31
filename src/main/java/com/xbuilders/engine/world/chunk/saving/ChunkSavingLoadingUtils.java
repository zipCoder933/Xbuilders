/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.world.chunk.saving;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.math.MathUtils;

import static com.xbuilders.engine.utils.ByteUtils.*;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.XBFilterOutputStream;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 * <br>
 * <h2>all Information about the format of the chunk files are saved in the "Chunk saving and loading.md" file</h2>
 */
public class ChunkSavingLoadingUtils {


    //DONT IMPORT ANY of these variables into the chunk file reading class. We want the previous chunk file versions to work no matter what.
    public static final byte NEWLINE_BYTE = Byte.MIN_VALUE;
    public static final byte VOXEL_BYTE = -127;
    public static final byte ENTITY_BYTE = -126;
    public static final byte BYTE_SKIP_ALL_VOXELS = -125;

    public static final int METADATA_BYTES = 9;
    public static final int FILE_VERSION = 0;


    public static final int ENTITY_MAX_BYTES = Integer.MAX_VALUE - 8; //this is the max size for a java array
    public static int BLOCK_DATA_MAX_BYTES = (int) (Math.pow(2, 16) - 1); //Unsigned short


    public static void writeBlockData(BlockData data, OutputStream out) throws IOException {
        if (data == null) {
            out.write(new byte[]{0, 0});//Just write 0 for the length
            return;
        }

        if (data.size() > BLOCK_DATA_MAX_BYTES) {
            ErrorHandler.report(new Throwable("Block data too large: " + data.size()));
            out.write(new byte[]{0, 0});//Just write 0 for the length
            return;
        }
        //First write the length of the block data as an unsigned short
        out.write(shortToBytes(data.size() & 0xffff));

        //Then write the bytes
        byte[] bytes = data.toByteArray();
        out.write(bytes);
    }

    public static BlockData readBlockData(byte[] bytes, AtomicInteger start) {
        //Get the length from unsigned short to int
        int length = bytesToShort(bytes[start.get()], bytes[start.get() + 1]) & 0xffff;
        start.set(start.get() + 2);

        //Read the bytes
        byte[] data = new byte[length];
        System.arraycopy(bytes, start.get(), data, 0, length);
        start.set(start.get() + length);

        return new BlockData(data);
    }

    public static void writeEntity(byte[] entityBytes, OutputStream out) throws IOException {
        //We dont have to check if the entity is out of bounds because we would be going over the max size anyway
        if (entityBytes == null) {
            out.write(new byte[]{0, 0, 0, 0});//Just write 0 for the length
            return;
        }
        //First write the length
        out.write(intToBytes(entityBytes.length));
        //Then write the bytes
        out.write(entityBytes);
    }

    public static byte[] readEntity(byte[] bytes, AtomicInteger start) {
        int length = bytesToInt(bytes[start.get()], bytes[start.get() + 1], bytes[start.get() + 2], bytes[start.get() + 3]);
        start.set(start.get() + 4);

        byte[] data = new byte[length];
        System.arraycopy(bytes, start.get(), data, 0, length);
        start.set(start.get() + length);

        return data;
    }

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

//    private static void writeAndVerifyBuffer(final OutputStream out, final ByteBuffer buffer) throws IOException {
//        for (int i = 0; i < buffer.capacity(); i++) {
//            byte b = buffer.get(i);
//            if (b == NEWLINE_BYTE) {
//                throw new IllegalArgumentException("The byte [" + NEWLINE_BYTE + "] is forbidden for use.");
//            }
//            out.write(b);
//        }
//    }

    protected final static float maxMult16bits = (float) ((Math.pow(2, 10) / Chunk.WIDTH) - 1);

    private static void writeChunkVoxelCoords(OutputStream out, Vector3f vec) throws IOException {
//        System.out.println("Writing as " + vec.x + ", " + vec.y + ", " + vec.z);
        writeUnsignedShort(out, (int) (vec.x * maxMult16bits));
        writeUnsignedShort(out, (int) (vec.y * maxMult16bits));
        writeUnsignedShort(out, (int) (vec.z * maxMult16bits));
    }


    private static void writeEntity(final XBFilterOutputStream filteredOut, final OutputStream out2, Entity entity) throws IOException {
        out2.write(ENTITY_BYTE);
        writeShort(out2, entity.link.id);

        //Write position
        entity.updatePosition();
        writeChunkVoxelCoords(out2, entity.chunkPosition.chunkVoxel);

        filteredOut.write(entity.toBytes()); //TODO: Change block data and entity data to write without the filteredoutputstream
        out2.write(NEWLINE_BYTE);
    }

    public static long getLastSaved(File f) {
        try (FileInputStream fis = new FileInputStream(f);
             GZIPInputStream input = new GZIPInputStream(fis)) {
            int fileVersion = input.read();
            return ChunkFile_V0.readMetadata(fis);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean writeChunkToFile(final Chunk chunk, final File f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            try (FileOutputStream fos = new FileOutputStream(f); GZIPOutputStream out = new GZIPOutputStream(fos)) {
                out.write(FILE_VERSION);//Write the version of the file
                writeMetadata(out, chunk);

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
                ErrorHandler.report(ex);
                return false;
            } catch (IOException ex) {
                ErrorHandler.report(ex);
                return false;
            } catch (Exception ex) {
                ErrorHandler.report(ex);
                return false;
            }
        }
        return true;
    }

    private static void writeMetadata(GZIPOutputStream out, Chunk chunk) throws IOException {
        //We only have METADATA_BYTES bytes of metadata to use
        int availableBytes = METADATA_BYTES;

        byte[] lastSaved = ByteUtils.longToBytes(chunk.lastModifiedTime); //Last modified time
        availableBytes -= lastSaved.length;
        out.write(lastSaved);//8 bytes

        out.write(new byte[availableBytes]); //Write the remaining bytes
    }


    public static boolean readChunkFromFile(final Chunk chunk, final File f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            try (FileInputStream fis = new FileInputStream(f); GZIPInputStream input = new GZIPInputStream(fis)) {

                int fileVersion = input.read();
                ChunkFile_V0.readChunk(chunk, input);

            } catch (FileNotFoundException ex) {
                ErrorHandler.report("An error occurred reading chunk " + chunk, "", ex);
                return false;
            } catch (IOException ex) {
                ErrorHandler.report("An error occurred reading chunk " + chunk, "", ex);
                return false;
            } catch (Exception ex) {
                ErrorHandler.report("An error occurred reading chunk " + chunk, "", ex);
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
            blockData.write(fout);
        }
        out.write(NEWLINE_BYTE);
    }
}
