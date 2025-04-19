package com.xbuilders.engine.server.multiplayer;

import com.esotericsoftware.kryo.io.Input;
import com.xbuilders.engine.server.Difficulty;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.entity.EntitySupplier;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.utils.bytes.ByteUtils;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.network.server.NetworkUtils;
import com.xbuilders.engine.server.world.data.WorldData;
import com.xbuilders.engine.server.world.WorldsHandler;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.chunk.saving.ChunkSavingLoadingUtils;
import com.xbuilders.engine.server.world.wcc.WCCf;
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
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.utils.MiscUtils.formatTime;

public class GameServer extends com.xbuilders.engine.utils.network.server.Server<Player> {

    //All localServer message headers
    public static final byte PLAYER_INFO = -128;
    public static final byte WORLD_INFO = -127;
    public static final byte PLAYER_POSITION = -126;
    public static final byte PLAYER_CHAT = -125;
    public static final byte WORLD_CHUNK = -124;
    public static final byte READY_TO_START = -123;
    public static final byte VOXELS_UPDATED = -122;
    public static final byte ENTITY_CREATED = -121;
    public static final byte ENTITY_DELETED = -120;
    public static final byte ENTITY_UPDATED = -119;
    public static final byte SET_TIME = -117;
    public static final byte PLEASE_CONNECT_TO_CLIENT = -116;
    public static final byte CHANGE_GAME_MODE = -115;
    public static final byte CHANGE_PLAYER_PERMISSION = -114;
    public static final byte WORLD_CHUNK_LAST_SAVED = -113;
    public static final byte CHANGE_DIFFICULTY = -112;

    LocalServer scene;
    NetworkJoinRequest req;
    UserControlledPlayer client_userPlayer;
    private WorldData worldInfo;
    public int loadedChunks = 0;
    boolean worldReady = false;
    Player hostClient;

    public GameServer(LocalServer scene, UserControlledPlayer player) {
        super(Player::new);
        this.client_userPlayer = player;
        this.scene = scene;
    }

    private boolean isHosting() {
        return req != null && req.hosting;
    }

    public boolean isPlayingMultiplayer() {
        return !clients.isEmpty();
    }

