package com.xbuilders.engine.world.chunk.saving;

import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.utils.ByteUtils.bytesToShort;

public class ChunkFile_V1 {

    public static final byte START_READING_VOXELS = -128;
    public static final byte BYTE_SKIP_ALL_VOXELS = -125;
    protected final static float maxMult16bits = (float) ((Math.pow(2, 10) / Chunk.WIDTH) - 1);
    public static final int METADATA_BYTES = 64;

    public static long readMetadata(InputStream input) throws IOException {
        //We only have METADATA_BYTES bytes of metadata
        int remaining = METADATA_BYTES;

        long lastModifiedTime = ByteUtils.bytesToLong(input.readNBytes(Long.BYTES));
        remaining -= Long.BYTES;

        byte[] metadata = input.readNBytes(remaining);//Read the remaining bytes
        return lastModifiedTime;
    }

    static void readChunk(final Chunk chunk, InputStream input) throws IOException {
        AtomicInteger start = new AtomicInteger(0);
        start.set(0);

        chunk.lastModifiedTime = readMetadata(input);

        final byte[] bytes = input.readAllBytes();

        //Load the entities
        while (true) {
            if (bytes[start.get()] == START_READING_VOXELS) { //This flags the end of the entities
                start.set(start.get() + 1);
                break;
            }
            makeEntity(chunk, bytes, start);
        }


        //Load the voxels
        chunkVoxels:
        for (int y = chunk.data.size.y - 1; y >= 0; y--) {
            for (int x = 0; x < chunk.data.size.x; ++x) {
                for (int z = 0; z < chunk.data.size.z; ++z) {
                    final byte startByte = bytes[start.get()];
                    if (startByte == BYTE_SKIP_ALL_VOXELS) {
                        start.set(start.get() + 1);
                        continue chunkVoxels;
                    } else { //Every non air voxel starts with pipe byte
                        readVoxel(bytes, chunk, x, y, z, start);
                    }
                }
            }
        }
    }


    private static Vector3f readChunkVoxelCoords(AtomicInteger start, byte[] bytes) {
        float x = bytesToShort(bytes[start.get()], bytes[start.get() + 1]);
        float y = bytesToShort(bytes[start.get() + 2], bytes[start.get() + 3]);
        float z = bytesToShort(bytes[start.get() + 4], bytes[start.get() + 5]);
        x = x / maxMult16bits;
        y = y / maxMult16bits;
        z = z / maxMult16bits;
        start.set(start.get() + 6);
        return new Vector3f(x, y, z);
    }

    protected static Entity makeEntity(Chunk chunk, final byte[] bytes, AtomicInteger start) {
        final short entityID = (short) bytesToShort(bytes[start.get()], bytes[start.get() + 1]); //read entity id
        EntityLink link = Registrys.getEntity(entityID);
        start.set(start.get() + 2);

        long identifier = ByteUtils.bytesToLong(bytes, start); //read entity identifier

        Vector3f chunkVox = readChunkVoxelCoords(start, bytes);  //Read position
        byte[] entityData = ChunkSavingLoadingUtils.readEntityData(bytes, start);//Read entity data

        return chunk.entities.placeNew(link, identifier,
                chunkVox.x + chunk.position.x * Chunk.WIDTH,
                chunkVox.y + chunk.position.y * Chunk.WIDTH,
                chunkVox.z + chunk.position.z * Chunk.WIDTH,
                entityData);
    }

    protected static void readVoxel(
            final byte[] bytes,
            final Chunk chunk, final int x, final int y, final int z,
            AtomicInteger start) {

        final short blockID = (short) bytesToShort(bytes[start.get()], bytes[start.get() + 1]);     //Read block id
        chunk.data.setBlock(x, y, z, blockID);

        chunk.data.setPackedLight(x, y, z, bytes[start.get() + 2]);   //Read light
        start.set(start.get() + 3);

        if (blockID != BlockRegistry.BLOCK_AIR.id) { //We dont read block data if the block is air
            BlockData blockData = ChunkSavingLoadingUtils.readBlockData(bytes, start); //Read block data
            if (blockData != null && blockData.size() > 0) {
                chunk.data.setBlockData(x, y, z, blockData);
            }
        }


    }


}
