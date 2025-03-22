package com.xbuilders.engine.utils.network.netty.packet.join;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class JoinHandler extends SimpleChannelInboundHandler<JoinPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, JoinPacket packet) {
        System.out.println("Recieved join packet from client with username: " + packet.username+" and client version: "+packet.clientVersion);
    }
}
