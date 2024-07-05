package com.xbuilders.engine.utils.network;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.utils.network.server.NetworkSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PlayerClient extends NetworkSocket {
    public Player player;

    public PlayerClient(InetSocketAddress addr) throws IOException {
        super(addr);
    }

    public PlayerClient(Socket socket) throws IOException {
        super(socket);
    }
}
