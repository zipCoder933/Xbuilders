/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.type;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.ObjToBlockModel;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.game.items.blocks.RenderType;

import java.util.function.Consumer;

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
            b.setBlockEvent((x, y, z) -> {
                BlockData bd = GameScene.world.getBlockData(x, y, z);
                // Get blocks at neighboring block locaitons
                Block block = GameScene.world.getBlock(x - 1, y, z);
                Block block2 = GameScene.world.getBlock(x + 1, y, z);
                Block block3 = GameScene.world.getBlock(x, y, z - 1);
                Block block4 = GameScene.world.getBlock(x, y, z + 1);
                if (block.type == RenderType.FENCE && block2.type == RenderType.FENCE) {
                    bd.set(0, (byte) 0);
                } else if (block3.type == RenderType.FENCE && block4.type == RenderType.FENCE) {
                    bd.set(0, (byte) 1);
                }
            });
            b.clickEvent((x, y, z, bd) -> {
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
    public BlockData getInitialBlockData(BlockData existingData, UserControlledPlayer player) {
        BlockData bd = new BlockData(2);
        int rotation = GameScene.player.camera.simplifiedPanTilt.x;
        bd.set(0, (byte) rotation);
        bd.set(1, (byte) 1); // (xz orientation), (0 = open, 1 = closed)
        return bd;
    }

    final float ONE_SIXTEENTH = 1 / 16f;

    @Override
    public void constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, byte[] light, int x,
            int y, int z) {
        boolean open = data != null && data.get(1) == 0;
        if (data == null || data.get(0) == 3) {
            if (open)
                open3.render(buffers, block, neighbors, light, x, y, z);
            else
                closed3.render(buffers, block, neighbors, light, x, y, z);
        } else if (data.get(0) == 0) {
            if (open)
                open0.render(buffers, block, neighbors, light, x, y, z);
            else
                closed0.render(buffers, block, neighbors, light, x, y, z);
        } else if (data.get(0) == 1) {
            if (open)
                open1.render(buffers, block, neighbors, light, x, y, z);
            else
                closed1.render(buffers, block, neighbors, light, x, y, z);
        } else {
            if (open)
                open2.render(buffers, block, neighbors, light, x, y, z);
            else
                closed2.render(buffers, block, neighbors, light, x, y, z);
        }
    }

    final float width = 4;
    final float offset = (ONE_SIXTEENTH / 2) + ((ONE_SIXTEENTH * width) * 1.5f);

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
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
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (data.get(1) == 1)
            getCursorBoxes(consumer, box, block, data, x, y, z);
    }
}
