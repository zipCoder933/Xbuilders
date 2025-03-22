package com.xbuilders.engine.utils.network.netty.packet;

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
        if(packetId == PingPongHandler.pingPacket) {
            out.add(new PingPongPacket(true));
            return;
        }else if (packetId == PingPongHandler.pongPacket) {
            out.add(new PingPongPacket(false));
            return;
        }

        switch (packetId) {
//            case 1 -> { // LoginPacket
//                if (in.readableBytes() < 4) {
//                    in.resetReaderIndex();
//                    return;
//                } // Ensure enough data
//                int userId = in.readInt();
//
//                if (in.readableBytes() < 4) {
//                    in.resetReaderIndex();
//                    return;
//                }
//                int usernameLength = in.readInt();
//
//                if (in.readableBytes() < usernameLength) {
//                    in.resetReaderIndex();
//                    return;
//                }
//                byte[] usernameBytes = new byte[usernameLength];
//                in.readBytes(usernameBytes);
//
//                out.add(new LoginPacket(userId, new String(usernameBytes)));
//            }
//            case 2 -> { // ChatPacket
//                if (in.readableBytes() < 4) {
//                    in.resetReaderIndex();
//                    return;
//                }
//                int msgLength = in.readInt();
//
//                if (in.readableBytes() < msgLength) {
//                    in.resetReaderIndex();
//                    return;
//                }
//                byte[] msgBytes = new byte[msgLength];
//                in.readBytes(msgBytes);
//
//                out.add(new ChatPacket(new String(msgBytes)));
//            }
        }
    }
}
