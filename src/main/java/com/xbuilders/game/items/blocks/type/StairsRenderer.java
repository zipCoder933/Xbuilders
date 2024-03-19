/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.type;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.mesh.chunkMesh.BufferSet;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;

import java.util.function.Consumer;

/**
 * @author zipCoder933
 */
public class StairsRenderer extends BlockType {

    BlockModel[] side = new BlockModel[4];
    BlockModel[] floor = new BlockModel[4];
    BlockModel[] ceiling = new BlockModel[4];

    public StairsRenderer() {
//        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
//                ResourceUtils.resource("block types\\stairs\\side.obj"));
//        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
//                ResourceUtils.resource("block types\\stairs\\floor.obj"));
//        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
//                ResourceUtils.resource("block types\\stairs\\ceiling.obj"));

        BlockModel.ShouldRenderSide renderSide = new BlockModel.ShouldRenderSide() {
            @Override
            public boolean shouldRenderSide(Block thisBlock, Block neighbor) {
                return shouldRenderFace_subBlock(thisBlock, neighbor);
//                return neighbor.isSolid();
            }
        };

        for (int i = 0; i < 4; i++)
            side[i] = BlockModelLoader.load(ResourceUtils.resource("block types\\stairs\\side" + i + ".blockType"), renderSide);

        for (int i = 0; i < 4; i++)
            floor[i] = BlockModelLoader.load(ResourceUtils.resource("block types\\stairs\\floor" + i + ".blockType"), renderSide);

        for (int i = 0; i < 4; i++)
            ceiling[i] = BlockModelLoader.load(ResourceUtils.resource("block types\\stairs\\ceiling" + i + ".blockType"), renderSide);
    }

    @Override
    public BlockData getInitialBlockData(BlockData existingData, UserControlledPlayer player) {
        BlockData data = new BlockData(2);
        player.camera.simplifiedPanTiltAsBlockData(data);
        if (data.get(1) == (byte) 0
                && (Math.abs(player.camera.cursorRay.getHitNormalAsInt().x) != 0
                || Math.abs(player.camera.cursorRay.getHitNormalAsInt().z) != 0)) {
            data.set(1, (byte) 3);
        }
        return data;
    }

    @Override
    public void constructBlock(BufferSet buffers, Block block, BlockData data,
                               Block[] neighbors, byte[] light,int x, int y, int z) {


        if (data == null) {
            floor[0].render(buffers, block, neighbors, light, x, y, z);
        } else {
            if (data.get(1) == 3) {
               side[data.get(0)].render(buffers, block, neighbors, light, x, y, z);
            } else if (data.get(1) >= 0) {
                floor[data.get(0)].render(buffers, block, neighbors, light, x, y, z);
            } else {
               ceiling[data.get(0)].render(buffers, block, neighbors, light, x, y, z);
            }
        }
    }

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCollisionBoxes(consumer, box, block, data, x, y, z);
    }


    @Override
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (data != null) {
            if (data.get(1) == 3) {
                if (data.get(0) == 1) {
                    box.setPosAndSize(x + 0.5f, y, z + 0.5f, 0.5f, 1f, 0.5f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z, 1f, 1f, 0.5f);
                    consumer.accept(box);
                } else if (data.get(0) == 2) {
                    box.setPosAndSize(x + 0.5f, y, z, 0.5f, 1f, 0.5f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z + 0.5f, 1f, 1f, 0.5f);
                    consumer.accept(box);
                } else if (data.get(0) == 3) {
                    box.setPosAndSize(x, y, z, 0.5f, 1f, 0.5f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z + 0.5f, 1f, 1f, 0.5f);
                    consumer.accept(box);
                } else {
                    box.setPosAndSize(x, y, z + 0.5f, 0.5f, 1f, 0.5f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z, 1f, 1f, 0.5f);
                    consumer.accept(box);
                }
            } else if (data.get(1) >= 0) {
                if (data.get(0) == 1) {
                    box.setPosAndSize(x, y + 0.5f, z, 0.5f, 0.5f, 1f);
                    consumer.accept(box);
                    box.setPosAndSize(x + 0.5f, y, z, 0.5f, 1f, 1);
                    consumer.accept(box);
                } else if (data.get(0) == 2) {
                    box.setPosAndSize(x, y + 0.5f, z, 1f, 0.5f, 0.5f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z + 0.5f, 1f, 1f, 0.5f);
                    consumer.accept(box);
                } else if (data.get(0) == 3) {
                    box.setPosAndSize(x + 0.5f, y + 0.5f, z, 0.5f, 0.5f, 1f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z, 0.5f, 1f, 1f);
                    consumer.accept(box);
                } else {
                    box.setPosAndSize(x, y + 0.5f, z + 0.5f, 1f, 0.5f, 0.5f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z, 1f, 1f, 0.5f);
                    consumer.accept(box);
                }
            } else {
                if (data.get(0) == 1) {
                    box.setPosAndSize(x, y, z, 0.5f, 0.5f, 1f);
                    consumer.accept(box);
                    box.setPosAndSize(x + 0.5f, y, z, 0.5f, 1f, 1);
                    consumer.accept(box);
                } else if (data.get(0) == 2) {
                    box.setPosAndSize(x, y, z, 1f, 0.5f, 0.5f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z + 0.5f, 1f, 1f, 0.5f);
                    consumer.accept(box);
                } else if (data.get(0) == 3) {
                    box.setPosAndSize(x + 0.5f, y, z, 0.5f, 0.5f, 1f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z, 0.5f, 1f, 1f);
                    consumer.accept(box);
                } else {
                    box.setPosAndSize(x, y, z + 0.5f, 1f, 0.5f, 0.5f);
                    consumer.accept(box);
                    box.setPosAndSize(x, y, z, 1f, 1f, 0.5f);
                    consumer.accept(box);
                }
            }
        }
    }
}
