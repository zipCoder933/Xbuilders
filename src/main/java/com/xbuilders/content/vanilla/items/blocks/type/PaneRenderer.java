package com.xbuilders.content.vanilla.items.blocks.type;

import com.xbuilders.engine.server.model.items.block.Block;
import com.xbuilders.engine.server.model.items.block.construction.BlockType;
import com.xbuilders.engine.server.model.items.block.construction.BlockTypeModel.BlockModel;
import com.xbuilders.engine.server.model.items.block.construction.BlockTypeModel.BlockModelLoader;
import com.xbuilders.engine.client.visuals.rendering.VertexSet;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.server.model.world.chunk.BlockData;
import com.xbuilders.engine.server.model.world.chunk.Chunk;


public class PaneRenderer extends BlockType {

    private final BlockModel horizontal, vertical0, vertical1;

    public PaneRenderer() {
        super();

        // ObjToBlockModel.parseFileWithYRotations( false,
        //         1.6f, ResourceUtils.resource("block types\\pane\\vertical.obj"));
        // ObjToBlockModel.parseFile(null, false, 1.6f, ResourceUtils.resource("block types\\pane\\horizontal.obj"));

        horizontal = BlockModelLoader.load(ResourceUtils.resource("block types\\pane\\horizontal.blockType"), renderSide_subBlock);
        vertical0 = BlockModelLoader.load(ResourceUtils.resource("block types\\pane\\vertical0.blockType"), renderSide_subBlock);
        vertical1 = BlockModelLoader.load(ResourceUtils.resource("block types\\pane\\vertical1.blockType"), renderSide_subBlock);
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = true;
        };
    }

    @Override
    public boolean constructBlock(VertexSet buffers, Block block, BlockData data, Block[] neighbors,
                                  BlockData[] neighborData, byte[] light, Chunk chunk,
                                  int chunkX, int chunkY, int chunkZ,
                                  boolean isUsingGreedyMesher) {
        if (data != null && data.size() == 2) {
            if (data.get(1) == 0) {
                if (data.get(0) == 1 || data.get(0) == 3) {
                    vertical1.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                } else {
                    vertical0.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
                }
            } else {
                horizontal.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
            }
        } else {
            horizontal.render(buffers, block, neighbors, light, chunkX, chunkY, chunkZ);
        }
        return false;
    }

    @Override
    public void getCursorBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        getCollisionBoxes(consumer, box, block, data, x, y, z);
    }

    @Override
    public BlockData getInitialBlockData(BlockData existingData, Block block, UserControlledPlayer player) {
        BlockData data = player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
        return data;
    }

    final float ONE_SIXTEENTH = 1 / 16f;

    @Override
    public void getCollisionBoxes(BoxConsumer consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        if (data != null && data.size() == 2) {
            if (data.get(1) == 0) {
                if (data.get(0) == 1 || data.get(0) == 3) {
                    box.setPosAndSize(x + (ONE_SIXTEENTH * 7), y, z, ONE_SIXTEENTH * 2, 1, 1);
                    consumer.accept(box);
                } else {
                    box.setPosAndSize(x, y, z + (ONE_SIXTEENTH * 7), 1, 1, ONE_SIXTEENTH * 2);
                    consumer.accept(box);
                }
            } else {
                box.setPosAndSize(x, y + (ONE_SIXTEENTH * 7), z, 1, ONE_SIXTEENTH * 2, 1);
                consumer.accept(box);
            }
        } else {
            box.setPosAndSize(x + (ONE_SIXTEENTH * 7), y, z, ONE_SIXTEENTH * 2, 1, 1);
            consumer.accept(box);
        }
    }
}