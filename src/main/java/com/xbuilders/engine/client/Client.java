package com.xbuilders.engine.client;

import com.xbuilders.Main;
import com.xbuilders.engine.common.players.localPlayer.LocalPlayer;
import com.xbuilders.engine.client.visuals.Page;
import com.xbuilders.engine.common.network.ChannelBase;
import com.xbuilders.engine.common.network.ClientBase;
import com.xbuilders.engine.common.network.fake.FakeClient;
import com.xbuilders.engine.common.network.fake.FakeServer;
import com.xbuilders.engine.common.network.old.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.common.progress.ProgressData;
import com.xbuilders.engine.common.resource.ResourceUtils;
import com.xbuilders.engine.common.utils.LoggingUtils;
import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.World;
import com.xbuilders.engine.common.world.WorldsHandler;
import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.common.world.data.WorldData;
import com.xbuilders.engine.server.Game;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.common.players.Player;
import com.xbuilders.window.developmentTools.FrameTester;
import com.xbuilders.window.developmentTools.MemoryGraph;
import org.joml.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xbuilders.Main.LOGGER;
import static com.xbuilders.engine.common.players.Player.PLAYER_HEIGHT;


public class Client {
    public final static int version = 1;
    //The world never changes objects
    public static final World world = new World();
    public static long GAME_VERSION;
    public static boolean LOAD_WORLD_ON_STARTUP = false;
    public static boolean FPS_TOOLS = false;
    public static boolean DEV_MODE = false;
    public static LocalPlayer userPlayer;
    public final ArrayList<Player> players = new ArrayList<>();
    public static FrameTester frameTester = new FrameTester("Game frame tester");
    public static FrameTester dummyTester = new FrameTester("");
    static MemoryGraph memoryGraph; //Make this priviate because it is null by default
    public final ClientWindow window;
    private final Game game;
    public ClientBase endpoint;






    public static long versionStringToNumber(String version) {
        String[] parts = version.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        int patch = Integer.parseInt(parts[2]);
        // Combine parts into a single number by shifting bits or scaling by powers of 1000.
        return (major * 1_000_000L) + (minor * 1_000L) + patch;
    }

    public String title;

    public void consoleOut(String s) {
        window.gameScene.ui.infoBox.addToHistory(s);
    }

    public void pauseGame() {
        if (window.isFullscreen()) window.minimizeWindow();
        window.gameScene.ui.baseMenu.setOpen(true);
    }

