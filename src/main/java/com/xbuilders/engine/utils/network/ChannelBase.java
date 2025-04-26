package com.xbuilders.engine.utils.network;

import com.xbuilders.engine.utils.network.packet.Packet;

public abstract class ChannelBase {
    public abstract void writeAndFlush(Packet packet);
}
