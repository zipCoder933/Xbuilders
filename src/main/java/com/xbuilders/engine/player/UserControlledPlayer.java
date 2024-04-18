package com.xbuilders.engine.player;

import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.player.pipeline.BlockEventPipeline;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.player.camera.Camera;
import com.xbuilders.engine.utils.UserID;
import com.xbuilders.engine.utils.network.PlayerServer;
import com.xbuilders.engine.world.World;
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
    Matrix4f projection;
    Matrix4f view;
    boolean isClimbing = false;
    PositionHandler positionHandler;
    boolean usePositionHandler = true;
    public PlayerServer server;
    public BlockEventPipeline eventPipeline;


    final int CHANGE_RAYCAST_MODE = GLFW.GLFW_KEY_TAB;


    private void disableGravity() {
        positionHandler.setGravityEnabled(false);
    }

    public void setColor(float r, float g, float b) {
//        positionHandler.color.set(r, g, b, 1);
    }

    private void updatePosHandler(boolean holdMouse) {
        if (usePositionHandler) {
            positionHandler.collisionsEnabled = holdMouse;
            positionHandler.update(projection, view);
        }
    }

    private void jump() {
        if (usePositionHandler) positionHandler.jump();
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
    }

    World chunks;

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

    private Block getBlockAtHeadPos() {
        return GameScene.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y),
                (int) Math.floor(worldPosition.z));
    }

    private boolean isInsideOfLadder() {
        return ItemList.blocks.getBlockType(getBlockAtHeadPos().type).isClimbable()
                || ItemList.blocks.getBlockType(GameScene.world.getBlock(
                (int) Math.floor(worldPosition.x),
                (int) Math.floor(worldPosition.y + aabb.box.getYLength()),
                (int) Math.floor(worldPosition.z)).type).isClimbable();
    }

    // boolean
    // playerForward,playerBackward,playerUp,playerDown,playerLeft,playerRight;
    public void update(boolean holdMouse) {
        eventPipeline.resolve(this);
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
                worldPosition.add(0, speed * 0.5f * window.getFrameDelta(), 0);
            } else if (upKeyPressed()) {
                isClimbing = true;
                canFly = false;
                worldPosition.sub(0, speed * 0.5f * window.getFrameDelta(), 0);
            }
            positionHandler.setGravityEnabled(false);
        } else {
            if (isClimbing) {
                positionHandler.setGravityEnabled(true);
                isClimbing = false;
            } else if (canFly) {
                if (upKeyPressed()) {
                    worldPosition.sub(0, speed * window.getFrameDelta(), 0);
                    disableGravity();
                }
                if (downKeyPressed()) {
                    worldPosition.add(0, speed * window.getFrameDelta(), 0);
                    disableGravity();
                }
            }
        }

        updatePosHandler(holdMouse);
        //The key to preventing shaking during collision is to update the camera AFTER  the position handler is done its job
        camera.update(holdMouse);

        camera.cursorRay.drawRay();
        if (camera.getThirdPersonDist() != 0.0f) skin.render(projection, view);
    }

    boolean raycastDistChanged = false;
    boolean canFly = true;

    public void mouseButtonEvent(int button, int action, int mods) {
        if (camera.cursorRay.createClickEvent(button, action, mods)) {
            setItem(Main.game.getSelectedItem());
        } else if (camera.cursorRay.destroyClickEvent(button, action, mods)) {
            removeItem();
        }
    }


    public void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_LEFT_SHIFT -> speed = 75f;
                case GLFW.GLFW_KEY_SPACE -> {
                    jump();
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (upKeyPressed(key) || downKeyPressed(key)) canFly = true;
            switch (key) {
                case GLFW.GLFW_KEY_LEFT_SHIFT -> speed = 10f;
                case GLFW.GLFW_KEY_MINUS -> removeItem();
                case GLFW.GLFW_KEY_EQUAL -> setItem(Main.game.getSelectedItem());
                case GLFW.GLFW_KEY_L -> {
                    lineMode = !lineMode;
                }
                case GLFW.GLFW_KEY_P -> {
                    usePositionHandler = !usePositionHandler;
                }
                case GLFW.GLFW_KEY_O -> {
                    camera.cycleToNextView(10);
                }
                case CHANGE_RAYCAST_MODE -> {
                    if (!raycastDistChanged) {
                        camera.cursorRay.cursorRayHitAllBlocks = !camera.cursorRay.cursorRayHitAllBlocks;
                        if (camera.cursorRay.cursorRayHitAllBlocks) {
                            camera.cursorRayDist = 5;
                        }
                    }
                    raycastDistChanged = false;
                }
                default -> {
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
                    setBlock(block, wcc);
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

    public void setBlock(Block block, int worldX, int worldY, int worldZ) {
        WCCi wcc = new WCCi();
        wcc.set(worldX, worldY, worldZ);
        setBlock(block, wcc);
    }

    public void setBlock(Block block, WCCi wcc) {
        Chunk chunk = chunks.getChunk(wcc.chunk);
        if (chunk != null) {
            Block prevBlock = ItemList.getBlock(chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z));
            eventPipeline.addEvent(wcc, new BlockHistory(prevBlock, block));
        }
    }


    public void setNewSpawnPoint(Terrain terrain) {
        System.out.println("Setting new spawn point...");
        int radius = Chunk.HALF_WIDTH;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                for (int y = terrain.MIN_HEIGHT; y < terrain.MAX_HEIGHT; y++) {
                    if (terrain.spawnRulesApply(PLAYER_HEIGHT, chunks, x, y, z)) {
                        System.out.println("Found new spawn point!");
                        worldPosition.set(x, y + PLAYER_HEIGHT + 0.5f, z);
                        return;
                    }
                }
            }
        }
        System.out.println("Spawn point not found");
    }

    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (window.isKeyPressed(CHANGE_RAYCAST_MODE) && camera.cursorRay.cursorRayHitAllBlocks) {
            raycastDistChanged = true;
            camera.cursorRayDist += scroll.y();
            camera.cursorRayDist = MathUtils.clamp(camera.cursorRayDist, 1, 50);
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
            setBlock(BlockList.BLOCK_AIR, new WCCi().set(camera.cursorRay.getHitPos()));
        }
    }

}
