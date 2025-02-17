package com.xbuilders.content.vanilla.propagation;

import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.builtinMechanics.liquid.LiquidPropagationTask;
import com.xbuilders.content.vanilla.items.Blocks;

public class LavaPropagation extends LiquidPropagationTask {

    public LavaPropagation() {
        super(Registrys.getBlock(Blocks.BLOCK_LAVA), 900);
    }

    public boolean isPenetrable(Block block) {
        return !block.solid && !block.isLiquid();
    }
}
