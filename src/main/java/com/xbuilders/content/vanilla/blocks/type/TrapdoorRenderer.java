/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.blocks.type;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.client.visuals.gameScene.rendering.VertexSet;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockType;
import com.xbuilders.engine.server.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.server.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.utils.math.AABB;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public class TrapdoorRenderer extends BlockType {

    BlockModel open0, open1, open2, open3;
    BlockModel closed0, closed1, closed2, closed3;

    public TrapdoorRenderer() throws IOException {
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\trapdoor\\open.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\trapdoor\\closed.obj"));
        generate3DIcon = false;
        initializationCallback = (b) -> {

            b.initialBlockData = (existingData, player) -> {
                BlockData bd = new BlockData(2);
                player.camera.simplifiedPanTiltAsBlockData(bd);
                bd.set(1, (byte) 1); // (xz orientation), (0 = open, 1 = closed)
                return bd;
            };

            b.opaque = false;
            b.solid = true;
            b.clickEvent(false, (x, y, z) -> {
                BlockData bd = LocalClient.world.getBlockData(x, y, z);
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

        open0 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/trapdoor\\open0.blockType"), renderSide);
        open1 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/trapdoor\\open1.blockType"), renderSide);
        open2 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/trapdoor\\open2.blockType"), renderSide);
        open3 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/trapdoor\\open3.blockType"), renderSide);
        closed0 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/trapdoor\\closed0.blockType"), renderSide);
        closed1 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/trapdoor\\closed1.blockType"), renderSide);
        closed2 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/trapdoor\\closed2.blockType"), renderSide);
        closed3 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/trapdoor\\closed3.blockType"), renderSide);
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

    final float width = 3;
    final float offset = (16 - width);

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (data != null) {
            if (data.get(1) == 1) {
                box.setPosAndSize(x, y + ONE_SIXTEENTH * offset, z, 1, ONE_SIXTEENTH * width, 1);
            } else {
                switch (data.get(0)) {
                    case 1 -> box.setPosAndSize(x + ONE_SIXTEENTH * offset, y, z, ONE_SIXTEENTH * width, 1, 1);
                    case 2 -> box.setPosAndSize(x, y, z + ONE_SIXTEENTH * offset, 1, 1, ONE_SIXTEENTH * width);
                    case 3 -> box.setPosAndSize(x, y, z, ONE_SIXTEENTH * width, 1, 1);
                    default -> box.setPosAndSize(x, y, z, 1, 1, ONE_SIXTEENTH * width);
                }
            }
            consumer.accept(box);
        }
    }

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCursorBoxes(consumer, box, block, data, x, y, z);
    }

}
