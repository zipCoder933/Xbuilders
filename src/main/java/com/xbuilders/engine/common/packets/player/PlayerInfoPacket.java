package com.xbuilders.engine.common.packets.player;

import com.xbuilders.engine.common.network.ChannelBase;
import com.xbuilders.engine.common.network.packet.Packet;
import com.xbuilders.engine.common.packets.AllPackets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PlayerInfoPacket extends Packet {

    final String name;
    final int skinID;

    public PlayerInfoPacket(String name, int skinID) {
        super(AllPackets.PLAYER_INFO);
        this.name = name;
        this.skinID = skinID;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) {
        PlayerInfoPacket packetInstance = (PlayerInfoPacket) packet;
        //Write a string
        out.writeInt(packetInstance.name.length());
        out.writeBytes(packetInstance.name.getBytes(StandardCharsets.UTF_8));
        out.writeByte(packetInstance.skinID);
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int nameLength = in.readInt();
        byte[] nameBytes = new byte[nameLength];
        in.readBytes(nameBytes);

        String name = new String(nameBytes, StandardCharsets.UTF_8);
        int skinID = in.readByte();

        out.add(new PlayerInfoPacket(name, skinID));
    }

    @Override
    public void handle(ChannelBase ctx, Packet packet) {

    }
}
