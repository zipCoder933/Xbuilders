/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.multiplayer.GameServer;
import com.xbuilders.engine.multiplayer.Local_MultiplayerPendingEntityChanges;
import com.xbuilders.engine.multiplayer.PlayerClient;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.progress.ProgressData;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.skybox.SkyBackground;
import com.xbuilders.engine.world.wcc.WCCf;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.WindowEvents;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_BLEND;

public class GameScene implements WindowEvents {

    public LivePropagationHandler livePropagationHandler;
    final boolean WAIT_FOR_ALL_CHUNKS_TO_LOAD_BEFORE_STARTING = true;
    public static final World  world = new World();
    public static boolean drawWireframe;
    public static boolean drawBoundingBoxes;
    public static UserControlledPlayer player;
    public static GameServer server;
    static MainWindow window;
    public final static Matrix4f projection = new Matrix4f();
    public final static Matrix4f view = new Matrix4f();
    public final static Matrix4f centeredView = new Matrix4f();
    private static Game game;
    static HashMap<String, String> commandHelp;
    public static Local_MultiplayerPendingEntityChanges localEntityChanges;


    public GameScene(MainWindow window) throws Exception {
        this.window = window;
        specialMode = true;
        player = new UserControlledPlayer(window, world, projection, view, centeredView);
        localEntityChanges = new Local_MultiplayerPendingEntityChanges(player);
        server = new GameServer(player);
        livePropagationHandler = new LivePropagationHandler();
    }


    public void initialize(MainWindow window, MyGame game) throws Exception {
        background = new SkyBackground(window);
        livePropagationHandler.tasks.clear();
        game.initialize(this);
        setProjection();
        ui = new GameUI(game, window.ctx, window);
        player.init();
        world.init(player, ItemList.blocks.textures);
        ui.init();
        game.uiInit(window.ctx, ui);
    }


    public static void alert(String s) {
        ui.infoBox.addToHistory("GAME: " + s);
    }

    public static void consoleOut(String s) {
        ui.infoBox.addToHistory(s);
    }

