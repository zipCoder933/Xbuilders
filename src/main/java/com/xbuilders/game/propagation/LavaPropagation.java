package com.xbuilders.game.propagation;

import com.xbuilders.game.MyGame;

public class LavaPropagation extends WaterPropagation {

    public LavaPropagation() {
        updateIntervalMS = 900;
        maxFlow = 6;
        interestedBlock = MyGame.BLOCK_LAVA;
    }
}
