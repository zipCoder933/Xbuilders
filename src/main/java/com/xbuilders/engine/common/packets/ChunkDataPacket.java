package com.xbuilders.engine.common.packets;

import com.xbuilders.Main;
import com.xbuilders.engine.common.network.ChannelBase;
import com.xbuilders.engine.common.network.packet.Packet;
import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.common.world.chunk.saving.ChunkReadingException;
import com.xbuilders.engine.common.world.chunk.saving.ChunkSavingLoadingUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.joml.Vector3i;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

/**
 * This packet is all about sending chunks to the client
 * The server injects the data into a chunk on the client
 */
public class ChunkDataPacket extends Packet {

    public Vector3i chunkPosition;
    public byte[] chunkData;
    public Chunk singleplayerChunkData;

    public ChunkDataPacket() {
        super(AllPackets.CHUNK_DATA);
    }

    /**
     * Load the chunk into bytes
     *
     * @param chunk                    the chunk to have binary data uploaded
     * @param writeAsSingleplayerChunk if set to true, the chunk will be written as a singleplayer chunk
     */
    public ChunkDataPacket(Chunk chunk, boolean writeAsSingleplayerChunk) {
        super(AllPackets.CHUNK_DATA);
        if (writeAsSingleplayerChunk) {
            this.singleplayerChunkData = chunk;
            this.chunkPosition = chunk.position;
        } else {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChunkSavingLoadingUtils.writeChunk(chunk, out);
            this.chunkData = out.toByteArray();
            this.chunkPosition = chunk.position;
        }
    }

    public ChunkDataPacket(Vector3i chunkPosition, byte[] chunkBytes) {
        super(AllPackets.CHUNK_DATA);
        this.chunkPosition = chunkPosition;
        this.chunkData = chunkBytes;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        ChunkDataPacket packetInstance = (ChunkDataPacket) packet;

        //Write the chunk position
        out.writeInt(packetInstance.chunkPosition.x);
        out.writeInt(packetInstance.chunkPosition.y);
        out.writeInt(packetInstance.chunkPosition.z);

        //Write the chunk data
        out.writeInt(packetInstance.chunkData.length);
        out.writeBytes(packetInstance.chunkData);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        //Read the chunk position
        int x = in.readInt();
        int y = in.readInt();
        int z = in.readInt();
        Vector3i chunkPosition = new Vector3i(x, y, z);

        //Read the chunk data
        int length = in.readInt();
        byte[] chunkBytes = new byte[length];
        in.readBytes(chunkBytes);

        //Add the packet
        out.add(new ChunkDataPacket(chunkPosition, chunkBytes));
    }

    @Override
    public void handleClientSide(ChannelBase ctx, Packet packet) {
        ChunkDataPacket packetInstance = (ChunkDataPacket) packet;
        System.out.println("Reading chunk " + packetInstance.chunkPosition);
        //Create or get the chunk first
        Chunk chunk = Main.getClient().world.addChunk(packetInstance.chunkPosition);


        if (packetInstance.singleplayerChunkData != null) { //Send the bytes directly to the client
            chunk.data = packetInstance.singleplayerChunkData.data; //Add the blocks directly
            chunk.entities.list.addAll(packetInstance.singleplayerChunkData.entities.list); //copy the entities
            //chunk.load();
        } else {//Load the data into the chunks on the client
            AtomicBoolean fileReadCorrectly = new AtomicBoolean(false);
            AtomicBoolean hasDetectedIfFileWasReadCorrectly = new AtomicBoolean(false);
            try {
                ChunkSavingLoadingUtils.readChunk(chunk, new ByteArrayInputStream(chunkData),
                        fileReadCorrectly,
                        hasDetectedIfFileWasReadCorrectly);
            } catch (IOException e) {
                Main.LOGGER.log(Level.WARNING, "Failed to read chunk data", e);
            } catch (ChunkReadingException e) {
                Main.LOGGER.log(Level.WARNING, "Failed to read chunk data", e);
            }
        }
    }

    @Override
    public void handleServerSide(ChannelBase ctx, Packet packet) {

    }
}
