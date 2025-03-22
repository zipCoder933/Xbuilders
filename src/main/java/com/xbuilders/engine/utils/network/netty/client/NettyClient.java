package com.xbuilders.engine.utils.network.netty.client;

import com.xbuilders.engine.utils.network.netty.packet.PacketDecoder;
import com.xbuilders.engine.utils.network.netty.packet.message.Message;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongEncoder;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongHandler;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPongPacket;
import com.xbuilders.engine.utils.network.netty.server.NettyServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.concurrent.TimeUnit;

public class NettyClient {

    private Channel channel;
    private EventLoopGroup group;

    private void registerPackets(SocketChannel ch) {
        //This allows the entire packet to be read before decoding
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                1024,
                0,
                4,
                0,
                4)); // Max 1024 bytes, 4-byte length field

        ch.pipeline().addLast(new PingPongEncoder());
        ch.pipeline().addLast(new PingPongHandler());

        new Message().register(ch);

        ch.pipeline().addLast(new PacketDecoder());
    }

    public NettyClient(String host, int port) {
        System.out.println("Connecting to " + host + ":" + port);
        group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new NettyClientHandler(NettyClient.this));

                            ch.pipeline().addLast(new PacketDecoder());
                            registerPackets(ch);
                        }
                    });

            // Connect to localServer
            ChannelFuture future = bootstrap.connect(host, port).sync();
            channel = future.channel();

            // Schedule periodic ping
            schedulePing();

            // Add a listener to the future to handle when the connection is successful
            future.addListener((ChannelFutureListener) this::onConnected);

            // Wait until connection is closed
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }

    public void onConnected(ChannelFuture channelFuture) {
        if (channelFuture.isSuccess()) {
            // This block will be executed when the client is successfully connected to the localServer
            System.out.println("Successfully connected to the localServer!");
            // You can schedule further events here
        } else {
            // This block will be executed if the connection fails
            System.err.println("Failed to connect to the localServer: " + channelFuture.cause());
        }
    }

    private void schedulePing() {
        channel.eventLoop().scheduleAtFixedRate(() -> {
            if (channel.isActive()) {
                channel.writeAndFlush(new PingPongPacket(true));
            }
        }, 10, NettyServer.PING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

//    public void sendData(byte[] data) {
//        if (channel != null && channel.isActive()) {
//            channel.writeAndFlush(Unpooled.wrappedBuffer(data));
//        }
//    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
    }
}
