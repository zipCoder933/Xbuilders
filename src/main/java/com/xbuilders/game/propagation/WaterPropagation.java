package com.xbuilders.game.propagation;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.liquid.LiquidPropagationTask;
import com.xbuilders.game.MyGame;


public class WaterPropagation extends LiquidPropagationTask {
    public WaterPropagation() {
        super(ItemList.getBlock(MyGame.BLOCK_WATER), 200);
    }
}