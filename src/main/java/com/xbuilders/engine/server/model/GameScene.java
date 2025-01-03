/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.server.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.xbuilders.engine.server.model.items.Registrys;
import com.xbuilders.engine.server.model.items.block.Block;
import com.xbuilders.engine.server.model.items.entity.Entity;
import com.xbuilders.engine.server.model.items.entity.EntityRegistry;
import com.xbuilders.engine.server.model.items.entity.EntitySupplier;
import com.xbuilders.engine.server.model.items.entity.ItemDrop;
import com.xbuilders.engine.server.model.items.item.ItemStack;
import com.xbuilders.engine.server.model.players.Player;
import com.xbuilders.engine.server.multiplayer.GameServer;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.server.model.players.pipeline.BlockEventPipeline;
import com.xbuilders.engine.server.model.players.pipeline.BlockHistory;
import com.xbuilders.engine.client.visuals.ui.gameScene.GameUI;
import com.xbuilders.engine.server.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.json.JsonManager;
import com.xbuilders.engine.utils.progress.ProgressData;
import com.xbuilders.engine.server.model.world.Terrain;
import com.xbuilders.engine.server.model.world.World;
import com.xbuilders.engine.server.model.world.data.WorldData;
import com.xbuilders.engine.server.model.world.chunk.BlockData;
import com.xbuilders.engine.server.model.world.chunk.Chunk;
import com.xbuilders.engine.server.model.world.skybox.SkyBackground;
import com.xbuilders.engine.server.model.world.wcc.WCCf;
import com.xbuilders.engine.server.model.world.wcc.WCCi;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.window.WindowEvents;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.server.model.players.Player.PLAYER_HEIGHT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_BLEND;

public class GameScene implements WindowEvents {

    public LivePropagationHandler livePropagationHandler;
    final boolean WAIT_FOR_ALL_CHUNKS_TO_LOAD_BEFORE_STARTING = true;
    public static final World world = new World();
    public static boolean drawWireframe;
    public static boolean drawBoundingBoxes;
    public static UserControlledPlayer userPlayer;
    public static GameServer server;
    static MainWindow window;
    public final static Matrix4f projection = new Matrix4f();
    public final static Matrix4f view = new Matrix4f();
    public final static Matrix4f centeredView = new Matrix4f();
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
        GameScene.gameMode = gameMode;
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


    public GameScene(MainWindow window, Game myGame) throws Exception {
        game = myGame;
        this.window = window;
        specialMode = true;
        userPlayer = new UserControlledPlayer(window, projection, view, centeredView);
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


    public void initialize(MainWindow window) throws Exception {
        //Tasks that dont depend on the world, player or blocks
        setProjection();
        commands = new GameCommands(this, game);
        background = new SkyBackground(window);
        livePropagationHandler.tasks.clear();

        //Setup blocks
        game.setup(this, window.ctx, ui);
        //init player
        userPlayer.init();
        //init world
        world.init(userPlayer, Registrys.blocks.textures);

        //Init UI
        ui = new GameUI(game, window.ctx, window);
        ui.init();
    }


    public static void alert(String s) {
        ui.infoBox.addToHistory("GAME: " + s);
    }

    public static void consoleOut(String s) {
        ui.infoBox.addToHistory(s);
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
        ui.baseMenu.setOpen(true);
    }

    public static void unpauseGame() {
        if (window.isFullscreen()) MainWindow.restoreWindow();
    }


    int completeChunks, framesWithCompleteChunkValue;
    NetworkJoinRequest req;
    ProgressData prog;

    public void playerJoinEvent(Player client) {
        GameScene.alert("A new player has joined: " + client);
        System.out.println("JOIN EVENT: " + client.getName());
        System.out.println("Players: " + world.players);
        world.players.add(client);
        System.out.println("Players: " + world.players);
    }

    public void playerLeaveEvent(Player client) {
        GameScene.alert(client.getName() + " has left");
        world.players.remove(client);

        if (client.isHost) {
            MainWindow.goToMenuPage();
            MainWindow.popupMessage.message(
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
        if (MainWindow.devMode) writeDebugText = true;
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
                        MainWindow.goToMenuPage();
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
                setProjection();
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


    private boolean holdMouse;
    public static boolean specialMode;
    public static GameUI ui;
    public static SkyBackground background;
    private GameMode lastGameMode;


    public void render() throws IOException {
        MainWindow.frameTester.startProcess();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); //Clear not only the color but the depth buffer
//        GL11C.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f); //Set the background color
        background.draw(projection, centeredView);   //Draw the background BEFORE ANYTHING ELSE! (Anything drawn before will be overridden)
        background.update();

        holdMouse = !ui.releaseMouse() && window.windowIsFocused();
        MainWindow.frameTester.endProcess("Clearing buffer");

        glEnable(GL_DEPTH_TEST);   // Enable depth test
        glDepthFunc(GL_LESS); // Accept fragment if it closer to the camera than the former one

        if (lastGameMode == null || lastGameMode != gameMode) {
            lastGameMode = gameMode; //Gane mode changed
            game.gameModeChangedEvent(getGameMode());
            userPlayer.gameModeChangedEvent(getGameMode());
            GameScene.alert("Game mode changed to: " + gameMode);
        }

        //TODO: move player logic into tick thread
        eventPipeline.update();
        userPlayer.update(holdMouse);


        //draw other players
        server.updatePlayers(projection, view);

        enableBackfaceCulling();
        MainWindow.frameTester.startProcess();

        glEnable(GL_BLEND); //Enable transparency
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        world.drawChunks(projection, view, userPlayer.worldPosition);
        MainWindow.frameTester.endProcess("Drawing chunks");
        setInfoText();
        livePropagationHandler.update();
        ui.draw();
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
            userPlayer.keyEvent(key, scancode, action, mods);
        }
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_F3 -> writeDebugText = !writeDebugText;
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
            userPlayer.mouseButtonEvent(button, action, mods);
        }
        return true;
    }

    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (ui.anyMenuOpen() && ui.mouseScrollEvent(scroll, xoffset, yoffset)) {
        } else if (userPlayer.mouseScrollEvent(scroll, xoffset, yoffset)) {
        } else if (game.uiMouseScrollEvent(scroll, xoffset, yoffset)) {
        } else {
            ui.hotbar.mouseScrollEvent(scroll, xoffset, yoffset);
        }
        return true;
    }


