package com.xbuilders.game.propagation;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.game.MyGame;

public class LavaPropagation extends WaterPropagation {

    public LavaPropagation() {
        updateIntervalMS = 900;
        liquidBlock = ItemList.getBlock(MyGame.BLOCK_LAVA);
    }
}
