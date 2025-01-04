package com.xbuilders.engine.utils.worldInteraction.collision;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.visuals.gameScene.rendering.wireframeBox.Box;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.items.block.BlockRegistry;
import com.xbuilders.engine.server.items.Registrys;
import com.xbuilders.engine.server.items.block.construction.BlockType;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.items.block.Block;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.server.world.World;
import com.xbuilders.engine.server.world.wcc.WCCi;

import java.util.HashSet;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import static com.xbuilders.engine.utils.worldInteraction.collision.PositionHandler.*;

/**
 * @author zipCoder933
 */
public class CollisionHandler {

    // Collision handler variables
    final private PositionHandler driver;
    final World chunks;
    final WCCi wcc = new WCCi();
    final HashSet<Chunk> exploredChunks = new HashSet<>();
    final EntityAABB myBox;
    final EntityAABB userControlledPlayerAABB;
    final AABB stepBox;
    final AABB collisionBox;
    public final CollisionData collisionData;
    boolean setFrozen = false;
    Block b;
    BlockData d;
    public Block floorBlock = BlockRegistry.BLOCK_AIR;
    Chunk chunk;


    //For dev purposes
    static Box renderingBox;

    private static void drawTestBox(AABB aabb, int color) {
        if (ClientWindow.devMode) {
            if (renderingBox == null) {
                renderingBox = new Box();
                renderingBox.setLineWidth(3);
            }
            switch (color) {
                case 0 -> renderingBox.setColor(255, 0, 0, 255);
                case 1 -> renderingBox.setColor(0, 255, 0, 255);
                case 2 -> renderingBox.setColor(0, 0, 255, 255);
                default -> renderingBox.setColor(255, 255, 255, 255);
            }
            renderingBox.set(aabb);
            renderingBox.draw(Server.projection, Server.view);
        }
    }

    public CollisionHandler(World chunks, PositionHandler driver, EntityAABB entityBox,
                            EntityAABB userControlledPlayerAABB) {

        this.userControlledPlayerAABB = userControlledPlayerAABB;
        this.chunks = chunks;
        this.myBox = entityBox;
        this.driver = driver;
        collisionData = new CollisionData();
        stepBox = new AABB();
        collisionBox = new AABB();

    }

    private boolean compareEntityAABB(Matrix4f projection, Matrix4f view, EntityAABB entityBox) {
        if (entityBox != myBox && entityBox.isSolid) {
            if (myBox.worldPosition.distance(entityBox.worldPosition) < ENTITY_COLLISION_CANDIDATE_CHECK_RADIUS) {
                processBox(entityBox.box, null, true);
                if (DRAW_COLLISION_CANDIDATES) {
                    driver.renderedBox.set(entityBox.box);
                    driver.renderedBox.draw(projection, view);
                }
                return true;
            }
        }
        return false;
    }

