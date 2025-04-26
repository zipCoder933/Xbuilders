package com.xbuilders.engine.utils.network.testing;

import com.xbuilders.engine.utils.network.fake.FakeChannel;
import com.xbuilders.engine.utils.network.fake.FakeClient;
import com.xbuilders.engine.utils.network.fake.FakeServer;
import com.xbuilders.engine.utils.network.packet.Packet;
import com.xbuilders.engine.utils.network.packet.message.MessagePacket;
import com.xbuilders.engine.utils.network.packet.ping.PingPacket;
import com.xbuilders.engine.utils.network.packet.ping.PongPacket;

public class SimulatedServerClient {

    public static void main(String[] args) throws InterruptedException {
        Packet.register( new MessagePacket());
        Packet.register( new PingPacket());
        Packet.register( new PongPacket());

        FakeServer server = new FakeServer() {
            @Override
            public boolean newClientEvent(FakeChannel client) {
                System.out.println("New client: " + client.remoteAddress());
                System.out.println("All clients: " + clients.toString());
                client.writeAndFlush(new MessagePacket("Hello from server!"));
                return true;
            }

            @Override
            public void clientDisconnectEvent(FakeChannel client) {
                System.out.println("Client disconnected: " + client.remoteAddress());
            }
        };

        server.start();

        Thread.sleep(1000); // Give the server a second

        FakeClient client = new FakeClient(server) {
            @Override
            public void onConnected(FakeChannel channel) {
                System.out.println("Successfully connected to the FakeServer!");

                new Thread(() -> {
                    while (isConnected()) {
                        try {
                            channel.writeAndFlush(new MessagePacket("Hello World!"));
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        };

    }
}