    boolean writeDebugText = false;
    private WCCi rayWCC = new WCCi();
    private Vector3i rayWorldPos = new Vector3i();

    private void setInfoText() {
        if (writeDebugText) {
            String text = "";
            try {
                WCCf playerWCC = new WCCf();
                playerWCC.set(userPlayer.worldPosition);
                text += MainWindow.mfpAndMemory + "   smoothDelta=" + window.smoothFrameDeltaSec + "\n";
                text += "Saved " + world.getTimeSinceLastSave() + "ms ago\n";
                text += "PLAYER pos: " +
                        ((int) userPlayer.worldPosition.x) + ", " +
                        ((int) userPlayer.worldPosition.y) + ", " +
                        ((int) userPlayer.worldPosition.z) +
                        "    velocity: " + MiscUtils.printVector(GameScene.userPlayer.positionHandler.getVelocity());
                text += "\n\tcamera: " + userPlayer.camera.toString();

                if (userPlayer.camera.cursorRay.hitTarget() || userPlayer.camera.cursorRay.angelPlacementMode) {
                    if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
                        rayWCC.set(userPlayer.camera.cursorRay.getHitPosPlusNormal());
                        rayWorldPos.set(userPlayer.camera.cursorRay.getHitPosPlusNormal());
                        text += "\nRAY (+normal) (Q): \n\t" + userPlayer.camera.cursorRay.toString() + "\n\t" + rayWCC.toString() + "\n";
                    } else {
                        rayWCC.set(userPlayer.camera.cursorRay.getHitPos());
                        rayWorldPos.set(userPlayer.camera.cursorRay.getHitPos());
                        text += "\nRAY (hit) (Q): \n\t" + userPlayer.camera.cursorRay.toString() + "\n\t" + rayWCC.toString() + "\n";
                    }

                    if (userPlayer.camera.cursorRay.getEntity() != null) {
                        Entity e = userPlayer.camera.cursorRay.getEntity();
                        text += "\nENTITY: " + e.toString() + "\n" +
                                "\tcontrolledByAnotherPlayer: " + e.multiplayerProps.controlledByAnotherPlayer;
                    }

                    Chunk chunk = world.getChunk(rayWCC.chunk);
                    if (chunk != null) {
                        text += "\nchunk gen status: " + chunk.getGenerationStatus() + ", pillar loaded: " + chunk.pillarInformation.isPillarLoaded();
                        text += "\nchunk neighbors: " + chunk.neghbors.toString();
                        text += "\nchunk mesh: visible:" + chunk.meshes.opaqueMesh.isVisible();
                        text += "\nchunk mesh: " + chunk.meshes;
                        text += "\nchunk last modified: " + MiscUtils.formatTime(chunk.lastModifiedTime);

                        Block block = Registrys.getBlock(chunk.data.getBlock(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z));
                        BlockData data = chunk.data.getBlockData(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z);

                        byte sun = chunk.data.getSun(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z);
                        text += "\n" + block + " data: " + printBlockData(data) + " type: " + Registrys.blocks.getBlockType(block.renderType);
                        text += "\nlight=" + getLightLevel(rayWorldPos.x, rayWorldPos.y, rayWorldPos.z)
                                + "  sun=" + (sun)
                                + "  torch=" + chunk.data.getTorch(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z);
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

    private String printBlockData(BlockData data) {
        if (data == null) return "null";
        else if (data.size() > 20)
            return "l=" + data.size() + "   \"" + new String(data.toByteArray())
                    .replaceAll("\n", "").replaceAll("\\s+", "") + "\"";
        else return data.toString();
    }


}
