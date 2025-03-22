package com.xbuilders.engine.utils.network.netty;

import com.xbuilders.engine.utils.network.netty.client.NettyClient;
import com.xbuilders.engine.utils.network.netty.packet.message.MessagePacket;
import com.xbuilders.engine.utils.network.netty.packet.ping.PingPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class Test_NettyClient {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(5000);
        NettyClient client = new NettyClient("localhost", 8080) {

            public void onConnected(ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    System.out.println("Successfully connected to the localServer!");

                    String str = "Hello";
                    while (true) {
                        Channel channel = channelFuture.channel();
                        channel.writeAndFlush(new MessagePacket(str));
                        str += ".";
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        channel.writeAndFlush(new PingPacket());
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }


                } else {
                    // This block will be executed if the connection fails
                    System.err.println("Failed to connect to the localServer: " + channelFuture.cause());
                }
            }
        };
    }

}
