package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.items.block.Block;

public class BlockEvent {
    Block previousBlock;
    Block currentBlock;

    public BlockEvent(Block previousBlock, Block currentBlock) {
        this.previousBlock = previousBlock;
        this.currentBlock = currentBlock;
    }
}
