/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.blocks.type;

import com.xbuilders.engine.server.model.Server;
import com.xbuilders.engine.server.model.items.block.Block;
import com.xbuilders.engine.server.model.items.block.construction.BlockType;
import com.xbuilders.engine.server.model.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.server.model.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.client.visuals.gameScene.rendering.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.server.model.world.chunk.BlockData;
import com.xbuilders.engine.server.model.world.chunk.Chunk;

/**
 * @author zipCoder933
 */
public class FloorItemRenderer extends BlockType {
    BlockModel floor0, floor1, floor2, floor3;


    public boolean allowExistence(Block block, int worldX, int worldY, int worldZ) {
        return !Server.world.getBlock(worldX, worldY + 1, worldZ).isLiquid()
                && !Server.world.getBlock(worldX, worldY + 1, worldZ).isAir();
    }

    public FloorItemRenderer() {
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f, ResourceUtils.resource("block types\\floor\\floor.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f, ResourceUtils.resource("block types\\wall\\wall.obj"));
        generate3DIcon = false;
        floor0 = BlockModelLoader.load(ResourceUtils.resource("block types\\floor\\floor0.blockType"), renderSide_subBlock);
        floor1 = BlockModelLoader.load(ResourceUtils.resource("block types\\floor\\floor1.blockType"), renderSide_subBlock);
        floor2 = BlockModelLoader.load(ResourceUtils.resource("block types\\floor\\floor2.blockType"), renderSide_subBlock);
        floor3 = BlockModelLoader.load(ResourceUtils.resource("block types\\floor\\floor3.blockType"), renderSide_subBlock);
        initializationCallback = (b) -> {
            b.initialBlockData = (existingData, player) -> {
                return player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
            };

            b.opaque = false;
            b.solid = true;
            b.toughness = 0.1f;
        };
    }

    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] light, Chunk chunk, int chunkX, int chunkY, int chunkZ, boolean isUsingGreedyMesher) {

        if (data == null || data.get(0) == 3) {
            floor0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 0) {
            floor1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 1) {
            floor2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else {
            floor3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        }
        return false;
    }

    private final float sixteenthConstant = 0.0625f;

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x, y + (sixteenthConstant * 15), z, 1, sixteenthConstant * 1, 1);
        consumer.accept(box);
    }

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x, y + (sixteenthConstant * 15), z, 1, sixteenthConstant * 1, 1);
        consumer.accept(box);
    }

}
