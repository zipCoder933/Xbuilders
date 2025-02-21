package com.xbuilders.engine.client.visuals.gameScene;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.server.world.World;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.data.WorldData;
import com.xbuilders.engine.server.world.skybox.SkyBackground;
import com.xbuilders.engine.server.world.wcc.WCCf;
import com.xbuilders.engine.server.world.wcc.WCCi;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.progress.ProgressData;
import com.xbuilders.window.WindowEvents;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengles.GLES20.GL_BLEND;

public class GameScene implements WindowEvents {
    public final static Matrix4f projection = new Matrix4f();
    public final static Matrix4f view = new Matrix4f();
    public final static Matrix4f centeredView = new Matrix4f();
    public static boolean drawWireframe;
    public static boolean drawBoundingBoxes;
    public static UserControlledPlayer userPlayer;
    public static SkyBackground background;
    static ClientWindow window;
    public boolean holdMouse;
    public static boolean specialMode;
    public static GameUI ui;
    public boolean writeDebugText = false;


    public GameScene(ClientWindow window) throws Exception {
        this.window = window;
    }

    public static void client_hudText(String s) {
        ui.hudText.setText(s);
    }


    public void initialize(ClientWindow window) throws Exception {
        setProjection();
        ui = new GameUI(ClientWindow.game, window.ctx, window);
        ui.init();
    }


    public static void alert(String s) {
        ui.infoBox.addToHistory("GAME: " + s);
    }

    public static void consoleOut(String s) {
        ui.infoBox.addToHistory(s);
    }

    public static void pauseGame() {
        if (window.isFullscreen()) window.minimizeWindow();
        ui.baseMenu.setOpen(true);
    }

    public static void unpauseGame() {
        if (window.isFullscreen()) ClientWindow.restoreWindow();
    }


    public void startGameEvent(WorldData worldData, NetworkJoinRequest req, ProgressData prog) {
        if (ClientWindow.devMode) writeDebugText = true;
    }

    public void stopGameEvent() {
    }

    public void render() {
        ClientWindow.frameTester.startProcess();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); //Clear not only the color but the depth buffer
//        GL11C.glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, 1.0f); //Set the background color
        background.draw(GameScene.projection, GameScene.centeredView);   //Draw the background BEFORE ANYTHING ELSE! (Anything drawn before will be overridden)

        boolean progressDay = !ClientWindow.devMode;
        GameScene.background.update(progressDay);

        holdMouse = !ui.releaseMouse() && window.windowIsFocused();
        ClientWindow.frameTester.endProcess("Clearing buffer");

        glEnable(GL_DEPTH_TEST);   // Enable depth test
        glDepthFunc(GL_LESS); // Accept fragment if it closer to the camera than the former one

        //The user player is one thing that the client has full control over
        //The client will check into the server occasionally to see if the server has any updates for the player
        userPlayer.updateAndRender(ClientWindow.gameScene.holdMouse);
        userPlayer.render(ClientWindow.gameScene.holdMouse);
        ClientWindow.server.server.drawPlayers(GameScene.projection, GameScene.view);

        ClientWindow.gameScene.enableBackfaceCulling();
        ClientWindow.frameTester.startProcess();

        glEnable(GL_BLEND); //Enable transparency
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        ClientWindow.server.world.drawChunks(GameScene.projection, GameScene.view, userPlayer.worldPosition);
        ClientWindow.frameTester.endProcess("Drawing chunks");


        setInfoText();
        ui.draw();
    }

    public void windowUnfocusEvent() {
        if (window.isFullscreen()) ui.baseMenu.setOpen(true);
        else if (!ui.anyMenuOpen()) {
            ui.baseMenu.setOpen(true);
        }
        holdMouse = false;
    }

    public void windowResizeEvent(int width, int height) {
        setProjection();
        ui.windowResizeEvent(width, height);
    }


    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (ui.keyEvent(key, scancode, action, mods)) {
        } else if (ClientWindow.game.keyEvent(key, scancode, action, mods)) {
        } else {
            userPlayer.keyEvent(key, scancode, action, mods);
        }
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_F3 -> ClientWindow.gameScene.writeDebugText = !ClientWindow.gameScene.writeDebugText;
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
        } else if (ClientWindow.game.uiMouseScrollEvent(scroll, xoffset, yoffset)) {
        } else {
            ui.hotbar.mouseScrollEvent(scroll, xoffset, yoffset);
        }
        return true;
    }


    public static void enableBackfaceCulling() {
        //If backface culling is not working, it means that another process has probably disabled it, after init3D.
        glEnable(GL_CULL_FACE); // enable face culling
        glFrontFace(GL_CCW);// specify the winding order of frontRay-facing triangles
        glCullFace(GL_BACK);// specify which faces to cull
    }

    public void setProjection() {
        projection.identity().perspective((float) Math.toRadians(70.0f), //Fov
                (float) window.getWidth() / (float) window.getHeight(), //screen ratio
                0.1f, 10000.0f); //display range (clipping planes)
    }


    private WCCi rayWCC = new WCCi();
    private Vector3i rayWorldPos = new Vector3i();

    private void setInfoText() {
        World world = Server.world;
        if (writeDebugText) {
            String text = "";
            try {
                WCCf playerWCC = new WCCf();
                playerWCC.set(userPlayer.worldPosition);
                text += ClientWindow.mfpAndMemory + "   smoothDelta=" + window.smoothFrameDeltaSec + "\n";
                text += "Saved " + world.getTimeSinceLastSave() + "ms ago\n";
                text += "PLAYER pos: " +
                        ((int) userPlayer.worldPosition.x) + ", " +
                        ((int) userPlayer.worldPosition.y) + ", " +
                        ((int) userPlayer.worldPosition.z) +
                        "    velocity: " + MiscUtils.printVector(userPlayer.positionHandler.getVelocity());
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
                        text += "\n" + block + " data: " + printBlockData(data) + " typeReference: " + Registrys.blocks.getBlockType(block.renderType);
                        text += "\nlight=" + Server.getLightLevel(rayWorldPos.x, rayWorldPos.y, rayWorldPos.z)
                                + "  sun=" + (sun)
                                + "  torch=" + chunk.data.getTorch(rayWCC.chunkVoxel.x, rayWCC.chunkVoxel.y, rayWCC.chunkVoxel.z);
                    }

                }

                text += "\nSpecial Mode: " + specialMode;
                text += "\nAny Menu Open: " + ui.anyMenuOpen();
                text += "\nBase Menus Open: " + ui.baseMenusOpen();

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
