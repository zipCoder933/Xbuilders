/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.blocks.type;

import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.server.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.client.visuals.gameScene.rendering.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;

import com.xbuilders.engine.server.block.construction.BlockType;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.content.vanilla.items.blocks.RenderType;

/**
 * @author zipCoder933
 */
public class TorchRenderer extends BlockType {

    BlockModel torch;
    BlockModel[] fenceSide, sideBlock;

    @Override
    public boolean allowExistence(Block block, int worldX, int worldY, int worldZ) {
        return sideIsSolid(Server.world.getBlock(worldX, worldY + 1, worldZ)) ||
                sideIsSolid(Server.world.getBlock(worldX + 1, worldY, worldZ)) ||
                sideIsSolid(Server.world.getBlock(worldX - 1, worldY, worldZ)) ||
                sideIsSolid(Server.world.getBlock(worldX, worldY, worldZ + 1)) ||
                sideIsSolid(Server.world.getBlock(worldX, worldY, worldZ - 1));
    }


    public TorchRenderer() {
        generate3DIcon = false;
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = true;
            b.toughness = 0.1f;
        };
        // ObjToBlockModel.parseFile(null, false,
        //         1.6f, ResourceUtils.resource("block types\\torch\\torch.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\torch\\side.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
        //         ResourceUtils.resource("block types\\torch\\side block.obj"));

        torch = BlockModelLoader.load(ResourceUtils.resource("block types\\torch\\torch.blockType"),
                (t, n) -> shouldRenderFace_subBlock(t, n));
        fenceSide = new BlockModel[4];
        sideBlock = new BlockModel[4];
        for (int i = 0; i < 4; i++) {
            fenceSide[i] = BlockModelLoader.load(ResourceUtils.resource("block types\\torch\\side" + i + ".blockType"),
                    (t, n) -> shouldRenderFace_subBlock(t, n));
            sideBlock[i] = BlockModelLoader.load(ResourceUtils.resource("block types\\torch\\side block" + i + ".blockType"),
                    (t, n) -> shouldRenderFace_subBlock(t, n));
        }
    }

    boolean sideIsSolid(Block block) {
        return block.solid;
    }

    void drawSide(int i, Block neighbor,
                  VertexSet buffers, Block block, Block[] neighbors, byte[] lightValues, int x, int y, int z) {

        if (neighbor.renderType == RenderType.FENCE) {
            fenceSide[i].render(buffers, block, neighbors, lightValues, x, y, z);
        } else {
            sideBlock[i].render(buffers, block, neighbors, lightValues, x, y, z);
        }
    }

    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors,
                                  BlockData[] neighborData, byte[] lightValues, Chunk chunk, int chunkX, int chunkY, int chunkZ,
                                  boolean isUsingGreedyMesher) {
        if (data != null && data.size() > 0) {
            int dataValue = data.get(0);
            if (sideIsSolid(neighbors[POS_Z]) && dataValue == 2) {
                drawSide(2, neighbors[POS_Z],
                        buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else if (sideIsSolid(neighbors[NEG_X]) && dataValue == 3) {
                drawSide(3, neighbors[NEG_X],
                        buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else if (sideIsSolid(neighbors[NEG_Z]) && dataValue == 0) {
                drawSide(0, neighbors[NEG_Z],
                        buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else if (sideIsSolid(neighbors[POS_X]) && dataValue == 1) {
                drawSide(1, neighbors[POS_X],
                        buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else {
                torch.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            }
        } else {
            if (sideIsSolid(neighbors[POS_Y])) {
                torch.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else if (sideIsSolid(neighbors[POS_Z])) {
                drawSide(2, neighbors[POS_Z],
                        buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else if (sideIsSolid(neighbors[NEG_X])) {
                drawSide(3, neighbors[NEG_X],
                        buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else if (sideIsSolid(neighbors[NEG_Z])) {
                drawSide(0, neighbors[NEG_Z],
                        buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else if (sideIsSolid(neighbors[POS_X])) {
                drawSide(1, neighbors[POS_X],
                        buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            } else {
                torch.render(buffers, block, neighbors, lightValues, chunkX, chunkY, chunkZ);
            }
        }

        return false;
    }

    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {

    }

    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        consumer.accept(box.setPosAndSize(x + 0.4f, y, z + 0.4f, 0.2f, 1, 0.2f));
    }
}
