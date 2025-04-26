package com.xbuilders.engine.utils.network.netty;

import com.xbuilders.engine.utils.network.ChannelBase;
import com.xbuilders.engine.utils.network.packet.Packet;
import io.netty.channel.Channel;

import java.net.SocketAddress;

public class NettyChannel extends ChannelBase {
    private final Channel channel;

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void writeAndFlush(Packet packet) {
        channel.writeAndFlush(packet);
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public SocketAddress remoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public void close() {
        channel.close();
    }
}
