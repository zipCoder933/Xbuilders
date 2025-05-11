package com.xbuilders.engine.common.packets;

import com.xbuilders.Main;
import com.xbuilders.engine.common.network.ChannelBase;
import com.xbuilders.engine.common.network.packet.Packet;
import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.server.Server;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.joml.Vector3i;

import java.util.List;

public class ChunkRequestPacket extends Packet {

    Vector3i requestedCoordinates;

    public ChunkRequestPacket() {
        super(AllPackets.CHUNK_REQUEST);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        ChunkRequestPacket packetInstance = (ChunkRequestPacket) packet;
        //Write the chunk request
        out.writeInt(packetInstance.requestedCoordinates.x);
        out.writeInt(packetInstance.requestedCoordinates.y);
        out.writeInt(packetInstance.requestedCoordinates.z);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {

    }

    @Override
    public void handleClientSide(ChannelBase ctx, Packet packet) {

    }

    @Override
    public void handleServerSide(ChannelBase ctx, Packet packet) {
        ChunkRequestPacket packetInstance = (ChunkRequestPacket) packet;
        Server server = Main.getServer();
        Chunk chunk = server.world.makeOrGetChunk(packetInstance.requestedCoordinates);
        ctx.writeAndFlush(new ChunkDataPacket(chunk));
    }
}
