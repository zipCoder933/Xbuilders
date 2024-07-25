package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.ui.topMenu.NetworkJoinRequest;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.network.server.NetworkUtils;
import com.xbuilders.engine.utils.network.server.Server;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;
import com.xbuilders.game.Main;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Arrays;

import static com.xbuilders.engine.utils.MiscUtils.formatTime;

public class GameServer extends Server<PlayerClient> {

    public static final byte PLAYER_INFO = -128;
    public static final byte WORLD_INFO = -127;
    public static final byte PLAYER_POSITION = -126;
    public static final byte PLAYER_CHAT = -125;
    public static final byte WORLD_CHUNK = -124;
    public static final byte READY_TO_START = -123;
    public static final byte VOXEL_BLOCK_CHANGE = -122;
    public static final byte ENTITY_CREATED = -121;
    public static final byte ENTITY_DELETED = -120;
    public static final byte ENTITY_UPDATED = -119;
    public static final byte PLAYER_CHUNK_DISTANCE = -118;

    NetworkJoinRequest req;
    UserControlledPlayer userPlayer;
    private WorldInfo worldInfo;
    public int loadedChunks = 0;
    boolean worldReady = false;
    PlayerClient hostClient;

    public GameServer(UserControlledPlayer player) {
        super(PlayerClient::new);
        this.userPlayer = player;
    }

