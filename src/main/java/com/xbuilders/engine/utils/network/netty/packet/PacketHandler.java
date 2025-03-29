package com.xbuilders.engine.utils.network.netty.packet;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        System.out.println("Recieved packet");
        //Get the message ID and decode it
        packet.handle(ctx, packet);
    }
}