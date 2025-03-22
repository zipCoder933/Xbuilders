package com.xbuilders.engine.utils.network.netty.packet.join;

import com.xbuilders.engine.utils.network.netty.packet.Packet;

public class JoinPacket extends Packet {
    String username;
    int clientVersion;

    public JoinPacket(String username, int clientVersion) {
        super(2);
        this.username = username;
        this.clientVersion = clientVersion;
    }
}
