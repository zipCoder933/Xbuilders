package com.xbuilders.game.propagation;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.liquid.LiquidPropagationTask;
import com.xbuilders.game.MyGame;

public class LavaPropagation extends LiquidPropagationTask {

    public LavaPropagation() {
        super(ItemList.getBlock(MyGame.BLOCK_LAVA), 900);
    }

    public boolean isPenetrable(Block block) {
        return !block.solid && !block.isLiquid();
    }
}