    public synchronized void resolveCollisions(Matrix4f projection, Matrix4f view) {
        driver.onGround = false;
        collisionData.reset();
        floorBlock = BlockRegistry.BLOCK_AIR;
       // drawTestBox(myBox.box, 0);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            stepBox.set(myBox.box);
            stepBox.setY(stepBox.max.y + driver.STEP_HEIGHT);
            setFrozen = false;
            exploredChunks.clear();

            processBlockCollisions();

            for (Chunk chunk : exploredChunks) {
                for (int i = 0; i < chunk.entities.list.size(); i++) {
                    compareEntityAABB(projection, view, chunk.entities.list.get(i).aabb);
                }
            }
            // Comparison against user controlled player (all entity and player boxes are
            // skipped if they match themselves)
            if (userControlledPlayerAABB != null) compareEntityAABB(projection, view, userControlledPlayerAABB);

            driver.setFrozen(setFrozen);
        }
    }

    private void processBlockCollisions() {
        //int ySpreadIndx = 0;

        /**
         * Top to bottom vs bottom to top determines if we will collide with the floor of a block when jumping against a wall, or collide with the ceiling
         */
        for (//Top to bottom
                int y = (int) (myBox.box.min.y - BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS);
                y < (myBox.box.max.y + BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS);
                y++) {
//        for (//Bottom to top
//                int y = (int) (myBox.box.max.y + BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS);
//                y >= (myBox.box.min.y - BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS);
//                y--) {
            //ySpreadIndx++;
            for (int x = (int) (myBox.box.min.x - BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS); x <= myBox.box.max.x
                    + BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS; x++) {
                for (int z = (int) (myBox.box.min.z - BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS); z <= myBox.box.max.z
                        + BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS; z++) {
                    wcc.set(x, y, z);
                    chunk = chunks.getChunk(wcc.chunk);
                    if (chunk != null) {
                        exploredChunks.add(chunk);

                        b = Registrys.blocks.getBlock(chunk.data.getBlock(
                                wcc.chunkVoxel.x,
                                wcc.chunkVoxel.y,
                                wcc.chunkVoxel.z));
                        if (b != null) {
                            if (b.solid) {
                                // if (Main.specialMode2) {
                                // TODO: chunk.getBlockData() is collision-handler memory culprit!!!
                                // Its ALL in the hashmap...
                                d = chunk.data.getBlockData(
                                        wcc.chunkVoxel.x,
                                        wcc.chunkVoxel.y,
                                        wcc.chunkVoxel.z);
                                // }
                                BlockType type = Registrys.blocks.getBlockType(b.renderType);
                                try {
                                    if (type != null) {
                                        //final float spread = 0;// ySpreadIndx * 0.001f
                                        type.getCollisionBoxes((aabb) -> {
                                            processBox(aabb, b, false);
                                        }, collisionBox, b, d, x, y, z);
                                    }
                                } catch (Exception e) {
                                    ErrorHandler.log(e);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private boolean calculateCollisionData(AABB box, Block block, boolean isEntity) {
        collisionData.calculateCollision(box, myBox.box, isEntity);
        if (isEntity) {
            collisionData.penPerAxes.mul(0.8f);
        }
        if (driver.velocity.y >= driver.maxFallSpeed * 0.99f) {//Hard collision
            if (collisionData.collisionNormal.y == -1) { //If we are hitting the ground hard, we still want to collide with it
                handleFloorCollision(box, block);
            }
        } else if (Math.abs(collisionData.penPerAxes.x) > 0.6f ||
                Math.abs(collisionData.penPerAxes.z) > 0.6f ||
                Math.abs(collisionData.penPerAxes.y) > 0.6f) {
            driver.velocity.y = 0;
            setFrozen = true;
            return false;
        }
        return true;
    }

    final float SIDE_COLLISON_CATCH_THRESHOLD = 0.15f;
    final float CEILING_COLLISON_CATCH_THRESHOLD = 0.2f;

    private void processBox(AABB box, Block block, boolean isEntity) {
        //We dont need to spread boxes out based on Y value
//        float spread = 0.01f;
//        if (box.min.x > myBox.box.min.x) {
//            box.max.x += spread;
//            box.min.x += spread;
//        } else if (box.max.x < myBox.box.max.x) {
//            box.max.x -= spread;
//            box.min.x -= spread;
//        }
//        if (box.min.z > myBox.box.min.z) {
//            box.max.z += spread;
//            box.min.z += spread;
//        } else if (box.max.z < myBox.box.max.z) {
//            box.max.z -= spread;
//            box.min.z -= spread;
//        }
        //drawTestBox(box, 1);

        if (box.intersects(myBox.box)) {
            if (calculateCollisionData(box, block, isEntity)) {
                if (collisionData.collisionNormal.x != 0) {
                    if (myBox.box.max.y - box.min.y < driver.STEP_HEIGHT) {
                        myBox.box.setY(myBox.box.min.y - Math.abs(collisionData.penPerAxes.x));
                    } else if (CollisionData.calculateZIntersection(myBox.box, box) > SIDE_COLLISON_CATCH_THRESHOLD) { //prevents the player from catching when rubbing against the wall
                        myBox.box.setX(myBox.box.min.x + (collisionData.penPerAxes.x));
                    }
                } else if (collisionData.collisionNormal.z != 0) {
                    if (myBox.box.max.y - box.min.y < driver.STEP_HEIGHT) {
                        myBox.box.setY(myBox.box.min.y - Math.abs(collisionData.penPerAxes.z));
                    } else if (CollisionData.calculateXIntersection(myBox.box, box) > SIDE_COLLISON_CATCH_THRESHOLD) {
                        myBox.box.setZ(myBox.box.min.z + (collisionData.penPerAxes.z));
                    }
                }

                if (collisionData.collisionNormal.y == 1 && box.min.y < myBox.box.min.y) { //Ceiling collision
                    /**
                     *If the size of the surface is small enough, cancel the collision
                     *This solves the problem when we jump and collide with a block
                     */
                    if (CollisionData.calculateXIntersection(myBox.box, box) > CEILING_COLLISON_CATCH_THRESHOLD
                            && CollisionData.calculateZIntersection(myBox.box, box) > CEILING_COLLISON_CATCH_THRESHOLD) {
                        driver.velocity.y = 0;
                        myBox.box.setY(myBox.box.min.y + collisionData.penPerAxes.y);
                    }

                } else if (collisionData.collisionNormal.y == -1) {  //Floor collision
                    handleFloorCollision(box, block);
                }
            }
        }
    }

    private void handleFloorCollision(AABB box, Block block) {
        floorBlock = block;
        if (floorBlock != null
                && floorBlock.bounciness > 0) {
            driver.velocity.y = -driver.velocity.y * floorBlock.bounciness;
        } else {
            driver.velocity.y = 0;
        }
        driver.onGround = true;
        myBox.box.setY(myBox.box.min.y + collisionData.penPerAxes.y);
    }
}
