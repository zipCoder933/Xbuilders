package com.xbuilders.engine.utils.network.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Arrays;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private final NettyClient client;
    
    public NettyClientHandler(NettyClient client) {
        this.client = client;
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        ByteBuf buf = (ByteBuf) msg;
//        byte[] receivedData = new byte[buf.readableBytes()];
//        buf.readBytes(receivedData);
//        buf.release();
//
//        if (Arrays.equals(receivedData, PONG_MESSAGE)) {
//            System.out.println("Received PONG from localServer");
//        } else {
//            System.out.println("Received data: " + Arrays.toString(receivedData));
//        }
//    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}