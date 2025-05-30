package com.xbuilders.engine.utils.network.fake;

import com.xbuilders.engine.utils.network.ChannelBase;
import com.xbuilders.engine.utils.network.packet.Packet;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FakeChannel extends ChannelBase {
    private final FakeServer server;
    private final FakeClient client;
    private final boolean isClientSide;
    private final BlockingQueue<Object> incoming = new LinkedBlockingQueue<>();
    private final AtomicBoolean active = new AtomicBoolean(true);

    public FakeChannel(FakeServer server, FakeClient client, boolean isClientSide) {
        this.server = server;
        this.client = client;
        this.isClientSide = isClientSide;
        startProcessing();
    }

    private void startProcessing() {
        new Thread(() -> {
            try {
                while (active.get()) {
                    Packet packet = (Packet) incoming.take();
                    if (isClientSide) {
                        server.receive(this, packet);
                    } else {
                        client.receive(packet);
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    public void writeAndFlush(Packet packet) {
        incoming.offer(packet);
    }

    public boolean isActive() {
        return active.get();
    }

    public void close() {
        active.set(false);
        server.clientDisconnectEvent(this);
    }
}
