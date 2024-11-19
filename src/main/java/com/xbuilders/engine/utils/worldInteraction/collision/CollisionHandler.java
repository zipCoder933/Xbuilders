package com.xbuilders.engine.utils.worldInteraction.collision;

import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.wcc.WCCi;

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
        collisionData.reset();
        floorBlock = BlockRegistry.BLOCK_AIR;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            stepBox.set(myBox.box);
            stepBox.setY(stepBox.max.y + driver.stepHeight);
            setFrozen = false;
            exploredChunks.clear();

            // Y goes down so that we can sort blocks from top (ceiling) to bottom
            for (int y = (int) (myBox.box.max.y + BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS); y >= myBox.box.min.y
                    - BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS; y--) {
                // for (int y = (int) (myBox.box.minPoint.y -
                // BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS); y <= myBox.box.maxPoint.y +
                // BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS; y++) {
                for (int x = (int) (myBox.box.min.x - BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS); x <= myBox.box.max.x
                        + BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS; x++) {
                    for (int z = (int) (myBox.box.min.z - BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS); z <= myBox.box.max.z
                            + BLOCK_COLLISION_CANDIDATE_CHECK_RADIUS; z++) {
                        wcc.set(x, y, z);
                        chunk = chunks.getChunk(wcc.chunk);
                        if (chunk != null) {
                            exploredChunks.add(chunk);
                            // if (Main.specialMode1) {
                            b = Registrys.blocks.getItem(chunk.data.getBlock(
                                    wcc.chunkVoxel.x,
                                    wcc.chunkVoxel.y,
                                    wcc.chunkVoxel.z));
                            if (b != null && b.solid) {
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
                                        type.getCollisionBoxes((aabb) -> {
                                            processBox(aabb, b, false);
                                        }, collisionBox, b, d, x, y, z);
                                    }
                                } catch (Exception e) {
                                    ErrorHandler.log(e);
                                }
                            }
                            // }
                        }
                    }
                }
            }

            for (Chunk chunk : exploredChunks) {
                for (int i = 0; i < chunk.entities.list.size(); i++) {
                    compareEntityAABB(projection, view, chunk.entities.list.get(i).aabb);
                }
            }
//            if (playerList != null) {
//                for (int i = 0; i < playerList.size(); i++) {
//                    compareEntityAABB(projection, view, playerList.get(i).aabb);
//                }
//            }
            // Comparison against user controlled player (all entity and player boxes are
            // skipped if they match themselves)
            if (userControlledPlayerAABB != null) compareEntityAABB(projection, view, userControlledPlayerAABB);

            driver.setFrozen(setFrozen);
        }
    }


    long lastIllegalFloorCollisionTime = 0;

    private void processBox(AABB box, Block block, boolean isEntity) {
        // if (stepBox.intersects(box)) {
        // stepWillHitCeiling = true;
        // System.out.println("STEP HIT CEILING " + System.currentTimeMillis());
        // }

        if (box.intersects(myBox.box)) {
            collisionData.calculateCollision(box, myBox.box, isEntity);

            if (isEntity) {
                collisionData.penPerAxes.mul(0.8f);
            }

            if (driver.velocity.y >= driver.maxFallSpeed * 0.99f) {
//                System.out.println("HARD COLLISION!: " + collisionData.penPerAxes);
                if (collisionData.collisionNormal.y == -1) { //If we are hitting the ground hard, we still want to collide with it
                    handleFloorCollision(box, block);
                }
            } else if (Math.abs(collisionData.penPerAxes.x) > 0.6f ||
                    Math.abs(collisionData.penPerAxes.z) > 0.6f ||
                    Math.abs(collisionData.penPerAxes.y) > 0.6f) {
                driver.velocity.y = 0;
                driver.onGround = true;
                setFrozen = true;
                return;
            }

            if (collisionData.collisionNormal.x != 0) {
                if (myBox.box.max.y - box.min.y < driver.stepHeight) {
                    myBox.box.setY(myBox.box.min.y - Math.abs(collisionData.penPerAxes.x));
                } else {
                    myBox.box.setX(myBox.box.min.x + (collisionData.penPerAxes.x));
                }
            } else if (collisionData.collisionNormal.z != 0) {
                if (myBox.box.max.y - box.min.y < driver.stepHeight) {
                    myBox.box.setY(myBox.box.min.y - Math.abs(collisionData.penPerAxes.z));
                } else {
                    myBox.box.setZ(myBox.box.min.z + (collisionData.penPerAxes.z));
                }
            }
            //Floor collision
            else if (collisionData.collisionNormal.y == -1) {
                if (CollisionData.calculateXIntersection(myBox.box, box) < 0.25f
                        || CollisionData.calculateZIntersection(myBox.box, box) < 0.25f) {
                    return;
                }
                handleFloorCollision(box, block);
            } else if (collisionData.collisionNormal.y == 1 && box.min.y < myBox.box.min.y) { //Ceiling collision
                driver.velocity.y = 0;
                myBox.box.setY(myBox.box.min.y + collisionData.penPerAxes.y);
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
