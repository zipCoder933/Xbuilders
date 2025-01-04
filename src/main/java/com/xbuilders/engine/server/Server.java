/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server;

import com.fasterxml.jackson.core.JsonGenerator;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.items.Registrys;
import com.xbuilders.engine.server.items.entity.Entity;
import com.xbuilders.engine.server.items.entity.EntityRegistry;
import com.xbuilders.engine.server.items.entity.EntitySupplier;
import com.xbuilders.engine.server.items.entity.ItemDrop;
import com.xbuilders.engine.server.items.item.ItemStack;
import com.xbuilders.engine.server.multiplayer.GameServer;
import com.xbuilders.engine.server.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.server.players.pipeline.BlockEventPipeline;
import com.xbuilders.engine.server.players.pipeline.BlockHistory;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.World;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.data.WorldData;
import com.xbuilders.engine.server.world.skybox.SkyBackground;
import com.xbuilders.engine.server.world.wcc.WCCf;
import com.xbuilders.engine.server.world.wcc.WCCi;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.json.JsonManager;
import com.xbuilders.engine.utils.progress.ProgressData;
import org.joml.Vector3f;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.server.players.Player.PLAYER_HEIGHT;

public class Server {

    public LivePropagationHandler livePropagationHandler;
    final boolean WAIT_FOR_ALL_CHUNKS_TO_LOAD_BEFORE_STARTING = true;
    public static final World world = new World();
    public static UserControlledPlayer userPlayer;
    public static GameServer server;
    static ClientWindow window;
    private static Game game;
    public static GameCommands commands;
    public static BlockEventPipeline eventPipeline;
    public static LogicThread tickThread;

    //Game Mode =======================================================================================================
    private static GameMode gameMode = GameMode.ADVENTURE;
    public static GameMode getGameMode() {
        return gameMode;
    }
    public static void setGameMode(GameMode gameMode) {
        Server.gameMode = gameMode;
    }


    //Permissions =======================================================================================================
    private static boolean isOperator;

    public static boolean isOperator() {
        return isOperator;
    }

    public static boolean setOperator(boolean isOperator2) {
        if (ownsGame()) return false;
        else {
            isOperator = isOperator2;
            alert("Operator privileges have been " + (isOperator ? "granted" : "revoked"));
        }
        return true;
    }

    public static boolean ownsGame() {
        return (server.isPlayingMultiplayer() && userPlayer.isHost) || !server.isPlayingMultiplayer();
    }


    public Server(ClientWindow window, Game myGame) throws Exception {
        game = myGame;
        this.window = window;
        userPlayer = new UserControlledPlayer(window, GameScene.projection, GameScene.view, GameScene.centeredView);
        server = new GameServer(this, userPlayer);
        livePropagationHandler = new LivePropagationHandler();
        eventPipeline = new BlockEventPipeline(world);
        tickThread = new LogicThread();
    }

