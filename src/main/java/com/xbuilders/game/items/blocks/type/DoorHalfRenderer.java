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
import com.xbuilders.engine.items.block.construction.BlockTypeModel.ObjToBlockModel;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;

import java.util.function.Consumer;

/**
 * @author zipCoder933
 */
public class DoorHalfRenderer extends BlockType {

    BlockModel left_open0, left_open1, left_open2, left_open3;
    BlockModel left_closed0, left_closed1, left_closed2, left_closed3;

    BlockModel right_open0, right_open1, right_open2, right_open3;
    BlockModel right_closed0, right_closed1, right_closed2, right_closed3;

    public DoorHalfRenderer() {
        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
                ResourceUtils.resource("block types\\door\\left closed.obj"));
        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
                ResourceUtils.resource("block types\\door\\left open.obj"));
        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
                ResourceUtils.resource("block types\\door\\right closed.obj"));
        ObjToBlockModel.parseFileWithYRotations(false, 1.6f,
                ResourceUtils.resource("block types\\door\\right open.obj"));

        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = true;
            b.clickEvent((x, y, z, bd) -> {
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

        left_open0 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left open0.blockType"),
                renderSide);
        left_open1 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left open1.blockType"),
                renderSide);
        left_open2 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left open2.blockType"),
                renderSide);
        left_open3 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left open3.blockType"),
                renderSide);

        left_closed0 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left closed0.blockType"),
                renderSide);
        left_closed1 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left closed1.blockType"),
                renderSide);
        left_closed2 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left closed2.blockType"),
                renderSide);
        left_closed3 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\left closed3.blockType"),
                renderSide);

        right_open0 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right open0.blockType"),
                renderSide);
        right_open1 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right open1.blockType"),
                renderSide);
        right_open2 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right open2.blockType"),
                renderSide);
        right_open3 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right open3.blockType"),
                renderSide);

        right_closed0 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right closed0.blockType"),
                renderSide);
        right_closed1 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right closed1.blockType"),
                renderSide);
        right_closed2 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right closed2.blockType"),
                renderSide);
        right_closed3 = BlockModelLoader.load(ResourceUtils.resource("block types\\door\\right closed3.blockType"),
                renderSide);
    }

    @Override
    public BlockData getInitialBlockData(BlockData existingData, UserControlledPlayer player) {
        BlockData bd = new BlockData(3);
        bd.set(0, (byte) GameScene.player.camera.simplifiedPanTilt.x);
        bd.set(1, (byte) 1); // (xz orientation), (0 = open, 1 = closed), (0=left, 1=right)
        bd.set(2, (byte) 1);
        return bd;
    }

    final float ONE_SIXTEENTH = 1 / 16f;

    @Override
    public void constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, byte[] light, int x,
            int y, int z) {
        boolean open = data.get(1) == 0;
        boolean left = data.get(2) == 0;
        if (left) {
            if (data == null || data.get(0) == 3) {
                if (open)
                    left_open3.render(buffers, block, neighbors, light, x, y, z);
                else
                    left_closed3.render(buffers, block, neighbors, light, x, y, z);
            } else if (data.get(0) == 0) {
                if (open)
                    left_open0.render(buffers, block, neighbors, light, x, y, z);
                else
                    left_closed0.render(buffers, block, neighbors, light, x, y, z);
            } else if (data.get(0) == 1) {
                if (open)
                    left_open1.render(buffers, block, neighbors, light, x, y, z);
                else
                    left_closed1.render(buffers, block, neighbors, light, x, y, z);
            } else {
                if (open)
                    left_open2.render(buffers, block, neighbors, light, x, y, z);
                else
                    left_closed2.render(buffers, block, neighbors, light, x, y, z);
            }
        } else {
            if (data == null || data.get(0) == 3) {
                if (open)
                    right_open3.render(buffers, block, neighbors, light, x, y, z);
                else
                    right_closed3.render(buffers, block, neighbors, light, x, y, z);
            } else if (data.get(0) == 0) {
                if (open)
                    right_open0.render(buffers, block, neighbors, light, x, y, z);
                else
                    right_closed0.render(buffers, block, neighbors, light, x, y, z);
            } else if (data.get(0) == 1) {
                if (open)
                    right_open1.render(buffers, block, neighbors, light, x, y, z);
                else
                    right_closed1.render(buffers, block, neighbors, light, x, y, z);
            } else {
                if (open)
                    right_open2.render(buffers, block, neighbors, light, x, y, z);
                else
                    right_closed2.render(buffers, block, neighbors, light, x, y, z);
            }
        }
    }

    final float width = 4;
    final float offset = (ONE_SIXTEENTH / 2) - (ONE_SIXTEENTH * width) / 2;
    final float open_offset = (16 - width);

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (data != null) {
            boolean open = data.get(1) == 0;
            boolean left = data.get(2) == 0;

            byte rotation = data.get(0); // Rotation stays from range 0-3
            //If closed, offset rotation by 1
            if (!open)
                rotation = (byte) ((rotation - 1) % 4);
           
            switch (rotation) {
                case 1 -> box.setPosAndSize(x + ONE_SIXTEENTH * open_offset, y, z, ONE_SIXTEENTH * width, 1, 1);
                case 2 -> box.setPosAndSize(x, y, z + ONE_SIXTEENTH * open_offset, 1, 1, ONE_SIXTEENTH * width);
                case 3 -> box.setPosAndSize(x, y, z, ONE_SIXTEENTH * width, 1, 1);
                default -> box.setPosAndSize(x, y, z, 1, 1, ONE_SIXTEENTH * width);
            }

            consumer.accept(box);
        }
    }

    @Override
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCursorBoxes(consumer, box, block, data, x, y, z);
    }

}
