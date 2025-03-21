package com.xbuilders.engine.utils.network.netty;

import com.xbuilders.engine.utils.network.netty.server.NettyServer;

public class Test_NettyServer {
    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer(8080) {
            @Override
            public boolean newClientEvent(io.netty.channel.Channel client) {
                System.out.println("New client: " + client.remoteAddress());
                //List all clients
                System.out.println("All clients: "+clients.toString());
                return true;
            }

            @Override
            public void dataFromClientEvent(io.netty.channel.Channel client, byte[] receivedData) {
                System.out.println("Data from client " + client.remoteAddress() + ": " + new String(receivedData));
            }
        };
    }

}
