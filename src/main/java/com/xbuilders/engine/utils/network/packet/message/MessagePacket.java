package com.xbuilders.engine.utils.network.packet.message;

import com.xbuilders.engine.utils.network.ChannelBase;
import com.xbuilders.engine.utils.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class MessagePacket extends Packet {
    String message;

    public MessagePacket(String message) {
        super(2);
        this.message = message;
    }

    public MessagePacket() {
        super(2);
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        MessagePacket packetInstance = (MessagePacket) packet;
        //Write a string
        out.writeInt(packetInstance.message.length());
        out.writeBytes(packetInstance.message.getBytes());
    }

    @Override
    public void handle(ChannelBase ctx, Packet packet) {
        MessagePacket packetInstance = (MessagePacket) packet;
        System.out.println("Messsage: " + packetInstance.message);
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
