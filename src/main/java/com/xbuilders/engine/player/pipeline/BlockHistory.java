package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.chunk.BlockData;

public class BlockHistory {
    public Block previousBlock;
    public Block newBlock;

    public BlockData previousBlockData;
    public BlockData newBlockData;

    public boolean updateBlockData = false; //If we should set the block data (if we want to set the block data to null, we set this to true, and set data to null)
    public boolean fromNetwork = false;

    public BlockHistory(Block currentBlock, BlockData data) {
        this.newBlock = currentBlock;
        this.newBlockData = data;
        updateBlockData = true;
    }

    public BlockHistory(short previousBlock, short currentBlock) {
        this.previousBlock = ItemList.getBlock(previousBlock);
        this.newBlock = ItemList.getBlock(currentBlock);
    }

    public BlockHistory(Block previousBlock, Block currentBlock) {
        this.previousBlock = previousBlock;
        this.newBlock = currentBlock;
    }

    public BlockHistory() {
    }


    public String toString() {
        return "BlockHistory{" +
                "previousBlock=" + previousBlock +
                ", currentBlock=" + newBlock +
                ", data=" + newBlockData +
                ", updateBlockData=" + updateBlockData +
                '}';
    }
}
