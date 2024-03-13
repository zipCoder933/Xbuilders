/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.type;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.ObjToBlockModel;
import com.xbuilders.engine.mesh.BufferSet;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * @author zipCoder933
 */
public class FloorItemRenderer extends BlockType {
    BlockModel floor0, floor1, floor2, floor3;

    public FloorItemRenderer() {
//        ObjToBlockModel.parseFileWithYRotations(false, 1.6f, ResourceUtils.resource("block types\\floor\\floor.obj"));
//        ObjToBlockModel.parseFileWithYRotations(false, 1.6f, ResourceUtils.resource("block types\\wall\\wall.obj"));
        floor0 = BlockModelLoader.load(ResourceUtils.resource("block types\\floor\\floor0.blockType"), renderSide_subBlock);
        floor1 = BlockModelLoader.load(ResourceUtils.resource("block types\\floor\\floor1.blockType"), renderSide_subBlock);
        floor2 = BlockModelLoader.load(ResourceUtils.resource("block types\\floor\\floor2.blockType"), renderSide_subBlock);
        floor3 = BlockModelLoader.load(ResourceUtils.resource("block types\\floor\\floor3.blockType"), renderSide_subBlock);
    }

    @Override
    public BlockData getInitialBlockData(BlockData existingData, UserControlledPlayer player) {
        return player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
    }

    @Override
    public void constructBlock(BufferSet buffers, Block block, BlockData data, Block[] neighbors, int x, int y, int z) {

        if (data == null || data.get(0) == 3) {
            floor0.render(buffers, block, neighbors, x, y, z);
        } else if (data.get(0) == 0) {
            floor1.render(buffers, block, neighbors, x, y, z);
        } else if (data.get(0) == 1) {
            floor2.render(buffers, block, neighbors, x, y, z);
        } else {
            floor3.render(buffers, block, neighbors, x, y, z);
        }
    }

    private final float sixteenthConstant = 0.0625f;

    @Override
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x, y + (sixteenthConstant * 15), z, 1, sixteenthConstant * 1, 1);
        consumer.accept(box);
    }

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x, y + (sixteenthConstant * 15), z, 1, sixteenthConstant * 1, 1);
        consumer.accept(box);
    }

}
