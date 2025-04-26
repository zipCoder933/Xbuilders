package com.xbuilders.engine.utils.network.fake;

import com.xbuilders.engine.utils.network.ServerBase;
import com.xbuilders.engine.utils.network.packet.Packet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class FakeServer extends ServerBase {
    protected final List<FakeChannel> clients = new CopyOnWriteArrayList<>();

    public void start() {
        System.out.println("FakeServer started.");
    }

    public FakeChannel connect(FakeClient client) {
        FakeChannel channel = new FakeChannel(this, client, false);
        clients.add(channel);
        boolean accepted = newClientEvent(channel);
        if (!accepted) {
            channel.close();
        }
        return channel;
    }

    public abstract boolean newClientEvent(FakeChannel client);

    public abstract void clientDisconnectEvent(FakeChannel client);

    public void receive(FakeChannel client, Packet packet) {
        System.out.println("Server received: " + packet);
        packet.handle(client, packet);
    }

    public void close(){
        for (FakeChannel client : clients) {
            client.close();
        }
    }
}
