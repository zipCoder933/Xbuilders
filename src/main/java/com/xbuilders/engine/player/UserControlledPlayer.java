package com.xbuilders.engine.player;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.*;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.camera.Camera;
import com.xbuilders.engine.player.pipeline.BlockEventPipeline;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.UserID;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.game.Main;
import com.xbuilders.window.BaseWindow;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import java.io.IOException;

import static com.xbuilders.engine.utils.math.MathUtils.positiveMod;
import static com.xbuilders.engine.world.wcc.WCCi.chunkDiv;

public class UserControlledPlayer extends Player {


    public Camera camera;
    public boolean allowKeyInput;
    BaseWindow window;


    final Vector4f lastOrientation = new Vector4f();

    public boolean runningMode;
    final static float UP_DOWN_SPEED = 14f;
    final float WALK_SPEED = 6.9f;
    final float RUN_SPEED = 14f;
    final float FLY_WALK_SPEED = 15f;
    final float FLY_RUN_SPEED = 30f;//XB2 runSpeed = 12f * 2.5f

    public float getPan() {
        return camera.pan;
    }

    public float getTilt() {
        return camera.tilt;
    }

    Matrix4f projection;
    Matrix4f view;
    boolean isClimbing = false;
    PositionHandler positionHandler;
    boolean usePositionHandler = true;
    public BlockEventPipeline eventPipeline;
    public PositionLock positionLock;


    //Mouse buttons
    public static int CREATE_MOUSE_BUTTON = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    public static int DELETE_MOUSE_BUTTON = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    // Keys
    public static final int KEY_CHANGE_RAYCAST_MODE = GLFW.GLFW_KEY_TAB;
    //    private static final int KEY_TOGGLE_PASSTHROUGH = GLFW.GLFW_KEY_P;
    public static final int KEY_CREATE_MOUSE_BUTTON = GLFW.GLFW_KEY_EQUAL;
    public static final int KEY_DELETE_MOUSE_BUTTON = GLFW.GLFW_KEY_MINUS;
    public static final int KEY_TOGGLE_VIEW = GLFW.GLFW_KEY_O;
    public static final int KEY_CROUCH = GLFW.GLFW_KEY_LEFT_CONTROL;

    private static final int KEY_ENABLE_FLYING = GLFW.GLFW_KEY_F;
    private static final int KEY_JUMP = GLFW.GLFW_KEY_SPACE;

    private boolean keyInputAllowed() {
        return allowKeyInput && !GameScene.ui.menusAreOpen();
    }

