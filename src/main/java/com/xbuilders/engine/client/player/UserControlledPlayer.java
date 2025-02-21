package com.xbuilders.engine.client.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.GameSceneEvents;
import com.xbuilders.engine.server.block.BlockRegistry;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.server.item.ItemStack;
import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.server.players.PositionLock;
import com.xbuilders.engine.client.player.camera.Camera;
import com.xbuilders.engine.client.visuals.gameScene.GameUI;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.json.ItemStackTypeAdapter;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.server.item.StorageSpace;
import com.xbuilders.engine.server.world.data.WorldData;
import com.xbuilders.content.vanilla.items.Blocks;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import java.io.*;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xbuilders.engine.client.visuals.gameScene.GameUI.printKeyConsumption;

public class UserControlledPlayer extends Player implements GameSceneEvents {


    public Camera camera;
    private final ClientWindow window;
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
    public final float MAX_HEALTH = 20f;
    public final float MAX_HUNGER = 20f;
    public final float MAX_OXYGEN = 20f;

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
        return status_oxygen;
    }

    public float getHungerLevel() {
        return status_hunger;
    }

    public float getHealth() {
        return status_health;
    }

    public void addHealth(float damage) {
        status_health += damage;
    }

    public void addHunger(float hungerPoints) {
        status_hunger += hungerPoints;
    }

    private void updateHealthbars(Block playerHead, Block playerFeet, Block playerWaist) {
        if (Server.getGameMode() == GameMode.ADVENTURE) {

            if (status_hunger > 0) {
                if (runningMode) status_hunger -= 0.0015f;
                else status_hunger -= 0.0004f;
            }

            float enterDamage = Math.max(Math.max(playerHead.enterDamage, playerFeet.enterDamage), playerWaist.enterDamage);
            if (enterDamage > 0) {
                status_health -= enterDamage;
            } else if (status_oxygen <= 0 || status_hunger <= 0) {
                status_health -= 0.2f;
            } else if (status_health < MAX_HEALTH && status_hunger > 3) {//Regenerate
                status_health += 0.004f;
            }
            if (playerHead.isLiquid()) {
                status_oxygen -= 0.02f;
            } else if (status_oxygen < MAX_OXYGEN) status_oxygen += 0.02f;


            if (status_health <= 0) {
                die();
            }
            status_hunger = MathUtils.clamp(status_hunger, 0, MAX_HUNGER);
            status_health = MathUtils.clamp(status_health, 0, MAX_HEALTH);
            status_oxygen = MathUtils.clamp(status_oxygen, 0, MAX_OXYGEN);
        }
    }

    private void resetHealthStats() {
        dieMode = false;
        status_health = MAX_HEALTH;
        status_hunger = MAX_HUNGER;
        status_oxygen = MAX_OXYGEN;
    }

    public void die() {
        dismount();
        dieMode = true;
        ClientWindow.popupMessage.message("Game Over!", "Press OK to teleport to spawnpoint", () -> {
            if (!inventory.isEmpty()) {
                Server.setBlock(Blocks.BLOCK_FLAG_BLOCK, (int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z);
            }
            System.out.println("Teleporting to spawnpoint... ("
                    + status_spawnPosition.x + ", " + status_spawnPosition.y + ", " + status_spawnPosition.z + ")");
            teleportSafely(status_spawnPosition);
            resetHealthStats();
            dieMode = false;
        });
    }

    private void teleportSafely(Vector3f target) {
        aabb.updateBox();
        System.out.println("Teleporting safely to " + target.x + ", " + target.y + ", " + target.z);
        worldPosition.set(target);
        Vector3f pos = new Vector3f();
        while (Server.world.inBounds((int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z)) {
            aabb.updateBox();
            System.out.println("Checking for ground: " + worldPosition.y);
            getPlayerBoxBottom(pos);
            if (Server.world.getBlock((int) pos.x, (int) pos.y, (int) pos.z).solid ||
                    Server.world.getBlock((int) pos.x, (int) pos.y + 1, (int) pos.z).solid) {
                System.out.println("Found ground at " + worldPosition.y);
                break;
            } else {
                worldPosition.y++;
            }
        }
        aabb.updateBox();

        System.out.println("The players head is " + getBlockAtPlayerHead());
        if (!isSafeHeadPos(getBlockAtPlayerHead())) {
            System.out.println("Moving up");
            while (Server.world.inBounds((int) worldPosition.x, (int) worldPosition.y, (int) worldPosition.z)) {
                aabb.updateBox();
                System.out.println("Checking for air: " + worldPosition.y);
                pos.set(
                        (int) Math.floor(worldPosition.x),
                        (int) Math.floor(worldPosition.y),
                        (int) Math.floor(worldPosition.z));
                if (isSafeHeadPos(Server.world.getBlock((int) pos.x, (int) pos.y, (int) pos.z))) {
                    System.out.println("Found air at " + worldPosition.y);
                    break;
                } else {
                    worldPosition.y--;
                    aabb.updateBox();
                }
            }
        }
    }

    private boolean isSafeHeadPos(Block block) {
        return block == BlockRegistry.BLOCK_AIR;
        //return //!block.solid && block.enterDamage <= 0;
    }

    //Other processes could mess with this if we dont have the proper way to set the spawnpoint
    private Vector3f status_spawnPosition = new Vector3f();

    public void setSpawnPoint(float x, float y, float z) {
        Server.alertClient("Spawn set to " + x + ", " + y + ", " + z);
        status_spawnPosition.set(x, y, z);
    }

    public void getPlayerBoxTop(Vector3f playerBoxBottom) {
        aabb.updateBox();
        playerBoxBottom.set(
                (GameScene.userPlayer.aabb.box.min.x + GameScene.userPlayer.aabb.box.max.x) / 2,
                GameScene.userPlayer.aabb.box.min.y,
                (GameScene.userPlayer.aabb.box.min.z + GameScene.userPlayer.aabb.box.max.z) / 2);
    }

    public void getPlayerBoxBottom(Vector3f playerBoxTop) {
        aabb.updateBox();
        playerBoxTop.set(
                (GameScene.userPlayer.aabb.box.min.x + GameScene.userPlayer.aabb.box.max.x) / 2,
                GameScene.userPlayer.aabb.box.max.y,
                (GameScene.userPlayer.aabb.box.min.z + GameScene.userPlayer.aabb.box.max.z) / 2
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


    public boolean holdingItem(Item item) {
        ItemStack s = getSelectedItem();
        if (s != null) {
            return s.item == item;
        }
        return false;
    }

    public void acquireItem(ItemStack itemStack) {
        inventory.acquireItem(itemStack, selectedItemIndex);
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
                    worldPosition.x = jsonObject.get("x").getAsFloat();
                    worldPosition.y = jsonObject.get("y").getAsFloat();
                    worldPosition.z = jsonObject.get("z").getAsFloat();
                }
                if (jsonObject.has("spawnX") && jsonObject.has("spawnY") && jsonObject.has("spawnZ")) {
                    status_spawnPosition.x = jsonObject.get("spawnX").getAsFloat();
                    status_spawnPosition.y = jsonObject.get("spawnY").getAsFloat();
                    status_spawnPosition.z = jsonObject.get("spawnZ").getAsFloat();
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
    private static final int KEY_SPRINT = GLFW.GLFW_KEY_LEFT_SHIFT;

    final static float WALK_SPEED = 6.5f;
    final static float RUN_SPEED = 14f;
    final static float FLY_VERTICAL_SPEED = 14f;
    final static float FLY_WALK_SPEED = 14f;
    final static float FLY_RUN_SPEED = 30f;//XB2 runSpeed = 12f * 2.5f

    public static int getCreateMouseButton() {
        return (ClientWindow.settings.game_switchMouseButtons ? GLFW.GLFW_MOUSE_BUTTON_LEFT : GLFW.GLFW_MOUSE_BUTTON_RIGHT);
    }

    public static int getDeleteMouseButton() {
        return (ClientWindow.settings.game_switchMouseButtons ? GLFW.GLFW_MOUSE_BUTTON_RIGHT : GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    private boolean keyInputAllowed() {
        return !ClientWindow.gameScene.ui.anyMenuOpen() && canMove();
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
        if (Server.getGameMode() == GameMode.SPECTATOR) {
        } else {
            dismount();
            if (positionHandler.isGravityEnabled()) {
                if (usePositionHandler)
                    positionHandler.jump();
                usePositionHandler = true;
                positionHandler.collisionsEnabled = true;
                positionHandler.setGravityEnabled(true);
            } else disableFlying();
        }
    }

    private boolean jumpKeyPressed = false;

    public UserControlledPlayer(ClientWindow window,
                                Matrix4f projection, Matrix4f view,
                                Matrix4f centeredView) throws IOException {
        super();
        this.window = window;
        this.projection = projection;
        this.view = view;
        inventory = new StorageSpace(33);
        camera = new Camera(this, window, projection, view, centeredView);
        positionHandler = new PositionHandler(window, Server.world, aabb, aabb);
        positionHandler.callback_onGround = (fallDistance) -> {
            if (fallDistance > 3) {
                float damage = MathUtils.map(fallDistance, 3, 15, 0, MAX_HEALTH);
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
        Server.world.chunkShader.setFlashlightDistance(distance);
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
        gameModeChangedEvent(Server.getGameMode());
    }

    public void stopGameEvent() {

    }

    public void gameModeChangedEvent(GameMode gameMode) {
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
        return Server.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y),
                (int) Math.floor(worldPosition.z));
    }

    public Block getBlockAtPlayerFeet() {
        return Server.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y + aabb.box.getYLength()),
                (int) Math.floor(worldPosition.z));
    }

    public Block getBlockAtPlayerWaist() {
        return Server.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y + aabb.box.getYLength()) - 1,
                (int) Math.floor(worldPosition.z));
    }

    public Block getBlockAtCameraPos() {
        return Server.world.getBlock(
                (int) Math.floor(camera.position.x),
                (int) Math.floor(camera.position.y),
                (int) Math.floor(camera.position.z));
    }

    public boolean isInsideOfLadder() {
        return getBlockAtPlayerHead().climbable
                ||
                Server.world.getBlock(
                        (int) Math.floor(worldPosition.x),
                        (int) Math.floor(worldPosition.y + aabb.box.getYLength()),
                        (int) Math.floor(worldPosition.z)).climbable;
    }


    Block prevCameraBlock = BlockRegistry.BLOCK_AIR;
    Block playerBlock = BlockRegistry.BLOCK_AIR;

    public void render(boolean holdMouse) {
    }

    public void updateAndRender(boolean holdMouse) {
        if (canMove()) camera.cursorRay.update();
        Block prevHeadBlock = getBlockAtPlayerHead();
        Block blockAtWaist = getBlockAtPlayerWaist();
        Block blockAtFeet = getBlockAtPlayerFeet();
        updateHealthbars(prevHeadBlock, blockAtFeet, blockAtWaist);


        if (positionLock != null && (positionLock.entity == null || positionLock.entity.isDestroyMode() || dieMode)) {
            //Dismount if riding entity is destroyed
            dismount();
        }

        Block newCameraBlock = getBlockAtCameraPos();
        if (newCameraBlock != prevCameraBlock) {
            prevCameraBlock = newCameraBlock;
            if (newCameraBlock.isAir()) {//Air is always transparent
                ClientWindow.gameScene.ui.setOverlayColor(0, 0, 0, 0);
            } else if (newCameraBlock.opaque && newCameraBlock.colorInPlayerHead[3] == 0
                    && positionHandler.collisionsEnabled && positionLock == null
                    && camera.getThirdPersonDist() == 0) { //If we are opaque, don't have a color and we are not in passthrough mode
                ClientWindow.gameScene.ui.setOverlayColor(0, 0, 0, 1);
            } else {
                ClientWindow.gameScene.ui.setOverlayColor(
                        newCameraBlock.colorInPlayerHead[0],
                        newCameraBlock.colorInPlayerHead[1],
                        newCameraBlock.colorInPlayerHead[2],
                        newCameraBlock.colorInPlayerHead[3]);
            }
        }


        if (prevHeadBlock.id != playerBlock.id) {
            playerBlock = prevHeadBlock;
            if (newCameraBlock.renderType == BlockRegistry.LIQUID_BLOCK_TYPE_ID) {
                System.out.println("Water!");
                positionHandler.setVelocity(0, 0, 0);
                positionHandler.setFallMedium(PositionHandler.DEFAULT_GRAVITY / 8,
                        PositionHandler.MAX_TERMINAL_VELOCITY / 30);
            } else if (newCameraBlock.isAir()) {
                System.out.println("Air!");
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
            runningMode = window.isKeyPressed(KEY_SPRINT);
//            /**
//             * We have to handle some keys in a separate way, due to some keyboards having key rollover
//             * https://en.wikipedia.org/wiki/Rollover_(key)
//             */
//            if (window.isKeyPressed(KEY_JUMP)) {
//                System.out.println("JUMP");
//                if (!jumpKeyPressed) {
//                    jumpKeyPressed = true;
//                    jump();
//                }
//            } else jumpKeyPressed = false;


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
            if (ClientWindow.settings.game_autoJump &&
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
            Server.server.sendPlayerPosition(lastOrientation);
        }
    }

    private boolean canRun() {
        return
                Server.getGameMode() == GameMode.FREEPLAY
                        || Server.getGameMode() == GameMode.SPECTATOR
                        || status_hunger > 5;
    }


    private boolean downKeyPressed() {
        if (Server.getGameMode() == GameMode.SPECTATOR)
            return window.isKeyPressed(KEY_FLY_DOWN) || window.isKeyPressed(KEY_JUMP);
        return window.isKeyPressed(KEY_FLY_DOWN);
    }

    private boolean isFlyingMode = true;

    public void enableFlying() {
        if (Server.getGameMode() == GameMode.FREEPLAY || Server.getGameMode() == GameMode.SPECTATOR) {
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
                GameUI.hotbar.pickItem(camera.cursorRay, Server.getGameMode() == GameMode.FREEPLAY);
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
                case KEY_JUMP -> {
                    jump();
                }
                case KEY_FLY_UP -> enableFlying();
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (key == KEY_CHANGE_RAYCAST_MODE && Server.getGameMode() == GameMode.FREEPLAY) {
                camera.cursorRay.angelPlacementMode = !camera.cursorRay.angelPlacementMode;
            }
            switch (key) {
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
        Vector3f pos = new Vector3f().set(GameScene.userPlayer.worldPosition);
        Vector3f addition = new Vector3f().set(GameScene.userPlayer.camera.look.x, 0, GameScene.userPlayer.camera.look.z).mul(1.5f);
        pos.add(addition);
        return Server.placeItemDrop(
                pos,
                itemStack,
                true);
    }


    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        return false;
    }


}
