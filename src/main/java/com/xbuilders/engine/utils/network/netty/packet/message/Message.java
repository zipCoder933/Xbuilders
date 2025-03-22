package com.xbuilders.engine.utils.network.netty.packet.message;

import com.xbuilders.engine.utils.network.netty.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class Message extends Packet<MessagePacket> {

    public Message() {
        super(2);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, MessagePacket packet, ByteBuf out) {
        //Write a string
        out.writeInt(packet.message.length());
        out.writeBytes(packet.message.getBytes());
    }

    @Override
    public void handle(ChannelHandlerContext ctx, MessagePacket packet) {
        System.out.println("Messsage: " + packet.message);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int length = in.readInt();
        byte[] messageBytes = new byte[length];
        in.readBytes(messageBytes);
        String msg = new String(messageBytes);
        out.add(new MessagePacket(msg));
    }
}
