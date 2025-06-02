/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.blocks.type;

import com.xbuilders.content.vanilla.blocks.RenderType;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockType;
import com.xbuilders.engine.server.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.server.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.client.visuals.gameScene.rendering.VertexSet;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public class OmniFlatRenderer extends BlockType {

    BlockModel floor0, floor1, floor2, floor3;
    BlockModel wall0, wall1, wall2, wall3;


    public boolean allowExistence(Block block, int worldX, int worldY, int worldZ) {
        return !Client.world.getBlock(worldX, worldY + 1, worldZ).isLiquid()
                && !Client.world.getBlock(worldX, worldY + 1, worldZ).isAir();
    }

    public OmniFlatRenderer() throws IOException {
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f, ResourceUtils.resource("block types\\floor\\floor.obj"));
        // ObjToBlockModel.parseFileWithYRotations(false, 1.6f, ResourceUtils.resource("block types\\wall\\wall.obj"));
        generate3DIcon = false;
        floor0 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/floor/floor0.blockType"), renderSide_subBlock);
        floor1 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/floor/floor1.blockType"), renderSide_subBlock);
        floor2 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/floor/floor2.blockType"), renderSide_subBlock);
        floor3 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/floor/floor3.blockType"), renderSide_subBlock);

        wall0 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/wall/wall0.blockType"), renderSide_subBlock);
        wall1 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/wall/wall1.blockType"), renderSide_subBlock);
        wall2 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/wall/wall2.blockType"), renderSide_subBlock);
        wall3 = BlockModelLoader.load(resourceLoader.getResourceAsStream("/assets/xbuilders/models/block/wall/wall3.blockType"), renderSide_subBlock);

        initializationCallback = (b) -> {
            b.initialBlockData = (existingData, player) -> {
                BlockData bd = player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
                Block hitBlock = Client.world.getBlock(player.camera.cursorRay.getHitPos());

                if (player.camera.cursorRay.getHitNormal().y == -1
                        || !hitBlock.solid
                        || hitBlock.type == RenderType.FLOOR
                        || hitBlock.type == RenderType.WALL_ITEM
                        || hitBlock.type == RenderType.SPRITE
                        || hitBlock.type == RenderType.FENCE
                        || hitBlock.type == RenderType.FENCE_GATE
                        || hitBlock.type == RenderType.DOOR_HALF) {
                    bd.set(1, (byte) 1);//Make this a floor
                }
                return bd;
            };

            b.opaque = false;
            b.solid = true;
            b.toughness = 0.1f;
        };
    }

    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors, BlockData[] neighborData, byte[] light, Chunk chunk, int chunkX, int chunkY, int chunkZ, boolean isUsingGreedyMesher) {

        if (isFloor(data)) {
            if (data == null || data.size() < 1 || data.get(0) == 3) {
                floor0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 0) {
                floor1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 1) {
                floor2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else {
                floor3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            }
        } else {
            if (data == null || data.size() < 1 || data.get(0) == 3) {
                wall3.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 0) {
                wall0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else if (data.get(0) == 1) {
                wall1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            } else {
                wall2.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            }
        }
        return false;
    }

    private boolean isFloor(BlockData data) {
        if (data == null || data.size() < 1) return true;
        return data.get(1) == 1;
    }

    private final float ONE_SIXTEENTH = 0.0625f;

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (isFloor(data)) {
            box.setPosAndSize(x, y + (ONE_SIXTEENTH * 15), z, 1, ONE_SIXTEENTH * 1, 1);
            consumer.accept(box);
        } else {
            if (data != null && data.size() > 0) {
                switch (data.get(0)) {
                    case 1 -> box.setPosAndSize(x + ONE_SIXTEENTH * 14, y, z, ONE_SIXTEENTH * 2, 1, 1);
                    case 2 -> box.setPosAndSize(x, y, z + ONE_SIXTEENTH * 14, 1, 1, ONE_SIXTEENTH * 2);
                    case 3 -> box.setPosAndSize(x, y, z, ONE_SIXTEENTH * 2, 1, 1);
                    default -> box.setPosAndSize(x, y, z, 1, 1, ONE_SIXTEENTH * 2);
                }
                consumer.accept(box);
            }
        }
    }

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCollisionBoxes(consumer, box, block, data, x, y, z);
    }

}
