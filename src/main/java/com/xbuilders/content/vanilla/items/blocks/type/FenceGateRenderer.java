/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.blocks.type;

import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.block.Block;
import com.xbuilders.engine.server.model.items.block.construction.BlockType;
import com.xbuilders.engine.server.model.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.server.model.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.client.visuals.rendering.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.server.model.world.chunk.BlockData;
import com.xbuilders.engine.server.model.world.chunk.Chunk;
import com.xbuilders.content.vanilla.items.blocks.RenderType;

/**
 * @author zipCoder933
 */
public class FenceGateRenderer extends BlockType {

    BlockModel open0, open1, open2, open3;
    BlockModel closed0, closed1, closed2, closed3;

    public FenceGateRenderer() {
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\fence gate\\open.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\fence gate\\closed.obj"));

        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = true;
            b.setBlockEvent(false, (x, y, z) -> {
                BlockData bd = GameScene.world.getBlockData(x, y, z);
                // Get blocks at neighboring block locaitons
                Block block = GameScene.world.getBlock(x - 1, y, z);
                Block block2 = GameScene.world.getBlock(x + 1, y, z);
                Block block3 = GameScene.world.getBlock(x, y, z - 1);
                Block block4 = GameScene.world.getBlock(x, y, z + 1);
                if (block.renderType == RenderType.FENCE && block2.renderType == RenderType.FENCE) {
                    bd.set(0, (byte) 0);
                } else if (block3.renderType == RenderType.FENCE && block4.renderType == RenderType.FENCE) {
                    bd.set(0, (byte) 1);
                }
            });
            b.clickEvent(false, (x, y, z) -> {
                BlockData bd = GameScene.world.getBlockData(x, y, z);
                bd.set(1, (byte) (bd.get(1) == 1 ? 0 : 1));
            });
        };

        BlockModel.ShouldRenderSide renderSide = new BlockModel.ShouldRenderSide() {
            @Override
            public boolean shouldRenderSide(Block thisBlock, Block neighbor) {
                return shouldRenderFace_subBlock(thisBlock, neighbor);
                // return neighbor.isSolid();
            }
        };

        open0 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence gate\\open0.blockType"), renderSide);
        open1 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence gate\\open1.blockType"), renderSide);
        open2 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence gate\\open2.blockType"), renderSide);
        open3 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence gate\\open3.blockType"), renderSide);
        closed0 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence gate\\closed0.blockType"),
                renderSide);
        closed1 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence gate\\closed1.blockType"),
                renderSide);
        closed2 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence gate\\closed2.blockType"),
                renderSide);
        closed3 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence gate\\closed3.blockType"),
                renderSide);
    }

    @Override
    public BlockData getInitialBlockData(BlockData existingData, Block block, UserControlledPlayer player) {
        BlockData bd = new BlockData(2);
        int rotation = GameScene.player.camera.simplifiedPanTilt.x;
        bd.set(0, (byte) rotation);
        bd.set(1, (byte) 1); // (xz orientation), (0 = open, 1 = closed)
        return bd;
    }

    final float ONE_SIXTEENTH = 1 / 16f;

    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] light, Chunk chunk, int chunkX,
                                  int chunkY, int chunkZ, boolean isUsingGreedyMesher) {
        boolean open = data != null && data.get(1) == 0;
        if (data == null || data.get(0) == 3) {
            if (open)
                open3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            else
                closed3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 0) {
            if (open)
                open0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            else
                closed0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 1) {
            if (open)
                open1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            else
                closed1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else {
            if (open)
                open2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            else
                closed2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        }
        return false;
    }

    final float width = 4;
    final float offset = (ONE_SIXTEENTH / 2) + ((ONE_SIXTEENTH * width) * 1.5f);

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (data != null) {
            if (data.get(0) == 1 || data.get(0) == 3) {
                box.setPosAndSize(x + offset, y, z,
                        ONE_SIXTEENTH * width, 1, 1);
            } else {
                box.setPosAndSize(x, y, z + offset,
                        1, 1, ONE_SIXTEENTH * width);
            }
            consumer.accept(box);
        }
    }

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (data.get(1) == 1)
            getCursorBoxes(consumer, box, block, data, x, y, z);
    }
}