package com.xbuilders.engine.utils.network.netty.packet.ping;

public class PingPongPacket {
    public final boolean ping;

    public PingPongPacket(boolean ping) {
        this.ping = ping;
    }
}
