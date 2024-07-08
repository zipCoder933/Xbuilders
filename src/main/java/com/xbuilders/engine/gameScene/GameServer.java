package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.ui.topMenu.NetworkJoinRequest;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.network.server.NetworkUtils;
import com.xbuilders.engine.utils.network.server.Server;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import org.joml.Vector4f;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class GameServer extends Server<PlayerSocket> {

    public static final byte PLAYER_INFO = -128;
    public static final byte WORLD_INFO = -127;
    public static final byte PLAYER_POSITION = -126;
    public static final byte PLAYER_CHAT = -125;

    NetworkJoinRequest req;
    UserControlledPlayer player;
    WorldInfo info;
    boolean worldReady = false;

    public GameServer(UserControlledPlayer player) {
        super(PlayerSocket::new);
        this.player = player;
    }

    public WorldInfo startGame(NetworkJoinRequest req) throws IOException, InterruptedException {
        this.req = req;
        info = null;
        worldReady = false;

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


            while (!worldReady) {
                Thread.sleep(100);
            }
            //If the world info already exists, we dont need to create a new one
            if (!WorldsHandler.worldNameAlreadyExists(info.getName())) {
                WorldsHandler.makeNewWorld(info);
            }
            return info;
        }

        return null;
    }

    public void closeGame() throws IOException {
        close();
        info = null;
        worldReady = false;
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
                if (req.hosting) {
                    client.sendData(player.infoToBytes());
                    System.out.println(GameScene.world.info.getName() + "\n" + GameScene.world.info.toJson());
                    client.sendData(NetworkUtils.formatMessage(WORLD_INFO,
                            GameScene.world.info.getName() + "\n" + GameScene.world.info.toJson()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }

    private void printDatafromClient(PlayerSocket client, byte[] receivedData) {
        String playerName = client.player == null ? "Unknown" : client.player.name;
        try {
            GameScene.consoleOut(playerName + ":  (L" + receivedData.length + ") " + new String(receivedData));
        } catch (Exception e) {
            GameScene.consoleOut(playerName + ":  (L" + receivedData.length + ") " + Arrays.toString(receivedData));
        }
    }

    @Override
    public void dataFromClientEvent(PlayerSocket client, byte[] receivedData) {
        try {
//            printDatafromClient(client, receivedData);

            if (receivedData.length > 0) {
                if (receivedData[0] == PLAYER_INFO) {
                    Player player = new Player();
                    player.loadInfoFromBytes(receivedData);
                    client.player = player;
                    playerJoinEvent(client);
                    client.sendData(NetworkUtils.formatMessage(PLAYER_CHAT, "Welcome \"" + player.name + "\"!"));
                } else if (receivedData[0] == PLAYER_CHAT) {
                    String message = new String(NetworkUtils.getMessage(receivedData));
                    String playerName = client.player == null ? "Unknown" : client.player.name;
                    GameScene.consoleOut(playerName + ":  \"" + message + "\"");
                } else if (receivedData[0] == WORLD_INFO) {
                    String value = new String(NetworkUtils.getMessage(receivedData));
                    String name = value.split("\n")[0];
                    String json = value.split("\n")[1];
                    info = new WorldInfo();
                    info.makeNew(name, json);
                    worldReady = true;

                } else if (receivedData[0] == PLAYER_POSITION) {
                    float x = ByteUtils.bytesToFloat(new byte[]{receivedData[1], receivedData[2], receivedData[3], receivedData[4]});
                    float y = ByteUtils.bytesToFloat(new byte[]{receivedData[5], receivedData[6], receivedData[7], receivedData[8]});
                    float z = ByteUtils.bytesToFloat(new byte[]{receivedData[9], receivedData[10], receivedData[11], receivedData[12]});
                    float w = ByteUtils.bytesToFloat(new byte[]{receivedData[13], receivedData[14], receivedData[15], receivedData[16]});
                    System.out.println("Player position: " + x + " " + y + " " + z + " " + w);
                    client.player.worldPosition.set(x, y, z);
                    client.player.pan = (w);
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

    public void onClientDisconnect(PlayerSocket client) {
        GameScene.consoleOut("Player disconnected: " + client.player.toString());
    }

    private void playerJoinEvent(PlayerSocket client) {
        GameScene.alert("A new player has joined: " + client.player.toString());
    }

    public String sendChatMessage(String playerName, String message) {
        try {
            if (playerName == null || playerName.equals("all")) {
                sendToAllClients(NetworkUtils.formatMessage(PLAYER_CHAT, message));
            } else {
                PlayerSocket player = getPlayerByName(playerName);
                if (player != null) {
                    player.sendData(NetworkUtils.formatMessage(PLAYER_CHAT, message));
                } else return "Player not found: " + playerName;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void sendPlayerPosition(Vector4f orientation) {
        byte[] x = ByteUtils.floatToBytes(orientation.x);
        byte[] y = ByteUtils.floatToBytes(orientation.y);
        byte[] z = ByteUtils.floatToBytes(orientation.z);
        byte[] w = ByteUtils.floatToBytes((float) (orientation.w + Math.PI));

        byte[] b = {PLAYER_POSITION,
                x[0], x[1], x[2], x[3],
                y[0], y[1], y[2], y[3],
                z[0], z[1], z[2], z[3],
                w[0], w[1], w[2], w[3]
        };

        try {
            sendToAllClients(b);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
