package com.xbuilders.engine.utils.network.packet;

import com.xbuilders.engine.utils.network.ChannelBase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

import java.util.List;

public abstract class Packet {

    public final byte id;

    public Packet(int id) {
        this.id = (byte) id;
    }


    public abstract void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out);

    public abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out);

    public abstract void handle(ChannelBase ctx, Packet packet);

    public static void register(SocketChannel ch, Packet p) {
        PacketDecoder.PACKET_REGISTRY.put(p.id, p);
    }

    public static void register(Packet p) {
        PacketDecoder.PACKET_REGISTRY.put(p.id, p);
    }
}
