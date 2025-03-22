package com.xbuilders.engine.utils.network.netty;

import com.xbuilders.engine.utils.network.netty.client.NettyClient;
import com.xbuilders.engine.utils.network.netty.packet.message.MessagePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class Test_NettyClient {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(5000);
        NettyClient client = new NettyClient("localhost", 8080) {

            public void onConnected(ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    System.out.println("Successfully connected to the localServer!");

                    Channel channel = channelFuture.channel();
                    channel.writeAndFlush(new MessagePacket("This is a test Hello World 1234567890!" +
                            "This is a test Hello World 1234567890!" +
                            "This is a test Hello World 1234567890!" +
                            "This is a test Hello World 1234567890!" +
                            "This is a test Hello World 1234567890!\n" +
                            "This is a test Hello World 1234567890!\n" +
                            "This is a test Hello World 1234567890!\n" +
                            "This is a test Hello World 1234567890!\n"));


                } else {
                    // This block will be executed if the connection fails
                    System.err.println("Failed to connect to the localServer: " + channelFuture.cause());
                }
            }
        };
    }

}
