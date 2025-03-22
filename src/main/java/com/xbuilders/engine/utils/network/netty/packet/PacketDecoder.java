package com.xbuilders.engine.utils.network.netty.packet;

import com.xbuilders.engine.utils.network.netty.packet.join.JoinPacket;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongHandler;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static com.xbuilders.engine.utils.network.netty.packet.Packet.PACKET_BYTES;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in.readableBytes() < PACKET_BYTES) return; // Ensure we can read packet ID
        in.markReaderIndex(); // Mark current position
        int packetId = in.readByte(); // Read packet ID

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

        if (packetId == 2) {// Join Packet
            if (in.readableBytes() < 4) {
                in.resetReaderIndex();
                return;
            } // Ensure enough data
            int clientVersion = in.readInt();
            out.add(new JoinPacket("Test", clientVersion));
        }
    }
}
