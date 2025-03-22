package com.xbuilders.engine.utils.network.netty.packet.ping;

import com.xbuilders.engine.utils.network.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class PingPacket extends Packet {

    public PingPacket() {
        super(0);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
    }

    @Override
    public void handle(ChannelHandlerContext ctx, Packet packet) {
        System.out.println("Received ping, Sending pong...");
        ctx.writeAndFlush(new PongPacket());
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(new PingPacket());
    }
}
