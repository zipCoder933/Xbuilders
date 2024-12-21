/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.blocks.type;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.block.BlockRegistry;
import com.xbuilders.engine.game.model.items.Registrys;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.game.model.items.block.construction.BlockType;
import com.xbuilders.engine.game.model.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.game.model.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.client.visuals.rendering.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.game.model.world.chunk.BlockData;
import com.xbuilders.engine.game.model.world.chunk.Chunk;

/**
 * @author zipCoder933
 */
public class DoorHalfRenderer extends BlockType {

    BlockModel left_open0, left_open1, left_open2, left_open3;
    BlockModel left_closed0, left_closed1, left_closed2, left_closed3;

    BlockModel right_open0, right_open1, right_open2, right_open3;
    BlockModel right_closed0, right_closed1, right_closed2, right_closed3;

    public DoorHalfRenderer() {
        generate3DIcon = false;
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\door\\left closed.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\door\\left open.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\door\\right closed.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\door\\right open.obj"));

        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = true;

            if (b.properties.containsKey("vertical_pair") && b.properties.containsKey("placement")
                    && b.properties.get("placement").equals("bottom")) { // If this is the bottom of a pair
                // System.out.println("DOOR: "+b.properties);
                short top = Short.parseShort(b.properties.get("vertical_pair"));
                Block topBlock = Registrys.getBlock(top);
                Block bottomBlock = b;

                topBlock.setBlockEvent(false, (x, y, z) -> { //KEEP THIS!
                    GameScene.setBlock(bottomBlock.id, x, y + 1, z);
                });

                topBlock.removeBlockEvent(false, (x, y, z, history) -> {
                    if (GameScene.world.getBlock(x, y + 1, z) == bottomBlock) {
                        GameScene.setBlock(BlockRegistry.BLOCK_AIR.id, x, y + 1, z);
                    }
                });

                bottomBlock.setBlockEvent(false, (x, y, z) -> {
                    BlockData data = GameScene.world.getBlockData(x, y, z);
                    GameScene.setBlock(topBlock.id, x, y - 1, z);
                    boolean right = orientRightOrLeft(data, x, y, z);
                    //We cant change right/left here because that will get overridden when initial block data gets written
                    //A solution to this is when the initialBlockData is called, it returns the existing data if it is already set
                    GameScene.world.setBlockData(data, x, y - 1, z);
                });

                bottomBlock.removeBlockEvent(false, (x, y, z, history) -> {
                    if (GameScene.world.getBlock(x, y - 1, z) == topBlock) {
                        GameScene.setBlock(BlockRegistry.BLOCK_AIR.id, x, y - 1, z);
                    }
                });

                topBlock.clickEvent(false, (x, y, z) -> {
                    BlockData bd = GameScene.world.getBlockData(x, y, z);
                    bd.set(1, (byte) (bd.get(1) == 1 ? 0 : 1));
                    // Transfer block data to bottom block
                    GameScene.world.setBlockData(bd, x, y + 1, z);
                });
                bottomBlock.clickEvent(false, (x, y, z) -> {
                    BlockData bd = GameScene.world.getBlockData(x, y, z);
                    bd.set(1, (byte) (bd.get(1) == 1 ? 0 : 1));
                    // Transfer block data to top block
                    GameScene.world.setBlockData(bd, x, y - 1, z);
                });

            } else {// If this is a single door
                b.setBlockEvent(false, (x, y, z) -> {
                    BlockData data = GameScene.world.getBlockData(x, y, z);
                    boolean right = orientRightOrLeft(data, x, y, z);
                });
                b.clickEvent(false, (x, y, z) -> {
                    BlockData bd = GameScene.world.getBlockData(x, y, z);
                    bd.set(1, (byte) (bd.get(1) == 1 ? 0 : 1));
                    GameScene.world.setBlockData(bd, x, y - 1, z);
                });
            }
        };

        BlockModel.ShouldRenderSide renderSide = new BlockModel.ShouldRenderSide() {
            @Override
            public boolean shouldRenderSide(Block thisBlock, Block neighbor) {
                return shouldRenderFace_subBlock(thisBlock, neighbor);
                // return neighbor.isSolid();
            }
        };

