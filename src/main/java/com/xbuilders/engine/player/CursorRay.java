package com.xbuilders.engine.player;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.player.camera.Camera;
import com.xbuilders.engine.player.raycasting.Ray;
import com.xbuilders.engine.player.raycasting.RayCasting;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.function.BiConsumer;

public class CursorRay {

    public boolean hitTarget() {
        return cursorRayHitAllBlocks || cursorRay.hitTarget;
    }

    public Vector3i getHitPos() {
        return cursorRay.getHitPositionAsInt();
    }

    public Vector3i getHitPosPlusNormal() {
        return cursorRay.getHitPosPlusNormal();
    }

    public Vector3i getHitNormalAsInt() {
        return cursorRay.getHitNormalAsInt();
    }

    public Vector3f getHitNormal() {
        return cursorRay.getHitNormal();
    }

    public Entity getEntity() {
        return cursorRay.entity;
    }

    public CursorRay(Camera camera) {
        this.camera = camera;
        cursorRay = new Ray();
    }

    public void init() {
        cursorBox = new Box();
        cursorBox.setPosAndSize(0, 0, 0, 1, 1, 1);
        cursorBox.setColor(DEFAULT_COLOR);
        cursorBox.setLineWidth(3);
    }

    public final Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);
    public final Camera camera;
    public Box cursorBox;
    public final Ray cursorRay;
    public boolean cursorRayHitAllBlocks = false;

    // Boundary mode:
    private boolean useBoundary = false;
    private boolean boundary_isStartNodeSet = false;
    public boolean boundary_useHitPos = false;
    public boolean boundary_lockToPlane = false;
    private BiConsumer<AABB, Boolean> boundaryConsumer;
    private final Vector3i boundary_startNode = new Vector3i();
    private final Vector3i boundary_endNode = new Vector3i();
    private final AABB boundary_aabb = new AABB();

    public void enableBoundaryMode(BiConsumer<AABB, Boolean> createBoundaryConsumer) {
        useBoundary = true;
        boundary_isStartNodeSet = false;
        this.boundaryConsumer = createBoundaryConsumer;
    }

    public void disableBoundaryMode() {
        useBoundary = false;
        boundaryConsumer = null;
    }

    /**
     * @return if the event was consumed
     */
    public boolean clickEvent(boolean creationMode) {
        ItemStack selectedItem = MainWindow.game.getSelectedItem();

        if (MainWindow.game.clickEvent(this, creationMode)) { //Game click event
            return true;
        } else if (useBoundary) { //Boundary click event
            boundaryClickEvent(creationMode);
            return true;
        }

        if (creationMode) {
            if (cursorRay.entity != null) { //Entity click event
                return cursorRay.entity.run_ClickEvent();
            } else { //Block click event
                Block block = GameScene.world.getBlock(getHitPos().x, getHitPos().y, getHitPos().z);
                boolean consumed = block.run_ClickEvent(GameScene.player.eventPipeline.clickEventThread, getHitPos());
                if (consumed) return true;
            }
        }

        if (selectedItem != null) { //Item click event
            if (creationMode) {
                if (selectedItem.item.createClickEvent != null) {
                    return selectedItem.item.createClickEvent.run(this);
                } else if (selectedItem.item.getBlock() != null || selectedItem.item.getEntity() != null)
                    defaultSetEvent(selectedItem);
            } else {
                if (selectedItem.item.destroyClickEvent != null) {
                    return selectedItem.item.destroyClickEvent.run(this);
                }
            }
        }

        if (hitTarget() && !creationMode) { //By default, remove anything the cursor is pointing at
            if (getEntity() != null) {
                getEntity().destroy();
            } else {
                GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, new WCCi().set(getHitPos()));
            }
            return true;
        }
        return false;
    }

    private void defaultSetEvent(ItemStack stack) {
        Block block = stack.item.getBlock();
        EntitySupplier entity = stack.item.getEntity();

        if (stack.stackSize <= 0) return;
        if (block != null) {
            Block hitBlock = MainWindow.gameScene.world.getBlock(cursorRay.getHitPositionAsInt());
            Vector3i set = cursorRay.getHitPosPlusNormal();
            if (hitBlock.getRenderType().replaceOnSet) set = cursorRay.getHitPositionAsInt();
            if (GameScene.getGameMode() != GameMode.FREEPLAY) stack.stackSize--;
            GameScene.player.setBlock(block.id, set.x, set.y, set.z);
        } else if (entity != null) {
            Vector3f pos = new Vector3f(cursorRay.getHitPosPlusNormal());
            if (GameScene.getGameMode() != GameMode.FREEPLAY) stack.stackSize--;
            GameScene.player.setEntity(entity, pos, null);
        }
    }

    private void boundaryClickEvent(boolean create) {
        if (!boundary_isStartNodeSet) {
            setBoundaryStartNode(boundary_startNode);
            boundary_isStartNodeSet = true;
        } else {
            if (boundaryIsWithinArea()) {
                if (boundaryConsumer != null)
                    boundaryConsumer.accept(boundary_aabb, create);
                makeAABBFrom2Points(boundary_startNode, boundary_endNode, boundary_aabb);
                boundary_isStartNodeSet = false;
            } else GameScene.alert("Boundary is too large");
        }
    }

    public static final int BOUNDARY_USE_HIT_POS_KEY = GLFW.GLFW_KEY_K;

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (useBoundary) {
                if (key == BOUNDARY_USE_HIT_POS_KEY) {
                    boundary_useHitPos = true;
                    return false;
                }
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            if (useBoundary) {
                if (key == BOUNDARY_USE_HIT_POS_KEY) {
                    boundary_useHitPos = false;
                    return false;
                }
            }
        }
        return false;
    }

    private void makeAABBFrom2Points(Vector3i start, Vector3i end, AABB aabb) {
        int minX = Math.min(start.x, end.x);
        int minY = Math.min(start.y, end.y);
        int minZ = Math.min(start.z, end.z);
        int maxX = Math.max(start.x, end.x);
        int maxY = Math.max(start.y, end.y);
        int maxZ = Math.max(start.z, end.z);
        aabb.setPosAndSize(
                minX,
                minY,
                minZ,
                (int) MathUtils.dist(minX, maxX + 1),
                (int) MathUtils.dist(minY, maxY + 1),
                (int) MathUtils.dist(minZ, maxZ + 1));
    }

    private void setBoundaryStartNode(Vector3i node) {
        if (boundary_useHitPos || (boundary_lockToPlane && boundary_isStartNodeSet))
            node.set(getHitPos());
        else
            node.set(getHitPosPlusNormal());
    }

    private void setBoundaryEndNode(Vector3i node) {
        setBoundaryStartNode(node);
    }


    public boolean boundaryIsWithinArea() {
        int maxWidth = GameScene.world.getDeletionViewDistance() - Chunk.WIDTH;
        return boundary_aabb.getXLength() < maxWidth &&
                boundary_aabb.getZLength() < maxWidth
                &&
                (
                        boundary_aabb.getXLength() *
                                boundary_aabb.getZLength() *
                                boundary_aabb.getYLength() <
                                MainWindow.settings.internal_blockBoundaryAreaLimit);
    }

    public void drawRay() {
        if (hitTarget() && useBoundary) {
            if (!boundary_isStartNodeSet) {
                setBoundaryStartNode(boundary_startNode);
                boundary_aabb.setPosAndSize(boundary_startNode.x, boundary_startNode.y, boundary_startNode.z, 1, 1,
                        1);
            } else {
                setBoundaryEndNode(boundary_endNode);
                makeAABBFrom2Points(boundary_startNode, boundary_endNode, boundary_aabb);
            }

            if (boundaryIsWithinArea()) {
                cursorBox.setColor(DEFAULT_COLOR);
            } else {
                cursorBox.setColor(1, 0, 0, 1);
            }
            cursorBox.set(boundary_aabb);
            cursorBox.draw(camera.projection, camera.view);
        } else if (MainWindow.game.drawCursor(this)) {
        } else if (hitTarget()) {
            if (cursorRay.entity != null) {
                cursorBox.set(cursorRay.entity.aabb.box);
                cursorBox.draw(camera.projection, camera.view);
            } else if (cursorRay.cursorBoxes == null) {
                cursorBox.setPosAndSize(
                        (int) cursorRay.getHitPosition().x,
                        (int) cursorRay.getHitPosition().y,
                        (int) cursorRay.getHitPosition().z,
                        1, 1, 1);
                cursorBox.draw(camera.projection, camera.view);
            } else {
                for (AABB box : cursorRay.cursorBoxes) {
                    cursorBox.set(box);
                    cursorBox.draw(camera.projection, camera.view);
                }
            }
        }
    }

    public int rayDistance = 1000;// Max distance for front ray

    public void cast(Vector3f position, Vector3f cursorRaycastLook, World world) {
        if (cursorRayHitAllBlocks)
            rayDistance = MathUtils.clamp(rayDistance, 1, MainWindow.settings.game_cursorRayDist);
        else
            rayDistance = MainWindow.settings.game_cursorRayDist;

        Vector2i simplifiedPanTilt = GameScene.player.camera.simplifiedPanTilt;

        RayCasting.traceComplexRay(cursorRay, position, cursorRaycastLook, rayDistance,
                ((block, forbiddenBlock, rx, ry, rz) -> {
                    // block ray if we are locking boundary to plane
                    if (useBoundary && boundary_lockToPlane && boundary_isStartNodeSet) {
                        if (simplifiedPanTilt.y != 0) {
                            if (ry == boundary_startNode.y) {
                                return true;
                            }
                        } else if (simplifiedPanTilt.x == 1 || simplifiedPanTilt.x == 3) {
                            if (rx == boundary_startNode.x) {
                                return true;
                            }
                        } else {
                            if (rz == boundary_startNode.z) {
                                return true;
                            }
                        }
                    }
                    if (cursorRayHitAllBlocks) {
                        return block != forbiddenBlock;
                    } else
                        return block != BlockRegistry.BLOCK_AIR.id &&
                                block != forbiddenBlock;
                }),
                ((entity) -> {
                    return true;
                }),
                world);


        if (useBoundary && boundary_lockToPlane && boundary_isStartNodeSet) {
            if (simplifiedPanTilt.y != 0) {
                cursorRay.hitPostition.y = boundary_startNode.y;
            } else if (simplifiedPanTilt.x == 1 || simplifiedPanTilt.x == 3) {
                cursorRay.hitPostition.x = boundary_startNode.x;
            } else {
                cursorRay.hitPostition.z = boundary_startNode.z;
            }
        }


        //Clamp the cursor position to the world bounds
        cursorRay.hitPostition.x = MathUtils.clamp(cursorRay.hitPostition.x, World.WORLD_SIZE_NEG_X, World.WORLD_SIZE_POS_X);
        cursorRay.hitPostition.z = MathUtils.clamp(cursorRay.hitPostition.z, World.WORLD_SIZE_NEG_Z, World.WORLD_SIZE_POS_Z);

        if (cursorRay.hitPostition.y < World.WORLD_TOP_Y + 1)
            cursorRay.hitPostition.y = World.WORLD_TOP_Y + 1;
    }

    public String toString() {
        return MiscUtils.printVector(cursorRay.getHitPosition());
    }


}
