package com.xbuilders.engine.server.world.chunk.saving;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.xbuilders.engine.server.items.Registrys;
import com.xbuilders.engine.server.items.block.BlockRegistry;
import com.xbuilders.engine.server.items.entity.Entity;
import com.xbuilders.engine.server.items.entity.EntitySupplier;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.utils.ByteUtils;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.utils.ByteUtils.bytesToShort;

public class ChunkFile_V2 {
    public static final byte START_READING_VOXELS = -128;
    protected final static float maxMult16bits = (float) ((Math.pow(2, 10) / Chunk.WIDTH) - 1);
    public static final int METADATA_BYTES = 56;


    public static void readMetadata(byte[] input) {
    }

    static void readChunk(final Chunk chunk, AtomicInteger start, byte[] bytes) throws IOException {
        Kryo kryo = ChunkSavingLoadingUtils.kryo;
        Input input = new Input(bytes,
                start.get(), //start
                bytes.length - start.get() //length
        );
        System.out.println("bytes length: " + bytes.length + " start: " + start.get());
        System.out.println("Input position: " + input.position() + " Input limit: " + input.limit() + " input available: " + input.available());

        try {

//            while (true) {  //Load the entities
//                int currentPosition = input.position(); // Save the current position
//                if (input.available() > 0 && input.readByte() == START_READING_VOXELS) { //This flags the end of the entities
//                    System.out.println("End of entities");
//                    break;
//                } else input.setPosition(currentPosition); // Restore the position
//
//                System.out.println("Reading entity");
//                makeEntity(chunk, input, kryo);
//            }


            for (int y = chunk.data.size.y - 1; y >= 0; y--) {   //Load the voxels
                for (int x = 0; x < chunk.data.size.x; ++x) {
                    for (int z = 0; z < chunk.data.size.z; ++z) {
                        readVoxel(input, kryo, chunk, x, y, z);
                    }
                }
            }
        } catch (Exception e) {
            synchronized (kryo) {
                System.out.println("\n\n\nCHUNK V2 EXCEPTION:");
                ChunkSavingLoadingUtils.printSubList(input.getBuffer(), input.position(), 10);
                StackTraceElement[] stackTrace = e.getStackTrace();
                System.out.println("Exception: " + e + ", stack trace length: " + stackTrace.length);
                for (StackTraceElement stackTraceElement : stackTrace) {
                    System.out.println(stackTraceElement);
                }
            }
        }
    }


    private static Vector3f readChunkVoxelCoords(Input input, Kryo kryo) {
        float x = kryo.readObject(input, Float.class);
        float y = kryo.readObject(input, Float.class);
        float z = kryo.readObject(input, Float.class);
        x = x / maxMult16bits;
        y = y / maxMult16bits;
        z = z / maxMult16bits;
        return new Vector3f(x, y, z);
    }

    protected static Entity makeEntity(Chunk chunk, Input input, Kryo kryo) {
        String id = kryo.readObject(input, String.class);
        EntitySupplier link = Registrys.getEntity(id);

        long identifier = kryo.readObject(input, Long.class); //read entity identifier

        Vector3f chunkVox = readChunkVoxelCoords(input, kryo);  //Read position
        byte[] entityData = kryo.readObject(input, byte[].class);//Read entity data

        return chunk.entities.placeNew(link, identifier,
                chunkVox.x + chunk.position.x * Chunk.WIDTH,
                chunkVox.y + chunk.position.y * Chunk.WIDTH,
                chunkVox.z + chunk.position.z * Chunk.WIDTH,
                entityData);
    }

    protected static void readVoxel(
            Input input, Kryo kryo,
            final Chunk chunk,
            final int x, final int y, final int z) {

        short id = kryo.readObject(input, short.class); //read id
        chunk.data.setBlock(x, y, z, id);

        byte light = kryo.readObject(input, byte.class); //read light
        chunk.data.setPackedLight(x, y, z, light);

        byte[] bytes = kryo.readObject(input, byte[].class); //Read block data
        if (bytes.length > 0) {
            BlockData blockData = new BlockData(bytes);
            chunk.data.setBlockData(x, y, z, blockData);
        }

    }

}
