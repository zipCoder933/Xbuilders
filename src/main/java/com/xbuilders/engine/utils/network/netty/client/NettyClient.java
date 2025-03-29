package com.xbuilders.engine.utils.network.netty.client;

import com.xbuilders.engine.utils.network.netty.packet.Packet;
import com.xbuilders.engine.utils.network.netty.packet.PacketDecoder;
import com.xbuilders.engine.utils.network.netty.packet.PacketEncoder;
import com.xbuilders.engine.utils.network.netty.packet.PacketHandler;
import com.xbuilders.engine.utils.network.netty.packet.message.MessagePacket;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPacket;
import com.xbuilders.engine.utils.network.netty.packet.ping.PongPacket;
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
    private final ChannelFuture future;

    private void registerPackets(SocketChannel ch) {
        Packet.register(ch, new MessagePacket());
        Packet.register(ch, new PingPacket());
        Packet.register(ch, new PongPacket());
    }

    public NettyClient(String host, int port) throws InterruptedException {
        System.out.println("Connecting to " + host + ":" + port);
        group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new NettyClientHandler(NettyClient.this));

                        /**
                         * Add  the decoder
                         * 1. The LengthFieldBasedFrameDecoder decodes the length of the packet and strips the length field
                         * 2. The PacketDecoder decodes the packet
                         */
                        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                                1024, // Max frame size (1 KB)
                                0,    // Length field offset (starts at byte 0)
                                4,    // Length field length (4 bytes for int)
                                0,    // No length adjustment
                                4     // Strip the length field from the output
                        ));
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new PacketEncoder());
                        ch.pipeline().addLast(new PacketHandler());
                        registerPackets(ch);
                    }
                });

        // Connect to localServer
        future = bootstrap.connect(host, port).sync();
        channel = future.channel();

        // Schedule periodic ping
        schedulePing();

        // Add a listener to the future to handle when the connection is successful
        future.addListener((ChannelFutureListener) this::onConnected);
    }

    public void waitUntilChannelIsClosed() {
        // Wait until connection is closed
        try {
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
                channel.writeAndFlush(new PingPacket());
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
