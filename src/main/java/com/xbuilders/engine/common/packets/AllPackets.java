package com.xbuilders.engine.common.packets;

import com.xbuilders.engine.common.network.packet.Packet;

public class AllPackets {
    //Ping and pong packets take up 0 and 1
    public static final int MESSAGE = 2;
    public static final int CLIENT_ENTRANCE = 3;
    public static final int SERVER_GATEKEEPER = 4;

    public static void registerPackets() {
        Packet.register(new ClientEntrancePacket());
        Packet.register(new ServerGatekeeperPacket());
        Packet.register(new MessagePacket());
    }
}
