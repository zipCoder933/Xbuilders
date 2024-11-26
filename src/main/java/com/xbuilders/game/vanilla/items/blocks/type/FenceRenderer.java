/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.items.blocks.type;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.vanilla.items.blocks.RenderType;

/**
 * @author zipCoder933
 */
public class FenceRenderer extends BlockType {

    BlockModel post, boards0, boards1, boards2, boards3;

    public FenceRenderer() {
//        ObjToBlockModel.parseDirectory(null,
//                false, 1.6f,
//                ResourceUtils.resource("block types\\fence"));
        post = BlockModelLoader.load(ResourceUtils.resource("block types\\fence\\post.blockType"), renderSide_subBlock);
        boards0 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence\\boards.blockType"), renderSide_subBlock);
        boards1 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence\\boards1.blockType"), renderSide_subBlock);
        boards2 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence\\boards2.blockType"), renderSide_subBlock);
        boards3 = BlockModelLoader.load(ResourceUtils.resource("block types\\fence\\boards3.blockType"), renderSide_subBlock);
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = true;
        };
    }

    private boolean isSolid(Block block) {
        return block != null
                && block.solid
                && block.renderType != RenderType.FLOOR
                && block.renderType != RenderType.WALL_ITEM
                && block.renderType != RenderType.SPRITE;
    }

    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] light, Chunk chunk, int chunkX, int chunkY, int chunkZ, boolean isUsingGreedyMesher) {

        post.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);

        if (isSolid(neighbors[POS_Z])) {
            boards3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        }
        if (isSolid(neighbors[NEG_X])) {
            boards0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        }
        if (isSolid(neighbors[NEG_Z])) {
            boards1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        }
        if (isSolid(neighbors[POS_X])) {
            boards2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        }
        return false;
    }

    float sixtheenth = 0.0625f;
    final float fenceHeight = 0.7f;//What to subtract regular fence top from

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x + (sixtheenth * 6), y - fenceHeight, z + (sixtheenth * 6), (sixtheenth * 4), 1 + fenceHeight, (sixtheenth * 4));
        consumer.accept(box);

        if (isSolid(GameScene.world.getBlock(x + 1, y, z))) {
            box.setPosAndSize(x + (sixtheenth * 10), y - fenceHeight, z + (sixtheenth * 6), (sixtheenth * 6), 1 + fenceHeight, (sixtheenth * 4));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x - 1, y, z))) {
            box.setPosAndSize(x, y - fenceHeight, z + (sixtheenth * 6), (sixtheenth * 6), 1 + fenceHeight, (sixtheenth * 4));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x, y, z + 1))) {
            box.setPosAndSize(x + (sixtheenth * 6), y - fenceHeight, z + (sixtheenth * 10), (sixtheenth * 4), 1 + fenceHeight, (sixtheenth * 6));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x, y, z - 1))) {
            box.setPosAndSize(x + (sixtheenth * 6), y - fenceHeight, z, (sixtheenth * 4), 1 + fenceHeight, (sixtheenth * 6));
            consumer.accept(box);
        }
    }

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x + (sixtheenth * 6), y, z + (sixtheenth * 6), (sixtheenth * 4), 1f, (sixtheenth * 4));
        consumer.accept(box);

        if (isSolid(GameScene.world.getBlock(x + 1, y, z))) {
            box.setPosAndSize(x + (sixtheenth * 10), y, z + (sixtheenth * 6), (sixtheenth * 6), 1f, (sixtheenth * 4));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x - 1, y, z))) {
            box.setPosAndSize(x, y, z + (sixtheenth * 6), (sixtheenth * 6), 1f, (sixtheenth * 4));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x, y, z + 1))) {
            box.setPosAndSize(x + (sixtheenth * 6), y, z + (sixtheenth * 10), (sixtheenth * 4), 1f, (sixtheenth * 6));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x, y, z - 1))) {
            box.setPosAndSize(x + (sixtheenth * 6), y, z, (sixtheenth * 4), 1f, (sixtheenth * 6));
            consumer.accept(box);
        }
    }

}
