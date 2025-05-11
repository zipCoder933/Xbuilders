package com.xbuilders.engine.common.packets;

import com.xbuilders.engine.common.network.ChannelBase;
import com.xbuilders.engine.common.network.packet.Packet;
import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.common.world.chunk.saving.ChunkSavingLoadingUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChunkDataPacket extends Packet {

    Chunk chunk;

    public ChunkDataPacket() {
        super(AllPackets.CHUNK_DATA);
    }

    public ChunkDataPacket(Chunk chunk) {
        super(AllPackets.CHUNK_DATA);
        this.chunk = chunk;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        ChunkDataPacket packetInstance = (ChunkDataPacket) packet;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChunkSavingLoadingUtils.writeChunk(packetInstance.chunk, baos);
        //Write the chunk data
        out.writeInt(baos.size());
        out.writeBytes(baos.toByteArray());
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
//        int length = in.readInt();
//        byte[] chunkBytes = new byte[length];
//        in.readBytes(chunkBytes);
//
//        AtomicBoolean fileReadCorrectly = new AtomicBoolean(false);
//        AtomicBoolean hasDetectedIfFileWasReadCorrectly = new AtomicBoolean(false);
//Chunk chunk = new Chunk();
//
//        Chunk chunk = ChunkSavingLoadingUtils.readChunk(chunk, new ByteArrayInputStream(chunkBytes),
//                fileReadCorrectly,
//                hasDetectedIfFileWasReadCorrectly);
//
//        out.add(new ChunkDataPacket(chunk));
    }

    @Override
    public void handleClientSide(ChannelBase ctx, Packet packet) {

    }

    @Override
    public void handleServerSide(ChannelBase ctx, Packet packet) {

    }
}
