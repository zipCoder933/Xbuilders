package com.xbuilders.engine.utils.network.testing;

import com.xbuilders.engine.utils.network.netty.NettyClient;
import com.xbuilders.engine.utils.network.packet.Packet;
import com.xbuilders.engine.utils.network.packet.message.MessagePacket;
import com.xbuilders.engine.utils.network.packet.ping.PingPacket;
import com.xbuilders.engine.utils.network.packet.ping.PongPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

public class Test_NettyClient {
    public static void main(String[] args) throws InterruptedException {

        Packet.register(new MessagePacket());
        Packet.register(new PingPacket());
        Packet.register(new PongPacket());

        Thread.sleep(10000);
        NettyClient client = new NettyClient("localhost", 8080) {
            public void onConnected(ChannelFuture channelFuture) {
                if (channelFuture.isSuccess()) {
                    System.out.println("Successfully connected to the localServer!");

                    //Schedule this on another thread
                    new Thread(() -> {
                        while (true) {
                            if (channelFuture.channel().isActive()) {
                                try {
                                    Channel channel = channelFuture.channel();
                                    channel.writeAndFlush(new MessagePacket("Hello World!"));
                                    Thread.sleep(5000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }).start();


                } else {
                    // This block will be executed if the connection fails
                    System.err.println("Failed to connect to the localServer: " + channelFuture.cause());
                }
            }
        };


    }

}
