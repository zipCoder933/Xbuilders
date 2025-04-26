package com.xbuilders.engine.utils.network;

import com.xbuilders.engine.utils.network.packet.Packet;

import java.net.SocketAddress;

public abstract class ChannelBase {
    public abstract void writeAndFlush(Packet packet);

    public abstract boolean isActive();

    public SocketAddress remoteAddress() {
        return null;
    }

    public abstract void close();
}
