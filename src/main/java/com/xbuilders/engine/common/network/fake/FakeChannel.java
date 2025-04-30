package com.xbuilders.engine.common.network.fake;

import com.xbuilders.engine.common.network.ChannelBase;
import com.xbuilders.engine.common.network.packet.Packet;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class FakeChannel extends ChannelBase {
    private final FakeServer server;
    private final FakeClient client;
    public final boolean sendMessagesToServer;
    private final BlockingQueue<Object> incoming = new LinkedBlockingQueue<>();
    private final AtomicBoolean active = new AtomicBoolean(true);
    private final SocketAddress fakeAddress = new SocketAddress() {
        public String toString() {
            return "FakeAddress-" + hashCode();
        }
    };
    private Thread processingThread;
    FakeChannel reverseChannel;

    public FakeChannel(FakeServer server, FakeClient client, boolean sendMessagesToServer) {
        this.server = server;
        this.client = client;
        this.sendMessagesToServer = sendMessagesToServer;
        startProcessing();
    }

    public void makeReverseChannel() {
        reverseChannel = new FakeChannel(server, client, !sendMessagesToServer);
        reverseChannel.reverseChannel = this;
    }

    private void startProcessing() {
        processingThread = new Thread(() -> {
            System.out.println("FakeChannel started " + this);
            try {
                while (active.get()) {
                    Packet packet = (Packet) incoming.take();
                    if (sendMessagesToServer) {
                        server.receive(reverseChannel, packet);
                    } else {
                        client.receive(packet);
                    }
                }
            } catch (InterruptedException ignored) {
                ignored.printStackTrace();
                close();
            }
        });
        processingThread.start();
    }

    public void writeAndFlush(Packet packet) {
        incoming.offer(packet);
    }

    public boolean isActive() {
        return active.get();
    }

    public void close() {
        System.out.println("FakeChannel closed " + this);
        active.set(false);
        server.clientDisconnectEvent(this);
        processingThread.interrupt();
    }

    public SocketAddress remoteAddress() {
        return fakeAddress;
    }

    public String toString() {
        return "FakeChannel{" +
                "sendMessagesToServer=" + sendMessagesToServer +
                '}';
    }
}
