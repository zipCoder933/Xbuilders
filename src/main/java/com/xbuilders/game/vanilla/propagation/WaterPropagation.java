package com.xbuilders.game.vanilla.propagation;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.builtinMechanics.liquid.LiquidPropagationTask;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.game.vanilla.items.Blocks;
import com.xbuilders.game.vanilla.items.blocks.RenderType;


public class WaterPropagation extends LiquidPropagationTask {
    public WaterPropagation() {
        super(Registrys.getBlock(Blocks.BLOCK_WATER), 200);
    }


    public boolean isPenetrable(Block block) {
        return block.isAir() || (
                !block.solid
                        && block.renderType == RenderType.SPRITE
                        && block.toughness <= 0.5f);
    }
}