    public void updatePlayers(Matrix4f projection, Matrix4f view) {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).update(userPlayer, projection, view);
        }

    }

    /**
     * @param worldInfo the world info if we are hosting the game
     * @param req       the network join request
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public void startGame(WorldInfo worldInfo, NetworkJoinRequest req) throws IOException, InterruptedException {
        this.req = req;
        loadedChunks = 0;
        this.worldInfo = worldInfo;
        worldReady = false;
        start(req.fromPortVal);

        if (req.hosting) {
            worldReady = true;
        } else {
            /**
             * We cant send our information until the host has accepted us and started listening for messages.
             * To get around this, we need to either wait, or only send our information when the host sends a welcome message
             */
            //Join the host
            System.out.println("Joining as " + req.hostIpAdress);
            hostClient = connectToServer(new InetSocketAddress(req.hostIpAdress, req.toPortVal));
            hostClient.isHost = true;
            Thread.sleep(1000);
            hostClient.sendData(userPlayer.infoToBytes());
        }
    }


    public WorldInfo getWorldInfo() {
        if (!worldReady) return null;
        return worldInfo;
    }

    public void closeGame() throws IOException {
        onLeaveEvent();
        super.close();
        worldInfo = null;
        worldReady = false;
    }

    @Override
    public boolean newClientEvent(PlayerClient client) {
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
                    client.sendData(userPlayer.infoToBytes());
                    System.out.println(GameScene.world.info.getName() + "\n" + GameScene.world.info.toJson());

                    //Send the world info
                    client.sendData(NetworkUtils.formatMessage(WORLD_INFO,
                            GameScene.world.info.getName() + "\n" + GameScene.world.info.toJson()));

                    new Thread(() -> {  //Load every file of the chunk
                        try {
                            System.out.println("Loading chunks from " + worldInfo.getDirectory().getAbsolutePath());
                            for (File f : worldInfo.getDirectory().listFiles()) {
                                Vector3i coordinates = worldInfo.getPositionOfChunkFile(f);
                                if (coordinates != null) {
                                    long lastSaved = ChunkSavingLoadingUtils.getLastSaved(f);

                                    System.out.println("Chunk " + coordinates.x + ", " + coordinates.y + ", " + coordinates.z
                                            + " Last saved: " + formatTime(lastSaved));

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    baos.write(WORLD_CHUNK);
                                    ByteUtils.writeInt(baos, coordinates.x);
                                    ByteUtils.writeInt(baos, coordinates.y);
                                    ByteUtils.writeInt(baos, coordinates.z);
                                    baos.write(Files.readAllBytes(f.toPath()));
                                    baos.flush();
                                    client.sendData(baos.toByteArray());

                                }
                            }
                            client.sendData(READY_TO_START);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();


                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
    }

    private void printDatafromClient(PlayerClient client, byte[] receivedData) {
        String playerName = client.getPlayer() == null ? "Unknown" : client.getPlayer().name;
        try {
            GameScene.consoleOut(playerName + ":  (L" + receivedData.length + ") " + new String(receivedData));
        } catch (Exception e) {
            GameScene.consoleOut(playerName + ":  (L" + receivedData.length + ") " + Arrays.toString(receivedData));
        }
    }

    @Override
    public void dataFromClientEvent(PlayerClient client, byte[] receivedData) {
        try {
//            printDatafromClient(client, receivedData);

            if (receivedData.length > 0) {
                if (receivedData[0] == PLAYER_INFO) {
                    Player player = new Player();
                    player.loadInfoFromBytes(receivedData);
                    client.setPlayer((player));
                    playerJoinEvent(client);
                    client.sendData(NetworkUtils.formatMessage(PLAYER_CHAT, "Welcome \"" + player.name + "\"!"));
                } else if (receivedData[0] == PLAYER_CHAT) {
                    String message = new String(NetworkUtils.getMessage(receivedData));
                    String playerName = client.getPlayer() == null ? "Unknown" : client.getPlayer().name;
                    GameScene.consoleOut(playerName + ":  \"" + message + "\"");
                } else if (receivedData[0] == PLAYER_POSITION) {
                    float x = ByteUtils.bytesToFloat(new byte[]{receivedData[1], receivedData[2], receivedData[3], receivedData[4]});
                    float y = ByteUtils.bytesToFloat(new byte[]{receivedData[5], receivedData[6], receivedData[7], receivedData[8]});
                    float z = ByteUtils.bytesToFloat(new byte[]{receivedData[9], receivedData[10], receivedData[11], receivedData[12]});
                    float w = ByteUtils.bytesToFloat(new byte[]{receivedData[13], receivedData[14], receivedData[15], receivedData[16]});
//                    System.out.println("Player position: " + x + " " + y + " " + z + " " + w);
                    client.getPlayer().worldPosition.set(x, y, z);
                    client.getPlayer().pan = (w);
                } else if (receivedData[0] == PLAYER_CHUNK_DISTANCE) {
                    //So far this feature is useless
//                    client.playerChunkDistance = ByteUtils.bytesToInt(receivedData[1], receivedData[2], receivedData[3], receivedData[4]);
//                    System.out.println("Player " + client.getName() + " chunk distance: " + client.playerChunkDistance);
                }

                //New world
                else if (receivedData[0] == READY_TO_START) {
                    worldReady = true;
                } else if (receivedData[0] == WORLD_CHUNK) {
                    int x = ByteUtils.bytesToInt(receivedData[1], receivedData[2], receivedData[3], receivedData[4]);
                    int y = ByteUtils.bytesToInt(receivedData[5], receivedData[6], receivedData[7], receivedData[8]);
                    int z = ByteUtils.bytesToInt(receivedData[9], receivedData[10], receivedData[11], receivedData[12]);
                    File chunkFile = worldInfo.getChunkFile(new Vector3i(x, y, z));
                    //Write the rest of the bytes to a file
                    Files.write(chunkFile.toPath(), Arrays.copyOfRange(receivedData, 13, receivedData.length));
                    loadedChunks++;
                    System.out.println("Received chunk " + x + ", " + y + ", " + z);
                } else if (receivedData[0] == WORLD_INFO) {//Make/load the world info
                    worldInfoEvent(receivedData);
                } else if (receivedData[0] == VOXEL_BLOCK_CHANGE) {
                    PendingMultiplayerChanges.readBlockChange(receivedData, (pos, blockHist) -> {
                        if (PendingMultiplayerChanges.changeWithinReach(userPlayer, pos)) {
                            GameScene.player.eventPipeline.addEvent(pos, blockHist);
                        } else {//Cache changes if they are out of bounds
                            GameScene.player.eventPipeline.pendingLocalChanges.addBlockChange(pos, blockHist);
                        }
                    });
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void worldInfoEvent(byte[] receivedData) throws IOException {
        String value = new String(NetworkUtils.getMessage(receivedData));
        String name = value.split("\n")[0];
        String json = value.split("\n")[1];
        WorldInfo hostWorld = new WorldInfo();

        //Make a unique name, but join the existing one if it exists
        int indx = 1;
        String originalName = name + " (joined)";
        name = originalName;
        while (true) {
            File existingWorld = WorldsHandler.worldFile(name);

            if (existingWorld.exists()) {
                WorldInfo i = new WorldInfo();
                i.load(existingWorld);
                if (i.infoFile.isJoinedMultiplayerWorld) break;
            } else break;

            indx++;
            name = originalName + " (" + indx + ")";
        }
        System.out.println("Making new world: " + name);

        hostWorld.makeNew(name, json);
        if (!req.hosting) {
            hostWorld.infoFile.isJoinedMultiplayerWorld = true;
        }
        WorldsHandler.makeNewWorld(hostWorld);
        worldInfo = hostWorld;
    }

    public PlayerClient getPlayerByName(String name) {
        for (PlayerClient client : clients) {
            if (client.getPlayer() != null && client.getPlayer().name.equalsIgnoreCase(name)) {
                return client;
            }
        }
        return null;
    }

    public void updateChunkDistance(int distance) {
        byte[] distInt = ByteUtils.intToBytes((int) distance);
        try {
            sendToAllClients(new byte[]{PLAYER_CHUNK_DISTANCE, distInt[0], distInt[1], distInt[2], distInt[3]});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClientDisconnect(PlayerClient client) {
        if (client.getPlayer() != null) {
            GameScene.consoleOut(client.getName() + " has left");
        } else {
            GameScene.consoleOut("Unknown player has left");
        }
        if (client.isHost) {
            onLeaveEvent();
            Main.goToMenuPage();
            Main.topMenu.popupMessage.message(
                    "Host has left",
                    "The host has left the game");
        }
    }

    private void onLeaveEvent() {
        for (PlayerClient client : clients) {//Send all changes before we leave
            client.blockChanges.sendAllChanges();
        }
    }

    private void playerJoinEvent(PlayerClient client) {
        //Initial information that is important can be sent to new players
        updateChunkDistance(GameScene.world.getViewDistance());
        GameScene.alert("A new player has joined: " + client.getPlayer().toString());
    }

    public String sendChatMessage(String playerName, String message) {
        try {
            PlayerClient player = getPlayerByName(playerName);
            byte[] data = new byte[message.length() + 1];
            data[0] = PLAYER_CHAT;
            System.arraycopy(message.getBytes(), 0, data, 1, message.length());

            if (playerName.toLowerCase().trim().equals("all")) {
                sendToAllClients(data);
            } else if (player == null) {
                return "Player \"" + playerName + "\" not found";
            } else {
                player.sendData(data);
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendBlockAllChanges() {
        for (PlayerClient client : clients) {
            client.blockChanges.sendNearBlockChanges();
        }
    }

    public void addBlockChange(Vector3i worldPos, Block block, BlockData data) {
        for (PlayerClient client : clients) {
            client.blockChanges.addBlockChange(worldPos, block, data);
        }
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
