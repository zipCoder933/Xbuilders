package com.xbuilders.game.propagation;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.builtinMechanics.liquid.LiquidPropagationTask;
import com.xbuilders.game.MyGame;


public class WaterPropagation extends LiquidPropagationTask {
    public WaterPropagation() {
        super(Registrys.getBlock(MyGame.BLOCK_WATER), 200);
    }
}