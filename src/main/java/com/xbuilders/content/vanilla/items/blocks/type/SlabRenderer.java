/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.blocks.type;

import com.xbuilders.engine.server.items.block.Block;
import com.xbuilders.engine.server.items.block.construction.BlockType;
import com.xbuilders.engine.server.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.server.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.client.visuals.gameScene.rendering.VertexSet;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;

/**
 * @author zipCoder933
 */
public class SlabRenderer extends BlockType {

    BlockModel ceiling, floor, side0, side1, side2, side3;

    public SlabRenderer() {
//        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
//                ResourceUtils.resource("block types\\slab\\sideSlab.obj"));
//
//        ObjToBlockModel.parseFile(null,false, 1.6f,
//                ResourceUtils.resource("block types\\slab\\ceilingSlab.obj"));
//
//        ObjToBlockModel.parseFile(null,false, 1.6f,
//                ResourceUtils.resource("block types\\slab\\floorSlab.obj"));


        BlockModel.ShouldRenderSide renderSide = new BlockModel.ShouldRenderSide() {
            @Override
            public boolean shouldRenderSide(Block thisBlock, Block neighbor) {
                return shouldRenderFace_subBlock(thisBlock, neighbor);
//                return neighbor.isSolid();
            }
        };

        ceiling = BlockModelLoader.load(ResourceUtils.resource("block types\\slab\\ceilingSlab.blockType"), renderSide);
        floor = BlockModelLoader.load(ResourceUtils.resource("block types\\slab\\floorSlab.blockType"), renderSide);

        side0 = BlockModelLoader.load(ResourceUtils.resource("block types\\slab\\sideSlab0.blockType"), renderSide);
        side1 = BlockModelLoader.load(ResourceUtils.resource("block types\\slab\\sideSlab1.blockType"), renderSide);
        side2 = BlockModelLoader.load(ResourceUtils.resource("block types\\slab\\sideSlab2.blockType"), renderSide);
        side3 = BlockModelLoader.load(ResourceUtils.resource("block types\\slab\\sideSlab3.blockType"), renderSide);
        initializationCallback = (b) -> {

            b.initialBlockData = (existingData, player) -> {
                BlockData data = player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
                return data;
            };

            b.opaque = false;
            b.solid = true;
        };
    }


    private static boolean sideIsVisible(Block thisBlock, Block side) {
        return side.opaque != thisBlock.opaque;
    }

    @Override
    public boolean constructBlock(VertexSet buffers, Block block,
                                  BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] light,
                                  Chunk chunk, int chunkX, int chunkY, int chunkZ, boolean isUsingGreedyMesher) {

        if (data != null && data.get(1) == -1) {
            ceiling.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else if (data != null && data.get(1) == 1) {
            floor.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else if (data != null) {
            if (data.get(0) == 0) {
                side1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 1) {
                side2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 2) {
                side3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 3) {
                side0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            }
        }
        else side0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);

        return false;
    }

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCollisionBoxes(consumer, box, block, data, x, y, z);
    }

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {

        if (data != null) {
            if (data.get(1) == -1) {
                box.setPosAndSize(x, y, z, 1, 0.5f, 1);
                consumer.accept(box);
            } else if (data.get(1) == 1) {
                box.setPosAndSize(x, y + 0.5f, z, 1, 0.5f, 1);
                consumer.accept(box);
            } else {
                if (data.get(0) == 0) {
                    box.setPosAndSize(x, y, z, 1, 1, 0.5f);
                    consumer.accept(box);
                } else if (data.get(0) == 1) {
                    box.setPosAndSize(x + 0.5f, y, z, 0.5f, 1, 1);
                    consumer.accept(box);
                } else if (data.get(0) == 2) {
                    box.setPosAndSize(x, y, z + 0.5f, 1, 1, 0.5f);
                    consumer.accept(box);
                } else {
                    box.setPosAndSize(x, y, z, 0.5f, 1, 1);
                    consumer.accept(box);
                }
            }
        }
    }


}
