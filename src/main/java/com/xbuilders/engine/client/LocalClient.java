package com.xbuilders.engine.client;

import com.xbuilders.engine.Client;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.client.visuals.Page;
import com.xbuilders.engine.common.network.ClientBase;
import com.xbuilders.engine.server.*;
import com.xbuilders.engine.server.commands.Command;
import com.xbuilders.engine.server.commands.CommandRegistry;
import com.xbuilders.engine.server.commands.GiveCommand;
import com.xbuilders.engine.server.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.World;
import com.xbuilders.engine.server.world.WorldsHandler;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.data.WorldData;
import com.xbuilders.engine.common.ErrorHandler;
import com.xbuilders.engine.common.progress.ProgressData;
import com.xbuilders.engine.common.resource.ResourceUtils;
import com.xbuilders.window.developmentTools.FrameTester;
import com.xbuilders.window.developmentTools.MemoryGraph;
import org.joml.Vector3f;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.server.players.Player.PLAYER_HEIGHT;


public class LocalClient extends Client {
    //The world never changes objects
    public static final World world = new World();
    public static long GAME_VERSION;
    public static boolean LOAD_WORLD_ON_STARTUP = false;
    public static boolean FPS_TOOLS = false;
    public static boolean DEV_MODE = false;
    public static UserControlledPlayer userPlayer;
    public static LocalServer localServer;
    public final CommandRegistry commands;
    public static FrameTester frameTester = new FrameTester("Game frame tester");
    public static FrameTester dummyTester = new FrameTester("");
    static MemoryGraph memoryGraph; //Make this priviate because it is null by default
    public final ClientWindow window;
    private final Game game;
    private ClientBase endpoint;


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

    public LocalClient(String[] args, String gameVersion, Game game) throws Exception {
        LocalClient.GAME_VERSION = versionStringToNumber(gameVersion);
        this.game = game;
        System.out.println("XBuilders (" + GAME_VERSION + ") started on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

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
                LocalClient.LOAD_WORLD_ON_STARTUP = true;
            }
        }
        ResourceUtils.initialize(DEV_MODE, appDataDir);

        commands = new CommandRegistry();
        registerCommands();

        /**
         * Testers
         */
        if (!LocalClient.DEV_MODE) LocalClient.FPS_TOOLS = false;
        dummyTester.setEnabled(false);
        if (LocalClient.FPS_TOOLS) {
            frameTester.setEnabled(true);
            frameTester.setStarted(true);
            frameTester.setUpdateTimeMS(1000);
            memoryGraph = new MemoryGraph();
        } else {
            frameTester.setEnabled(false);
        }

