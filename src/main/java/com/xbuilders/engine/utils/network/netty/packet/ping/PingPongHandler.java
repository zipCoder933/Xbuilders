package com.xbuilders.engine.utils.network.netty.packet.ping;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class PingPongHandler extends SimpleChannelInboundHandler<PingPongPacket> {

    public static final byte pingPacket = 0;
    public static final byte pongPacket = 1;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PingPongPacket packet) {
        System.out.println("Received " + (packet.ping ? "PING" : "PONG"));

        if (packet.ping) { //If we received a ping, respond with a pong
            ctx.writeAndFlush(new PingPongPacket(false));
        }
    }
}