    public Client(String[] args, String gameVersion, Game game, Logger LOGGER) throws Exception {
        Client.GAME_VERSION = versionStringToNumber(gameVersion);
        this.game = game;
        LOGGER.finest("XBuilders (" + GAME_VERSION + ") started on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        //Process args
        System.out.println("args: " + Arrays.toString(args));
        String appDataDir = null;
        title = "XBuilders";

        for (String arg : args) {
            if (arg.equals("devmode")) {
                DEV_MODE = true;
            } else if (arg.startsWith("appData")) {
                appDataDir = arg.split("=")[1];
            } else if (arg.startsWith("name")) {
                title = arg.split("=")[1];
            } else if (arg.equals("loadWorldOnStartup")) {
                Client.LOAD_WORLD_ON_STARTUP = true;
            }
        }
        ResourceUtils.initialize(DEV_MODE, appDataDir);

        /**
         * Testers
         */
        if (!Client.DEV_MODE) Client.FPS_TOOLS = false;
        dummyTester.setEnabled(false);
        if (Client.FPS_TOOLS) {
            frameTester.setEnabled(true);
            frameTester.setStarted(true);
            frameTester.setUpdateTimeMS(1000);
            memoryGraph = new MemoryGraph();
        } else {
            frameTester.setEnabled(false);
        }

        window = new ClientWindow(title, this);
        window.init(game, world);
        LOGGER.addHandler(new LoggingUtils.SevereErrorHandler(window));
    }


    public boolean makeNewWorld(String name, int size, Terrain terrain, int seed, GameMode gameMode) {
        try {
            WorldData info = new WorldData();
            info.makeNew(name, size, terrain, seed);
            info.data.gameMode = gameMode;
            if (WorldsHandler.worldNameAlreadyExists(info.getName())) {
                ClientWindow.popupMessage.message("Error", "World name \"" + info.getName() + "\" Already exists!");
                return false;
            } else WorldsHandler.makeNewWorld(info);
        } catch (IOException ex) {
            ClientWindow.popupMessage.message("Error", ex.getMessage());
            return false;
        }
        return true;
    }


    public void onConnected(boolean success, Throwable cause, ChannelBase channel) {
    }

    public void loadWorld(final WorldData singleplayerWorld, final NetworkJoinRequest remoteWorld) {
        boolean singleplayer = remoteWorld == null;
        String title = "Joining " + (singleplayer ? "Singleplayer" : "Multiplayer") + " World...";
        ProgressData prog = new ProgressData(title);
        Main.getClient().window.gameScene.setProjection();


        if (singleplayer) { //Spin up a local server
            world.data = singleplayerWorld; //set the world data


            //The server must have a separate world even if it's a single-player game
            //In singleplayer, the chunks are shared by both client and server to save memory
            World serverWorld = new World(world.chunks, world.data);

            Main.setServer(new Server(game, serverWorld)); //Create our server
            new Thread(() -> { //Start the server on another thread
                try {
                    Main.getServer().run();
                } finally {
                    stopGame();
                }
            }).start();

            //Start up our endpoint
            endpoint = new FakeClient((FakeServer) Main.getServer().endpoint) {
                @Override
                public void onConnected(boolean success, Throwable cause, ChannelBase channel) {
                    Client.this.onConnected(success, cause, channel);
                }
            };

            window.topMenu.progress.enable(prog, () -> {//update
                try {
                    joinGameUpdate(prog, remoteWorld);
                } catch (Exception e) {
                    LOGGER.log(Level.INFO,"error", e);
                    prog.abort();
                }
            }, () -> {//finished
                window.goToGamePage();
                window.topMenu.setPage(Page.HOME);
            }, () -> {//canceled
                stopGame();
                window.topMenu.setPage(Page.HOME);
            });
        } else { //Connect to a remote server

        }

    }


    private void waitForTasksToComplete(ProgressData prog) {
        if (world.newGameTasks.get() < prog.bar.getMax()) {
            prog.bar.setProgress(world.newGameTasks.get());
        } else {
            prog.stage++;
            world.newGameTasks.set(0);
        }
    }


    int completeChunks, framesWithCompleteChunkValue;
    final boolean WAIT_FOR_ALL_CHUNKS_TO_LOAD_BEFORE_JOINING = true;

    public void joinGameUpdate(ProgressData prog, NetworkJoinRequest req) throws Exception {
        switch (prog.stage) {
            case 0 -> {
                prog.setTask("Joining game...");
                boolean ok;
                if (world.data.getSpawnPoint() == null) { //Create spawn point
                    Client.userPlayer.worldPosition.set(0, 0, 0);
                    ok = world.init(prog, new Vector3f(0, 0, 0));
                } else {//Load spawn point
                    Client.userPlayer.worldPosition.set(world.data.getSpawnPoint().x, world.data.getSpawnPoint().y, world.data.getSpawnPoint().z);
                    ok = world.init(prog, Client.userPlayer.worldPosition);
                }
                if (!ok) {
                    prog.abort();
                    window.goToMenuPage();
                }
                prog.stage++;
            }
            case 1 -> {
                waitForTasksToComplete(prog);
            }
            case 2 -> { //Prepare chunks
                if (WAIT_FOR_ALL_CHUNKS_TO_LOAD_BEFORE_JOINING) {
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
                //The client controls the player and so it should decide where to spawn
                if (world.data.getSpawnPoint() == null) {
                    //Find spawn point
                    //new World Event runs for the first time in a new world
                    Vector3f spawnPoint = getInitialSpawnPoint(world.terrain);
                    Client.userPlayer.worldPosition.set(spawnPoint);
                    System.out.println("Spawn point: " + spawnPoint.x + ", " + spawnPoint.y + ", " + spawnPoint.z);
                    Client.userPlayer.setSpawnPoint(spawnPoint.x, spawnPoint.y, spawnPoint.z);
                }
                userPlayer.loadFromWorld(world.data);
                game.startGameEvent(world.data);
                prog.finish();
            }
        }
    }


    public Vector3f getInitialSpawnPoint(Terrain terrain) {
        Vector3f worldPosition = new Vector3f();
        System.out.println("Setting new spawn point...");
        int radius = Chunk.WIDTH * 2;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                for (int y = terrain.minSurfaceHeight - 20; y < terrain.maxSurfaceHeight + 20; y++) {
                    if (terrain.canSpawnHere(world, x, y, z)) {
                        System.out.println("Found new spawn point!");
                        worldPosition.set(x, y - 0.5f, z);
                        return worldPosition;
                    }
                }
            }
        }
        System.out.println("Spawn point not found!");
        worldPosition.set(0, terrain.minSurfaceHeight - PLAYER_HEIGHT - 0.5f, 0);
        return worldPosition;
    }

    public void stopGame() {
        try {
            if (Main.getServer() != null) Main.getServer().stop();  //If we have a local server
        } catch (Exception e) {
            LOGGER.log(Level.INFO,"error", e);
        } finally {
            Main.setServer(null);
            System.gc();
        }
    }

}
