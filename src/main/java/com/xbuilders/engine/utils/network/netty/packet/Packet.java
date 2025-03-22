package com.xbuilders.engine.utils.network.netty.packet;

public class Packet {
    final byte id;

    public Packet(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
