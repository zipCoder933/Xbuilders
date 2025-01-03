/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.model.world.chunk.saving;

import com.xbuilders.engine.server.model.items.block.BlockRegistry;
import com.xbuilders.engine.server.model.items.entity.Entity;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.math.MathUtils;

import static com.xbuilders.engine.utils.ByteUtils.*;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.xbuilders.engine.server.model.world.chunk.BlockData;
import com.xbuilders.engine.server.model.world.chunk.Chunk;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;


public class ChunkSavingLoadingUtils {


    //DONT IMPORT ANY of these variables into the chunk file reading class. We want the previous chunk file versions to work no matter what.
    public static final byte START_READING_VOXELS = -128;
    public static final byte BYTE_SKIP_ALL_VOXELS = -125;

    public static final int METADATA_BYTES = 64;
    public static final int LATEST_FILE_VERSION = 2;
    public static final byte[] ENDING_OF_CHUNK_FILE = "END_OF_CHUNK_FILE".getBytes();


    public static final int ENTITY_DATA_MAX_BYTES = Integer.MAX_VALUE - 8; //this is the max size for a java array
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

        try {
            //Read the bytes
            byte[] data = new byte[length];
            System.arraycopy(bytes, start.get(), data, 0, length);
            start.set(start.get() + length);
            return new BlockData(data);
        } catch (IndexOutOfBoundsException e) {
            ErrorHandler.log(e);
            return null; //Catch the error just to be safe
        }
    }

    public static void writeEntityData(byte[] entityBytes, OutputStream out) throws IOException {
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

    public static byte[] readEntityData(byte[] bytes, AtomicInteger start) {
        int length = bytesToInt(bytes[start.get()], bytes[start.get() + 1], bytes[start.get() + 2], bytes[start.get() + 3]);
        start.set(start.get() + 4);

        byte[] data = new byte[length];
        System.arraycopy(bytes, start.get(), data, 0, length);
        start.set(start.get() + length);

        return data;
    }

    protected static String printSubList(byte[] bytes, int target, int radius) {
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
            if (bytes[i] == START_READING_VOXELS) str += "(-128\n)";
            else if (bytes[i] == BYTE_SKIP_ALL_VOXELS) str += "SKIP ";
            else str += bytes[i] + " ";
        }
        return str;
    }

    protected final static float maxMult16bits = (float) ((Math.pow(2, 10) / Chunk.WIDTH) - 1);

    private static void writeChunkVoxelCoords(OutputStream out, Vector3f vec) throws IOException {
//        System.out.println("Writing as " + vec.x + ", " + vec.y + ", " + vec.z);
        writeUnsignedShort(out, (int) (vec.x * maxMult16bits));
        writeUnsignedShort(out, (int) (vec.y * maxMult16bits));
        writeUnsignedShort(out, (int) (vec.z * maxMult16bits));
    }


    public static File backupFile(File f) {
        return new File(f.getParentFile(), "backups\\" + f.getName());
    }

    public static boolean writeChunkToFile(final Chunk chunk, final File f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            //Rename the existing file to a backup if it exists
            if (f.exists()) renameToBackup(chunk, f);

            try (FileOutputStream fos = new FileOutputStream(f); GZIPOutputStream out = new GZIPOutputStream(fos)) {
                out.write(LATEST_FILE_VERSION);//Write the version of the file
                writeMetadata(out, chunk);

                boolean iterateOverVoxels = true;

                //Write entities
                for (int i = 0; i < chunk.entities.list.size(); i++) {

                    Entity entity = chunk.entities.list.get(i);
                    writeShort(out, entity.id); //Write entity id
                    writeLong(out, entity.getUniqueIdentifier()); //Write entity identifier

                    entity.updatePosition();  //Write position
                    writeChunkVoxelCoords(out, entity.chunkPosition.chunkVoxel);

                    //Write entity data
                    byte[] entityBytes = entity.serializeDefinitionData();
                    writeEntityData(entityBytes, out);

                }

                out.write(START_READING_VOXELS);

                //Write voxels
                if (iterateOverVoxels) {
                    for (int y = chunk.data.size.y - 1; y >= 0; y--) {
                        for (int x = 0; x < chunk.data.size.x; ++x) {
                            for (int z = 0; z < chunk.data.size.z; ++z) {

                                short blockID = chunk.data.getBlock(x, y, z); //Write block id
                                writeShort(out, blockID);

                                out.write(chunk.data.getPackedLight(x, y, z)); //Write light as a single byte

                                if (blockID != BlockRegistry.BLOCK_AIR.id) { //We dont have to write block data if the block is air
                                    final BlockData blockData = chunk.data.getBlockData(x, y, z); //Write block data
                                    writeBlockData(blockData, out);
                                }

                            }
                        }
                    }
                } else {
                    out.write(BYTE_SKIP_ALL_VOXELS);
                }
                out.write(ENDING_OF_CHUNK_FILE);
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

    /**
     * In order to ensure safe saving, we need to:
     * 1. rename the old file first
     * 2. write the new file
     * <p>
     * Eventually we could delete the old file, but for now we will just rename it
     */
    private static boolean renameToBackup(Chunk chunk, File f) {
        File backupFile = backupFile(f);

        //Delete old backup if it exists
        if (backupFile.exists()) {
            try {
                Files.delete(backupFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Rename old file
        //https://stackoverflow.com/questions/13826045/file-renameto-fails
        try {
            //make sure the directory exists
            if (!backupFile.getParentFile().exists()) backupFile.getParentFile().mkdirs();
            //Files.move is a more reliable way to do this
            Files.move(f.toPath(), backupFile.toPath());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void writeMetadata(GZIPOutputStream out, Chunk chunk) throws IOException {
        //We only have METADATA_BYTES bytes of metadata to use
        int availableBytes = METADATA_BYTES;

        byte[] lastSaved = ByteUtils.longToByteArray(chunk.lastModifiedTime); //Last modified time
        availableBytes -= lastSaved.length;
        out.write(lastSaved);//8 bytes

        out.write(new byte[availableBytes]); //Write the remaining bytes
    }


    public static long getLastSaved(File f) {
        try (FileInputStream fis = new FileInputStream(f);
             GZIPInputStream input = new GZIPInputStream(fis)) {
            int fileVersion = input.read();
            return ByteUtils.bytesToLong(input.readNBytes(Long.BYTES));
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean fileIsComplete(File f) {
        try (FileInputStream fis = new FileInputStream(f);
             GZIPInputStream input = new GZIPInputStream(fis)) {
            int fileVersion = input.read();
            final byte[] bytes = input.readAllBytes();

            //Any version less than 2 doesnt have an ending
            return fileVersion < 2 || hasEnding(bytes);

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hasEnding(byte[] allRemainingBytse) {
        byte[] endingBytes = new byte[ENDING_OF_CHUNK_FILE.length];

        if(endingBytes.length > allRemainingBytse.length) return false;

        System.arraycopy(allRemainingBytse,
                allRemainingBytse.length - endingBytes.length,
                endingBytes,
                0, endingBytes.length);
        return Arrays.equals(endingBytes, ENDING_OF_CHUNK_FILE);
    }

    public static boolean readChunkFromFile(final Chunk chunk, final File f) {
        boolean fileReadCorrectly = false;
        boolean hasDetectedIfFileWasReadCorrectly = false;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            try (FileInputStream fis = new FileInputStream(f); GZIPInputStream input = new GZIPInputStream(fis)) {
                //Read the file version
                int fileVersion = input.read();
                //Read the last modified time
                chunk.lastModifiedTime = ByteUtils.bytesToLong(input.readNBytes(Long.BYTES));
                //Custom metadata for each version
                switch (fileVersion) {
                    case 0 -> input.readNBytes(ChunkFile_V0.METADATA_BYTES);
                    //Last version
                    default -> ChunkFile_V1.readMetadata(input.readNBytes(ChunkFile_V1.METADATA_BYTES));
                }

                //read all bytes
                AtomicInteger start = new AtomicInteger(0);
                final byte[] bytes = input.readAllBytes();

                //Read all ending bytes to see if the file was read correctly
                fileReadCorrectly = fileVersion < 2 || hasEnding(bytes);
                hasDetectedIfFileWasReadCorrectly = true;

                try {
                    switch (fileVersion) {
                        case 0 -> ChunkFile_V0.readChunk(chunk, start, bytes);
                        case 1 -> ChunkFile_V1.readChunk(chunk, start, bytes);
                        //Last version
                        default -> ChunkFile_V1.readChunk(chunk, start, bytes);
                    }
                } catch (Exception ex) {
                    File backupFile = backupFile(f);
                    ErrorHandler.report("Error reading chunk " + chunk
                                    + " \nFile Read Correctly: " + fileReadCorrectly
                                    + " \nBackup File Exists: " + backupFile.exists()
                            , ex);

                    if (!fileReadCorrectly) {
                        //Load from backup
                        if (backupFile.exists()) {
                            System.out.println("Loading " + chunk + " from backup");
                            chunk.data.reset();
                            return readChunkFromFile(chunk, backupFile);
                        }
                    }
                    return false;
                }
            } catch (FileNotFoundException ex) {
                ErrorHandler.report("No Chunk file found! " + chunk, ex);

                //Load from backup
                File backupFile = backupFile(f);
                if (backupFile.exists()) {
                    System.out.println("Loading " + chunk + " from backup");
                    chunk.data.reset();
                    return readChunkFromFile(chunk, backupFile);
                }

                return false;
            } catch (IOException ex) {
                File backupFile = backupFile(f);
                String errorMessage = "IO error occurred reading chunk: " + chunk +
                        " \nBackup File Exists: " + backupFile.exists();
                if (hasDetectedIfFileWasReadCorrectly) {
                    errorMessage += " \nFile Read Correctly: " + fileReadCorrectly;
                }
                ErrorHandler.report(errorMessage, ex);

                //Load from backup
                if (backupFile.exists()) {
                    System.out.println("Loading " + chunk + " from backup");
                    chunk.data.reset();
                    return readChunkFromFile(chunk, backupFile);
                }
                return false;
            }
        }
        return true;
    }
}
