/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.gameScene;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.window.WindowEvents;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.utils.progress.ProgressData;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCf;
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.NKWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_BLEND;

/**
 * This is a Java greedy meshing implementation based on the javascript
 * implementation written by Mikola Lysenko and described in this blog post:
 * <p>
 * http://0fps.wordpress.com/2012/06/30/meshing-in-a-minecraft-game/
 * <p>
 * The principal changes are:
 * <p>
 * - Porting to Java - Modification in order to compare *voxel faces*, rather
 * than voxels themselves - Modification to provide for comparison based on
 * multiple attributes simultaneously
 * <p>
 * This class is ready to be used on the JMonkey platform - but the algorithm
 * should be usable in any case.
 *
 * @author Rob O'Leary
 */
public class GameScene implements WindowEvents {


    final boolean WAIT_FOR_ALL_CHUNKS_TO_LOAD_BEFORE_STARTING = true;
    public static final World world = new World();
    public static boolean drawWireframe;
    public static UserControlledPlayer player;
    public static List<Player> otherPlayers;


    NKWindow window;

    public final static Matrix4f projection = new Matrix4f();
    public final static Matrix4f view = new Matrix4f();

    public GameScene(NKWindow window) throws Exception {
        this.window = window;
        specialMode = true;
        player = new UserControlledPlayer(Main.user);
        otherPlayers = new ArrayList<>();
    }

    private static String alertMessage = "";
    private static long alertTime = 0;

    public static void alert(String s) {
        alertMessage = s;
        alertTime = System.currentTimeMillis();
        System.out.println("GAME: " + s);
    }

    public void closeGame() {
        if (world.terrain != null) {
            System.out.println("Closing " + world.info.getName() + "...");
            player.stopGame();
            world.stopGame(player.worldPosition);
            Main.game.saveState();
        }
    }

    int completeChunks, framesWithCompleteChunkValue;

