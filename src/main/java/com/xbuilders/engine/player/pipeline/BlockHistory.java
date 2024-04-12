package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;

public class BlockHistory {
    Block previousBlock;
    Block currentBlock;

    public BlockHistory(Block previousBlock, Block currentBlock) {
        this.previousBlock = previousBlock;
        this.currentBlock = currentBlock;
    }

    public BlockHistory(short previousBlock, short currentBlock) {
        this.previousBlock = ItemList.getBlock(previousBlock);
        this.currentBlock = ItemList.getBlock(currentBlock);
    }
}