        left_open0 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left open0.blockType"),
                renderSide);
        left_open1 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left open1.blockType"),
                renderSide);
        left_open2 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left open2.blockType"),
                renderSide);
        left_open3 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left open3.blockType"),
                renderSide);

        left_closed0 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left closed0.blockType"),
                renderSide);
        left_closed1 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left closed1.blockType"),
                renderSide);
        left_closed2 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left closed2.blockType"),
                renderSide);
        left_closed3 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left closed3.blockType"),
                renderSide);

        right_open0 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right open0.blockType"),
                renderSide);
        right_open1 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right open1.blockType"),
                renderSide);
        right_open2 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right open2.blockType"),
                renderSide);
        right_open3 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right open3.blockType"),
                renderSide);

        right_closed0 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right closed0.blockType"),
                renderSide);
        right_closed1 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right closed1.blockType"),
                renderSide);
        right_closed2 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right closed2.blockType"),
                renderSide);
        right_closed3 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right closed3.blockType"),
                renderSide);
    }

    private boolean check_orientRightOrLeft(int x, int y, int z) {
        Block block = GameScene.world.getBlock(x, y, z);
        return block.solid
                && block.getRenderType().isCubeShape();
    }

    private boolean orientRightOrLeft(BlockData data, int x, int y, int z) {
        // Get blocks at neighboring block locaitons
        boolean right = true;
        int xzOrientation = data.get(0);
        if (xzOrientation == 1) {
            if (check_orientRightOrLeft((int) x - 1, (int) y, (int) z)) {
                right = false;
            }
        } else if (xzOrientation == 2) {
            if (check_orientRightOrLeft((int) x, (int) y, (int) z - 1)) {
                right = false;
            }
        } else if (xzOrientation == 3) {
            if (check_orientRightOrLeft((int) x + 1, (int) y, (int) z)) {
                right = false;
            }
        } else {
            if (check_orientRightOrLeft((int) x, (int) y, (int) z + 1)) {
                right = false;
            }
        }
        data.set(2, (byte) (right ? 1 : 0));
        return right;
    }

    @Override
    public BlockData getInitialBlockData(BlockData existingData, Block block, UserControlledPlayer player) {
        //If we already set the block data for this block, skip making new stuff
        if (existingData != null && existingData.size() == 3) return existingData;

        BlockData bd = new BlockData(3);
        byte rotation = (byte) GameScene.player.camera.simplifiedPanTilt.x;
        rotation = (byte) MathUtils.positiveMod((rotation - 1), 4);
        bd.set(0, rotation);// rotation
        bd.set(1, (byte) 1); // (0 = open, 1 = closed),
        bd.set(2, (byte) 1);// (0=left, 1=right)
        return bd;
    }

    final float ONE_SIXTEENTH = 1 / 16f;

    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] light, Chunk chunk, int chunkX,
                                  int chunkY, int chunkZ, boolean isUsingGreedyMesher) {
        boolean open = data != null && data.get(1) == 0;
        boolean left = data != null && data.get(2) == 0;
        if (left) {
            if (data == null || data.get(0) == 3) {
                if (open)
                    left_open3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                else
                    left_closed3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 0) {
                if (open)
                    left_open0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                else
                    left_closed0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 1) {
                if (open)
                    left_open1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                else
                    left_closed1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else {
                if (open)
                    left_open2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                else
                    left_closed2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            }
        } else {
            if (data == null || data.get(0) == 3) {
                if (open)
                    right_open3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                else
                    right_closed3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 0) {
                if (open)
                    right_open0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                else
                    right_closed0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 1) {
                if (open)
                    right_open1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                else
                    right_closed1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else {
                if (open)
                    right_open2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                else
                    right_closed2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            }
        }
        return false;
    }

    final float width = 3.3f;
    final float offset = (ONE_SIXTEENTH / 2) - (ONE_SIXTEENTH * width) / 2;
    final float open_offset = (16 - width);

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (data != null) {
            boolean open = data.get(1) == 0;
            boolean left = data.get(2) == 0;

            int rotation = data.get(0); // Rotation stays from range 0-3
            // If closed, offset rotation by 1
            if (open) {
                if (left) {
                    rotation = MathUtils.positiveMod((rotation - 1), 4);
                } else {
                    rotation = MathUtils.positiveMod((rotation + 1), 4);
                }
            }

            switch (rotation) {
                case 2 -> box.setPosAndSize(x + ONE_SIXTEENTH * open_offset, y, z, ONE_SIXTEENTH * width, 1, 1);
                case 3 -> box.setPosAndSize(x, y, z + ONE_SIXTEENTH * open_offset, 1, 1, ONE_SIXTEENTH * width);
                case 0 -> box.setPosAndSize(x, y, z, ONE_SIXTEENTH * width, 1, 1);
                default -> box.setPosAndSize(x, y, z, 1, 1, ONE_SIXTEENTH * width);
            }

            consumer.accept(box);
        }
    }

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCursorBoxes(consumer, box, block, data, x, y, z);
    }

}
