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
import com.xbuilders.game.vanilla.items.Blocks;
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

    //Health
    private boolean dieMode;
    public final float MAX_HEALTH = 10f;
    public final float MAX_HUNGER = 10f;
    public final float MAX_OXYGEN = 10f;

    private float status_health;
    private float status_hunger;
    private float status_oxygen;

    public boolean isDieMode() {
        return dieMode;
    }

    public boolean canMove() {
        return !isDieMode();
    }

    public float getOxygenLevel() {
        return MathUtils.clamp(status_oxygen, 0, MAX_OXYGEN);
    }

    public float getHungerLevel() {
        return MathUtils.clamp(status_hunger, 0, MAX_HUNGER);
    }

    public float getHealth() {
        return MathUtils.clamp(status_health, 0, MAX_HEALTH);
    }

    public void addHealth(float damage) {
        status_health += damage;
    }

    public void addHunger(float hungerPoints) {
        status_hunger += hungerPoints;
    }

    private void updateHealthbars(Block playerHead, Block playerFeet, Block playerWaist) {
        if (GameScene.getGameMode() == GameMode.ADVENTURE) {
            if (status_health <= 0) {
                die();
            }

            if (status_hunger > 0) {
                status_hunger -= 0.00001f;
            }

            float enterDamage = Math.max(Math.max(playerHead.enterDamage, playerFeet.enterDamage), playerWaist.enterDamage);
            if (enterDamage > 0) {
                status_health -= enterDamage;
            } else if (status_oxygen <= 0 || status_hunger <= 0) {
                status_health -= 0.1f;
            } else if (status_health < MAX_HEALTH && status_hunger > 3) {//Regenerate
                status_health += 0.001f;
            }
            if (playerHead.solid) {
                status_oxygen -= 0.1f;
            } else if (playerHead.isLiquid()) {
                status_oxygen -= 0.01f;
            } else status_oxygen += 0.01f;
        }
    }

    private void resetHealthStats() {
        dieMode = false;
        status_health = MAX_HEALTH;
        status_hunger = MAX_HUNGER;
        status_oxygen = MAX_OXYGEN;
    }

    public void die() {
        dieMode = true;
        MainWindow.popupMessage.message("Game Over!", "Press OK to teleport to spawnpoint", () -> {
            System.out.println("Teleporting to spawnpoint...");
            if (!inventory.isEmpty()) {
                GameScene.setBlock(Blocks.BLOCK_FLAG, (int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z);
            }
            worldPosition.set(status_spawnPosition);
            resetHealthStats();
            dieMode = false;
        });
    }


    public Vector3f status_spawnPosition = new Vector3f();

    public void getPlayerBoxBottom(Vector3f playerBoxBottom) {
        playerBoxBottom.set(
                (GameScene.player.aabb.box.min.x + GameScene.player.aabb.box.max.x) / 2,
                GameScene.player.aabb.box.min.y,
                (GameScene.player.aabb.box.min.z + GameScene.player.aabb.box.max.z) / 2);
    }

    public void getPlayerBoxTop(Vector3f playerBoxTop) {
        playerBoxTop.set(
                (GameScene.player.aabb.box.min.x + GameScene.player.aabb.box.max.x) / 2,
                GameScene.player.aabb.box.max.y,
                (GameScene.player.aabb.box.min.z + GameScene.player.aabb.box.max.z) / 2
        );
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

        jsonObject.addProperty("spawnX", (float) status_spawnPosition.x);
        jsonObject.addProperty("spawnY", (float) status_spawnPosition.y);
        jsonObject.addProperty("spawnZ", (float) status_spawnPosition.z);

        jsonObject.addProperty("health", status_health);
        jsonObject.addProperty("hunger", status_hunger);
        jsonObject.addProperty("oxygen", status_oxygen);

        try {
            Files.write(playerFile.toPath(), pdGson.toJson(jsonObject).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromWorld(WorldData worldData) {
        File playerFile = new File(worldData.getDirectory(), PLAYER_DATA_FILE);
        inventory.clear();
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
                if (jsonObject.has("x") && jsonObject.has("y") && jsonObject.has("z")) {
                    worldPosition.x = jsonObject.get("x").getAsInt();
                    worldPosition.y = jsonObject.get("y").getAsInt();
                    worldPosition.z = jsonObject.get("z").getAsInt();
                }
                if (jsonObject.has("spawnX") && jsonObject.has("spawnY") && jsonObject.has("spawnZ")) {
                    status_spawnPosition.x = jsonObject.get("spawnX").getAsInt();
                    status_spawnPosition.y = jsonObject.get("spawnY").getAsInt();
                    status_spawnPosition.z = jsonObject.get("spawnZ").getAsInt();
                }
                if (jsonObject.has("health")) status_health = jsonObject.get("health").getAsFloat();
                if (jsonObject.has("hunger")) status_hunger = jsonObject.get("hunger").getAsFloat();
                if (jsonObject.has("oxygen")) status_oxygen = jsonObject.get("oxygen").getAsFloat();

                selectedItemIndex = 0;
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
        return !GameScene.ui.anyMenuOpen() && canMove();
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
        positionHandler.callback_onGround = (fallDistance) -> {
            if (fallDistance > 10) {
                float damage = MathUtils.map(fallDistance, 10, 30, 0, 5f);
                status_health -= damage;
            }
            //System.out.println("onGround: " + fallDistance);
        };
        userInfo.loadFromDisk();
    }

    public void init() {
        camera.init();
    }

    public void setFlashlight(float distance) {
        GameScene.world.chunkShader.setFlashlightDistance(distance);
    }

    /**
     * Called at the beginning of a new world
     *
     * @param worldInfo
     */
    public void newWorldEvent(WorldData worldInfo) {
        resetHealthStats();
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
        resetHealthStats();
        if (gameMode == GameMode.SPECTATOR) {
            enableFlying();
            setFlashlight(100);
        } else if (gameMode == GameMode.FREEPLAY) {
            setFlashlight(0);
            camera.cursorRay.setRayDistance(90);
        } else if (gameMode == GameMode.ADVENTURE) {
            disableFlying();
            setFlashlight(0);
            camera.cursorRay.setRayDistance(6);
        }
        camera.cursorRay.angelPlacementMode = false;
    }

    public Block getBlockAtPlayerHead() {
        return GameScene.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y),
                (int) Math.floor(worldPosition.z));
    }

    public Block getBlockAtPlayerFeet() {
        return GameScene.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y + aabb.box.getYLength()),
                (int) Math.floor(worldPosition.z));
    }

    public Block getBlockAtPlayerWaist() {
        return GameScene.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y + aabb.box.getYLength()) - 1,
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
        if (canMove()) camera.cursorRay.update();
        Block blockAtHead = getBlockAtPlayerHead();
        Block blockAtWaist = getBlockAtPlayerWaist();
        Block blockAtFeet = getBlockAtPlayerFeet();
        updateHealthbars(blockAtHead, blockAtFeet, blockAtWaist);


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


        if (playerBlock == null || blockAtHead.id != playerBlock.id) {
            playerBlock = blockAtHead;
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
            if (runningMode && canRun()) speed = RUN_SPEED;
            else speed = WALK_SPEED;
        }

        if (positionLock != null) {
            worldPosition.set(positionLock.getPosition());
            usePositionHandler = false;
        } else if (canMove()) {
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
                    } else if (keyInputAllowed() && downKeyPressed()) {
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

        if (canMove()) {
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

    private boolean canRun() {
        return
                GameScene.getGameMode() == GameMode.FREEPLAY
                        || GameScene.getGameMode() == GameMode.SPECTATOR
                        || status_hunger > 5;
    }


    private boolean downKeyPressed() {
        if (GameScene.getGameMode() == GameMode.SPECTATOR)
            return window.isKeyPressed(KEY_FLY_DOWN) || window.isKeyPressed(KEY_JUMP);
        return window.isKeyPressed(KEY_FLY_DOWN);
    }

    private boolean isFlyingMode = true;

    public void enableFlying() {
        if (GameScene.getGameMode() == GameMode.FREEPLAY || GameScene.getGameMode() == GameMode.SPECTATOR) {
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

    public boolean mouseButtonEvent(int button, int action, int mods) {
        if (isDieMode()) return false;

        if (action == GLFW.GLFW_PRESS) {
            if (button == UserControlledPlayer.getCreateMouseButton()) {
                camera.cursorRay.clickEvent(true);
                return true;
            } else if (button == UserControlledPlayer.getDeleteMouseButton()) {
                camera.cursorRay.clickEvent(false);
                return true;
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
                GameUI.hotbar.pickItem(camera.cursorRay, GameScene.getGameMode() == GameMode.FREEPLAY);
                return true;
            }
        }
        return false;
    }

    public void keyEvent(int key, int scancode, int action, int mods) {
        if (camera.cursorRay.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(camera.cursorRay.getClass());
        } else if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_LEFT_SHIFT -> runningMode = true;
                case KEY_JUMP -> {
                    if (GameScene.getGameMode() == GameMode.SPECTATOR) {
                    } else {
                        dismount();
                        if (positionHandler.isGravityEnabled()) {
                            jump();
                        } else disableFlying();
                    }
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
