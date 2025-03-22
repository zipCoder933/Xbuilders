package com.xbuilders.engine.utils.network.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

public abstract class Packet<T> {

    public final byte id;

    public Packet(int id) {
        this.id = (byte) id;
    }

    public abstract void encode(ChannelHandlerContext ctx, T packet, ByteBuf out);

    public abstract void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out);

    public abstract void handle(ChannelHandlerContext ctx, T packet);

    public void register(SocketChannel ch) {
        ch.pipeline().addLast(new PacketHandler());
        ch.pipeline().addLast(new PacketEncoder());
        PacketDecoder.packetInstances.put(id, this);
    }

    public void register(Channel ch) {
        ch.pipeline().addLast(new PacketHandler());
        ch.pipeline().addLast(new PacketEncoder());
        PacketDecoder.packetInstances.put(id, this);
    }

    class PacketHandler extends SimpleChannelInboundHandler<T> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, T packet) {
            handle(ctx, packet);
        }
    }

    class PacketEncoder extends MessageToByteEncoder<T> {

        @Override
        protected void encode(ChannelHandlerContext ctx, T packet, ByteBuf out) {
            out.writeInt(0); // Placeholder for length, updated later
            int startIndex = out.writerIndex(); // Mark position

            out.writeByte(id);  // Write packet ID
            Packet.this.encode(ctx, packet, out);   // Encode packet

            int endIndex = out.writerIndex();
            out.setInt(0, endIndex - startIndex); // Update length at the beginning
        }
    }
}