    private static String[] splitWhitespacePreserveQuotes(String input) {
        ArrayList<String> parts = new ArrayList<>();

        Pattern pattern = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                parts.add(matcher.group(1)); // Add double-quoted string without quotes
            } else if (matcher.group(2) != null) {
                parts.add(matcher.group(2)); // Add single-quoted string without quotes
            } else {
                parts.add(matcher.group()); // Add unquoted word
            }
        }

        return parts.toArray(new String[0]);
    }

    public static void setGame(Game game2) {
        game = game2;
        commandHelp = new HashMap<>();
        commandHelp.put("msg", "Usage: msg <player/all> <message>");
        commandHelp.put("help", "Usage: help <command>");
        commandHelp.put("time", "Usage: time <day/evening/night>");
        commandHelp.put("players", "Lists all connected players");
        commandHelp.put("teleport", "Usage: teleport <player>" +
                "\nUsage: teleport <x> <y> <z>");
        commandHelp.put("address", "Returns the server's address");
        if (game.getCommandHelp() != null) commandHelp.putAll(game.getCommandHelp());
    }

    public static String handleGameCommand(String command) {
        String[] parts = splitWhitespacePreserveQuotes(command);
        System.out.println("handleGameCommand: " + Arrays.toString(parts));
        if (parts.length > 0) {
            try {
                switch (parts[0].toLowerCase()) {
                    case "address" -> {
                        return server.getIpAdress() + ":" + server.getPort();
                    }
                    case "help" -> {
                        String out = "Available commands:\n";
                        for (Map.Entry<String, String> entry : commandHelp.entrySet()) {
                            if (entry.getValue().contains("\n")) {
                                String[] lines = entry.getValue().split("\n");
                                for (String line : lines) {
                                    out += entry.getKey() + "\t    " + line + "\n";
                                }
                            } else out += entry.getKey() + "\t    " + entry.getValue() + "\n";
                        }
                        return out;
                    }
                    case "players" -> {
                        String str = "" + server.clients.size() + " players:\n";
                        for (PlayerClient client : server.clients) {
                            str += client.player.name + "\n";
                        }
                        return str;
                    }
                    case "msg" -> {
                        if (parts.length > 2) {
                            return server.sendChatMessage(parts[1], parts[2]);
                        } else return commandHelp.get("msg");
                    }
                    case "time" -> {
                        if (parts.length == 2) {
                            //It doesnt matter if we had 2 players with different time
//                            if(!server.isHosting() && server.isPlayingMultiplayer()) return "You cannot change time";
                            if (parts[1].toLowerCase().equals("day") || parts[1].toLowerCase().equals("morning")) {
                                setTimeOfDay(0.0f);
                                return null;
                            } else if (parts[1].toLowerCase().equals("evening")) {
                                setTimeOfDay(0.25f);
                                return null;
                            } else if (parts[1].toLowerCase().equals("night")) {
                                setTimeOfDay(0.5f);
                                return null;
                            } else return commandHelp.get("time");
                        } else return commandHelp.get("time");
                    }
                    case "teleport" -> {
                        if (parts.length == 2) {
                            PlayerClient target = server.getPlayerByName(parts[1]);
                            if (target != null) {
                                player.worldPosition.set(target.player.worldPosition);
                                return null;
                            } else {
                                return "Player not found";
                            }
                        } else if (parts.length > 3) {
                            if (!server.isPlayingMultiplayer() || server.isHosting()) {
                                player.worldPosition.set(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                            } else {
                                return "You cannot teleport";
                            }
                        } else return commandHelp.get("teleport");
                    }
                    default -> {
                        String out = game.handleCommand(parts);
                        if (out != null) {
                            return out;
                        }
                    }
                }
            } catch (Exception e) {
                return "Error handling command \"" + parts[0].toLowerCase() + "\": " + e.getMessage();
            }
        }
        return "Unknown command. Type 'help' for a list of commands";
    }

    public static void setTimeOfDay(double v) throws IOException {
        background.setTimeOfDay(v);
        byte[] timeFloat = ByteUtils.floatToBytes((float) v);
        server.sendToAllClients(new byte[]{GameServer.SET_TIME, timeFloat[0], timeFloat[1], timeFloat[2], timeFloat[3]});
    }

    public static void pauseGame() {
        if (window.isFullscreen()) window.minimizeWindow();
        ui.baseMenu.setOpen(true);
    }

    public static void unpauseGame() {
        if (window.isFullscreen()) MainWindow.restoreWindow();
    }


    public void gameClosedEvent() {
        if (world.terrain != null) {
            System.out.println("Closing " + world.info.getName() + "...");
            player.stopGame();
            world.stopGame(player.worldPosition);
            game.saveState();
        }
        livePropagationHandler.endGame();
        try {
            server.closeGame();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int completeChunks, framesWithCompleteChunkValue;
    WorldInfo worldInfo;
    NetworkJoinRequest req;
    ProgressData prog;

    public void startGame(WorldInfo world, NetworkJoinRequest req, ProgressData prog) {
        this.worldInfo = world;
        this.req = req;
        this.prog = prog;
        livePropagationHandler.startGame(world);
    }

    public void newGameUpdate() {
        switch (prog.stage) {
            case 0 -> {//Start multiplayer
                if (req != null) {
                    prog.setTask(req.hosting ? "Hosting game..." : "Joining game...");
                    try {
                        GameScene.server.startGame(worldInfo, req);
                    } catch (IOException | InterruptedException e) {
                        prog.abort("Error Starting Server", e.getMessage());
                    }
                }
                prog.stage++;
            }
            case 1 -> {
                if (req != null) {//We want to wait until all chunks have been given from the host
                    if (!req.hosting) {
                        prog.setTask("Received " + server.loadedChunks + " chunks");
                    }

                    if (server.getWorldInfo() != null) {
                        worldInfo = server.getWorldInfo();//Reassign the world info to the one we got from the host
                        prog.stage++;
                    }
                } else prog.stage++;
            }
            case 2 -> {
                prog.setTask("Starting game...");
                if (worldInfo.getSpawnPoint() == null) { //Create spawn point
                    player.worldPosition.set(0, 0, 0);
                    boolean ok = world.startGame(prog, worldInfo, new Vector3f(0, 0, 0));
                    if (!ok) {
                        prog.abort();
                        MainWindow.goToMenuPage();
                    }
                } else {//Load spawn point
                    player.worldPosition.set(worldInfo.getSpawnPoint().x, worldInfo.getSpawnPoint().y, worldInfo.getSpawnPoint().z);
                    world.startGame(prog, worldInfo, player.worldPosition);
                }
                prog.stage++;
            }
            case 3 -> {
                waitForTasksToComplete(prog);
            }

            case 4 -> { //Prepare chunks
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
                if (worldInfo.getSpawnPoint() == null) {
                    //Find spawn point
                    player.setNewSpawnPoint(world.terrain);
                }
                game.startGame(worldInfo);
                player.startGame(worldInfo);
                prog.finish();
                setProjection();
            }
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


    boolean holdMouse;
    public static boolean specialMode;
    public static GameUI ui;
    public static SkyBackground background;



    public void render() throws IOException {
        MainWindow.frameTester.startProcess();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); //Clear not only the color but the depth buffer
//        GL11C.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f); //Set the background color
        background.draw(projection, centeredView);   //Draw the background BEFORE ANYTHING ELSE! (Anything drawn before will be overridden)

        holdMouse = !ui.releaseMouse() && window.windowIsFocused();
        MainWindow.frameTester.endProcess("Clearing buffer");

        glEnable(GL_DEPTH_TEST);   // Enable depth test
        glDepthFunc(GL_LESS); // Accept fragment if it closer to the camera than the former one

        MainWindow.frameTester.startProcess();
        player.update(holdMouse);

        //draw other players
        server.updatePlayers(projection, view);

        MainWindow.frameTester.endProcess("Updating player");
        enableBackfaceCulling();
        MainWindow.frameTester.startProcess();

        glEnable(GL_BLEND); //Enable transparency
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        world.drawChunks(projection, view, player.worldPosition);
        MainWindow.frameTester.endProcess("Drawing chunks");
        setInfoText();
        livePropagationHandler.update();
        ui.draw();
        game.update();
    }

    public void windowUnfocusEvent() {
        if (window.isFullscreen()) ui.baseMenu.setOpen(true);
        else if (!GameScene.ui.anyMenuOpen()) {
            ui.baseMenu.setOpen(true);
        }
        holdMouse = false;
    }

    public void windowResizeEvent(int width, int height) {
        setProjection();
        ui.windowResizeEvent(width, height);
    }


    public static void enableBackfaceCulling() {
        //If backface culling is not working, it means that another process has probably disabled it, after init3D.
        glEnable(GL_CULL_FACE); // enable face culling
        glFrontFace(GL_CCW);// specify the winding order of frontRay-facing triangles
        glCullFace(GL_BACK);// specify which faces to cull
    }

    private void setProjection() {
        projection.identity().perspective((float) Math.toRadians(70.0f), //Fov
                (float) window.getWidth() / (float) window.getHeight(), //screen ratio
                0.1f, 10000.0f); //display range (clipping planes)
    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (ui.keyEvent(key, scancode, action, mods)) {
        } else if (game.keyEvent(key, scancode, action, mods)) {
        } else {
            player.keyEvent(key, scancode, action, mods);
        }
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_F3 -> debugText = !debugText;
                case GLFW.GLFW_KEY_F5 -> specialMode = !specialMode;
                case GLFW.GLFW_KEY_F6 -> drawWireframe = !drawWireframe;
                case GLFW.GLFW_KEY_F7 -> drawBoundingBoxes = !drawBoundingBoxes;
            }
        }
        return true;
    }

    public boolean mouseButtonEvent(int button, int action, int mods) {
        ui.mouseButtonEvent(button, action, mods);
        if (!ui.anyMenuOpen()) {
            player.mouseButtonEvent(button, action, mods);
        }
        return true;
    }

    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        boolean letUIHandleScroll = true;
        if (!ui.anyMenuOpen()) {
            letUIHandleScroll = !player.mouseScrollEvent(scroll, xoffset, yoffset);
        }
        if (letUIHandleScroll) ui.mouseScrollEvent(scroll, xoffset, yoffset);
        return true;
    }


    boolean debugText = false;
    public static WCCi rayWCC = new WCCi();

    private void setInfoText() {
        if (MainWindow.devMode || debugText) {
            String text = "";
            try {
                WCCf wcc2 = new WCCf();
                wcc2.set(player.worldPosition);
                text += MainWindow.mfpAndMemory + "   smoothDelta=" + window.smoothFrameDeltaSec + "\n";
                text += "Player pos: " +
                        ((int) player.worldPosition.x) + ", " +
                        ((int) player.worldPosition.y) + ", " +
                        ((int) player.worldPosition.z) +
                        "    Player velocity: " + MiscUtils.printVector(GameScene.player.positionHandler.getVelocity());
                text += "\nPlayer camera: " + player.camera.toString();

                if (player.camera.cursorRay.hitTarget() || player.camera.cursorRay.cursorRayHitAllBlocks) {

                    if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
                        rayWCC.set(player.camera.cursorRay.getHitPosPlusNormal());
                        text += "\nRay+normal (Q): \n\t" + player.camera.cursorRay.toString() + "\n\t" + rayWCC.toString() + "\n";
                    } else {
                        rayWCC.set(player.camera.cursorRay.getHitPos());
                        text += "\nRay hit (Q): \n\t" + player.camera.cursorRay.toString() + "\n\t" + rayWCC.toString() + "\n";
                    }

                    if (player.camera.cursorRay.getEntity() != null) {
                        Entity e = player.camera.cursorRay.getEntity();
                        text += "\nEntity: " + e + " controlledByAnotherPlayer: " + e.multiplayerProps.controlledByAnotherPlayer;
                    }

                    Chunk chunk = world.getChunk(rayWCC.chunk);
                    if (chunk != null) {
                        text += "\nchunk gen status: " + chunk.getGenerationStatus() + ", pillar loaded: " + chunk.pillarInformation.isPillarLoaded();
                        text += "\nchunk neighbors: " + chunk.neghbors.toString();
                        text += "\nchunk mesh: visible:" + chunk.meshes.opaqueMesh.isVisible();
                        text += "\nchunk mesh: " + chunk.meshes;
                        text += "\nchunk last modified: " + MiscUtils.formatTime(chunk.lastModifiedTime);

                        Block block = ItemList.getBlock(chunk.data.getBlock(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z));
                        BlockData data = chunk.data.getBlockData(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z);

                        byte sun = chunk.data.getSun(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z);
                        text += "\n" + block + " data: " + (data == null ? "null" : data.toString()) + " type: " + ItemList.blocks.getBlockType(block.renderType);
                        text += "\nsun: " + (sun) + ", torch: " + chunk.data.getTorch(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z);
                    }

                }

                text += "\nSpecial Mode: " + specialMode;
                text += "\nAny Menu Open: " + GameScene.ui.anyMenuOpen();
                text += "\nBase Menus Open: " + GameScene.ui.baseMenusOpen();

            } catch (Exception ex) {
                text = "Error: " + ex.getMessage();
                ex.printStackTrace();
            }
            ui.setDevText(text);
        } else ui.setDevText(null);
    }


}
