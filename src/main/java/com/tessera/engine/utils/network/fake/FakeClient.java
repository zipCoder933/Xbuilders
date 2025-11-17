package com.tessera.engine.utils.network.fake;

import com.tessera.engine.utils.network.ChannelBase;
import com.tessera.engine.utils.network.ClientBase;
import com.tessera.engine.utils.network.packet.Packet;

public abstract class FakeClient extends ClientBase {

    private final FakeServer server;
    private FakeChannel channel;

    public FakeClient(FakeServer server) {
        this.server = server;
        connect();
    }

    private void connect() {
        this.channel = new FakeChannel(server, this, true); // client-side channel
        server.connect(this); // Still register with server
        onConnected(true, null, channel);
    }

    public abstract void onConnected(boolean success, Throwable cause, ChannelBase channel);

    protected void receive(Packet packet) {
        //System.out.println("Client received: " + packet);
        packet.handle(channel, packet);
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    public void close() {
        channel.close();
    }
}
