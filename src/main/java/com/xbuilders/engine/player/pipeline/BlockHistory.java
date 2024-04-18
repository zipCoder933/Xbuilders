package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.chunk.BlockData;

public class BlockHistory {
    Block previousBlock;
    Block currentBlock;
    BlockData data;
    boolean updateBlockData = false;

    public BlockHistory(Block previousBlock, Block currentBlock) {
        this.previousBlock = previousBlock;
        this.currentBlock = currentBlock;
    }

    public BlockHistory(short previousBlock, short currentBlock) {
        this.previousBlock = ItemList.getBlock(previousBlock);
        this.currentBlock = ItemList.getBlock(currentBlock);
    }

    public BlockHistory(Block previousBlock, Block currentBlock, BlockData data) {
        this.previousBlock = previousBlock;
        this.currentBlock = currentBlock;
        this.data = data;
        updateBlockData = true;
    }

    public BlockHistory(short previousBlock, short currentBlock, BlockData data) {
        this.previousBlock = ItemList.getBlock(previousBlock);
        this.currentBlock = ItemList.getBlock(currentBlock);
        this.data = data;
        updateBlockData = true;
    }

    public BlockHistory(BlockData data) {
        this.data = data;
        updateBlockData = true;
    }
}