        window = new ClientWindow(title, this);
        window.init(game, world);
    }

    private void registerCommands() {
        commands.registerCommand(new Command("tickrate", "Sets the random tick likelihood. Usage: tickrate <ticks>")
                .requiresOP(true).executes((parts) -> {
                    if (parts.length >= 1) {
                        Chunk.randomTickLikelyhoodMultiplier = (float) Double.parseDouble(parts[0]);
                        return "Tick rate changed to: " + Chunk.randomTickLikelyhoodMultiplier;
                    }
                    return "Tick rate is " + Chunk.randomTickLikelyhoodMultiplier;
                }));

        commands.registerCommand(new Command("mode",
                "Usage (to get the current mode): mode\n" +
                        "Usage (to change mode): mode <mode> <all (optional)>")
                .requiresOP(true).executes((parts) -> {
                    if (parts.length >= 1) {
                        String mode = parts[0].toUpperCase().trim().replace(" ", "_");

                        boolean sendToAll = (parts.length >= 2 && parts[1].equalsIgnoreCase("all"));
                        try {
                            localServer.setGameMode(GameMode.valueOf(mode.toUpperCase()));
                            if (sendToAll) {
                                int gameMode = localServer.getGameMode().ordinal();
                            }
                            return "Game mode changed to: " + localServer.getGameMode();
                        } catch (IllegalArgumentException e) {
                            return "No game mode \"" + mode + "\" Valid game modes are "
                                    + Arrays.toString(GameMode.values());
                        } catch (IOException e) {
                            return "Error: " + e;
                        }
                    } else {
                        return "Game mode: " + localServer.getGameMode();
                    }
                }));

        commands.registerCommand(new Command("die",
                "Kills the current player")
                .requiresOP(false).executes((parts) -> {
                    LocalClient.userPlayer.die();
                    return "Player " + LocalClient.userPlayer.getName() + " has died";
                }));

        commands.registerCommand(new Command("setSpawn",
                "Set spawnpoint for the current player")
                .requiresOP(false).executes((parts) -> {
                    LocalClient.userPlayer.setSpawnPoint(
                            LocalClient.userPlayer.worldPosition.x,
                            LocalClient.userPlayer.worldPosition.y,
                            LocalClient.userPlayer.worldPosition.z);
                    return "Set spawn point for " + LocalClient.userPlayer.getName() + " to current position";
                }));

        commands.registerCommand(new Command("op", "Usage: op <true/false> <player>")
                .requiresOP(true).executes((parts) -> {
//                    if (!Main.getServer().ownsGame())
//                        return "Only the host can change OP status"; //We cant change permissions if we arent the host
//                    if (parts.length >= 2) {
//                        boolean operator = Boolean.parseBoolean(parts[0]);
//                        Player target = localServer.server.getPlayerByName(parts[1]);
//                        if (target != null) {
//                            try {
//                                target.sendData(new byte[]{GameServer.CHANGE_PLAYER_PERMISSION, (byte) (operator ? 1 : 0)});
//                            } catch (IOException e) {
//                                return "Error: " + e;
//                            }
//                            return "Player " + target.userInfo.name + " has been " + (operator ? "given" : "removed") + " operator privileges";
//                        } else {
//                            return "Player not found";
//                        }
//                    }
                    return null;
                }));

        commands.registerCommand(new Command("address", "Returns your local IP address")
                .executes((parts) -> {
                    InetAddress localHost = null;
                    try {
                        localHost = InetAddress.getLocalHost();
                        return localHost.getHostAddress();
                    } catch (UnknownHostException e) {
                        return "error";
                    }
                }));

        commands.registerCommand(new Command("msg",
                "Usage: msg <player/all> <message>").executes((parts) -> {
            if (parts.length >= 2) {
                // return localServer.server.sendChatMessage(parts[0], parts[1]);
            }
            return null;
        }));

        commands.registerCommand(new GiveCommand());

        commands.registerCommand(new Command("time",
                "Usage: time set <day/evening/night>\n" +
                        "Usage: time get")
                .requiresOP(true)
                .executes((parts) -> {
                    if (parts.length >= 1 && parts[0].equalsIgnoreCase("get")) {
                        return "Time of day: " + localServer.getTimeOfDay();
                    } else if (parts.length >= 2 && parts[0].equalsIgnoreCase("set")) {
                        if (parts[1].equalsIgnoreCase("morning") || parts[1].equalsIgnoreCase("m")) {
                            localServer.setTimeOfDay(0.95f);
                            return "Time of day set to: " + localServer.getTimeOfDay();
                        } else if (parts[1].equalsIgnoreCase("day") || parts[1].equalsIgnoreCase("d")) {
                            localServer.setTimeOfDay(0.0f);
                            return "Time of day set to: " + localServer.getTimeOfDay();
                        } else if (parts[1].equalsIgnoreCase("evening") || parts[1].equalsIgnoreCase("e")) {
                            localServer.setTimeOfDay(0.25f);
                            return "Time of day set to: " + localServer.getTimeOfDay();
                        } else if (parts[1].equalsIgnoreCase("night") || parts[1].equalsIgnoreCase("n")) {
                            localServer.setTimeOfDay(0.5f);
                            return "Time of day set to: " + localServer.getTimeOfDay();
                        } else {
                            float time = Float.parseFloat(parts[1]);
                            localServer.setTimeOfDay(time);
                            return "Time of day set to: " + localServer.getTimeOfDay();
                        }
                    }
                    return null;
                }));

        commands.registerCommand(new Command("alwaysDay",
                "Usage: alwaysDay true/false")
                .requiresOP(true)
                .executes((parts) -> {
                    if (parts.length >= 1) {
                        LocalClient.world.data.data.alwaysDayMode = parts[0].equalsIgnoreCase("true");
                        try {
                            LocalClient.world.data.save();
                            return "Always day mode: " + LocalClient.world.data.data.alwaysDayMode;
                        } catch (IOException e) {
                            return "Error: " + e;
                        }
                    }
                    return null;
                }));

        commands.registerCommand(new Command("teleport",
                "Usage: teleport <player>\nUsage: teleport <x> <y> <z>")
                .requiresOP(true)
                .executes((parts) -> {
                    if (parts.length >= 3) {
                        LocalClient.userPlayer.worldPosition.set(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
                        return null;
                    }

                    if (parts.length >= 1) {
//                        Player target = localServer.server.getPlayerByName(parts[0]);
//                        if (target != null) {
//                            LocalClient.userPlayer.worldPosition.set(target.worldPosition);
//                            return null;
//                        } else {
//                            return "Player not found";
//                        }
                    }
                    return null;
                }));

        commands.registerCommand(new Command("difficulty",
                "Usage: difficulty <easy/normal/hard>")
                .requiresOP(true)
                .executes((parts) -> {
                    if (parts.length >= 1) {
                        String mode = parts[0].toUpperCase().trim().replace(" ", "_");
                        try {
                            localServer.setDifficulty(Difficulty.valueOf(mode.toUpperCase()));
//                            if (localServer.server.isPlayingMultiplayer())
//                                localServer.server.sendToAllClients(new byte[]{
//                                        GameServer.CHANGE_DIFFICULTY, (byte) localServer.getDifficulty().ordinal()});
                            return "Difficulty changed to: " + localServer.getDifficulty();
                        } catch (IllegalArgumentException e) {
                            return "Invalid mode \"" + mode + "\" Valid modes are "
                                    + Arrays.toString(Difficulty.values());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return "Difficulty: " + localServer.getDifficulty();
                }));

        commands.registerCommand(new Command("list",
                "Lists all connected players")
                .requiresOP(true)
                .executes((parts) -> {
                    StringBuilder str = new StringBuilder(LocalClient.world.players.size() + " players:\n");
                    for (Player client : LocalClient.world.players) {
                        str.append(client.getName()).append(";   ").append(client.getConnectionStatus()).append("\n");
                    }
                    System.out.println("\nPLAYERS:\n" + str);
                    return str.toString();
                }));
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

    public void loadWorld(final WorldData singleplayerWorld, final NetworkJoinRequest remoteWorld) {
        boolean singleplayer = remoteWorld == null;
        String title = "Joining " + (singleplayer ? "Singleplayer" : "Multiplayer") + " World...";
        ProgressData prog = new ProgressData(title);

        if (singleplayer) { //Spin up a local server
            world.data = singleplayerWorld; //set the world data

            //The server must have a separate world even if it's a single-player game
            //In singleplayer, the chunks are shared by both client and server to save memory
            World serverWorld = new World(world.chunks);
            localServer = new LocalServer(game, serverWorld, userPlayer);
            window.topMenu.progress.enable(prog, () -> {//update
                try {
                    joinGameUpdate(prog, remoteWorld);
                } catch (Exception ex) {
                    ErrorHandler.report(ex);
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
                    LocalClient.userPlayer.worldPosition.set(0, 0, 0);
                    ok = world.init(prog, new Vector3f(0, 0, 0));
                } else {//Load spawn point
                    LocalClient.userPlayer.worldPosition.set(world.data.getSpawnPoint().x, world.data.getSpawnPoint().y, world.data.getSpawnPoint().z);
                    ok = world.init(prog, LocalClient.userPlayer.worldPosition);
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
                    LocalClient.userPlayer.worldPosition.set(spawnPoint);
                    System.out.println("Spawn point: " + spawnPoint.x + ", " + spawnPoint.y + ", " + spawnPoint.z);
                    LocalClient.userPlayer.setSpawnPoint(spawnPoint.x, spawnPoint.y, spawnPoint.z);
                    LocalClient.userPlayer.newWorldEvent(world.data);
                }
                game.startGameEvent(world.data);
                localServer.startGameEvent(req);
                prog.finish();
            }
        }
    }


    public Vector3f getInitialSpawnPoint(Terrain terrain) {
        Vector3f worldPosition = new Vector3f();
        System.out.println("Setting new spawn point...");
        int radius = Chunk.WIDTH + 2;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                for (int y = terrain.minSurfaceHeight - 10; y < terrain.maxSurfaceHeight + 10; y++) {
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
            System.out.println("Closing World...");
            userPlayer.stopGameEvent();
            //If we have a local server
            if (localServer != null) localServer.close();
        } catch (Exception e) {
            ErrorHandler.report(e);
        } finally {
            localServer = null;
            System.gc();
        }
    }


    public static void createWindowedMessage(String title, String str) {
        final JFrame parent = new JFrame();
        JLabel label = new JLabel("");
        label.setText("<html><body style='padding:5px;'>" + str.replace("\n", "<br>") + "</body></html>");
        label.setFont(label.getFont().deriveFont(12f));
        label.setVerticalAlignment(JLabel.TOP);
        parent.add(label);
        parent.pack();
        parent.getContentPane().setBackground(Color.white);
        parent.setVisible(true);
        parent.pack();
        parent.setTitle(title);
        parent.setLocationRelativeTo(null);
        parent.setAlwaysOnTop(true);
        parent.setVisible(true);
        parent.setSize(350, 200);
    }
}
