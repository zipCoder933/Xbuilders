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
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.game.items.blocks.RenderType;

import java.util.function.Consumer;

/**
 * @author zipCoder933
 */
public class FenceRenderer extends BlockType {

    BlockModel post, boards0, boards1, boards2, boards3;

    public FenceRenderer() {
        ObjToBlockModel.parseDirectory(null,
                false, 1.6f,
                ResourceUtils.resource("block types\\fence"));
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
                && block.type != RenderType.FLOOR
                && block.type != RenderType.WALL_ITEM
                && block.type != RenderType.SPRITE;
    }

    @Override
    public void constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, byte[] light, int x, int y, int z) {

        post.render(buffers, block, neighbors,light, x, y, z);

        if (isSolid(neighbors[POS_Z])) {
            boards3.render(buffers, block, neighbors,light, x, y, z);
        }
        if (isSolid(neighbors[NEG_X])) {
            boards0.render(buffers, block, neighbors,light, x, y, z);
        }
        if (isSolid(neighbors[NEG_Z])) {
            boards1.render(buffers, block, neighbors,light, x, y, z);
        }
        if (isSolid(neighbors[POS_X])) {
            boards2.render(buffers, block, neighbors,light, x, y, z);
        }
    }

    float sixtheenth = 0.0625f;

    @Override
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x + (sixtheenth * 6), y - 0.5f, z + (sixtheenth * 6), (sixtheenth * 4), 1.5f, (sixtheenth * 4));
        consumer.accept(box);

        if (isSolid(GameScene.world.getBlock(x + 1, y, z))) {
            box.setPosAndSize(x + (sixtheenth * 10), y - 0.5f, z + (sixtheenth * 6), (sixtheenth * 6), 1.5f, (sixtheenth * 4));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x - 1, y, z))) {
            box.setPosAndSize(x, y - 0.5f, z + (sixtheenth * 6), (sixtheenth * 6), 1.5f, (sixtheenth * 4));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x, y, z + 1))) {
            box.setPosAndSize(x + (sixtheenth * 6), y - 0.5f, z + (sixtheenth * 10), (sixtheenth * 4), 1.5f, (sixtheenth * 6));
            consumer.accept(box);
        }
        if (isSolid(GameScene.world.getBlock(x, y, z - 1))) {
            box.setPosAndSize(x + (sixtheenth * 6), y - 0.5f, z, (sixtheenth * 4), 1.5f, (sixtheenth * 6));
            consumer.accept(box);
        }
    }

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
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