    // Getters
    public static int getLightLevel(int worldX, int worldY, int worldZ) {
        WCCi wcc = new WCCi().set(worldX, worldY, worldZ);
        Chunk chunk = world.getChunk(wcc.chunk);
        if (chunk != null) {
            int sun = chunk.data.getSun(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
            int torch = chunk.data.getTorch(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
            sun = (int) Math.min(sun, background.getLightness() * 15);
            return Math.max(sun, torch);
        }
        return 0;
    }

    //Set block ===============================================================================
    public static void setBlock(short block, int worldX, int worldY, int worldZ) {
        setBlock(block, new WCCi().set(worldX, worldY, worldZ));
    }

    public static void setBlock(short block, BlockData data, int worldX, int worldY, int worldZ) {
        setBlock(block, data, new WCCi().set(worldX, worldY, worldZ));
    }

    public static void setBlockData(BlockData data, int worldX, int worldY, int worldZ) {
        setBlockData(data, new WCCi().set(worldX, worldY, worldZ));
    }

    public static void setBlock(short newBlock, BlockData blockData, WCCi wcc) {
        if (!World.inYBounds((wcc.chunk.y * Chunk.WIDTH) + wcc.chunkVoxel.y)) return;
        Chunk chunk = world.getChunk(wcc.chunk);
        if (chunk != null) {
            //Get the previous block
            short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);

            //we need to set the block because some algorithms want to check to see if the block has changed immediately
            chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, newBlock); //Important
            chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockData); //Important

            BlockHistory history = new BlockHistory(previousBlock, newBlock);
            history.updateBlockData = true;
            history.newBlockData = blockData;
            eventPipeline.addEvent(wcc, history);
        }
    }

    public static void setBlock(short newBlock, WCCi wcc) {
        if (!World.inYBounds((wcc.chunk.y * Chunk.WIDTH) + wcc.chunkVoxel.y)) return;
        Chunk chunk = world.getChunk(wcc.chunk);
        if (chunk != null) {
            //Get the previous block
            short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);

            //we need to set the block because some algorithms want to check to see if the block has changed immediately
            chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, newBlock); //Important

            BlockHistory history = new BlockHistory(previousBlock, newBlock);
            eventPipeline.addEvent(wcc, history);
        }
    }

    public static void setBlockData(BlockData blockData, WCCi wcc) {
        if (!World.inYBounds((wcc.chunk.y * Chunk.WIDTH) + wcc.chunkVoxel.y)) return;
        Chunk chunk = world.getChunk(wcc.chunk);
        if (chunk != null) {
            //Get the previous block
            short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);

            //we need to set the block because some algorithms want to check to see if the block has changed immediately
            chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockData); //Important

            BlockHistory history = new BlockHistory(previousBlock, previousBlock);
            history.updateBlockData = true;
            history.newBlockData = blockData;
            eventPipeline.addEvent(wcc, history);
        }
    }

    //Set entity =================================================================================
    public static Entity placeItemDrop(Vector3f position, ItemStack item, boolean droppedFromPlayer) {
        Entity e = placeEntity(EntityRegistry.ENTITY_ITEM_DROP, position, null);

        ItemDrop drop = (ItemDrop) e;
        drop.stack = item;
        drop.droppedFromPlayer = droppedFromPlayer;
        return e;
    }
    
    /**
     *
     * @param entity the entity to place
     * @param w the world position to place the entity
     * @param simpleGen the JSON to inject into the entity
     * @return the entity that was placed
     */
    public static Entity placeEntity(EntitySupplier entity, Vector3f w, JsonManager.SimpleJsonGenerator simpleGen) {
        byte[] entityBytes = null;
        if (simpleGen != null) {
            //Write the entity input as binary data
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (JsonGenerator generator = Entity.smileFactory.createGenerator(baos)) {
                generator.writeStartObject(); // Start root object
                simpleGen.write(generator);
                generator.writeEndObject(); // End root object
                generator.close();
            } catch (IOException e) {
                ErrorHandler.report(e);
            }
            entityBytes = baos.toByteArray();
        }

        WCCf wcc = new WCCf();
        wcc.set(w);
        Chunk chunk = world.chunks.get(wcc.chunk);
        if (chunk != null) {
            chunk.markAsModified();
            Entity e = chunk.entities.placeNew(w, entity, entityBytes);
            e.sendMultiplayer = true;//Tells the chunkEntitySet to send the entity to the clients
            return e;
        }
        return null;
    }


    public void initialize(ClientWindow window) throws Exception {
        commands = new GameCommands(this, game);
        background = new SkyBackground(window);
        livePropagationHandler.tasks.clear();

        //Setup blocks
        game.setup(this, window.ctx, ClientWindow.gameScene.ui);
        //init player
        userPlayer.init();
        //init world
        world.init(userPlayer, Registrys.blocks.textures);
    }


    public static void alert(String s) {
        ClientWindow.gameScene.ui.infoBox.addToHistory("GAME: " + s);
    }

    public static void consoleOut(String s) {
        ClientWindow.gameScene.ui.infoBox.addToHistory(s);
    }


    public static void setTimeOfDay(double v) {
        try {
            byte[] timeFloat = ByteUtils.floatToBytes((float) v);
            server.sendToAllClients(new byte[]{GameServer.SET_TIME, timeFloat[0], timeFloat[1], timeFloat[2], timeFloat[3]});
            System.out.println("Setting time of day to " + v);
            background.setTimeOfDay(v);
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }

    public static void pauseGame() {
        if (window.isFullscreen()) window.minimizeWindow();
        ClientWindow.gameScene.ui.baseMenu.setOpen(true);
    }


    int completeChunks, framesWithCompleteChunkValue;
    NetworkJoinRequest req;
    ProgressData prog;

    public void playerJoinEvent(Player client) {
        Server.alert("A new player has joined: " + client);
        System.out.println("JOIN EVENT: " + client.getName());
        System.out.println("Players: " + world.players);
        world.players.add(client);
        System.out.println("Players: " + world.players);
    }

    public void playerLeaveEvent(Player client) {
        Server.alert(client.getName() + " has left");
        world.players.remove(client);

        if (client.isHost) {
            ClientWindow.goToMenuPage();
            ClientWindow.popupMessage.message(
                    "Host has left the game",
                    "Last ping from host " + client.getSecSinceLastPing() + "s ago");
        }
    }

    public void startGameEvent(WorldData worldData, NetworkJoinRequest req, ProgressData prog) {
        world.data = worldData;
        this.req = req;
        this.prog = prog;
        livePropagationHandler.startGameEvent(worldData);
        eventPipeline.startGameEvent(worldData);
        world.startGameEvent(worldData);
        tickThread.startGameEvent();
    }

    public void stopGameEvent() {
        try {
            System.out.println("Closing World...");
            userPlayer.stopGameEvent();
            world.stopGameEvent();
            tickThread.stopGameEvent();
            eventPipeline.stopGameEvent();
            livePropagationHandler.stopGameEvent();
            server.stopGameEvent();
        } catch (Exception e) {
            ErrorHandler.report(e);
        }
    }

    /**
     * The event that starts the new game
     */
    public void startGameUpdateEvent() throws Exception {
        switch (prog.stage) {
            case 0 -> {
                if (req != null) {
                    server.initNewGame(world.data, req);
                    prog.setTask("Joining game...");
                    server.startJoiningWorld();
                }
                prog.stage++;
            }
            case 1 -> {
                if (req != null && !req.hosting) { //If we are not hosting, we need to get the world
                    prog.setTask("Received " + server.loadedChunks + " chunks");
                    if (server.getWorldInfo() != null) {
                        world.data = server.getWorldInfo();//Reassign the world info to the one we got from the host
                        prog.stage++;
                    }
                } else prog.stage++;
            }
            case 2 -> {
                prog.setTask("Starting game...");
                gameMode = (GameMode.values()[world.data.data.gameMode]);
                if (world.data.getSpawnPoint() == null) { //Create spawn point
                    userPlayer.worldPosition.set(0, 0, 0);
                    boolean ok = world.startGame(prog, world.data, new Vector3f(0, 0, 0));
                    if (!ok) {
                        prog.abort();
                        ClientWindow.goToMenuPage();
                    }
                } else {//Load spawn point
                    userPlayer.worldPosition.set(world.data.getSpawnPoint().x, world.data.getSpawnPoint().y, world.data.getSpawnPoint().z);
                    world.startGame(prog, world.data, userPlayer.worldPosition);
                }
                prog.stage++;
            }
            case 3 -> {
                waitForTasksToComplete(prog);
            }
            case 4 -> { //After we have loaded all chunks, THAN host
                prog.setTask("Hosting game...");
                server.startHostingWorld();
                prog.stage++;
            }
            case 5 -> { //Prepare chunks
                if (WAIT_FOR_ALL_CHUNKS_TO_LOAD_BEFORE_STARTING) {
                    prog.setTask("Preparing chunks");
                    AtomicInteger finishedChunks = new AtomicInteger();
                    world.chunks.forEach((vec, c) -> { //For simplicity, We call the same prepare method the same as in world class
                        c.prepare(world.terrain, 0, true);
                        if (c.gen_Complete()) {
                            finishedChunks.getAndIncrement();
                        }
                    });

                    prog.bar.setProgress(finishedChunks.get(), world.chunks.size() / 2);
                    if (finishedChunks.get() != completeChunks) {
                        completeChunks = finishedChunks.get();
                        framesWithCompleteChunkValue = 0;
                    } else {
                        framesWithCompleteChunkValue++; //We cant easily determine how many chunks can be loaded, so we just wait
                        if (framesWithCompleteChunkValue > 50) {
                            prog.stage++;
                        }
                    }
                } else prog.stage++;
            }
            default -> {
                lastGameMode = gameMode;
                if (world.data.getSpawnPoint() == null) {
                    //Find spawn point
                    //new World Event runs for the first time in a new world
                    Vector3f spawnPoint = getInitialSpawnPoint(world.terrain);
                    userPlayer.setSpawnPoint(spawnPoint.x, spawnPoint.y, spawnPoint.z);
                    userPlayer.worldPosition.set(spawnPoint);

                    userPlayer.newWorldEvent(world.data);
                }
                setTimeOfDay(world.data.data.timeOfDay);
                game.startGameEvent(world.data);
                isOperator = ownsGame();
                System.out.println("Starting game... Operator: " + isOperator);
                userPlayer.startGameEvent(world.data);
                prog.finish();
                ClientWindow.gameScene.setProjection();
            }
        }
    }

    public Vector3f getInitialSpawnPoint(Terrain terrain) {
        Vector3f worldPosition = new Vector3f();
        System.out.println("Setting new spawn point...");
        int radius = Chunk.WIDTH;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                for (int y = terrain.MIN_SURFACE_HEIGHT - 10; y < terrain.MAX_SURFACE_HEIGHT + 10; y++) {
                    if (terrain.canSpawnHere(PLAYER_HEIGHT, world, x, y, z)) {
                        System.out.println("Found new spawn point!");
                        worldPosition.set(x, y - PLAYER_HEIGHT - 0.5f, z);
                        return worldPosition;
                    }
                }
            }
        }
        System.out.println("Spawn point not found!");
        worldPosition.set(0, terrain.MIN_SURFACE_HEIGHT - PLAYER_HEIGHT - 0.5f, 0);
        return worldPosition;
    }


    private void waitForTasksToComplete(ProgressData prog) {
        if (world.newGameTasks.get() < prog.bar.getMax()) {
            prog.bar.setProgress(world.newGameTasks.get());
        } else {
            prog.stage++;
            world.newGameTasks.set(0);
        }
    }


    public static SkyBackground background;
    private GameMode lastGameMode;


    public void update() throws IOException {
        if (lastGameMode == null || lastGameMode != gameMode) {
            lastGameMode = gameMode; //Gane mode changed
            game.gameModeChangedEvent(getGameMode());
            userPlayer.gameModeChangedEvent(getGameMode());
            Server.alert("Game mode changed to: " + gameMode);
        }
        //draw other players
        server.updatePlayers();
        livePropagationHandler.update();
        //TODO: move player logic into tick thread
        eventPipeline.update();
        background.update();
    }
}
