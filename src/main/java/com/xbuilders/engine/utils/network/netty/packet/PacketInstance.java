//package com.xbuilders.engine.utils.network.netty.packet;
//
//import com.xbuilders.engine.utils.network.netty.packet.join.JoinEncoder;
//import com.xbuilders.engine.utils.network.netty.packet.join.JoinHandler;
//import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongEncoder;
//import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongHandler;
//import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongPacket;
//import io.netty.buffer.ByteBuf;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.channel.socket.SocketChannel;
//import io.netty.handler.codec.MessageToByteEncoder;
//
//public abstract class PacketInstance {
//
//    public PacketInstance() {
//        super();
//    }
//
//    public abstract void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out);
//
//
//    public void register(SocketChannel ch){
//            ch.pipeline().addLast(new PacketHandler());
//            ch.pipeline().addLast(new PacketEncoder());
//    }
//
//     public class PacketHandler extends SimpleChannelInboundHandler<PingPongPacket> {
//
//        @Override
//        protected void channelRead0(ChannelHandlerContext ctx, PingPongPacket packet) {
//
//        }
//    }
//
//
//    class PacketEncoder extends MessageToByteEncoder<Packet> {
//
//        @Override
//        protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
//            out.writeInt(0); // Placeholder for length, updated later
//            int startIndex = out.writerIndex(); // Mark position
//
//            // Write packet ID
//            out.writeInt(packet.getId());
//
//            // Encode packet
//            PacketInstance.this.encode(ctx, packet, out);
//
//            int endIndex = out.writerIndex();
//            out.setInt(0, endIndex - startIndex); // Update length at the beginning
//        }
//    }
//}