    public boolean leftKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(GLFW.GLFW_KEY_LEFT) || window.isKeyPressed(GLFW.GLFW_KEY_A);
    }

    public boolean rightKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(GLFW.GLFW_KEY_RIGHT) || window.isKeyPressed(GLFW.GLFW_KEY_D);
    }

    public boolean forwardKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(GLFW.GLFW_KEY_UP) || window.isKeyPressed(GLFW.GLFW_KEY_W);
    }

    public boolean backwardKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(GLFW.GLFW_KEY_DOWN) || window.isKeyPressed(GLFW.GLFW_KEY_S);
    }

    public boolean upJumpKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(KEY_JUMP) || window.isKeyPressed(KEY_ENABLE_FLYING);
    }

    public boolean downKeyPressed() {
        if (!keyInputAllowed()) return false;
        return window.isKeyPressed(KEY_CROUCH);
    }


    private void disableGravity() {
        positionHandler.setGravityEnabled(false);
    }

    public void setColor(float r, float g, float b) {
        // positionHandler.color.set(r, g, b, 1);
    }

    long lastSave = System.currentTimeMillis();

    public void save() { //Periodic saving
        eventPipeline.save();
    }

    private void jump() {
        if (usePositionHandler)
            positionHandler.jump();
        usePositionHandler = true;
        positionHandler.collisionsEnabled = true;
        positionHandler.setGravityEnabled(true);
    }

    public UserControlledPlayer(UserID user) throws IOException {
        super(user);
    }

    public void init(
            BaseWindow window, World world,
            Matrix4f projection,
            Matrix4f view) {
        this.window = window;
        this.chunks = world;
        this.projection = projection;
        this.view = view;
        camera = new Camera(this, window, view, projection, world);

        if (Main.settings.game_switchMouseButtons) {
            DELETE_MOUSE_BUTTON = GLFW.GLFW_MOUSE_BUTTON_LEFT;
            CREATE_MOUSE_BUTTON = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
        }

        positionHandler = new PositionHandler(window, world, aabb, aabb, GameScene.otherPlayers);
        setColor(1, 1, 0);
        skin = Main.game.availableSkins.get(0).get(this);
        eventPipeline = new BlockEventPipeline(world, this);
    }

    public void setFlashlight(float distance) {
        GameScene.world.chunkShader.setFlashlightDistance(distance);
    }

    public void startGame(WorldInfo world) {
        eventPipeline.startGame(world);
    }

    public void stopGame() {
        save();
        eventPipeline.endGame();
    }

    World chunks;

    private Block getBlockAtPlayerHead() {
        return GameScene.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y),
                (int) Math.floor(worldPosition.z));
    }

    private Block getBlockAtCameraPos() {
        return GameScene.world.getBlock(
                (int) Math.floor(camera.position.x),
                (int) Math.floor(camera.position.y),
                (int) Math.floor(camera.position.z));
    }

    private boolean isInsideOfLadder() {
        return getBlockAtPlayerHead().climbable
                ||
                GameScene.world.getBlock(
                        (int) Math.floor(worldPosition.x),
                        (int) Math.floor(worldPosition.y + aabb.box.getYLength()),
                        (int) Math.floor(worldPosition.z)).climbable;
    }

    Block cameraBlock, playerBlock;

    // playerForward,playerBackward,playerUp,playerDown,playerLeft,playerRight;
    public void update(boolean holdMouse) {
        Block newCameraBlock = getBlockAtCameraPos();
        if (newCameraBlock != cameraBlock) {
            cameraBlock = newCameraBlock;
            if (newCameraBlock.isAir()) {//Air is always transparent
                GameScene.ui.setOverlayColor(0, 0, 0, 0);
            } else if (newCameraBlock.opaque
                    && newCameraBlock.colorInPlayerHead[3] == 0
                    && positionHandler.collisionsEnabled
                    && !Main.devMode) { //If we are opaque, don't have a color and we are not in passthrough mode
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
            if (newCameraBlock.type == BlockList.LIQUID_BLOCK_TYPE_ID) {
                positionHandler.velocity.set(0, 0, 0);
                positionHandler.setFallMedium(PositionHandler.DEFAULT_GRAVITY / 8,
                        PositionHandler.DEFAULT_TERMINAL_VELOCITY / 30);
            } else if (newCameraBlock.isAir()) {
                positionHandler.resetFallMedium();
            }
        }

        eventPipeline.update();

        if (System.currentTimeMillis() - lastSave > 60000) {
            lastSave = System.currentTimeMillis();
            save();   //Save every 60 seconds
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
                worldPosition.add(
                        camera.cameraForward.x * speed * window.smoothFrameDeltaSec,
                        camera.cameraForward.y * speed * window.smoothFrameDeltaSec,
                        camera.cameraForward.z * speed * window.smoothFrameDeltaSec);
            }
            if (backwardKeyPressed()) {
                worldPosition.sub(
                        camera.cameraForward.x * speed * window.smoothFrameDeltaSec,
                        camera.cameraForward.y * speed * window.smoothFrameDeltaSec,
                        camera.cameraForward.z * speed * window.smoothFrameDeltaSec);
            }

            if (leftKeyPressed()) {
                worldPosition.sub(
                        camera.right.x * speed * window.smoothFrameDeltaSec,
                        camera.right.y * speed * window.smoothFrameDeltaSec,
                        camera.right.z * speed * window.smoothFrameDeltaSec);
            }
            if (rightKeyPressed()) {
                worldPosition.add(
                        camera.right.x * speed * window.smoothFrameDeltaSec,
                        camera.right.y * speed * window.smoothFrameDeltaSec,
                        camera.right.z * speed * window.smoothFrameDeltaSec);
            }

            if (isInsideOfLadder()) {
                if (upJumpKeyPressed()) {
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
                    if (upJumpKeyPressed()) {
                        worldPosition.sub(0, UP_DOWN_SPEED * window.smoothFrameDeltaSec, 0);
                        disableGravity();
                    } else if (downKeyPressed()) {
                        worldPosition.add(0, UP_DOWN_SPEED * window.smoothFrameDeltaSec, 0);
                        disableGravity();
                    }
                } else if (playerBlock.isLiquid() && upJumpKeyPressed()) {
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

        // The key to preventing shaking during collision is to update the camera AFTER
        // the position handler is done its job
        camera.update(holdMouse);
        this.pan = camera.pan;
        this.tilt = camera.tilt;

        camera.cursorRay.drawRay();
        if (camera.getThirdPersonDist() != 0.0f) {
            skin.init(projection, view);
            skin.render();
        }

        if (lastOrientation.x != worldPosition.x
                || lastOrientation.y != worldPosition.y
                || lastOrientation.z != worldPosition.z
                || lastOrientation.w != pan) {
            lastOrientation.set(worldPosition.x, worldPosition.y, worldPosition.z, pan);
            GameScene.server.sendPlayerPosition(lastOrientation);
        }
    }

    boolean isFlyingMode = true;
    long lastJumpKeyPress = 0;

    public void enableFlying() {
        isFlyingMode = true;
        positionHandler.setGravityEnabled(false);
        positionHandler.collisionsEnabled = false;
    }

    public void disableFlying() {
        isFlyingMode = false;
        positionHandler.setGravityEnabled(true);
        positionHandler.collisionsEnabled = true;
    }

    boolean doubleJumped() {
        boolean jumped = false;
        if (System.currentTimeMillis() - lastJumpKeyPress < 400) {
            jumped = true;
        }
        lastJumpKeyPress = System.currentTimeMillis();
        return jumped;
    }

    public void mouseButtonEvent(int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (button == UserControlledPlayer.CREATE_MOUSE_BUTTON
                    && !camera.cursorRay.clickEvent(true)) {
                setItem(Main.game.getSelectedItem());
            } else if (button == UserControlledPlayer.DELETE_MOUSE_BUTTON
                    && !camera.cursorRay.clickEvent(false)) {
                removeItem();
            }
        }
    }

    public void keyEvent(int key, int scancode, int action, int mods) {
        if (camera.cursorRay.keyEvent(key, scancode, action, mods)) {
        } else if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_LEFT_SHIFT) {
                runningMode = true;
            } else if (key == KEY_JUMP) {
                if (positionLock != null) {
                    positionLock = null;
                }
                if (positionHandler.isGravityEnabled()) {
                    jump();
                }
            } else if (key == KEY_ENABLE_FLYING) {
                enableFlying();
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (key == KEY_JUMP) {
                if (doubleJumped()) {
                    if (positionHandler.isGravityEnabled()) {
                        enableFlying();
                    } else {
                        disableFlying();
                    }
                }
            } else {
                switch (key) {
                    case GLFW.GLFW_KEY_LEFT_SHIFT -> runningMode = false;
                    case KEY_CHANGE_RAYCAST_MODE -> {
                        camera.cursorRay.cursorRayHitAllBlocks = !camera.cursorRay.cursorRayHitAllBlocks;
                        if (camera.cursorRay.cursorRayHitAllBlocks) {
                            camera.cursorRay.rayDistance = 7;
                        }
                    }
                    case KEY_TOGGLE_VIEW -> {
                        camera.cycleToNextView(15);
                    }
                    case KEY_CREATE_MOUSE_BUTTON -> {
                        if (!camera.cursorRay.clickEvent(true)) {
                            setItem(Main.game.getSelectedItem());
                        }
                    }
                    case KEY_DELETE_MOUSE_BUTTON -> {
                        if (!camera.cursorRay.clickEvent(false)) {
                            removeItem();
                        }
                    }
                    default -> {
                    }
                }
            }
        }
    }

    public void setItem(Item item) {
        if (camera.cursorRay != null && camera.cursorRay.hitTarget()) {
            if (item != null) {
                if (item.getType() == ItemType.BLOCK) {
                    Block block = (Block) item;
                    Vector3i w;

                    if (block == BlockList.BLOCK_AIR || !camera.cursorRay.hitTarget()) {
                        w = camera.cursorRay.getHitPos();
                    } else {
                        w = camera.cursorRay.getHitPosPlusNormal();
                    }

                    WCCi wcc = new WCCi();
                    wcc.set(w);
                    setBlock(block.id, wcc);
                } else if (item.getType() == ItemType.ENTITY_LINK) {
                    EntityLink entity = (EntityLink) item;
                    Vector3i w;

                    if (!camera.cursorRay.hitTarget()) {
                        w = camera.cursorRay.getHitPos();
                    } else {
                        w = camera.cursorRay.getHitPosPlusNormal();
                    }

                    setEntity(entity, w);
                }
            }
        }
    }

    private void setEntity(EntityLink entity, Vector3i w) {
        WCCi wcc = new WCCi();
        wcc.set(w);
        Chunk chunk = GameScene.world.chunks.get(wcc.chunk);
        if (chunk != null) {
            chunk.markAsModifiedByUser();
            Entity e = chunk.entities.placeNew(w, entity, null);
            e.sendMultiplayer = true;//Tells the chunkEntitySet to send the entity to the clients
        }
    }


    //Set block method ===============================================================================
//The master method
    public void setBlock(short newBlock, BlockData blockData, WCCi wcc) {
        Chunk chunk = chunks.getChunk(wcc.chunk);
        if (chunk != null) {
            //Get the previous block
            short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);

            //we need to set the block because some algorithms want to check to see if the block has changed immediately
            chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, newBlock); //Important

            BlockHistory history = new BlockHistory(previousBlock, newBlock);
            if (blockData != null) {
                history.updateBlockData = true;
                history.newBlockData = blockData;
            }
            eventPipeline.addEvent(wcc, history);
        }
    }

    public void setBlock(short newBlock, WCCi wcc) {
        setBlock(newBlock, null, wcc);
    }

    public void setBlock(short block, int worldX, int worldY, int worldZ) {
        setBlock(block, null, new WCCi().set(worldX, worldY, worldZ));
    }

    public void setBlock(short block, BlockData data, int worldX, int worldY, int worldZ) {
        setBlock(block, data, new WCCi().set(worldX, worldY, worldZ));
    }
//==============================================================================================

    public void setNewSpawnPoint(Terrain terrain) {
        System.out.println("Setting new spawn point...");
        int radius = Chunk.HALF_WIDTH;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                for (int y = terrain.MIN_SURFACE_HEIGHT - 10; y < terrain.MAX_SURFACE_HEIGHT + 10; y++) {
                    if (terrain.spawnRulesApply(PLAYER_HEIGHT, chunks, x, y, z)) {
                        System.out.println("Found new spawn point!");
                        worldPosition.set(x, y - PLAYER_HEIGHT - 0.5f, z);
                        return;
                    }
                }
            }
        }
        worldPosition.set(0, terrain.MIN_SURFACE_HEIGHT - PLAYER_HEIGHT - 0.5f, 0);
        System.out.println("Spawn point not found");
    }

    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        return false;
    }

    private void removeItem() {
        if (camera.cursorRay.getEntity() != null) {
            camera.cursorRay.getEntity().destroy();
        } else {
            setBlock(BlockList.BLOCK_AIR.id, new WCCi().set(camera.cursorRay.getHitPos()));
        }
    }


}
