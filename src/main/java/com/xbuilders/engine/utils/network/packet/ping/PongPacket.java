package com.xbuilders.engine.utils.network.packet.ping;

import com.xbuilders.engine.utils.network.ChannelBase;
import com.xbuilders.engine.utils.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class PongPacket extends Packet {

    public PongPacket() {
        super(1);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
    }

    @Override
    public void handle(ChannelBase ctx, Packet packet) {
        System.out.println("Received pong");
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        out.add(new PongPacket());
    }
}
