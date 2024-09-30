package com.xbuilders.engine.player;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;
import com.xbuilders.engine.player.camera.Camera;
import com.xbuilders.engine.player.pipeline.BlockEventPipeline;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Matrix4f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.xbuilders.engine.ui.gameScene.GameUI.printKeyConsumption;

public class UserControlledPlayer extends Player {


    public Camera camera;
    private MainWindow window;
    final Vector4f lastOrientation = new Vector4f();
    public boolean runningMode;
    Matrix4f projection;
    Matrix4f view;
    boolean isClimbing = false;
    public PositionHandler positionHandler;
    boolean usePositionHandler = true;
    public BlockEventPipeline eventPipeline;
    public PositionLock positionLock;
    boolean autoForward = false;
    boolean autoJump_unCollided = true;
    float autoJump_ticksWhileColidingWithBlock = 0;


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
        return (MainWindow.settings.game_switchMouseButtons ? GLFW.GLFW_MOUSE_BUTTON_RIGHT : GLFW.GLFW_MOUSE_BUTTON_LEFT);
    }

    public static int getDeleteMouseButton() {
        return (MainWindow.settings.game_switchMouseButtons ? GLFW.GLFW_MOUSE_BUTTON_LEFT : GLFW.GLFW_MOUSE_BUTTON_RIGHT);
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

    public UserControlledPlayer(MainWindow window, World world,
                                Matrix4f projection, Matrix4f view, Matrix4f centeredView) throws IOException {
        super();
        this.window = window;
        this.chunks = world;
        this.projection = projection;
        this.view = view;
        camera = new Camera(this, window, projection, view, centeredView);
        positionHandler = new PositionHandler(window, world, aabb, aabb, GameScene.otherPlayers);
        eventPipeline = new BlockEventPipeline(world, this);

        //Load first person data
        if (playerModelFile.exists()) {
            loadInfoFromBytes(Files.readAllBytes(playerModelFile.toPath()));
            System.out.println("Loaded player model: " + toString());
        } else {
            name = System.getProperty("user.name");
            save();
        }
    }

    public void init() {
        camera.init();
    }

    private static final File playerModelFile = ResourceUtils.appDataResource("playerModel.bin");
    long lastSave = System.currentTimeMillis();

    public void save() { //Periodic saving
        eventPipeline.save();
        //Save first person data
        try {
            Files.write(playerModelFile.toPath(), infoToBytes());
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }


    public void setFlashlight(float distance) {
        GameScene.world.chunkShader.setFlashlightDistance(distance);
    }

    public void startGame(WorldInfo world) {
        eventPipeline.startGame(world);
        autoForward = false;
        save();
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

    public void update(boolean holdMouse) {
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
            if (newCameraBlock.renderType == BlockList.LIQUID_BLOCK_TYPE_ID) {
                positionHandler.setVelocity(0, 0, 0);
                positionHandler.setFallMedium(PositionHandler.DEFAULT_GRAVITY / 8,
                        PositionHandler.MAX_TERMINAL_VELOCITY / 30);
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
            getSkin().super_render(projection, view);
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

    public void mouseButtonEvent(int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (button == UserControlledPlayer.getCreateMouseButton()
                    && !camera.cursorRay.clickEvent(true)) {
                setItem(MainWindow.game.getSelectedItem());
            } else if (button == UserControlledPlayer.getDeleteMouseButton()
                    && !camera.cursorRay.clickEvent(false)) {
                removeItem();
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
            switch (key) {
                case GLFW.GLFW_KEY_LEFT_SHIFT -> runningMode = false;
                case KEY_CHANGE_RAYCAST_MODE -> {
                    camera.cursorRay.cursorRayHitAllBlocks = !camera.cursorRay.cursorRayHitAllBlocks;
                    if (camera.cursorRay.cursorRayHitAllBlocks) {
                        camera.cursorRay.rayDistance = 7;
                    }
                }
                case KEY_TOGGLE_VIEW -> camera.cycleToNextView(10);
                case KEY_CREATE_MOUSE_BUTTON -> {
                    if (!camera.cursorRay.clickEvent(true)) {
                        setItem(MainWindow.game.getSelectedItem());
                    }
                }
                case KEY_DELETE_MOUSE_BUTTON -> {
                    if (!camera.cursorRay.clickEvent(false)) {
                        removeItem();
                    }
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

    public void setItem(Item item) {
        if (camera.cursorRay != null && camera.cursorRay.hitTarget()) {
            if (item != null) {
                if (item.getType() == ItemType.BLOCK) {
                    Block block = (Block) item;
                    Vector3i w;

                    Block blockAtHitPos = GameScene.world.getBlock(
                            camera.cursorRay.getHitPos().x,
                            camera.cursorRay.getHitPos().y,
                            camera.cursorRay.getHitPos().z);

                    if (block == BlockList.BLOCK_AIR
                            || !camera.cursorRay.hitTarget()
                            || blockAtHitPos.getRenderType().replaceOnSet) {
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

                    setEntity(entity, w, null);
                }
            }
        }
    }

    public Entity setEntity(EntityLink entity, Vector3i w, byte[] data) {
        WCCi wcc = new WCCi();
        wcc.set(w);
        Chunk chunk = GameScene.world.chunks.get(wcc.chunk);
        if (chunk != null) {
            chunk.markAsModifiedByUser();
            Entity e = chunk.entities.placeNew(w, entity, data);
            e.sendMultiplayer = true;//Tells the chunkEntitySet to send the entity to the clients
            return e;
        }
        return null;
    }


    //Set block method ===============================================================================
//The master method
    public void setBlock(short newBlock, BlockData blockData, WCCi wcc) {
        if (!World.inYBounds((wcc.chunk.y * Chunk.WIDTH) + wcc.chunkVoxel.y)) return;
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