    public void updatePlayers() {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).update(client_userPlayer);
        }
    }

    public void drawPlayers(Matrix4f projection, Matrix4f view) {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).drawPlayer(projection, view);
        }
    }

    public void sendAllChangesToClients() {
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).sendAllChanges();
        }
    }


    public void initNewGame(WorldData worldInfo, NetworkJoinRequest req) {
        this.req = req;
        GameScene.userPlayer.isHost = req.hosting;
        loadedChunks = 0;
        this.worldInfo = worldInfo;
        worldReady = false;
    }

    public void startJoiningWorld() throws IOException, InterruptedException {
        if (req != null && !req.hosting) {
            start(req.fromPortVal); //Start the localServer
            /**
             * We cant send our information until the host has accepted us and started listening for messages.
             * To get around this, we need to either wait, or only send our information when the host sends a welcome message
             */
            //Join the host
            System.out.println("Joining as " + req.hostIpAdress);
            hostClient = connectToServer(new InetSocketAddress(req.hostIpAdress, req.toPortVal));
            hostClient.isHost = true;
            Thread.sleep(1000);
            hostClient.sendData(client_userPlayer.userInfo.toBytes());
        }
    }

    public void startHostingWorld() throws IOException {
        if (req != null && req.hosting) {
            start(req.fromPortVal); //Start the localServer
            worldReady = true;
        }
    }


    public WorldData getWorldInfo() {
        if (!worldReady) return null;
        return worldInfo;
    }

    public void stopGameEvent() throws IOException {
        sendAllChangesToClients();
        onLeaveEvent();
        super.close();
        worldInfo = null;
        worldReady = false;
    }

    @Override
    public boolean newClientEvent(Player client) {
        try {
            if (clientAlreadyJoined(client)) {
                System.out.println(client.getRemoteSocketAddress().toString() + " has already joined");
                return false;
            } else {
                client.sendData(client_userPlayer.userInfo.toBytes());
                if (req.hosting) {
                    sendWorldToClient(client);
                }
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendWorldToClient(Player client) throws IOException {
        //Save the world first to ensure that all changes are on the disk
        LocalServer.world.save();

        //Send the world info to the client
        System.out.println("Sending world to client: " + LocalServer.world.data.getName() + "\n" + LocalServer.world.data.toJson());
        client.sendData(NetworkUtils.formatMessage(WORLD_INFO,
                LocalServer.world.data.getName() + "\n" + LocalServer.world.data.toJson()));

        new Thread(() -> {  //Load every file of the chunk
            try {
                System.out.println("Loading chunks from " + worldInfo.getDirectory().getAbsolutePath());
                for (File f : worldInfo.getDirectory().listFiles()) {
                    Vector3i coordinates = worldInfo.getPositionOfChunkFile(f);
                    if (coordinates != null) {
                        if (!ChunkSavingLoadingUtils.fileIsComplete(f)) {//If the file is incomplete
                            if (ChunkSavingLoadingUtils.backupFile(f).exists()) { //If a backup exists, load from that
                                ErrorHandler.report("Loading from backup",
                                        "chunk " + f.getAbsolutePath() + " is corrupt!");
                                f = ChunkSavingLoadingUtils.backupFile(f); //Load from backup
                            } else {
                                ErrorHandler.report("Chunk is corrupt",
                                        "chunk " + f.getAbsolutePath() + " is corrupt and had no backup!");
                            }
                        }
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

    public void clientDisconnectEvent(Player client) {
        scene.playerLeaveEvent(client);
    }

    @Override
    public void dataFromClientEvent(Player client, byte[] receivedData) {
        try {
            if (receivedData.length == 0) return;

            if (receivedData[0] == PLAYER_INFO) { //Given when the player updates his info
                //Update player information first
                client.userInfo.fromBytes(receivedData);
                //If the player has not joined yet, add him
                if (!client.isKnown) {
                    client.isKnown = true;
                    if (isHosting()) { //If we are the host, ask other players to connect too
                        sendChatMessage(client, "Welcome \"" + client.userInfo.name + "\"!");
                        byte[] joinMessage = NetworkUtils.formatMessage(PLEASE_CONNECT_TO_CLIENT,
                                client.getRemoteSocketAddress().getHostName()
                                        + ":" + client.getRemoteSocketAddress().getPort());
                        for (Player otherClient : clients) {
                            if (!otherClient.equals(client)) otherClient.sendData(joinMessage);
                        }
                    }
                    scene.playerJoinEvent(client);
                }
            } else if (receivedData[0] == PLEASE_CONNECT_TO_CLIENT) {
                String[] adress = new String(receivedData).substring(1).split(":");
                int port = Integer.parseInt(adress[1]);
                System.out.println("Trying to connect to fellow client: " + Arrays.toString(adress));
                connectToServer(new InetSocketAddress(adress[0], port));

            } else if (receivedData[0] == PLAYER_CHAT) {
                String message = new String(NetworkUtils.getMessage(receivedData));
                String playerName = client.userInfo.name;
                LocalServer.consoleOut(playerName + ":  \"" + message + "\"");
            } else if (receivedData[0] == PLAYER_POSITION) {
                float x = ByteUtils.bytesToFloat(receivedData[1], receivedData[2], receivedData[3], receivedData[4]);
                float y = ByteUtils.bytesToFloat(receivedData[5], receivedData[6], receivedData[7], receivedData[8]);
                float z = ByteUtils.bytesToFloat(receivedData[9], receivedData[10], receivedData[11], receivedData[12]);
                float pan = ByteUtils.bytesToFloat(receivedData[13], receivedData[14], receivedData[15], receivedData[16]);
                client.worldPosition.set(x, y, z);
                client.pan = (pan);
            } else if (receivedData[0] == VOXELS_UPDATED) {
                final AtomicInteger inReachChanges = new AtomicInteger(0);
                final AtomicInteger outOfReachChanges = new AtomicInteger(0);

                MultiplayerPendingBlockChanges.readBlockChange(receivedData, (pos, blockHist) -> {
                    if (MultiplayerPendingBlockChanges.changeCanBeLoaded(client_userPlayer, pos)) {//If change is within reach
                        LocalServer.eventPipeline.addEvent(pos, blockHist);
                        inReachChanges.incrementAndGet();
                    } else {//Cache changes if they are out of bounds
                        LocalServer.world.multiplayerPendingBlockChanges.addBlockChange(pos, blockHist);
                        outOfReachChanges.incrementAndGet();
                    }
                });

//                MainWindow.printlnDev("Voxels updated event triggered\t In reach: " + inReachChanges.get() + "\tOut of reach: " + outOfReachChanges.get());

            } else if (receivedData[0] == ENTITY_CREATED || receivedData[0] == ENTITY_DELETED || receivedData[0] == ENTITY_UPDATED) {
                MultiplayerPendingEntityChanges.readEntityChange(new Input(receivedData), (
                        mode, entity, identifier, currentPos, data, isControlledByAnotherPlayer) -> {
                    //printEntityChange(client, mode, entity, identifier, currentPos, data);
                    if (MultiplayerPendingEntityChanges.changeWithinReach(client_userPlayer, currentPos)) {
                        if (mode == ENTITY_CREATED) {
                            setEntity(entity, identifier, currentPos, data);
                        } else if (mode == ENTITY_DELETED) {
                            Entity e = LocalServer.world.entities.get(identifier);
                            if (e != null) {
                                e.destroy();
                            }
                        } else if (mode == ENTITY_UPDATED) {
                            Entity e = LocalServer.world.entities.get(identifier);
                            if (e != null) {
                                e.multiplayerProps.updateState(data, currentPos, isControlledByAnotherPlayer);
                            }
                        }
                    } else {//Cache changes if they are out of bounds
                        LocalServer.world.multiplayerPendingEntityChanges.addEntityChange(mode, entity, identifier, currentPos, data);
                    }
                });
            } else if (receivedData[0] == READY_TO_START) { //New world
                worldReady = true;
            } else if (receivedData[0] == WORLD_CHUNK_LAST_SAVED) {
                int x = ByteUtils.bytesToInt(receivedData[1], receivedData[2], receivedData[3], receivedData[4]);
                int y = ByteUtils.bytesToInt(receivedData[5], receivedData[6], receivedData[7], receivedData[8]);
                int z = ByteUtils.bytesToInt(receivedData[9], receivedData[10], receivedData[11], receivedData[12]);
                File chunkFile = worldInfo.getChunkFile(new Vector3i(x, y, z));
                long lastSaved = ChunkSavingLoadingUtils.getLastSaved(chunkFile);
                client.sendData(ByteUtils.longToBytes(lastSaved));
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
            } else if (receivedData[0] == CHANGE_GAME_MODE) {
                try {
                    int mode = receivedData[1];
                    GameMode gameMode = GameMode.values()[mode];
                    LocalServer.setGameMode(gameMode);
                } catch (ArrayIndexOutOfBoundsException e) {
                    LocalServer.alertClient("Unable to change game mode");
                }
            } else if (receivedData[0] == CHANGE_DIFFICULTY) {
                try {
                    int difficulty = receivedData[1];
                    Difficulty gameDifficulty = Difficulty.values()[difficulty];
                    LocalServer.setDifficulty(gameDifficulty);
                } catch (ArrayIndexOutOfBoundsException e) {
                    LocalServer.alertClient("Unable to change game difficulty");
                }
            } else if (receivedData[0] == CHANGE_PLAYER_PERMISSION) {
                try {
                    boolean permission = receivedData[1] == 1;
                    LocalServer.setOperator(permission);
                } catch (ArrayIndexOutOfBoundsException e) {
                    LocalServer.alertClient("Unable to change player permission");
                }
            }


        } catch (Exception e) {
            ErrorHandler.report(e);
        }
    }

    private void printEntityChange(Player client, int mode, EntitySupplier entity,
                                   long identifier, Vector3f currentPos,
                                   byte[] data) {
        if (!LocalClient.DEV_MODE) return;
        String modeStr;
        switch (mode) {
            case ENTITY_CREATED -> modeStr = "ENTITY CREATED";
            case ENTITY_DELETED -> modeStr = "ENTITY DELETED";
            case ENTITY_UPDATED -> modeStr = "ENTITY UPDATED";
            default -> modeStr = " UNKNOWN ENTITY COMMAND";
        }
        String str = client.getName() + ": (" + modeStr + ")" +
                ", entity= " + entity.toString() +
                ", id=" + Long.toHexString(identifier) +
                ", pos=" + MiscUtils.printVector(currentPos) +
                ", data=" + Arrays.toString(data);
        ClientWindow.printlnDev(str);
        if (LocalClient.DEV_MODE) LocalServer.alertClient(str);
    }

    public Entity setEntity(EntitySupplier entity, long identifier, Vector3f worldPosition, byte[] data) {
        WCCf wcc = new WCCf();
        wcc.set(worldPosition);
        Chunk chunk = LocalServer.world.chunks.get(wcc.chunk);
        if (chunk != null) {
            chunk.markAsModified();
            return chunk.entities.placeNew(worldPosition, identifier, entity, data);
        }
        return null;
    }


    private void getWorldInformationFromHost(byte[] receivedData) throws IOException {
        String value = new String(NetworkUtils.getMessage(receivedData));
        String name = value.split("\n")[0];
        String json = value.split("\n")[1];

        WorldData hostsWorldInfo = new WorldData();
        hostsWorldInfo.makeNew(name, json);
        if (!req.hosting) {
            hostsWorldInfo.data.isJoinedMultiplayerWorld = true;
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
        hostsWorldInfo.data.isJoinedMultiplayerWorld = true;
        WorldsHandler.makeNewWorld(hostsWorldInfo);
        worldInfo = hostsWorldInfo;
    }

    private boolean hasDifferentWorldUnderSameName(WorldData hostWorld) throws IOException {
        File existingWorld = WorldsHandler.worldFile(hostWorld.getName());
        if (existingWorld.exists()) {
            WorldData myWorld = new WorldData();
            myWorld.load(existingWorld);
            return !myWorld.data.isJoinedMultiplayerWorld
                    || !hostWorld.getTerrain().equals(myWorld.getTerrain())
                    || hostWorld.getSeed() != myWorld.getSeed();
        }
        return false;
    }

    public Player getPlayerByName(String name) {
        for (Player client : clients) {
            if (client.userInfo.name.equalsIgnoreCase(name)) {
                return client;
            }
        }
        return null;
    }


    private void onLeaveEvent() {
        for (Player client : clients) {//Send all changes before we leave
            client.model_blockChanges_ToBeSentToPlayer.sendAllChanges();
        }
    }


    public String sendChatMessage(Player player, String message) {
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
            Player player = getPlayerByName(playerName);
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
        for (Player client : clients) {
            client.model_blockChanges_ToBeSentToPlayer.sendNearBlockChanges();
        }
    }

    public void addBlockChange(Vector3i worldPos, Block block, BlockData data) {
        for (Player client : clients) {
            client.model_blockChanges_ToBeSentToPlayer.addBlockChange(worldPos, block, data);
        }
    }

    public void addEntityChange(Entity entity, byte mode, boolean sendImmediately) {
        for (Player client : clients) {
            client.model_entityChanges_ToBeSentToPlayer.addEntityChange(entity, mode, sendImmediately);
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
            e.printStackTrace();
        }
    }

}
