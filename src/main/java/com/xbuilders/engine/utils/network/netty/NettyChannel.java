package com.xbuilders.engine.utils.network.netty;

import com.xbuilders.engine.utils.network.ChannelBase;
import com.xbuilders.engine.utils.network.packet.Packet;
import io.netty.channel.Channel;

public class NettyChannel extends ChannelBase {
    Channel channel;

    public NettyChannel(Channel channel) {
        this.channel = channel;
    }

    //Using methods from ChannelBase

    @Override
    public void writeAndFlush(Packet packet) {
        channel.writeAndFlush(packet);
    }
}
