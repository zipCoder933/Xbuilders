package com.xbuilders.engine.common.network.packet;

import com.xbuilders.engine.common.network.ChannelBase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

public abstract class Packet {

    private final static int MIN_PACKET_ID = 2;
    public final byte id;

    public Packet(int id) {
        if (id < MIN_PACKET_ID) throw new IllegalArgumentException("Packet id must be greater than " + MIN_PACKET_ID);
        this.id = (byte) id;
    }


    public abstract void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out);

    public abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out);

    public abstract void handleClientSide(ChannelBase ctx, Packet packet);
    public abstract void handleServerSide(ChannelBase ctx, Packet packet);


    public static void register(SocketChannel ch, Packet p) {
        PacketDecoder.PACKET_REGISTRY.put(p.id, p);
    }

    public static void register(Packet p) {
        PacketDecoder.PACKET_REGISTRY.put(p.id, p);
    }
}
