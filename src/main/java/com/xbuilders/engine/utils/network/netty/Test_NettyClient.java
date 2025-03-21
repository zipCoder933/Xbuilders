package com.xbuilders.engine.utils.network.netty;

import com.xbuilders.engine.utils.network.netty.client.NettyClient;
import io.netty.channel.ChannelFuture;

public class Test_NettyClient {
    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient("localhost", 8080) {

            public void onConnected(ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    // This block will be executed when the client is successfully connected to the server
                    System.out.println("Successfully connected to the server!");
                    sendData("Hello World!".getBytes());
                    // You can schedule further events here
                } else {
                    // This block will be executed if the connection fails
                    System.err.println("Failed to connect to the server: " + channelFuture.cause());
                }
            }
        };
    }

}
