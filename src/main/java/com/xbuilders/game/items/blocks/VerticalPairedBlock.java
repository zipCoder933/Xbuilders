package com.xbuilders.game.items.blocks;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;

import java.util.function.Consumer;

public class VerticalPairedBlock {

    public final Block topBlock;
    public final Block bottomBlock;

    public VerticalPairedBlock(String name, int id_top, int id_bottom,
                               BlockTexture texture_top, BlockTexture texture_bottom, int renderType) {
        topBlock = new Block(id_top, name, texture_top, renderType);
        bottomBlock = new Block(id_bottom, name, texture_bottom, renderType);

        topBlock.setBlockEvent(false, (x, y, z, data) -> {
            GameScene.player.setBlock(bottomBlock, x, y + 1, z);
        });
        topBlock.removeBlockEvent((x, y, z) -> {
            if (GameScene.world.getBlock(x, y + 1, z) == bottomBlock) {
                GameScene.player.setBlock(BlockList.BLOCK_AIR, x, y + 1, z);
            }
        });

        bottomBlock.setBlockEvent(false, (x, y, z, data) -> {
            GameScene.player.setBlock(topBlock, x, y - 1, z);
        });
        bottomBlock.removeBlockEvent((x, y, z) -> {
            if (GameScene.world.getBlock(x, y - 1, z) == topBlock) {
                GameScene.player.setBlock(BlockList.BLOCK_AIR, x, y - 1, z);
            }
        });
    }


}
