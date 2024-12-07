package com.xbuilders.engine.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.GameSceneEvents;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.player.camera.Camera;
import com.xbuilders.engine.ui.gameScene.GameUI;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.json.ItemStackTypeAdapter;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.items.item.StorageSpace;
import com.xbuilders.engine.world.data.WorldData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.ui.gameScene.GameUI.printKeyConsumption;

public class UserControlledPlayer extends Player implements GameSceneEvents {


    public Camera camera;
    private final MainWindow window;
    final Vector4f lastOrientation = new Vector4f();
    public boolean runningMode;
    Matrix4f projection;
    Matrix4f view;
    boolean isClimbing = false;
    public PositionHandler positionHandler;
    boolean usePositionHandler = true;

    public PositionLock positionLock;
    boolean autoForward = false;
    boolean autoJump_unCollided = true;
    float autoJump_ticksWhileColidingWithBlock = 0;

    final static Vector3f playerBoxBottom = new Vector3f();
    final static Vector3f playerBoxTop = new Vector3f();

    public Vector3f getPlayerBoxBottom() {
        playerBoxBottom.set(
                (GameScene.player.aabb.box.min.x + GameScene.player.aabb.box.max.x) / 2,
                GameScene.player.aabb.box.min.y,
                (GameScene.player.aabb.box.min.z + GameScene.player.aabb.box.max.z) / 2);
        return playerBoxBottom;
    }

    public Vector3f getPlayerBoxTop() {
        playerBoxTop.set(
                (GameScene.player.aabb.box.min.x + GameScene.player.aabb.box.max.x) / 2,
                GameScene.player.aabb.box.max.y,
                (GameScene.player.aabb.box.min.z + GameScene.player.aabb.box.max.z) / 2
        );
        return playerBoxTop;
    }


