package com.xbuilders.engine.utils.network.netty.packet;

import com.xbuilders.engine.utils.network.netty.packet.message.MessagePacket;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongHandler;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int packetId = in.readByte(); // Read packet ID

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

        if (packetId == 2) {// Join Packet
            int length = in.readInt();
            byte[] messageBytes = new byte[length];
            in.readBytes(messageBytes);
            String msg = new String(messageBytes);
            out.add(new MessagePacket(msg));
        }
    }

}
