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
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;

/**
 * @author zipCoder933
 */
public class RaisedTrackRenderer extends BlockType {
    BlockModel floor0, floor1, floor2, floor3;

    public boolean allowExistence(Block block, int worldX, int worldY, int worldZ) {
        return !GameScene.world.getBlock(worldX, worldY + 1, worldZ).isLiquid()
                && !GameScene.world.getBlock(worldX, worldY + 1, worldZ).isAir();
    }

    public RaisedTrackRenderer() {
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f, ResourceUtils.resource("block types\\raisedTrack\\raisedTrack.obj"));
        generate3DIcon = false;
        floor0 = BlockModelLoader.load(ResourceUtils.resource("block types\\raisedTrack\\raisedTrack0.blockType"), renderSide_subBlock);
        floor1 = BlockModelLoader.load(ResourceUtils.resource("block types\\raisedTrack\\raisedTrack1.blockType"), renderSide_subBlock);
        floor2 = BlockModelLoader.load(ResourceUtils.resource("block types\\raisedTrack\\raisedTrack2.blockType"), renderSide_subBlock);
        floor3 = BlockModelLoader.load(ResourceUtils.resource("block types\\raisedTrack\\raisedTrack3.blockType"), renderSide_subBlock);
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = false;
        };
    }

    @Override
    public BlockData getInitialBlockData(BlockData existingData, UserControlledPlayer player) {
        return player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
    }


    @Override
    public void constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] lightValues, Chunk chunk, int chunkX, int chunkY, int chunkZ) {
        if (data == null || data.get(0) == 0) {
            floor0.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 1) {
            floor1.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 2) {
            floor2.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
        } else {
            floor3.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
        }
    }
}
