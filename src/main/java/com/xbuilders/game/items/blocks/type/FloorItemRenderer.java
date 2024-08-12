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
import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;

import java.util.function.Consumer;

/**
 * @author zipCoder933
 */
public class FloorItemRenderer extends BlockType {
    BlockModel floor0, floor1, floor2, floor3;


    public boolean allowExistence(Block block, int worldX, int worldY, int worldZ) {
        return !GameScene.world.getBlock(worldX, worldY + 1, worldZ).isLiquid()
                && !GameScene.world.getBlock(worldX, worldY + 1, worldZ).isAir();
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
            b.opaque = false;
            b.solid = true;
        };
    }

    @Override
    public BlockData getInitialBlockData(BlockData existingData, UserControlledPlayer player) {
        return player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
    }

    @Override
    public void constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] light, Chunk chunk, int chunkX, int chunkY, int chunkZ) {

        if (data == null || data.get(0) == 3) {
            floor0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 0) {
            floor1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 1) {
            floor2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        } else {
            floor3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
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
