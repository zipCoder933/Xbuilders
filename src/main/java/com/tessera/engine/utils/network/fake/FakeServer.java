package com.tessera.engine.utils.network.fake;

import com.tessera.engine.utils.network.ChannelBase;
import com.tessera.engine.utils.network.ServerBase;
import com.tessera.engine.utils.network.packet.Packet;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class FakeServer extends ServerBase {
    private final List<FakeChannel> clients = new CopyOnWriteArrayList<>();

    protected FakeChannel connect(FakeClient client) {
        FakeChannel channel = new FakeChannel(this, client, false);
        clients.add(channel);
        boolean accepted = newClientEvent(channel);
        if (!accepted) {
            channel.close();
        }
        return channel;
    }

    public abstract boolean newClientEvent(ChannelBase client);

    public abstract void clientDisconnectEvent(ChannelBase client);

    protected void receive(FakeChannel client, Packet packet) {
        //System.out.println("Server received: " + packet);
        packet.handle(client, packet);
    }

    public void close(){
        for (FakeChannel client : clients) {
            client.close();
        }
    }
}
