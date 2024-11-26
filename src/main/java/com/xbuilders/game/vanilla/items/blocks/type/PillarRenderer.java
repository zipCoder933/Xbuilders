/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.items.blocks.type;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;

/**
 * @author zipCoder933
 */
public class PillarRenderer extends BlockType {

    BlockModel pillar;

    public PillarRenderer() {
        // ObjToBlockModel.parseFile(null, false, 1.6f,
        //         ResourceUtils.resource("block types\\pillar.obj"));

        pillar = BlockModelLoader.load(ResourceUtils.resource("block types\\pillar.blockType"), renderSide_subBlock);
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = true;
        };
    }

    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data,
                                  Block[] neighbors, BlockData[] neighborData, byte[] light, Chunk chunk, int chunkX, int chunkY, int chunkZ, boolean isUsingGreedyMesher) {
        pillar.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);

        return false;
    }

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCollisionBoxes(consumer, box, block, data, x, y, z);
    }

    private final float sixteenthConstant = 0.0625f;
    private final float width = 1 - (sixteenthConstant * 4);

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x + (sixteenthConstant * 2), y, z + (sixteenthConstant * 2), width, 1, width);
        consumer.accept(box);
    }
}
