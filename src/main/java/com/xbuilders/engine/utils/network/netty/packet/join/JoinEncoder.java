package com.xbuilders.engine.utils.network.netty.packet.join;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JoinEncoder extends MessageToByteEncoder<JoinPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, JoinPacket packet, ByteBuf out) {
        out.writeByte(packet.getId());
//        out.writeBytes(packet.username.getBytes());
        out.writeInt(packet.clientVersion);
    }
}
