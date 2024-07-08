package com.xbuilders.engine.world.chunk.saving;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.utils.ByteUtils.*;
import static com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils.*;

public class ChunkFile_V0 {

    private static void readMetadata(Chunk chunk, InputStream input) throws IOException {
        //We only have METADATA_BYTES bytes of metadata
        int remaining = METADATA_BYTES;

        chunk.lastModifiedTime = ByteUtils.bytesToLong(input.readNBytes(Long.BYTES));
        remaining -= Long.BYTES;

        byte[] metadata = input.readNBytes(remaining);//Read the remaining bytes
    }

    static void readChunk(final Chunk chunk, InputStream input) throws IOException {
        AtomicInteger start = new AtomicInteger(0);
        start.set(0);

        readMetadata(chunk, input);

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

    protected static Entity readEntity(Chunk chunk, final byte[] bytes, AtomicInteger start) {
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

    protected static void readVoxel(
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
