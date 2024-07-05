package com.xbuilders.engine.utils.network;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.ui.topMenu.NetworkJoinRequest;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.network.server.Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;

public class GameServer extends Server {//extends Server<NetworkSocket>

    public static final byte PLAYER_INFO = -128;

    NetworkJoinRequest req;
    UserControlledPlayer player;
//    HashMap<NetworkSocket, Player> players = new HashMap<>();

    public GameServer(UserControlledPlayer player) {
        super();
        this.player = player;
    }

    public void joinGame(NetworkJoinRequest req) {
        this.req = req;
        try {
            start(req.fromPortVal);
            if (!req.hosting) {
                /**
                 * We cant send our information until the host has accepted us and started listening for messages.
                 * To get around this, we need to either wait, or only send our information when the host sends a welcome message
                 */

                //Join the host
                System.out.println("Joining as " + req.hostIpAdress);
                NetworkSocket newClient = connectToServer(new InetSocketAddress(req.hostIpAdress, req.toPortVal));
                Thread.sleep(1000);
                newClient.sendData(player.infoToBytes());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean newClientEvent(NetworkSocket client) {
        System.out.println("New client: " + client.toString());
        if (clientAlreadyJoined(client)) {
            try {
                client.sendString("You already joined the game!");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        } else {
            try {
//                client.sendString("Welcome New player!");
                if (req.hosting) {
                    client.sendData(player.infoToBytes());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }

    @Override
    public void dataFromClientEvent(NetworkSocket client, byte[] receivedData) {
        try {
            if (client.player != null) {
                GameScene.alert("(" + client.player.name + "): " + new String(receivedData) + " l=" + receivedData.length);
            } else GameScene.alert("(Server): " + new String(receivedData) + " l=" + receivedData.length);

            if (receivedData.length > 0) {
                if (receivedData[0] == PLAYER_INFO) {
                    Player player = new Player();
                    player.loadInfoFromBytes(receivedData);
                    client.player = player;
//                    GameScene.alert("Player info: " + client.player.toString());
                    playerJoinEvent(client);
                    client.sendString("Welcome \"" + client.player.name + "\"!");
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void playerJoinEvent(NetworkSocket client) {
        GameScene.alert("A new player has joined: " + client.player.toString());
    }
}
