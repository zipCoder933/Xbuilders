package com.xbuilders.engine.utils.network.netty;

import com.xbuilders.engine.utils.network.netty.packet.message.MessagePacket;
import com.xbuilders.engine.utils.network.netty.server.NettyServer;

public class Test_NettyServer {
    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer(8080) {
            @Override
            public boolean newClientEvent(io.netty.channel.Channel client) {

                System.out.println("New client: " + client.remoteAddress());
                System.out.println("All clients: " + clients.toString());
                client.writeAndFlush(new MessagePacket("Hello from server!"));

                return true;
            }
        };
    }

}
