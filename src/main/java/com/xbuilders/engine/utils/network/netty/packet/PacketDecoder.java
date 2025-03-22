package com.xbuilders.engine.utils.network.netty.packet;

import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongHandler;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.HashMap;
import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {

    public final static HashMap<Byte, Packet> packetInstances = new HashMap<>();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        byte packetId = in.readByte(); // Read packet ID

        //Read the whole packet
        in.markReaderIndex(); // Mark reader index
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        System.out.println("Packet: " + packetId + " " + new String(bytes));
        in.resetReaderIndex();

        /**
         * Handle the ping/pong packets
         */
        if (packetId == PingPongHandler.pingPacket) {
            out.add(new PingPongPacket(true));
            return;
        } else if (packetId == PingPongHandler.pongPacket) {
            out.add(new PingPongPacket(false));
            return;
        }


        //Get the message ID and decode it
        Packet packetInstance = packetInstances.get((byte) packetId);
        if (packetInstance != null) {
            packetInstance.decode(ctx, in, out);
        } else {
            System.out.println("Unknown packet: " + packetId);
        }
    }
}
