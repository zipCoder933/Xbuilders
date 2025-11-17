package com.tessera.engine.utils.network.packet.ping;

import com.tessera.engine.utils.network.ChannelBase;
import com.tessera.engine.utils.network.packet.Packet;
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
    public void handle(ChannelBase ctx, Packet packet) {
        System.out.println("Received ping, Sending pong...");
        ctx.writeAndFlush(new PongPacket());
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(new PingPacket());
    }
}
