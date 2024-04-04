package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.items.block.Block;

public class BlockHistory {
    Block previousBlock;
    Block currentBlock;

    public BlockHistory(Block previousBlock, Block currentBlock) {
        this.previousBlock = previousBlock;
        this.currentBlock = currentBlock;
    }
}