    //Saving/loading in world
    public final StorageSpace inventory;
    private int selectedItemIndex;
    private final Gson pdGson = new GsonBuilder()
            .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackTypeAdapter())
            .create();

    public void changeSelectedIndex(float increment) {
        selectedItemIndex += increment;
        selectedItemIndex = (MathUtils.clamp(selectedItemIndex, 0, inventory.size() - 1));
    }

    public void setSelectedIndex(int index) {
        selectedItemIndex = MathUtils.clamp(index, 0, inventory.size() - 1);
        selectedItemIndex = (MathUtils.clamp(selectedItemIndex, 0, inventory.size() - 1));
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public ItemStack getSelectedItem() {
        return inventory.get(selectedItemIndex);
    }

    public final String PLAYER_DATA_FILE = "player.json";

    public void saveToWorld(WorldData worldData) {
        File playerFile = new File(worldData.getDirectory(), PLAYER_DATA_FILE);

        JsonObject jsonObject = new JsonObject();
        // Serialize playerStuff and other data
        jsonObject.add("inventory", pdGson.toJsonTree(inventory.getList()));
        jsonObject.addProperty("x", (float) worldPosition.x);
        jsonObject.addProperty("y", (float) worldPosition.y);
        jsonObject.addProperty("z", (float) worldPosition.z);

        try {
            Files.write(playerFile.toPath(), pdGson.toJson(jsonObject).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromWorld(WorldData worldData) {
        File playerFile = new File(worldData.getDirectory(), PLAYER_DATA_FILE);

        if (playerFile.exists()) {
            try {
                String jsonData = new String(Files.readAllBytes(playerFile.toPath()));
                JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

                // Deserialize playerStuff if present
                if (jsonObject.has("inventory")) {
                    AtomicInteger i = new AtomicInteger(0);
                    jsonObject.get("inventory").getAsJsonArray().forEach(element -> {
                        ItemStack itemStack = pdGson.fromJson(element, ItemStack.class);
                        inventory.set(i.get(), itemStack);
                        i.addAndGet(1);
                    });
                }

                // Deserialize worldPosition
                if (jsonObject.has("x") && jsonObject.has("y") && jsonObject.has("z")) {
                    worldPosition.x = jsonObject.get("x").getAsInt();
                    worldPosition.y = jsonObject.get("y").getAsInt();
                    worldPosition.z = jsonObject.get("z").getAsInt();
                }
            } catch (Exception e) {
                ErrorHandler.report(e);
            }
        }
    }

    // Keys
    public static final int KEY_CHANGE_RAYCAST_MODE = GLFW.GLFW_KEY_TAB;
    public static final int KEY_CREATE_MOUSE_BUTTON = GLFW.GLFW_KEY_EQUAL;
    public static final int KEY_DELETE_MOUSE_BUTTON = GLFW.GLFW_KEY_MINUS;
    public static final int KEY_TOGGLE_AUTO_FORWARD = GLFW.GLFW_KEY_J;
    public static final int KEY_TOGGLE_VIEW = GLFW.GLFW_KEY_O;

    public static final int KEY_MOVE_RIGHT = GLFW.GLFW_KEY_D;
    public static final int KEY_MOVE_LEFT = GLFW.GLFW_KEY_A;
    public static final int KEY_MOVE_FORWARD = GLFW.GLFW_KEY_W;
    public static final int KEY_MOVE_BACKWARD = GLFW.GLFW_KEY_S;

    private static final int KEY_FLY_UP = GLFW.GLFW_KEY_F;
    public static final int KEY_FLY_DOWN = GLFW.GLFW_KEY_LEFT_CONTROL;
    private static final int KEY_JUMP = GLFW.GLFW_KEY_SPACE;

    final static float WALK_SPEED = 6.5f;
    final static float RUN_SPEED = 14f;
    final static float FLY_VERTICAL_SPEED = 14f;
    final static float FLY_WALK_SPEED = 14f;
    final static float FLY_RUN_SPEED = 30f;//XB2 runSpeed = 12f * 2.5f

    public static int getCreateMouseButton() {
        return (MainWindow.settings.game_switchMouseButtons ? GLFW.GLFW_MOUSE_BUTTON_LEFT : GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }

    public static int getDeleteMouseButton() {
        return (MainWindow.settings.game_switchMouseButtons ? GLFW.GLFW_MOUSE_BUTTON_RIGHT : GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private boolean keyInputAllowed() {
        return !GameScene.ui.anyMenuOpen();
    }

    public boolean leftKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(KEY_MOVE_LEFT);
    }

    public boolean rightKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(KEY_MOVE_RIGHT);
    }

    public boolean forwardKeyPressed() {
        if (autoForward) return true;
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(KEY_MOVE_FORWARD);
    }

    public boolean backwardKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(KEY_MOVE_BACKWARD);
    }


    private void disableGravity() {
        positionHandler.setGravityEnabled(false);
    }


    private void jump() {
        if (usePositionHandler)
            positionHandler.jump();
        usePositionHandler = true;
        positionHandler.collisionsEnabled = true;
        positionHandler.setGravityEnabled(true);
    }

    public UserControlledPlayer(MainWindow window,
                                Matrix4f projection, Matrix4f view,
                                Matrix4f centeredView) throws IOException {
        super();
        this.window = window;
        this.projection = projection;
        this.view = view;
        inventory = new StorageSpace(33);
        camera = new Camera(this, window, projection, view, centeredView);
        positionHandler = new PositionHandler(window, GameScene.world, aabb, aabb);
        userInfo.loadFromDisk();
    }

    public void init() {
        camera.init();
    }

    public void setFlashlight(float distance) {
        GameScene.world.chunkShader.setFlashlightDistance(distance);
    }

    public void startGameEvent(WorldData world) {
        autoForward = false;
        isFlyingMode = true;
        loadFromWorld(world);
        event_gameModeChanged(GameScene.getGameMode());
    }

    public void stopGameEvent() {

    }

    public void event_gameModeChanged(GameMode gameMode) {
        if (gameMode != GameMode.FREEPLAY) {
            disableFlying();
        }
        camera.cursorRay.angelPlacementMode = false;
        if (GameScene.getGameMode() == GameMode.FREEPLAY) {
            camera.cursorRay.setRayDistance(128);
        } else camera.cursorRay.setRayDistance(6);
    }

    public Block getBlockAtPlayerHead() {
        return GameScene.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y),
                (int) Math.floor(worldPosition.z));
    }

    public Block getBlockAtCameraPos() {
        return GameScene.world.getBlock(
                (int) Math.floor(camera.position.x),
                (int) Math.floor(camera.position.y),
                (int) Math.floor(camera.position.z));
    }

    public boolean isInsideOfLadder() {
        return getBlockAtPlayerHead().climbable
                ||
                GameScene.world.getBlock(
                        (int) Math.floor(worldPosition.x),
                        (int) Math.floor(worldPosition.y + aabb.box.getYLength()),
                        (int) Math.floor(worldPosition.z)).climbable;
    }


    Block cameraBlock, playerBlock;

    public void update(boolean holdMouse) {
        camera.cursorRay.update();

        if (positionLock != null && (positionLock.entity == null || positionLock.entity.isDestroyMode())) {
            //Dismount if riding entity is destroyed
            dismount();
        }

        Block newCameraBlock = getBlockAtCameraPos();
        if (newCameraBlock != cameraBlock) {
            cameraBlock = newCameraBlock;
            if (newCameraBlock.isAir()) {//Air is always transparent
                GameScene.ui.setOverlayColor(0, 0, 0, 0);
            } else if (newCameraBlock.opaque && newCameraBlock.colorInPlayerHead[3] == 0
                    && positionHandler.collisionsEnabled && positionLock == null
                    && camera.getThirdPersonDist() == 0) { //If we are opaque, don't have a color and we are not in passthrough mode
                GameScene.ui.setOverlayColor(0, 0, 0, 1);
            } else {
                GameScene.ui.setOverlayColor(
                        newCameraBlock.colorInPlayerHead[0],
                        newCameraBlock.colorInPlayerHead[1],
                        newCameraBlock.colorInPlayerHead[2],
                        newCameraBlock.colorInPlayerHead[3]);
            }
        }

        Block newPlayerBlock = getBlockAtPlayerHead();
        if (playerBlock == null || newPlayerBlock.id != playerBlock.id) {
            playerBlock = newPlayerBlock;
            if (newCameraBlock.renderType == BlockRegistry.LIQUID_BLOCK_TYPE_ID) {
                positionHandler.setVelocity(0, 0, 0);
                positionHandler.setFallMedium(PositionHandler.DEFAULT_GRAVITY / 8,
                        PositionHandler.MAX_TERMINAL_VELOCITY / 30);
            } else if (newCameraBlock.isAir()) {
                positionHandler.resetFallMedium();
            }
        }


        if (positionHandler.isGravityEnabled()) {
            disableFlying();
        }

        float speed;
        if (isFlyingMode) {
            if (runningMode) speed = FLY_RUN_SPEED;
            else speed = FLY_WALK_SPEED;
        } else {
            if (runningMode) speed = RUN_SPEED;
            else speed = WALK_SPEED;
        }

        if (positionLock != null) {
            worldPosition.set(positionLock.getPosition());
            usePositionHandler = false;
        } else {
            usePositionHandler = true;
            if (forwardKeyPressed()) {
                positionHandler.setVelocity(
                        camera.cameraForward.x * speed * window.smoothFrameDeltaSec,
                        camera.cameraForward.z * speed * window.smoothFrameDeltaSec);
            }
            if (backwardKeyPressed()) {
                positionHandler.setVelocity(
                        -(camera.cameraForward.x * speed * window.smoothFrameDeltaSec),
                        -(camera.cameraForward.z * speed * window.smoothFrameDeltaSec));
            }

            if (leftKeyPressed()) {
                positionHandler.setVelocity(
                        -(camera.right.x * speed * window.smoothFrameDeltaSec),
                        -(camera.right.z * speed * window.smoothFrameDeltaSec));
            }
            if (rightKeyPressed()) {
                positionHandler.setVelocity(
                        camera.right.x * speed * window.smoothFrameDeltaSec,
                        camera.right.z * speed * window.smoothFrameDeltaSec);
            }

            if (isInsideOfLadder()) {
                if (keyInputAllowed() && window.isKeyPressed(KEY_JUMP)) {
                    isClimbing = true;
                    disableFlying();
                    worldPosition.sub(0, 3f * window.smoothFrameDeltaSec, 0);
                } else {
                    isClimbing = true;
                    disableFlying();
                    worldPosition.add(0, 3f * window.smoothFrameDeltaSec, 0);
                }
                positionHandler.setGravityEnabled(false);
            } else {
                if (isClimbing) {
                    positionHandler.setGravityEnabled(true);
                    isClimbing = false;
                } else if (isFlyingMode) {
                    if (keyInputAllowed() && window.isKeyPressed(KEY_FLY_UP)) {
                        worldPosition.sub(0, FLY_VERTICAL_SPEED * window.smoothFrameDeltaSec, 0);
                        disableGravity();
                    } else if (keyInputAllowed() && window.isKeyPressed(KEY_FLY_DOWN)) {
                        worldPosition.add(0, FLY_VERTICAL_SPEED * window.smoothFrameDeltaSec, 0);
                        disableGravity();
                    }
                } else if (playerBlock.isLiquid() && keyInputAllowed() && window.isKeyPressed(KEY_JUMP)) {
                    positionHandler.addVelocity(0, -0.05f, 0);
                }
            }
        }

        if (usePositionHandler) {
            positionHandler.update();
            aabb.isSolid = true;
        } else {
            aabb.isSolid = false;
            aabb.update();
        }

        if (MainWindow.settings.game_autoJump &&
                (Math.abs(positionHandler.collisionHandler.collisionData.totalPenPerAxes.x) > 0.02 ||
                        Math.abs(positionHandler.collisionHandler.collisionData.totalPenPerAxes.z) > 0.02)
        ) {

            autoJump_ticksWhileColidingWithBlock += window.getMsPerFrame();
            if (autoJump_ticksWhileColidingWithBlock > 150 && autoJump_unCollided) {
                positionHandler.jump();
                autoJump_ticksWhileColidingWithBlock = 0;
                autoJump_unCollided = false;
            }
        } else {
            autoJump_ticksWhileColidingWithBlock = 0;
            autoJump_unCollided = true;
        }

        // The key to preventing shaking during collision is to update the camera AFTER
        // the position handler is done its job

        camera.update(holdMouse);
        this.pan = camera.pan;
        this.tilt = camera.tilt;

        camera.cursorRay.drawRay();
        if (camera.getThirdPersonDist() != 0.0f) {
            userInfo.getSkin().super_render(projection, view);
        }

        if (lastOrientation.x != worldPosition.x
                || lastOrientation.y != worldPosition.y
                || lastOrientation.z != worldPosition.z
                || lastOrientation.w != pan) {
            lastOrientation.set(worldPosition.x, worldPosition.y, worldPosition.z, pan);
            GameScene.server.sendPlayerPosition(lastOrientation);
        }
    }

    private boolean isFlyingMode = true;

    public void enableFlying() {
        if (GameScene.getGameMode() == GameMode.FREEPLAY) {
            isFlyingMode = true;
            positionHandler.setGravityEnabled(false);
            positionHandler.collisionsEnabled = false;
        }
    }

    public void disableFlying() {
        isFlyingMode = false;
        positionHandler.setGravityEnabled(true);
        positionHandler.collisionsEnabled = true;
    }

    public void mouseButtonEvent(int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (button == UserControlledPlayer.getCreateMouseButton()) {
                camera.cursorRay.clickEvent(true);
            } else if (button == UserControlledPlayer.getDeleteMouseButton()) {
                camera.cursorRay.clickEvent(false);
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                GameUI.hotbar.pickItem(camera.cursorRay, GameScene.getGameMode() == GameMode.FREEPLAY);
            }
        }
    }

    public void keyEvent(int key, int scancode, int action, int mods) {
        if (camera.cursorRay.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(camera.cursorRay.getClass());
        } else if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_LEFT_SHIFT -> runningMode = true;
                case KEY_JUMP -> {
                    dismount();
                    if (positionHandler.isGravityEnabled()) {
                        jump();
                    } else disableFlying();
                }
                case KEY_FLY_UP -> enableFlying();
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (key == KEY_CHANGE_RAYCAST_MODE && GameScene.getGameMode() == GameMode.FREEPLAY) {
                camera.cursorRay.angelPlacementMode = !camera.cursorRay.angelPlacementMode;
            }

            switch (key) {
                case GLFW.GLFW_KEY_LEFT_SHIFT -> runningMode = false;
                case KEY_TOGGLE_VIEW -> camera.cycleToNextView(10);
                case KEY_CREATE_MOUSE_BUTTON -> {
                    camera.cursorRay.clickEvent(true);
                }
                case KEY_DELETE_MOUSE_BUTTON -> {
                    camera.cursorRay.clickEvent(false);
                }
                case KEY_TOGGLE_AUTO_FORWARD -> autoForward = !autoForward;
            }
        }
    }

    private void dismount() {
        if (positionLock != null) {
            worldPosition.y = positionLock.entity.aabb.box.min.y - aabb.box.getYLength();
            positionLock = null;
        }
    }

    public Entity dropItem(ItemStack itemStack) {
        Vector3f pos = new Vector3f().set(GameScene.player.worldPosition);
        Vector3f addition = new Vector3f().set(GameScene.player.camera.look.x, 0, GameScene.player.camera.look.z).mul(1.5f);
        pos.add(addition);
        return GameScene.placeItemDrop(
                pos,
                itemStack,
                true);
    }


    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        return false;
    }

}
