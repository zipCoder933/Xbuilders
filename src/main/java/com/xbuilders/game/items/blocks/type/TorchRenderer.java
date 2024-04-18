/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.type;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.items.block.construction.BlockTypeModel.ObjToBlockModel;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;

import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.game.items.blocks.RenderType;

import java.util.function.Consumer;

/**
 * @author zipCoder933
 */
public class TorchRenderer extends BlockType {

    BlockModel torch;
    BlockModel[] fenceSide, sideBlock;

    @Override
    public boolean allowToBeSet(Block block, BlockData blockData, int worldX, int worldY, int worldZ) {
        return sideIsSolid(GameScene.world.getBlock(worldX, worldY+1, worldZ))||
                sideIsSolid(GameScene.world.getBlock(worldX+1, worldY, worldZ))||
                sideIsSolid(GameScene.world.getBlock(worldX-1, worldY, worldZ))||
                sideIsSolid(GameScene.world.getBlock(worldX, worldY, worldZ+1))||
                sideIsSolid(GameScene.world.getBlock(worldX, worldY, worldZ-1));
    }



    public TorchRenderer() {
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = true;
        };
//        ObjToBlockModel.parseFile(null, false,
//                1.6f, ResourceUtils.resource("block types\\torch\\torch.obj"));
//        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
//                ResourceUtils.resource("block types\\torch\\side.obj"));
//        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
//                ResourceUtils.resource("block types\\torch\\side block.obj"));

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

        if (neighbor.type == RenderType.FENCE) {
            fenceSide[i].render(buffers, block, neighbors, lightValues, x, y, z);
        } else {
            sideBlock[i].render(buffers, block, neighbors, lightValues, x, y, z);
        }
    }

    @Override
    public void constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, byte[] lightValues, int x, int y, int z) {
        if (data != null) {
            int dataValue = data.get(0);
            if (sideIsSolid(neighbors[POS_Z]) && dataValue == 2) {
                drawSide(2, neighbors[POS_Z],
                        buffers, block, neighbors, lightValues, x, y, z);
            } else if (sideIsSolid(neighbors[NEG_X]) && dataValue == 3) {
                drawSide(3, neighbors[NEG_X],
                        buffers, block, neighbors, lightValues, x, y, z);
            } else if (sideIsSolid(neighbors[NEG_Z]) && dataValue == 0) {
                drawSide(0, neighbors[NEG_Z],
                        buffers, block, neighbors, lightValues, x, y, z);
            } else if (sideIsSolid(neighbors[POS_X]) && dataValue == 1) {
                drawSide(1, neighbors[POS_X],
                        buffers, block, neighbors, lightValues, x, y, z);
            } else {
                torch.render(buffers, block, neighbors, lightValues, x, y, z);
            }
        } else {
            if (sideIsSolid(neighbors[POS_Y])) {
                torch.render(buffers, block, neighbors, lightValues, x, y, z);
            } else if (sideIsSolid(neighbors[POS_Z])) {
                drawSide(2, neighbors[POS_Z],
                        buffers, block, neighbors, lightValues, x, y, z);
            } else if (sideIsSolid(neighbors[NEG_X])) {
                drawSide(3, neighbors[NEG_X],
                        buffers, block, neighbors, lightValues, x, y, z);
            } else if (sideIsSolid(neighbors[NEG_Z])) {
                drawSide(0, neighbors[NEG_Z],
                        buffers, block, neighbors, lightValues, x, y, z);
            } else if (sideIsSolid(neighbors[POS_X])) {
                drawSide(1, neighbors[POS_X],
                        buffers, block, neighbors, lightValues, x, y, z);
            } else {
                torch.render(buffers, block, neighbors, lightValues, x, y, z);
            }
        }
    }

    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {

    }

    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        consumer.accept(box.setPosAndSize(x + 0.4f, y, z + 0.4f, 0.2f, 1, 0.2f));
    }
}
