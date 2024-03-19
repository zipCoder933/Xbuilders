/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.type;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.ObjToBlockModel;
import com.xbuilders.engine.mesh.chunkMesh.BufferSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;

import java.util.function.Consumer;

/**
 * @author zipCoder933
 */
public class PillarRenderer extends BlockType {

    BlockModel pillar;

    public PillarRenderer() {
//        ObjToBlockModel.parseFile(null, false, 1.6f,
//                ResourceUtils.resource("block types\\pillar.obj"));

        pillar = BlockModelLoader.load(ResourceUtils.resource("block types\\pillar.blockType"), renderSide_subBlock);
    }

    @Override
    public void constructBlock(BufferSet buffers, Block block, BlockData data,
                               Block[] neighbors, byte[] light,int x, int y, int z) {
        pillar.render(buffers, block, neighbors, light,x, y, z);
    }

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCollisionBoxes(consumer, box, block, data, x, y, z);
    }

    private final float sixteenthConstant = 0.0625f;
    private final float width = 1 - (sixteenthConstant * 4);

    @Override
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x + (sixteenthConstant * 2), y, z + (sixteenthConstant * 2), width, 1, width);
        consumer.accept(box);
    }
}
