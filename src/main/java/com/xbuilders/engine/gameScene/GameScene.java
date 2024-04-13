/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.gameScene;

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
import com.xbuilders.engine.utils.ErrorHandler;
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

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CCW;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glFrontFace;

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

    public static final World world = new World();
    public static boolean drawWireframe;
    public static UserControlledPlayer player;
    public static List<Player> otherPlayers;

    NKWindow window;

    final Matrix4f projection, view;

    public GameScene(NKWindow window) throws Exception {
        this.window = window;
        projection = new Matrix4f();
        view = new Matrix4f();
        specialMode = true;
        player = new UserControlledPlayer(Main.user);
        otherPlayers = new ArrayList<>();
    }

    public void close() throws IOException {
        System.out.println("Closing...");
        world.close(player.worldPosition);
        Main.game.saveState();
    }

    final AtomicInteger anyChunkIncomplete = new AtomicInteger(0);

    public void newGameUpdate(WorldInfo worldInfo, ProgressData prog) {
        switch (prog.stage) {
            case 0 -> {
                if (worldInfo.getSpawnPoint() == null) {
                    player.worldPosition.set(0, 0, 0);
                    world.newGame(prog, worldInfo, new Vector3f(0, 0, 0));
                    player.setNewSpawnPoint(world.terrain);
                } else {
                    System.out.println("Loading spawn point: " + player.worldPosition);
                    player.worldPosition.set(
                            worldInfo.getSpawnPoint().x,
                            worldInfo.getSpawnPoint().y,
                            worldInfo.getSpawnPoint().z);
                    world.newGame(prog, worldInfo, player.worldPosition);
                }
                prog.stage++;
            }
            case 1 -> {
                waitForTasksToComplete(prog);
            }
            default -> {
                Main.game.newGame(worldInfo);
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
        player.init(window, world, projection, view);
        world.init(ItemList.blocks.textures);
        ui.init();

    }

    boolean holdMouse;
    public static boolean specialMode;
    public final static Vector3f backgroundColor = new Vector3f(0.5f, 0.5f, 1.0f);
    public GameUI ui;

    public void render() throws IOException {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); //Clear not only the color but the depth buffer
        GL11C.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f); //Set the background color
        holdMouse = !ui.menusAreOpen() && window.windowIsFocused();

        init3D();
        player.update(holdMouse);
        enableBackfaceCulling();
        world.drawChunks(projection, view, player.worldPosition);
        setInfoText();
        ui.draw();
        Main.game.update();
    }

    public void windowResizeEvent(int width, int height) {
        setProjection();
        ui.windowResizeEvent(width, height);
    }

    private void init3D() {
        glEnable(GL_DEPTH_TEST);   // Enable depth test
        glDepthFunc(GL_LESS); // Accept fragment if it closer to the camera than the former one
    }

    private void enableBackfaceCulling() {
        //If backface culling is not working, it means that another process has probably disabled it, after init3D.
        glEnable(GL_CULL_FACE); // enable face culling
        glFrontFace(GL_CCW);// specify the winding order of frontRay-facing triangles
        glCullFace(GL_BACK);// specify which faces to cull
    }

    private void setProjection() {
        projection.identity().perspective(
                (float) Math.toRadians(60.0f), //Fov
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
                case GLFW.GLFW_KEY_P -> specialMode = !specialMode;
                case GLFW.GLFW_KEY_Z -> drawWireframe = !drawWireframe;
            }
        }
    }

    public void mouseButtonEvent(int button, int action, int mods) {
        if (!ui.menusAreOpen()) {
            player.mouseButtonEvent(button, action, mods);
        }
        ui.mouseButtonEvent(button, action, mods);
    }

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        boolean letUIHandleScroll = true;
        if (!ui.menusAreOpen()) {
            letUIHandleScroll = !player.mouseScrollEvent(scroll, xoffset, yoffset);
        }
        if (letUIHandleScroll) ui.mouseScrollEvent(scroll, xoffset, yoffset);
    }

    private void leaveGamePage() {
        try {
            Main.goToMenuPage();
            close();
        } catch (IOException ex) {
            ErrorHandler.handleFatalError(ex);
        }
    }

    public static WCCi rayWCC = new WCCi();

    private void setInfoText() {
        String text = "";
        try {
            WCCf wcc2 = new WCCf();
            wcc2.set(player.worldPosition);
            text += "\nPlayer pos: " + MiscUtils.printVector(player.worldPosition);

            if (player.camera.cursorRay.hitTarget || player.camera.cursorRayHitAllBlocks) {

                if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
                    rayWCC.set(player.camera.cursorRay.getHitPosPlusNormal());
                    text += "\nRay+normal (Q): \n\t" + player.camera.cursorRay.toString() + "\n\t" + rayWCC.toString() + "\n";
                } else {
                    rayWCC.set(player.camera.cursorRay.getHitPositionAsInt());
                    text += "\nRay (Q): \n\t" + player.camera.cursorRay.toString() + "\n\t" + rayWCC.toString() + "\n";
                }

                Chunk chunk = world.getChunk(rayWCC.chunk);
                if (chunk != null) {
                    text += "\nchunk gen status: " + chunk.generationStatus;
                    text += "\nchunk mesh: " + chunk.meshes;
                    BlockData data = chunk.data.getBlockData(
                            rayWCC.chunkVoxel.x,
                            rayWCC.chunkVoxel.y,
                            rayWCC.chunkVoxel.z);

                    byte sun = chunk.data.getSun(
                            rayWCC.chunkVoxel.x,
                            rayWCC.chunkVoxel.y,
                            rayWCC.chunkVoxel.z);
                    text += "\nblock data: " + (data == null ? "null" : data.toString());
                    text += "\nsun: " + (sun);
                    text += "\ntorch: " + chunk.data.getTorch(
                            rayWCC.chunkVoxel.x,
                            rayWCC.chunkVoxel.y,
                            rayWCC.chunkVoxel.z);
                }

            }
            ;
            text += "\nPlayer camera: " + player.camera.toString();
            text += "\nSpecial Mode: " + specialMode;
        } catch (Exception ex) {
            text = "Error";
        }
        ui.setInfoText(text);
    }
}
