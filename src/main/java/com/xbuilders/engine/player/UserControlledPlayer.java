package com.xbuilders.engine.player;

import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.player.pipeline.BlockEventPipeline;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.player.camera.Camera;
import com.xbuilders.engine.utils.UserID;
import com.xbuilders.engine.utils.network.PlayerServer;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.game.Main;
import com.xbuilders.window.BaseWindow;

import java.io.IOException;
import java.lang.Math;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

public class UserControlledPlayer extends Player {


    public Camera camera;
    BaseWindow window;
    static float speed = 10f;
    final static float PLAYER_HEIGHT = 2.0f;
    final static float PLAYER_WIDTH = 0.8f;

    final static float FLY_SPEED = 12f;
    final float DEFAULT_SPEED = 12f;
    final float RUN_SPEED = DEFAULT_SPEED * 10;

    Matrix4f projection;
    Matrix4f view;
    boolean isClimbing = false;
    PositionHandler positionHandler;
    boolean usePositionHandler = true;
    public PlayerServer server;
    public BlockEventPipeline eventPipeline;
    public PositionLock positionLock;


    //Mouse buttons
    public static final int CREATE_MOUSE_BUTTON = GLFW.GLFW_MOUSE_BUTTON_LEFT;
    public static final int DELETE_MOUSE_BUTTON = GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    // Keys
    public static final int KEY_CHANGE_RAYCAST_MODE = GLFW.GLFW_KEY_TAB;
    private static final int KEY_TOGGLE_PASSTHROUGH = GLFW.GLFW_KEY_P;
    public static final int KEY_CREATE_MOUSE_BUTTON = GLFW.GLFW_KEY_EQUAL;
    public static final int KEY_DELETE_MOUSE_BUTTON = GLFW.GLFW_KEY_MINUS;

    public boolean leftKeyPressed() {
        return window.isKeyPressed(GLFW.GLFW_KEY_LEFT) || window.isKeyPressed(GLFW.GLFW_KEY_A);
    }

    public boolean rightKeyPressed() {
        return window.isKeyPressed(GLFW.GLFW_KEY_RIGHT) || window.isKeyPressed(GLFW.GLFW_KEY_D);
    }

    public boolean forwardKeyPressed() {
        return window.isKeyPressed(GLFW.GLFW_KEY_UP) || window.isKeyPressed(GLFW.GLFW_KEY_W);
    }

    public boolean backwardKeyPressed() {
        return window.isKeyPressed(GLFW.GLFW_KEY_DOWN) || window.isKeyPressed(GLFW.GLFW_KEY_S);
    }

    public boolean jumpKeyPressed() {
        return window.isKeyPressed(GLFW.GLFW_KEY_SPACE);
    }

