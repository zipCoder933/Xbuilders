package com.xbuilders.engine.utils.network.netty.packet.message;

import com.xbuilders.engine.utils.network.netty.packet.Packet;

public class MessagePacket extends Packet {
    String message;

    public MessagePacket(String message) {
        super(2);
        this.message = message;
    }
}
