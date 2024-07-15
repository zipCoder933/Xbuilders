package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.chunk.BlockData;

public class BlockHistory {
    public Block previousBlock;
    public Block currentBlock;
    public BlockData data;
    public boolean updateBlockData = false;
    public boolean isFromMultiplayer = false;

    public BlockHistory(short previousBlock, short currentBlock) {
        this.previousBlock = ItemList.getBlock(previousBlock);
        this.currentBlock = ItemList.getBlock(currentBlock);
    }

    public BlockHistory(short currentBlock, BlockData data) {
        this.currentBlock = ItemList.getBlock(currentBlock);
        this.data = data;
        updateBlockData = true;
    }

    public BlockHistory(short currentBlock) {
        this.currentBlock = ItemList.getBlock(currentBlock);
    }

    public BlockHistory(BlockData data) {
        this.data = data;
        updateBlockData = true;
    }

    public BlockHistory() {}


        public String toString() {
        return "BlockHistory{" +
                "previousBlock=" + previousBlock +
                ", currentBlock=" + currentBlock +
                ", data=" + data +
                ", updateBlockData=" + updateBlockData +
                '}';
    }
}