    public boolean upKeyPressed() {
        return window.isKeyPressed(GLFW.GLFW_KEY_F) &&
                !window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT);
    }

    public boolean downKeyPressed() {
        return window.isKeyPressed(GLFW.GLFW_KEY_F) &&
                window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT);
    }


    public boolean upKeyPressed(int key) {
        return key == GLFW.GLFW_KEY_F &&
                key != GLFW.GLFW_KEY_LEFT_SHIFT;
    }

    public boolean downKeyPressed(int key) {
        return key == GLFW.GLFW_KEY_F &&
                key == GLFW.GLFW_KEY_LEFT_SHIFT;
    }

    private void disableGravity() {
        positionHandler.setGravityEnabled(false);
    }

    public void setColor(float r, float g, float b) {
        // positionHandler.color.set(r, g, b, 1);
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

        aabb.size.set(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        aabb.offset.set(-(PLAYER_WIDTH * 0.5f), -0.5f, -(PLAYER_WIDTH * 0.5f));
        positionHandler = new PositionHandler(world, window, aabb, aabb, GameScene.otherPlayers);
        setColor(1, 1, 0);
        skin = new DefaultSkin(aabb);
        eventPipeline = new BlockEventPipeline(world);
        server = new PlayerServer(this);
        GameScene.world.chunkShader.setFlashlightDistance(20f);
    }

    public void startGame() {
        eventPipeline.startGame();
    }

    public void stopGame() {
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
        BlockType type = ItemList.blocks.getBlockType(getBlockAtPlayerHead().type);
        BlockType belowType = ItemList.blocks.getBlockType(GameScene.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y + aabb.box.getYLength()),
                (int) Math.floor(worldPosition.z)).type);

        if (type == null || belowType == null)
            return false;

        return (type.isClimbable())
                || belowType.isClimbable();
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
        if (newPlayerBlock != playerBlock) {
            playerBlock = newPlayerBlock;
            if (newCameraBlock.type == BlockList.LIQUID_BLOCK_TYPE_ID) {
                positionHandler.velocity.set(0, 0, 0);
                positionHandler.setFallMedium(PositionHandler.DEFAULT_GRAVITY / 4,
                        PositionHandler.DEFAULT_TERMINAL_VELOCITY / 30);
            } else if (newCameraBlock.isAir()) {
                positionHandler.resetFallMedium();
            }
        }

        eventPipeline.resolve(this);
        if (positionLock != null) {
            worldPosition.set(positionLock.getPosition());
            usePositionHandler = false;
        } else {
            usePositionHandler = true;
            if (forwardKeyPressed()) {
                worldPosition.add(
                        camera.cameraForward.x * speed * window.getFrameDelta(),
                        camera.cameraForward.y * speed * window.getFrameDelta(),
                        camera.cameraForward.z * speed * window.getFrameDelta());
            }
            if (backwardKeyPressed()) {
                worldPosition.sub(
                        camera.cameraForward.x * speed * window.getFrameDelta(),
                        camera.cameraForward.y * speed * window.getFrameDelta(),
                        camera.cameraForward.z * speed * window.getFrameDelta());
            }

            if (leftKeyPressed()) {
                worldPosition.sub(
                        camera.right.x * speed * window.getFrameDelta(),
                        camera.right.y * speed * window.getFrameDelta(),
                        camera.right.z * speed * window.getFrameDelta());
            }
            if (rightKeyPressed()) {
                worldPosition.add(
                        camera.right.x * speed * window.getFrameDelta(),
                        camera.right.y * speed * window.getFrameDelta(),
                        camera.right.z * speed * window.getFrameDelta());
            }

            if (isInsideOfLadder()) {
                if (downKeyPressed()) {
                    isClimbing = true;
                    canFly = false;
                    worldPosition.add(0, FLY_SPEED * window.getFrameDelta(), 0);
                } else if (upKeyPressed()) {
                    isClimbing = true;
                    canFly = false;
                    worldPosition.sub(0, FLY_SPEED * window.getFrameDelta(), 0);
                }
                positionHandler.setGravityEnabled(false);
            } else {
                if (isClimbing) {
                    positionHandler.setGravityEnabled(true);
                    isClimbing = false;
                } else if (canFly) {
                    if (upKeyPressed()) {
                        worldPosition.sub(0, FLY_SPEED * window.getFrameDelta(), 0);
                        disableGravity();
                    } else if (downKeyPressed()) {
                        worldPosition.add(0, FLY_SPEED * window.getFrameDelta(), 0);
                        disableGravity();
                    }
                }
            }
        }

        if (usePositionHandler) {
            positionHandler.update(projection, view);
            aabb.isSolid = true;
        } else {
            aabb.isSolid = false;
            aabb.update();
        }

        // The key to preventing shaking during collision is to update the camera AFTER
        // the position handler is done its job
        camera.update(holdMouse);

        camera.cursorRay.drawRay();
        if (camera.getThirdPersonDist() != 0.0f)
            skin.render(projection, view);
    }

    boolean raycastDistChanged = false;
    boolean canFly = true;

    public void mouseButtonEvent(int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (button == UserControlledPlayer.CREATE_MOUSE_BUTTON
                    && !camera.cursorRay.createClickEvent()) {
                setItem(Main.game.getSelectedItem());
            } else if (button == UserControlledPlayer.DELETE_MOUSE_BUTTON
                    && !camera.cursorRay.destroyClickEvent()) {
                removeItem();
            }
        }
    }

    public void keyEvent(int key, int scancode, int action, int mods) {
        if (camera.cursorRay.keyEvent(key, scancode, action, mods)) {
        } else if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_LEFT_SHIFT) {
                speed = RUN_SPEED;
            } else {
                switch (key) {
                    case GLFW.GLFW_KEY_SPACE -> {
                        if (positionLock != null) {
                            positionLock = null;
                        }
                        jump();
                    }
                    case KEY_CHANGE_RAYCAST_MODE -> {
                        camera.cursorRay.cursorRayHitAllBlocks = true;
                        if (camera.cursorRay.cursorRayHitAllBlocks) {
                            camera.cursorRay.cursorRayDist = 6;
                        }
                    }
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (upKeyPressed(key)) canFly = true;
            else {
                switch (key) {
                    case GLFW.GLFW_KEY_LEFT_SHIFT -> speed = DEFAULT_SPEED;
                    case GLFW.GLFW_KEY_L -> {
                        lineMode = !lineMode;
                    }
                    case KEY_TOGGLE_PASSTHROUGH -> {
                        System.out.println("PASSTHROUGH: " + !usePositionHandler);
                        positionHandler.collisionsEnabled = !positionHandler.collisionsEnabled;
                    }
                    case GLFW.GLFW_KEY_O -> {
                        camera.cycleToNextView(10);
                    }
                    case KEY_CHANGE_RAYCAST_MODE -> {
                        camera.cursorRay.cursorRayHitAllBlocks = false;
                        raycastDistChanged = true;
                    }
                    case KEY_CREATE_MOUSE_BUTTON -> {
                        if (!camera.cursorRay.createClickEvent()) {
                            setItem(Main.game.getSelectedItem());
                        }
                    }
                    case KEY_DELETE_MOUSE_BUTTON -> {
                        if (!camera.cursorRay.destroyClickEvent()) {
                            removeItem();
                        }
                    }
                    default -> {
                    }
                }
            }
        }
    }

    boolean lineMode = false;

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

                    WCCi wcc = new WCCi();
                    wcc.set(w);
                    Chunk chunk = GameScene.world.chunks.get(wcc.chunk);
                    if (chunk != null) {
                        chunk.markAsModifiedByUser();
                        chunk.entities.placeNew(w, entity);
                    }
                }
            }
        }
    }

    public void setBlock(short block, int worldX, int worldY, int worldZ) {
        WCCi wcc = new WCCi();
        wcc.set(worldX, worldY, worldZ);
        setBlock(block, wcc);
    }

    public void setBlock(short block, int worldX, int worldY, int worldZ, BlockData blockData) {
        WCCi wcc = new WCCi();
        wcc.set(worldX, worldY, worldZ);
        Chunk chunk = chunks.getChunk(wcc.chunk);
        if (chunk != null) {
            short prevBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
            eventPipeline.addEvent(new Vector3i(worldX, worldY, worldZ), new BlockHistory(prevBlock, block, blockData));
        }
    }

    public void setBlock(BlockData blockData, int worldX, int worldY, int worldZ) {
        eventPipeline.addEvent(new Vector3i(worldX, worldY, worldZ), new BlockHistory(blockData));
    }

    public void setBlock(short block, WCCi wcc) {
        Chunk chunk = chunks.getChunk(wcc.chunk);
        if (chunk != null) {
            short prevBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
            eventPipeline.addEvent(wcc, new BlockHistory(prevBlock, block));
        }
    }

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
        if (window.isKeyPressed(KEY_CHANGE_RAYCAST_MODE) && camera.cursorRay.cursorRayHitAllBlocks) {
            raycastDistChanged = true;
            camera.cursorRay.cursorRayDist += scroll.y();
            camera.cursorRay.cursorRayDist = MathUtils.clamp(camera.cursorRay.cursorRayDist, 1, 50);
            return true;
        }
        return false;
    }

    private void removeItem() {
        if (camera.cursorRay.getEntity() != null) {
            System.out.println("Deleting entity");
            camera.cursorRay.getEntity().destroy();
        } else {
            System.out.println("Deleting block at " + camera.cursorRay.getHitPos());
            setBlock(BlockList.BLOCK_AIR.id, new WCCi().set(camera.cursorRay.getHitPos()));
        }
    }


}
