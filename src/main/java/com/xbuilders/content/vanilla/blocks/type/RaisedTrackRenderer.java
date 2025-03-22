/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.blocks.type;

import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockType;
import com.xbuilders.engine.server.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.server.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.client.visuals.gameScene.rendering.VertexSet;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public class RaisedTrackRenderer extends BlockType {
    BlockModel floor0, floor1, floor2, floor3;

    public boolean allowExistence(Block block, int worldX, int worldY, int worldZ) {
        return !LocalServer.world.getBlock(worldX, worldY + 1, worldZ).isLiquid()
                && !LocalServer.world.getBlock(worldX, worldY + 1, worldZ).isAir();
    }

    public RaisedTrackRenderer() throws IOException {
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f, ResourceUtils.resource("block types\\raisedTrack\\raisedTrack.obj"));
        generate3DIcon = false;
        floor0 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/raisedTrack\\raisedTrack0.blockType"), renderSide_subBlock);
        floor1 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/raisedTrack\\raisedTrack1.blockType"), renderSide_subBlock);
        floor2 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/raisedTrack\\raisedTrack2.blockType"), renderSide_subBlock);
        floor3 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/raisedTrack\\raisedTrack3.blockType"), renderSide_subBlock);
        initializationCallback = (b) -> {

            b.initialBlockData = (existingData, player) -> {
                return player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
            };

            b.opaque = false;
            b.solid = false;
        };
    }


    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] lightValues, Chunk chunk, int chunkX, int chunkY, int chunkZ, boolean isUsingGreedyMesher) {
        if (data == null || data.get(0) == 0) {
            floor0.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 1) {
            floor1.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
        } else if (data.get(0) == 2) {
            floor2.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
        } else {
            floor3.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
        }
        return false;
    }
}
