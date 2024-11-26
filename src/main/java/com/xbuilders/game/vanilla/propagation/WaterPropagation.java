package com.xbuilders.game.vanilla.propagation;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.builtinMechanics.liquid.LiquidPropagationTask;
import com.xbuilders.game.vanilla.items.Blocks;


public class WaterPropagation extends LiquidPropagationTask {
    public WaterPropagation() {
        super(Registrys.getBlock(Blocks.BLOCK_WATER), 200);
    }
}