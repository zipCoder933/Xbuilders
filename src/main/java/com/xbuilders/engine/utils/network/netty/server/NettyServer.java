package com.xbuilders.engine.utils.network.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.TimeUnit;

/**
 * Abstract Netty-based localServer optimized for game servers.
 * <p>
 * This class replaces the old blocking ServerSocket implementation.
 * It uses Netty’s ServerBootstrap and pipeline:
 * <ul>
 *   <li>A {@code ChannelGroup} holds all active client channels.</li>
 *   <li>An {@code IdleStateHandler} sends PING messages if no write has occurred within the given interval.</li>
 *   <li>The inner {@code ServerHandler} takes care of connection, disconnection, ping/pong, and data events.</li>
 * </ul>
 * Subclasses should implement the abstract {@code newClientEvent(Channel)} and
 * {@code dataFromClientEvent(Channel, byte[])} methods.
 */
public abstract class NettyServer {

    protected final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static final byte[] pingMessage = new byte[]{0};
    public static final byte[] pongMessage = new byte[]{1};
    // The idle interval (in seconds) for sending pings.
    public static final long PING_INTERVAL_SECONDS = 30;

    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected Channel serverChannel;

    /**
     * Starts the Netty localServer on the given port.
     *
     * @param port the port (must be >= 1024)
     * @throws InterruptedException if the binding is interrupted
     */
    public NettyServer(int port) throws InterruptedException {
        if (port < 1024) {
            throw new IllegalArgumentException("Port number must be higher than 1024");
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();


        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // Set up the pipeline:
                        // 1. IdleStateHandler triggers an IdleStateEvent if no write for PING_INTERVAL_SECONDS.
                        // 2. ServerHandler processes connection, disconnection, ping/pong, and inbound data.
                        ch.pipeline().addLast(new IdleStateHandler(PING_INTERVAL_SECONDS, PING_INTERVAL_SECONDS, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new NettyServerHandler(NettyServer.this));
                    }
                });

        ChannelFuture future = bootstrap.bind(port).sync();
        serverChannel = future.channel();
        System.out.println("Server started at " + port);
    }

    /**
     * Closes the localServer and all client connections.
     */
    public void close() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        clients.close();

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * Sends data to all connected clients.
     *
     * @param data the data to send
     */
    public void sendToAllClients(byte[] data) {
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        clients.writeAndFlush(buf);
    }

    /**
     * Called when a new client connects.
     * <p>
     * Return {@code true} to accept the connection, or {@code false} to immediately close it.
     *
     * @param client the client's channel
     * @return whether to accept the client
     */
    public abstract boolean newClientEvent(Channel client);

    /**
     * Called when data is received from a client.
     *
     * @param client       the client's channel
     * @param receivedData the received bytes
     */
    public abstract void dataFromClientEvent(Channel client, byte[] receivedData);

    /**
     * Called when a client disconnects.
     *
     * @param client the client's channel
     */
    public void clientDisconnectEvent(Channel client) {
        System.out.println("Disconnected: " + client.remoteAddress());
    }


}
