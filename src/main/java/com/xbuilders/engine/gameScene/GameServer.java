package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.player.pipeline.BlockEventPipeline;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.ui.topMenu.NetworkJoinRequest;
import com.xbuilders.engine.utils.ArrayUtils;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.network.server.NetworkSocket;
import com.xbuilders.engine.utils.network.server.NetworkUtils;
import com.xbuilders.engine.utils.network.server.Server;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;
import org.joml.Vector3i;
import org.joml.Vector4f;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import static com.xbuilders.engine.utils.MiscUtils.formatTime;

public class GameServer extends Server<PlayerSocket> {

    public static final byte PLAYER_INFO = -128;
    public static final byte WORLD_INFO = -127;
    public static final byte PLAYER_POSITION = -126;
    public static final byte PLAYER_CHAT = -125;
    public static final byte WORLD_CHUNK = -124;
    public static final byte READY_TO_START = -123;
    public static final byte VOXEL_BLOCK_CHANGE = -122;

    NetworkJoinRequest req;
    UserControlledPlayer player;
    private WorldInfo worldInfo;
    public int loadedChunks = 0;
    boolean worldReady = false;

    public GameServer(UserControlledPlayer player) {
        super(PlayerSocket::new);
        this.player = player;
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
            NetworkSocket newClient = connectToServer(new InetSocketAddress(req.hostIpAdress, req.toPortVal));
            Thread.sleep(1000);
            newClient.sendData(player.infoToBytes());
        }
    }

    public WorldInfo getWorldInfo() {
        if (!worldReady) return null;
        return worldInfo;
    }

    public void closeGame() throws IOException {
        super.close();
        worldInfo = null;
        worldReady = false;
    }

    @Override
    public boolean newClientEvent(PlayerSocket client) {
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
                    System.out.println("Received chat: " + new String(receivedData));
                    String message = new String(NetworkUtils.getMessage(receivedData));
                    String playerName = client.player == null ? "Unknown" : client.player.name;
                    GameScene.consoleOut(playerName + ":  \"" + message + "\"");
                } else if (receivedData[0] == PLAYER_POSITION) {
                    float x = ByteUtils.bytesToFloat(new byte[]{receivedData[1], receivedData[2], receivedData[3], receivedData[4]});
                    float y = ByteUtils.bytesToFloat(new byte[]{receivedData[5], receivedData[6], receivedData[7], receivedData[8]});
                    float z = ByteUtils.bytesToFloat(new byte[]{receivedData[9], receivedData[10], receivedData[11], receivedData[12]});
                    float w = ByteUtils.bytesToFloat(new byte[]{receivedData[13], receivedData[14], receivedData[15], receivedData[16]});
//                    System.out.println("Player position: " + x + " " + y + " " + z + " " + w);
                    client.player.worldPosition.set(x, y, z);
                    client.player.pan = (w);
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
                    readBlockChange(receivedData, (pos, blockHist) -> {
//                        System.out.println("Received block change: " + pos + " " + blockHist.currentBlock + " " + blockHist.previousBlock);
                        GameScene.player.eventPipeline.addEvent(pos, blockHist);
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

        //Make a unique name
        int indx = 1;
        String originalName = name;
        while (true) {
            File existingWorld = WorldsHandler.worldFile(name);
            if (existingWorld.exists()) {
                indx++;
                name = originalName + " (" + indx + ")";
            } else break;
        }

        hostWorld.makeNew(name, json);
        WorldsHandler.makeNewWorld(hostWorld);
        worldInfo = hostWorld;
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
        if (client.player != null) {
            GameScene.consoleOut(client.player.name + " has left");
        } else {
            GameScene.consoleOut("Unknown player has left");
        }
    }

    private void playerJoinEvent(PlayerSocket client) {
        GameScene.alert("A new player has joined: " + client.player.toString());
    }

    public String sendChatMessage(String playerName, String message) {
        try {
            PlayerSocket player = getPlayerByName(playerName);
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


    public void readBlockChange(byte[] receivedData, BiConsumer<Vector3i, BlockHistory> newEvent) {
        //Split the recievedData by the newline byte
        int i = 0;
        while (i < receivedData.length) {
            if (receivedData[i] == VOXEL_BLOCK_CHANGE) {
                int x = ByteUtils.bytesToInt(receivedData[i + 1], receivedData[i + 2], receivedData[i + 3], receivedData[i + 4]);
                int y = ByteUtils.bytesToInt(receivedData[i + 5], receivedData[i + 6], receivedData[i + 7], receivedData[i + 8]);
                int z = ByteUtils.bytesToInt(receivedData[i + 9], receivedData[i + 10], receivedData[i + 11], receivedData[i + 12]);
                int blockID = ByteUtils.bytesToShort(receivedData[i + 13], receivedData[i + 14]);
                i += 15;
                List<Byte> data = new ArrayList<>();
                while (i < receivedData.length && receivedData[i] != VOXEL_BLOCK_CHANGE) {
                    data.add(receivedData[i]);
                    i++;
                }

                BlockHistory blockHistory = new BlockHistory();
                blockHistory.currentBlock = ItemList.getBlock((short) blockID);
                blockHistory.isFromMultiplayer = true;
                blockHistory.data = new BlockData(data);

                Vector3i position = new Vector3i(x, y, z);
                newEvent.accept(position, blockHistory);

            }
        }
    }

    public void sendBlockChange(Vector3i worldPos, Block block, BlockData data) {
        try {
//            System.out.println("\n\nSending block change: " + MiscUtils.printVector(worldPos) + " \t" + block + " \t" + data);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();//This is like an arraylist in that it can grow but makes new byte arrays to resize itself or get byte array

            byte[] x = ByteUtils.intToBytes(worldPos.x);
            byte[] y = ByteUtils.intToBytes(worldPos.y);
            byte[] z = ByteUtils.intToBytes(worldPos.z);
            byte[] b = ByteUtils.shortToBytes(block.id);
            byte[] listA = new byte[]{
                    VOXEL_BLOCK_CHANGE,
                    x[0], x[1], x[2], x[3],
                    y[0], y[1], y[2], y[3],
                    z[0], z[1], z[2], z[3],
                    b[0], b[1]
            };
            byte[] listB = data == null ? new byte[0] : data.toByteArray();


            //Create a new array with listA and blockData combined, using system.arraycopy
            byte[] byteList = new byte[listA.length + listB.length];
            System.arraycopy(
                    listA, 0, //Where to start reading from source
                    byteList, 0, //where to copy at Destination
                    listA.length);//# of elements to copy from source
            System.arraycopy(
                    listB, 0, //Where to start reading from source
                    byteList, listA.length,//where to copy at Destination
                    listB.length);//# of elements to copy from source

//            System.out.println("Bytes of block change: " + Arrays.toString(byteList));
//
//            readBlockChange(byteList, (pos, history) -> {
//                System.out.println("Player position:   " +
//                        "    world:  " + MiscUtils.printVector(pos)
//                        + "   history: " + history);
//            });

            //Merge bData and data to

            for (int i = 0; i < clients.size(); i++) {
                PlayerSocket client = clients.get(i);
                client.sendData(byteList);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
