package com.xbuilders.engine.utils.network.netty.packet.ping;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PingPongEncoder extends MessageToByteEncoder<PingPongPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, PingPongPacket packet, ByteBuf out) {
        out.writeByte(packet.ping ? PingPongHandler.pingPacket : PingPongHandler.pongPacket);
    }
}
