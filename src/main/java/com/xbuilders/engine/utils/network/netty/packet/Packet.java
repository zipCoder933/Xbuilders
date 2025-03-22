package com.xbuilders.engine.utils.network.netty.packet;

public class Packet {
    final byte id;
    public final static int PACKET_BYTES = 1;

    public Packet(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
