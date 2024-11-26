package com.xbuilders.game.propagation;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.builtinMechanics.liquid.LiquidPropagationTask;
import com.xbuilders.game.items.Blocks;

public class LavaPropagation extends LiquidPropagationTask {

    public LavaPropagation() {
        super(Registrys.getBlock(Blocks.BLOCK_LAVA), 900);
    }

    public boolean isPenetrable(Block block) {
        return !block.solid && !block.isLiquid();
    }
}
