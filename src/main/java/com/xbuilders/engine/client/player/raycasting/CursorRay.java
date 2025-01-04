package com.xbuilders.engine.client.player.raycasting;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.items.block.Block;
import com.xbuilders.engine.server.items.block.BlockRegistry;
import com.xbuilders.engine.server.items.entity.Entity;
import com.xbuilders.engine.server.items.entity.EntitySupplier;
import com.xbuilders.engine.server.items.item.ItemStack;
import com.xbuilders.engine.server.items.loot.LootTableRegistry;
import com.xbuilders.engine.client.player.camera.Camera;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.client.visuals.gameScene.rendering.wireframeBox.Box;
import com.xbuilders.engine.server.world.World;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.wcc.WCCi;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

public class CursorRay {

    public boolean hitTarget() {
        return angelPlacementMode || cursorRay.hitTarget;
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

    public CursorRay(Camera camera, ClientWindow window) {
        this.camera = camera;
        this.window = window;
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

    public int angelPlacementReachDistance = 7;
    public boolean angelPlacementMode = false;
    ClientWindow window;

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
        if (Server.getGameMode() == GameMode.SPECTATOR) return false;
        breakAmt = 0;
        breakPercentage = 0;
        ItemStack selectedItem = Server.userPlayer.getSelectedItem();
        if (!hitTarget()) return false;

        if (ClientWindow.game.clickEvent(this, creationMode)) { //Game click event
            return true;
        } else if (useBoundary) { //Boundary click event
            boundaryClickEvent(creationMode);
            return true;
        }

        //TODO: Add crouching to user so that they can flip the priority of block and entity click events
        if (blockEntityClickEvent(creationMode)) return true;
        if (itemClickEvent(selectedItem, creationMode)) return true;


        if (!creationMode) { //By default, remove anything the cursor is pointing at
            breakBlock(false, selectedItem);
            return true;
        }
        return false;
    }

    private boolean blockEntityClickEvent(boolean creationMode) {
        if (creationMode) {
            if (cursorRay.entity != null) { //Entity click event
                return cursorRay.entity.run_ClickEvent();
            } else { //Block click event
                Block block = Server.world.getBlock(getHitPos().x, getHitPos().y, getHitPos().z);
                boolean consumed = block.run_ClickEvent(Server.eventPipeline.clickEventThread, getHitPos());
                if (consumed) return true;
            }
        }
        return false;
    }


    private boolean itemClickEvent(ItemStack selectedItem, boolean creationMode) {
        if (selectedItem != null) {
            if (selectedItem.item.isFood()) {
                eatFood(selectedItem);
                return true;
            }
            if (creationMode) {
                if (selectedItem.item.createClickEvent != null) {
                    return selectedItem.item.createClickEvent.run(this, selectedItem);
                } else if (selectedItem.item.getBlock() != null || selectedItem.item.getEntity() != null)
                    return defaultSetEvent(selectedItem);
            } else {
                if (selectedItem.item.destroyClickEvent != null) {
                    return selectedItem.item.destroyClickEvent.run(this, selectedItem);
                }
            }
        }
        return false;
    }

    private float breakAmt = 0;
    public float breakPercentage = 0;
    private final Vector3i lastBreakPos = new Vector3i();


    private float getMiningSpeed(ItemStack selectedItem) {
        float miningSpeed = 0.005f;
        if (selectedItem != null) miningSpeed *= selectedItem.item.miningSpeedMultiplier;
        return miningSpeed;
    }


    private void eatFood(ItemStack selectedItem) {
        Server.userPlayer.addHunger(selectedItem.item.hungerSaturation);
        selectedItem.stackSize--;
    }

    private boolean toolIsEasierToMineWith(Block block, ItemStack tool) {
        if (tool != null) {
            //if the item has a tag that makes mining easier, double the mining speed
            if (block.easierMiningTool_tag != null
                    && tool.item.tags.contains(block.easierMiningTool_tag)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasToolThatCanMine(Block block, ItemStack tool) {
        if (block.toolsThatCanMine_tags != null) {
            if (tool != null) {
                for (String toolTag : block.toolsThatCanMine_tags) {
                    if (tool.item.tags.contains(toolTag)) {
                        return true;
                    }
                }
            }
            return false;
        } else return true;
    }

    private void breakBlock(boolean isHeld, ItemStack selectedItem) {
        if (!Server.world.inBounds(getHitPos().x, getHitPos().y, getHitPos().z)) return;

        if (isHeld) {
            if (Server.getGameMode() != GameMode.FREEPLAY) {
                if (!getHitPos().equals(lastBreakPos)) {
                    breakAmt = 0;
                    lastBreakPos.set(getHitPos());
                }
                Block existingBlock = Server.world.getBlock(getHitPos().x, getHitPos().y, getHitPos().z);
                if (existingBlock.isLiquid()) return;
                float miningSpeed = getMiningSpeed(selectedItem);

                //If the block should have a tool that can mine it, and the player is using the right tool, mine faster
                if (toolIsEasierToMineWith(existingBlock, selectedItem)) {
                    miningSpeed *= 2.5f;
                }

                //If the block requires a tool to mine it, and the player doesn't have the right tool, don't mine at all
                if (!hasToolThatCanMine(existingBlock, selectedItem)) {
                    breakAmt = 0;
                }


                float blockToughness = existingBlock.toughness;
                breakPercentage = breakAmt / blockToughness;
                breakAmt += miningSpeed;

                if (selectedItem != null && selectedItem.item.maxDurability > 0) {
                    selectedItem.durability -= 0.005f;
                    if (selectedItem.durability <= 0) selectedItem.destroy();
                }
                if (breakAmt >= blockToughness) {
                    if (LootTableRegistry.blockLootTables.get(existingBlock.alias) != null) {
                        LootTableRegistry.blockLootTables.get(existingBlock.alias).randomItems((itemStack) -> {
                            Server.placeItemDrop(new Vector3f(getHitPos()), itemStack, false);
                        });
                    }
                    Server.setBlock(BlockRegistry.BLOCK_AIR.id, new WCCi().set(getHitPos()));
                    breakAmt = 0;
                }
            }
        } else { //Click
            if (Server.getGameMode() == GameMode.FREEPLAY) {
                if (getEntity() != null) {
                    getEntity().destroy();
                } else {
                    Server.setBlock(BlockRegistry.BLOCK_AIR.id, new WCCi().set(getHitPos()));
                }
            }
        }
    }

    long autoClick_timeSinceReleased;
    long autoClick_lastClicked = 0;
    final int AUTO_CLICK_INTERVAL = 250;

    public void update() {
        if (Server.getGameMode() == GameMode.SPECTATOR) return;

        if (!ClientWindow.gameScene.ui.anyMenuOpen()) {
            //Auto click
            if (window.isMouseButtonPressed(UserControlledPlayer.getCreateMouseButton())) {
                if (System.currentTimeMillis() - autoClick_timeSinceReleased > AUTO_CLICK_INTERVAL * 1.5 &&
                        System.currentTimeMillis() - autoClick_lastClicked > AUTO_CLICK_INTERVAL) {
                    autoClick_lastClicked = System.currentTimeMillis();
                    camera.cursorRay.clickEvent(true);
                }
            } else if (Server.getGameMode() == GameMode.FREEPLAY && window.isMouseButtonPressed(UserControlledPlayer.getDeleteMouseButton())) {
                if (System.currentTimeMillis() - autoClick_timeSinceReleased > AUTO_CLICK_INTERVAL * 1.5 &&
                        System.currentTimeMillis() - autoClick_lastClicked > AUTO_CLICK_INTERVAL) {
                    autoClick_lastClicked = System.currentTimeMillis();
                    camera.cursorRay.clickEvent(false);
                }
            } else autoClick_timeSinceReleased = System.currentTimeMillis();

            //Removal
            if (window.isMouseButtonPressed(UserControlledPlayer.getDeleteMouseButton())) {
                ItemStack selectedItem = Server.userPlayer.getSelectedItem();
                if (selectedItem == null || !selectedItem.item.isFood()) {
                    breakBlock(true, selectedItem);
                }
            } else breakPercentage = 0;
        }
    }

    private boolean blockIntersectsPlayer(Block block, Vector3i set) {
        AABB boxAABB = new AABB();
        //If the block is too close to the player, don't place
        AtomicBoolean intersects = new AtomicBoolean(false);
        BlockData initialData = block.getInitialBlockData(null, Server.userPlayer);

        block.getRenderType().getCollisionBoxes((aabb) -> {
            if (aabb.intersects(Server.userPlayer.aabb.box) &&
                    Server.userPlayer.aabb.box.max.y > aabb.min.y + 0.1f) //small padding to help with placing
                intersects.set(true);
        }, boxAABB, block, initialData, set.x, set.y, set.z);

        return intersects.get();
    }

    private boolean defaultSetEvent(ItemStack stack) {
        Block block = stack.item.getBlock();
        EntitySupplier entity = stack.item.getEntity();

        if (stack.stackSize <= 0) return false;
        if (block != null) {
            Block hitBlock = Server.world.getBlock(cursorRay.getHitPositionAsInt());
            Vector3i set = cursorRay.getHitPositionAsInt();

            if (!hitBlock.getRenderType().replaceOnSet) {
                set = cursorRay.getHitPosPlusNormal();
                if (blockIntersectsPlayer(block, set)) return false;
            }
            if (Server.getGameMode() != GameMode.FREEPLAY) stack.stackSize--;
            Server.setBlock(block.id, set.x, set.y, set.z);
            return true;
        } else if (entity != null) {
            Vector3f pos = new Vector3f(cursorRay.getHitPosPlusNormal());
            if (Server.getGameMode() != GameMode.FREEPLAY) stack.stackSize--;
            Server.placeEntity(entity, pos, null);
            return true;
        }
        return false;
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
            } else Server.alert("Boundary is too large");
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
        int maxWidth = Server.world.getDeletionViewDistance() - Chunk.WIDTH;
        return boundary_aabb.getXLength() < maxWidth &&
                boundary_aabb.getZLength() < maxWidth
                &&
                (
                        boundary_aabb.getXLength() *
                                boundary_aabb.getZLength() *
                                boundary_aabb.getYLength() <
                                ClientWindow.settings.internal_blockBoundaryAreaLimit);
    }

    public void drawRay() {
        if (Server.getGameMode() == GameMode.SPECTATOR) return;

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
        } else if (ClientWindow.game.drawCursor(this)) {
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

    final int MAX_RAY_DISTANCE = 512;
    private int rayDistance = MAX_RAY_DISTANCE;// Max distance for front ray

    public void cast(Vector3f position, Vector3f cursorRaycastLook, World world) {

        Vector2i simplifiedPanTilt = Server.userPlayer.camera.simplifiedPanTilt;


        int distance = getRayDistance();
        if (angelPlacementMode) distance = Math.min(distance, angelPlacementReachDistance);

        RayCasting.traceComplexRay(cursorRay, position, cursorRaycastLook, distance,
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
                    if (angelPlacementMode) {
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


    public int getRayDistance() {
        return rayDistance;
    }

    public void setRayDistance(int rayDistance) {
        this.rayDistance = MathUtils.clamp(rayDistance, 1, MAX_RAY_DISTANCE);
    }
}