    public void newGameUpdate(WorldInfo worldInfo, ProgressData prog) {
        switch (prog.stage) {
            case 0 -> {
                System.out.println("\n\nStarting game " + worldInfo.getName() + "...");
                if (worldInfo.getSpawnPoint() == null) {
                    player.worldPosition.set(0, 0, 0);
                    boolean ok = world.startGame(prog, worldInfo, new Vector3f(0, 0, 0));
                    if (!ok) {
                        prog.abort();
                        Main.goToMenuPage();
                    }
                } else {
                    System.out.println("Loading spawn point: " + player.worldPosition);
                    player.worldPosition.set(
                            worldInfo.getSpawnPoint().x,
                            worldInfo.getSpawnPoint().y,
                            worldInfo.getSpawnPoint().z);
                    world.startGame(prog, worldInfo, player.worldPosition);
                }
                prog.stage++;
            }
            case 1 -> {
                waitForTasksToComplete(prog);
            }
            case 2 -> {
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
                Main.game.startGame(worldInfo);
                player.startGame();
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

    public void init(UIResources uiResources, MyGame game) throws IOException {
        setProjection();
        ui = new GameUI(Main.game, window.ctx, window, uiResources);
        world.init(ItemList.blocks.textures);
        player.init(window, world, projection, view);
        ui.init();
        Main.game.uiInit(window.ctx, window, uiResources, ui);
    }

    boolean holdMouse;
    public static boolean specialMode;
    public final static Vector3f backgroundColor = new Vector3f(0.5f, 0.5f, 1.0f);
    public static GameUI ui;

    public void render() throws IOException {
        Main.frameTester.startProcess();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); //Clear not only the color but the depth buffer
        GL11C.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f); //Set the background color
        holdMouse = !ui.menusAreOpen() && window.windowIsFocused();
        Main.frameTester.endProcess("Clearing buffer");

        glEnable(GL_DEPTH_TEST);   // Enable depth test
        glDepthFunc(GL_LESS); // Accept fragment if it closer to the camera than the former one

        Main.frameTester.startProcess();
        player.update(holdMouse);
        Main.frameTester.endProcess("Updating player");
        enableBackfaceCulling();
        Main.frameTester.startProcess();

        glEnable(GL_BLEND); //Enable transparency
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        world.drawChunks(projection, view, player.worldPosition);
        Main.frameTester.endProcess("Drawing chunks");
        setInfoText();

        ui.draw();


        Main.game.update();
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
        projection.identity().perspective(
                (float) Math.toRadians(70.0f), //Fov
                (float) window.getWidth() / (float) window.getHeight(), //screen ratio
                0.1f, 10000.0f); //display range (clipping planes)
    }

    public void keyEvent(int key, int scancode, int action, int mods) {
        if (!ui.menusAreOpen()) {
            player.keyEvent(key, scancode, action, mods);
        }
        ui.keyEvent(key, scancode, action, mods);
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_F3 -> debugText = !debugText;
                case GLFW.GLFW_KEY_P -> specialMode = !specialMode;
                case GLFW.GLFW_KEY_Z -> drawWireframe = !drawWireframe;
            }
        }
    }

    public void mouseButtonEvent(int button, int action, int mods) {
        ui.mouseButtonEvent(button, action, mods);
        if (!ui.menusAreOpen()) {
            player.mouseButtonEvent(button, action, mods);
        }
    }

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        boolean letUIHandleScroll = true;
        if (!ui.menusAreOpen()) {
            letUIHandleScroll = !player.mouseScrollEvent(scroll, xoffset, yoffset);
        }
        if (letUIHandleScroll) ui.mouseScrollEvent(scroll, xoffset, yoffset);
    }


    boolean debugText = false;
    public static WCCi rayWCC = new WCCi();

    private void setInfoText() {
        String text = "";
        try {
            WCCf wcc2 = new WCCf();
            wcc2.set(player.worldPosition);
            text += "\nPlayer pos: " + MiscUtils.printVector(player.worldPosition);

            if (player.camera.cursorRay.hitTarget() || player.camera.cursorRay.cursorRayHitAllBlocks) {

                if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
                    rayWCC.set(player.camera.cursorRay.getHitPosPlusNormal());
                    text += "\nRay+normal (Q): \n\t" + player.camera.cursorRay.toString() + "\n\t" + rayWCC.toString() + "\n";
                } else {
                    rayWCC.set(player.camera.cursorRay.getHitPos());
                    text += "\nRay hit (Q): \n\t" + player.camera.cursorRay.toString() + "\n\t" + rayWCC.toString() + "\n";
                }

                Chunk chunk = world.getChunk(rayWCC.chunk);
                if (chunk != null) {
                    text += "\nchunk gen status: " + chunk.getGenerationStatus() + ", pillar loaded: " + chunk.pillarInformation.isPillarLoaded();
                    text += "\nchunk mesh: visible:" + chunk.meshes.opaqueMesh.isVisible();
                    text += "\nchunk mesh: " + chunk.meshes;

                    BlockData data = chunk.data.getBlockData(
                            rayWCC.chunkVoxel.x,
                            rayWCC.chunkVoxel.y,
                            rayWCC.chunkVoxel.z);
                    Block block = ItemList.getBlock(chunk.data.getBlock(
                            rayWCC.chunkVoxel.x,
                            rayWCC.chunkVoxel.y,
                            rayWCC.chunkVoxel.z)
                    );

                    byte sun = chunk.data.getSun(
                            rayWCC.chunkVoxel.x,
                            rayWCC.chunkVoxel.y,
                            rayWCC.chunkVoxel.z);
                    text += "\nblock: " + block + " data: "
                            + (data == null ? "null" : data.toString())
                            + " type: " + ItemList.blocks.getBlockType(block.type);
                    text += "\nsun: " + (sun) + ", torch: " + chunk.data.getTorch(
                            rayWCC.chunkVoxel.x,
                            rayWCC.chunkVoxel.y,
                            rayWCC.chunkVoxel.z);
                }

            }
            ;
            text += "\nPlayer camera: " + player.camera.toString();
            text += "\nSpecial Mode: " + specialMode;
        } catch (Exception ex) {
            text = "Error: " + ex.getMessage();
            ex.printStackTrace();
        }


        if (System.currentTimeMillis() - alertTime < 1000) {
            text = alertMessage;
            ui.setInfoText(text);
        } else if (Main.devMode || debugText) {
            ui.setInfoText(text);
        }
    }
}
