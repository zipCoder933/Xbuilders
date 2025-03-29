package com.xbuilders.engine.utils.network.netty.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder  {

    public final static HashMap<Byte, Packet> packetInstances = new HashMap<>();

    public PacketDecoder() {
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        /**
         * Preview the packet
         */
       // previewPacket(in);

        /**
         * Packet ID
         */
        byte packetId = in.readByte();

        //Get the message ID and decode it
        Packet packetInstance = packetInstances.get((byte) packetId);
        if (packetInstance != null) {
            packetInstance.decode(ctx, in, out);
        } else {
            System.out.println("Unknown packet: " + packetId);
        }
    }

    private void previewPacket(ByteBuf in) {
        in.markReaderIndex(); // Mark reader index
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        System.out.print("Decoding Packet: \"" + new String(bytes) + "\"; " + Arrays.toString(bytes) + "; l=" + bytes.length);
        in.resetReaderIndex();
        in.markReaderIndex(); // Mark reader index
        //System.out.println("Packet length: " + in.readInt());
        System.out.print("; Packet id: " + in.readByte());
        in.resetReaderIndex();
        System.out.println();
    }
}
