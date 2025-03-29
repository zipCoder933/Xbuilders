package com.xbuilders.engine.utils.network.netty;

import com.xbuilders.engine.utils.network.netty.client.NettyClient;
import com.xbuilders.engine.utils.network.netty.packet.message.MessagePacket;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPacket;
import com.xbuilders.engine.utils.network.netty.server.NettyServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.TimeUnit;

public class Test_NettyClient {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(5000);
        NettyClient client = new NettyClient("localhost", 8080) {

            public void onConnected(ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    System.out.println("Successfully connected to the localServer!");

                    //Schedule this on another thread
                    channelFuture.channel().eventLoop().scheduleAtFixedRate(() -> {
                        if (channelFuture.channel().isActive()) {
                            try {
                                Channel channel = channelFuture.channel();
                                channel.writeAndFlush(new MessagePacket("Hello World!"));
                                Thread.sleep(5000);

                                channel.writeAndFlush(new PingPacket());
                                Thread.sleep(5000);

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, 10, 5, TimeUnit.SECONDS);


                } else {
                    // This block will be executed if the connection fails
                    System.err.println("Failed to connect to the localServer: " + channelFuture.cause());
                }
            }
        };
    }

}
