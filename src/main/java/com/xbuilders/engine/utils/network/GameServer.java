package com.xbuilders.engine.utils.network;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.ui.topMenu.NetworkJoinRequest;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.network.server.Server;
import java.io.IOException;
import java.net.InetSocketAddress;

public class GameServer extends Server<PlayerSocket> {

    public static final byte PLAYER_INFO = -128;

    NetworkJoinRequest req;
    UserControlledPlayer player;

    public GameServer(UserControlledPlayer player) {
        super(PlayerSocket::new);
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
    public boolean newClientEvent(PlayerSocket client) {
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
    public void dataFromClientEvent(PlayerSocket client, byte[] receivedData) {
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

    public PlayerSocket getPlayerByName(String name) {
        for (PlayerSocket client : clients) {
            if (client.player != null && client.player.name.equalsIgnoreCase(name)) {
                return client;
            }
        }
        return null;
    }

    private void playerJoinEvent(PlayerSocket client) {
        GameScene.alert("A new player has joined: " + client.player.toString());
    }
}
