package com.xbuilders.engine.common.network.packet;

import com.xbuilders.engine.common.network.netty.NettyChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PacketHandler extends SimpleChannelInboundHandler<Packet> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet packet) {
        packet.handle(new NettyChannel(ctx.channel()), packet);
    }
}