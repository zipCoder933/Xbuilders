package com.xbuilders.engine.multiplayer;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.network.server.NetworkUtils;
import com.xbuilders.engine.utils.network.server.Server;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;
import com.xbuilders.engine.world.wcc.WCCf;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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

    //All server message headers
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
    public static final byte SET_TIME = -117;
    public static final byte PLEASE_CONNECT_TO_CLIENT = -116;

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

    public boolean isHosting() {
        return req != null && req.hosting;
    }

    public boolean isPlayingMultiplayer() {
        return !clients.isEmpty();
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
        try {
            if (clientAlreadyJoined(client)) {
                System.out.println(client.getRemoteSocketAddress().toString() + " has already joined");
                return false;
            } else {
                client.sendData(userPlayer.infoToBytes());
                if (req.hosting) {
                    sendWorldToClient(client);
                }
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendWorldToClient(PlayerClient client) throws IOException {
        //Send the world info to the client
        System.out.println("Sending world to client: " + GameScene.world.info.getName() + "\n" + GameScene.world.info.toJson());
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

    @Override
    public void dataFromClientEvent(PlayerClient client, byte[] receivedData) {
        try {
            if (receivedData.length == 0) return;

            if (receivedData[0] == PLAYER_INFO) { //Given when the player updates his info
                //Update player information first
                client.player.loadInfoFromBytes(receivedData);
                //If the player has not joined yet, add him
                if (!client.player.isKnown) {
                    client.player.isKnown = true;
                    if (isHosting()) { //If we are the host, ask other players to connect too
                        sendChatMessage(client, "Welcome \"" + client.player.name + "\"!");

                        byte[] joinMessage = NetworkUtils.formatMessage(PLEASE_CONNECT_TO_CLIENT,
                                client.getRemoteSocketAddress().getHostName()
                                        + ":" + client.getRemoteSocketAddress().getPort());
                        for (PlayerClient otherClient : clients) {
                            if (!otherClient.equals(client)) otherClient.sendData(joinMessage);
                        }
                    }
                    playerJoinEvent(client);
                }
            } else if (receivedData[0] == PLEASE_CONNECT_TO_CLIENT) {
                String[] adress = new String(receivedData).substring(1).split(":");
                int port = Integer.parseInt(adress[1]);
                System.out.println("Trying to connect to fellow client: " + Arrays.toString(adress));
                connectToServer(new InetSocketAddress(adress[0], port));

            } else if (receivedData[0] == PLAYER_CHAT) {
                String message = new String(NetworkUtils.getMessage(receivedData));
                String playerName = client.player.name;
                GameScene.consoleOut(playerName + ":  \"" + message + "\"");
            } else {
                if (receivedData[0] == PLAYER_POSITION) {
                    float x = ByteUtils.bytesToFloat(receivedData[1], receivedData[2], receivedData[3], receivedData[4]);
                    float y = ByteUtils.bytesToFloat(receivedData[5], receivedData[6], receivedData[7], receivedData[8]);
                    float z = ByteUtils.bytesToFloat(receivedData[9], receivedData[10], receivedData[11], receivedData[12]);
                    float pan = ByteUtils.bytesToFloat(receivedData[13], receivedData[14], receivedData[15], receivedData[16]);
                    client.player.worldPosition.set(x, y, z);
                    client.player.pan = (pan);
                } else if (receivedData[0] == VOXEL_BLOCK_CHANGE) {
                    MultiplayerPendingBlockChanges.readBlockChange(receivedData, (pos, blockHist) -> {
                        if (MultiplayerPendingBlockChanges.changeCanBeLoaded(userPlayer, pos)) {
                            GameScene.player.eventPipeline.addEvent(pos, blockHist);
                        } else {//Cache changes if they are out of bounds
                            GameScene.world.multiplayerPendingBlockChanges.addBlockChange(pos, blockHist);
                        }
                    });
                } else if (receivedData[0] == ENTITY_CREATED || receivedData[0] == ENTITY_DELETED || receivedData[0] == ENTITY_UPDATED) {
                    MultiplayerPendingEntityChanges.readEntityChange(receivedData, (
                            mode, entity, identifier, currentPos, data, isControlledByAnotherPlayer) -> {
                        //                        printEntityChange(mode, entity, identifier, currentPos, data);

                        if (MultiplayerPendingEntityChanges.changeWithinReach(userPlayer, currentPos)) {
                            if (mode == ENTITY_CREATED) {
                                setEntity(entity, identifier, currentPos, data);
                            } else if (mode == ENTITY_DELETED) {
                                Entity e = GameScene.world.entities.get(identifier);
                                if (e != null) {
                                    e.destroy();
                                }
                            } else if (mode == ENTITY_UPDATED) {
                                Entity e = GameScene.world.entities.get(identifier);
                                if (e != null) {
                                    e.multiplayerProps.updateState(data, currentPos, isControlledByAnotherPlayer);
                                }
                            }
                        } else {//Cache changes if they are out of bounds
                            GameScene.localEntityChanges.addEntityChange(mode, entity, identifier, currentPos, data);
                        }
                    });
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
                    getWorldInformationFromHost(receivedData);
                } else if (receivedData[0] == SET_TIME) {
                    GameScene.background.setTimeOfDay(ByteUtils.bytesToFloat(receivedData[1], receivedData[2], receivedData[3], receivedData[4]));
                }
            }

        } catch (Exception e) {
            ErrorHandler.report(e);
        }
    }

    private void printEntityChange(int mode, EntityLink entity, long identifier, Vector3f currentPos, byte[] data) {
        String modeStr;
        switch (mode) {
            case ENTITY_CREATED -> modeStr = "CREATED";
            case ENTITY_DELETED -> modeStr = "DELETED";
            case ENTITY_UPDATED -> modeStr = "UPDATED";
            default -> modeStr = "UNKNOWN";
        }
        MainWindow.printlnDev("RECEIVED (" + modeStr + ") " + entity +
                ", id=" + Long.toHexString(identifier) +
                ", pos=" + MiscUtils.printVector(currentPos) +
                ", data=" + Arrays.toString(data));
    }

    public Entity setEntity(EntityLink entity, long identifier, Vector3f worldPosition, byte[] data) {
        WCCf wcc = new WCCf();
        wcc.set(worldPosition);
        Chunk chunk = GameScene.world.chunks.get(wcc.chunk);
        if (chunk != null) {
            chunk.markAsModifiedByUser();
            return chunk.entities.placeNew(worldPosition, identifier, entity, data);
        }
        return null;
    }


    private void getWorldInformationFromHost(byte[] receivedData) throws IOException {
        String value = new String(NetworkUtils.getMessage(receivedData));
        String name = value.split("\n")[0];
        String json = value.split("\n")[1];

        WorldInfo hostsWorldInfo = new WorldInfo();
        hostsWorldInfo.makeNew(name, json);
        if (!req.hosting) {
            hostsWorldInfo.infoFile.isJoinedMultiplayerWorld = true;
        }


        //If an already existing world that is NOT the joined world, don't make a new one
        int index = 0;
        while (hasDifferentWorldUnderSameName(hostsWorldInfo)) {
            String newName = name + " " + index;
            index++;
            System.out.println("World already exists under \"" + hostsWorldInfo.getName() + "\"! New world name: " + newName);
            hostsWorldInfo.makeNew(newName, json);
        }


        //Make the world from the host
        hostsWorldInfo.infoFile.isJoinedMultiplayerWorld = true;
        WorldsHandler.makeNewWorld(hostsWorldInfo);
        worldInfo = hostsWorldInfo;
    }

    private boolean hasDifferentWorldUnderSameName(WorldInfo hostWorld) throws IOException {
        File existingWorld = WorldsHandler.worldFile(hostWorld.getName());
        if (existingWorld.exists()) {
            WorldInfo myWorld = new WorldInfo();
            myWorld.load(existingWorld);
            return !myWorld.infoFile.isJoinedMultiplayerWorld
                    || !hostWorld.getTerrain().equals(myWorld.getTerrain())
                    || hostWorld.getSeed() != myWorld.getSeed();
        }
        return false;
    }

    public PlayerClient getPlayerByName(String name) {
        for (PlayerClient client : clients) {
            if (client.player != null) {
                if (client.player.name.equalsIgnoreCase(name)) {
                    return client;
                }
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
        if (client.player != null) {
            GameScene.consoleOut(client.getName() + " has left");
        } else {
            GameScene.consoleOut("Unknown player has left");
        }
        if (client.isHost) {
            onLeaveEvent();
            MainWindow.goToMenuPage();
            MainWindow.popupMessage.message(
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
        GameScene.alert("A new player has joined: " + client.player.toString());
    }

    public String sendChatMessage(PlayerClient player, String message) {
        try {
            byte[] data = new byte[message.length() + 1];
            data[0] = PLAYER_CHAT;
            System.arraycopy(message.getBytes(), 0, data, 1, message.length());
            player.sendData(data);
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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


    public void sendNearBlockChanges() {
        for (PlayerClient client : clients) {
            if (client.player == null) continue;
            client.blockChanges.sendNearBlockChanges();
        }
    }

    public void addBlockChange(Vector3i worldPos, Block block, BlockData data) {
        for (PlayerClient client : clients) {
            client.blockChanges.addBlockChange(worldPos, block, data);
        }
    }

    public void addEntityChange(Entity entity, byte mode, boolean sendImmediately) {
        for (PlayerClient client : clients) {
            client.entityChanges.addEntityChange(entity, mode, sendImmediately);
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
