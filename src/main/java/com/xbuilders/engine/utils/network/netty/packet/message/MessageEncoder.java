package com.xbuilders.engine.utils.network.netty.packet.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<MessagePacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePacket packet, ByteBuf out) {
//        out.writeInt(0); // Placeholder for length, updated later
//        int startIndex = out.writerIndex(); // Mark position

        innerEncode(ctx, packet, out);

//        int endIndex = out.writerIndex();
//        out.setInt(0, endIndex - startIndex); // Update length at the beginning
    }

    private void innerEncode(ChannelHandlerContext ctx, MessagePacket packet, ByteBuf out) {
        out.writeByte(packet.getId());

        //Write a string
        out.writeInt(packet.message.length());
        out.writeBytes(packet.message.getBytes());
    }
}
