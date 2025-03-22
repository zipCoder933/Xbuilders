package com.xbuilders.engine.utils.network.netty.packet.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageHandler extends SimpleChannelInboundHandler<MessagePacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessagePacket packet) {
        System.out.println("Messsage: " + packet.message);
    }
}
