/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.world.chunk.saving;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xbuilders.engine.server.items.block.BlockRegistry;
import com.xbuilders.engine.server.items.entity.Entity;
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

import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;


public class ChunkSavingLoadingUtils {
    //DONT IMPORT ANY of these variables into the chunk file reading class. We want the previous chunk file versions to work no matter what.
    public static final byte START_READING_VOXELS = -128;
    public static final byte BYTE_SKIP_ALL_VOXELS = -125;

    public static final int METADATA_BYTES = 64;
    public static final int LATEST_FILE_VERSION = 3;
    public static final byte[] ENDING_OF_CHUNK_FILE = "END_OF_CHUNK_FILE".getBytes();

    public static final int ENTITY_DATA_MAX_BYTES = Integer.MAX_VALUE - 8; //this is the max size for a java array
    public static int BLOCK_DATA_MAX_BYTES = (int) (Math.pow(2, 16) - 1); //Unsigned short

    final static Kryo kryo = new Kryo();

    static {
        kryo.register(byte[].class);
    }




    protected static void printSubList(byte[] bytes, int target, int radius) {
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
        System.out.println(str + ")");
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


    public static File backupFile(File f) {
        return new File(f.getParentFile(), "backups\\" + f.getName());
    }

    public static boolean writeChunkToFile(final Chunk chunk, final File f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            //Rename the existing file to a backup if it exists
            if (f.exists()) renameToBackup(chunk, f);
            try (FileOutputStream fos = new FileOutputStream(f); GZIPOutputStream outStream = new GZIPOutputStream(fos)) {
                outStream.write(LATEST_FILE_VERSION);//Write the version of the file
                writeMetadata(outStream, chunk);

                //Start writing using kryo
                Output out = new Output(outStream);

                //Write entities
//                for (int i = 0; i < chunk.entities.list.size(); i++) {
//
//                    Entity entity = chunk.entities.list.get(i);
//                    kryo.writeObject(out, entity.getId()); //write entity id
//                    kryo.writeObject(out, entity.getUniqueIdentifier()); //Write entity uuid
//
//                    entity.updatePosition();  //Write position (unsigned short)
//                    kryo.writeObject(out, (short) (entity.chunkPosition.chunkVoxel.x * maxMult16bits) & 0xFFFF);
//                    kryo.writeObject(out, (short) (entity.chunkPosition.chunkVoxel.y * maxMult16bits) & 0xFFFF);
//                    kryo.writeObject(out, (short) (entity.chunkPosition.chunkVoxel.z * maxMult16bits) & 0xFFFF);
//
//                    //Write entity data
//                    byte[] entityBytes = entity.serializeDefinitionData();
//                    if (entityBytes == null) entityBytes = new byte[0];
//                    kryo.writeObject(out, entityBytes);
//                }
//
//                kryo.writeObject(out, START_READING_VOXELS);

                //Write voxels
                for (int y = chunk.data.size.y - 1; y >= 0; y--) {
                    for (int x = 0; x < chunk.data.size.x; ++x) {
                        for (int z = 0; z < chunk.data.size.z; ++z) {

                            short blockID = chunk.data.getBlock(x, y, z); //Write block id
                            kryo.writeObject(out, (short) blockID);

                            byte light = chunk.data.getPackedLight(x, y, z); //Write light
                            kryo.writeObject(out, (byte) light);

                            final BlockData blockData = chunk.data.getBlockData(x, y, z); //Write block data
                            if (blockData == null) kryo.writeObject(out, new byte[0]);
                            else kryo.writeObject(out, blockData.toByteArray());
                        }
                    }
                }

                kryo.writeObject(out, ENDING_OF_CHUNK_FILE);
                out.close();

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

        if (endingBytes.length > allRemainingBytse.length) return false;

        System.arraycopy(allRemainingBytse,
                allRemainingBytse.length - endingBytes.length,
                endingBytes,
                0, endingBytes.length);
        return Arrays.equals(endingBytes, ENDING_OF_CHUNK_FILE);
    }

    public static int getFileVersion(File f) throws IOException {
        try (FileInputStream fis = new FileInputStream(f); GZIPInputStream input = new GZIPInputStream(fis)) {
            //Read the file version
            return input.read();
        }
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
                    case 1 -> ChunkFile_V1.readMetadata(input.readNBytes(ChunkFile_V1.METADATA_BYTES));
                    case 2 -> ChunkFile_V1.readMetadata(input.readNBytes(ChunkFile_V1.METADATA_BYTES));
                    default -> ChunkFile_V2.readMetadata(input.readNBytes(ChunkFile_V2.METADATA_BYTES));
                }

                //read all bytes
                AtomicInteger start = new AtomicInteger(0);
                final byte[] bytes = input.readAllBytes();

                //Read all ending bytes to see if the file was read correctly
                fileReadCorrectly = fileVersion < 2 || hasEnding(bytes);
                hasDetectedIfFileWasReadCorrectly = true;

                try {
                    if (bytes.length > 0) {
                        switch (fileVersion) {
                            case 0 -> ChunkFile_V0.readChunk(chunk, start, bytes);
                            case 1 -> ChunkFile_V1.readChunk(chunk, start, bytes);
                            case 2 -> ChunkFile_V1.readChunk(chunk, start, bytes);
                            default -> ChunkFile_V2.readChunk(chunk, start, bytes);
                        }
                    } else throw new IllegalStateException("File is empty past metadata!");
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
