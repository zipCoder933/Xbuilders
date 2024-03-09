package com.xbuilders.engine.player;

import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.player.camera.Camera;
import com.xbuilders.engine.player.raycasting.Ray;
import com.xbuilders.engine.utils.UserID;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.network.PlayerServer;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.utils.rendering.wireframeBox.Box;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.game.Main;
import com.xbuilders.window.BaseWindow;

import java.io.IOException;

import org.joml.*;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkVec2;

public class UserControlledPlayer extends Player {

    Box cursor;
    public Camera camera;
    BaseWindow window;
    static float speed = 10f;
    final static float PLAYER_HEIGHT = 2.0f;
    final static float PLAYER_WIDTH = 0.8f;
    Matrix4f projection;
    Matrix4f view;
    PositionHandler positionHandler;
    boolean usePositionHandler = true;
    public PlayerServer server;


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
        if (usePositionHandler) {
            positionHandler.jump();
            positionHandler.setGravityEnabled(true);
        }
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
        intOrientation = new Vector2i();
        camera = new Camera(this, window, view, projection, world);
        cursor = new Box();
        cursor.set(0, 0, 0, 1, 1, 1);
        cursor.setColor(new Vector4f(1, 1, 1, 1));
        orientation = new Vector2f();
        aabb.size.set(PLAYER_WIDTH, PLAYER_HEIGHT, PLAYER_WIDTH);
        aabb.offset.set(-(PLAYER_WIDTH * 0.5f), 0.5f, -(PLAYER_WIDTH * 0.5f));
        positionHandler = new PositionHandler(world, window, aabb, aabb, GameScene.otherPlayers);
        setColor(1, 1, 0);
        server = new PlayerServer(this);
    }

    World chunks;
    Vector2f orientation;
    Vector2i intOrientation;

    // boolean
    // playerForward,playerBackward,playerUp,playerDown,playerLeft,playerRight;
    public void update(boolean holdMouse) {
        if (window.isKeyPressed(GLFW.GLFW_KEY_UP)) {
            worldPosition.add(
                    camera.cameraForward.x * speed * window.getFrameDelta(),
                    camera.cameraForward.y * speed * window.getFrameDelta(),
                    camera.cameraForward.z * speed * window.getFrameDelta());
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN)) {
            worldPosition.sub(
                    camera.cameraForward.x * speed * window.getFrameDelta(),
                    camera.cameraForward.y * speed * window.getFrameDelta(),
                    camera.cameraForward.z * speed * window.getFrameDelta());
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT)) {
            worldPosition.sub(
                    camera.right.x * speed * window.getFrameDelta(),
                    camera.right.y * speed * window.getFrameDelta(),
                    camera.right.z * speed * window.getFrameDelta());
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_RIGHT)) {
            worldPosition.add(
                    camera.right.x * speed * window.getFrameDelta(),
                    camera.right.y * speed * window.getFrameDelta(),
                    camera.right.z * speed * window.getFrameDelta());
        }

        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            worldPosition.sub(0, speed * window.getFrameDelta(), 0);
            disableGravity();

        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            worldPosition.add(0, speed * window.getFrameDelta(), 0);
            disableGravity();
        }

        updatePosHandler(holdMouse);
        //The key to preventing shaking during collision is to update the camera AFTER  the position handler is done its job
        camera.update(holdMouse);
        drawRay(camera.cursorRay);
    }

    public void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            switch (key) {
                case GLFW.GLFW_KEY_SPACE -> {
                    jump();
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_MINUS -> removeItem();
                case GLFW.GLFW_KEY_EQUAL -> setItem(Main.game.getSelectedItem());
                case GLFW.GLFW_KEY_F -> {
                    if (speed == 60.0) {
                        speed = 10f;
                    } else {
                        speed = 60.0f;
                    }
                }
                case GLFW.GLFW_KEY_L -> {
                    lineMode = !lineMode;
                }
                case GLFW.GLFW_KEY_P -> {
                    usePositionHandler = !usePositionHandler;
                }
                case GLFW.GLFW_KEY_V -> {
                    camera.cycleToNextView(10);
                }
                default -> {
                }
            }
        }
    }

    boolean lineMode = false;

    public void setItem(Item item) {
        if (camera.cursorRay != null && camera.cursorRay.hitTarget) {
            if (item != null) {
                if (item.getType() == ItemType.BLOCK) {
                    Block block = (Block) item;
                    Vector3i w;
                    if (block == BlockList.BLOCK_AIR) {
                        w = camera.cursorRay.getHitPositionAsInt();
                    } else {
                        w = camera.cursorRay.getHitPosPlusNormal();
                    }
                    WCCi wcc = new WCCi();
                    wcc.set(w);
                    setBlock(wcc, block);
                } else if (item.getType() == ItemType.ENTITY_LINK) {
                    EntityLink entity = (EntityLink) item;
                    Vector3i w = camera.cursorRay.getHitPosPlusNormal();
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

    private void setBlock(WCCi wcc, Block block) {
        Chunk chunk = chunks.getChunk(wcc.chunk);
        if (chunk != null) {
            chunk.markAsModifiedByUser();
            chunk.data.setBlock(
                    wcc.chunkVoxel.x,
                    wcc.chunkVoxel.y,
                    wcc.chunkVoxel.z, block.id);

            BlockData data = chunk.data.getBlockData(
                    wcc.chunkVoxel.x,
                    wcc.chunkVoxel.y,
                    wcc.chunkVoxel.z);

            BlockType type = ItemList.blocks.getBlockType(block.type);
            if (type != null) {
                chunk.data.setBlockData(wcc.chunkVoxel.x,
                        wcc.chunkVoxel.y,
                        wcc.chunkVoxel.z,
                        type.getInitialBlockData(data, this));
            }

            chunk.updateMesh(
                    wcc.chunkVoxel.x,
                    wcc.chunkVoxel.y,
                    wcc.chunkVoxel.z);
        }
    }

    public void mouseButtonEvent(int button, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                setItem(Main.game.getSelectedItem());
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                removeItem();
            }
        }
    }

    public void setNewSpawnPoint(Terrain terrain) {
        System.out.println("Setting new spawn point...");
        int radius = Chunk.HALF_WIDTH;
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                for (int y = terrain.MAX_HEIGHT; y > terrain.MIN_HEIGHT; y--) {
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

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
    }

    private void drawRay(Ray frontRay) {
        if (frontRay.hitTarget) {
            if (frontRay.entity != null) {
                cursor.set(frontRay.entity.aabb.box);
                cursor.draw(projection, view);
            } else if (frontRay.cursorBoxes == null) {
                cursor.set(
                        (int) camera.cursorRay.getHitPosition().x,
                        (int) camera.cursorRay.getHitPosition().y,
                        (int) camera.cursorRay.getHitPosition().z,
                        1, 1, 1);
                cursor.draw(projection, view);
            } else {
                for (AABB box : frontRay.cursorBoxes) {
                    cursor.set(box);
                    cursor.draw(projection, view);
                }
            }
        }
    }

    private void removeItem() {
        if (camera.cursorRay.entity != null) {
            System.out.println("Deleting entity");
            camera.cursorRay.entity.destroy();
        } else {
            setBlock(new WCCi().set(camera.cursorRay.getHitPositionAsInt()), BlockList.BLOCK_AIR);
        }
    }

}
