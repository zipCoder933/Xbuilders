package com.xbuilders.content.vanilla.propagation;

import com.xbuilders.engine.game.model.items.Registrys;
import com.xbuilders.engine.game.model.builtinMechanics.liquid.LiquidPropagationTask;
import com.xbuilders.content.vanilla.items.Blocks;


public class WaterPropagation extends LiquidPropagationTask {
    public WaterPropagation() {
        super(Registrys.getBlock(Blocks.BLOCK_WATER), 200);
    }


//    public boolean isPenetrable(Block block) {
//        return block.isAir() || (
//                !block.solid
//                        && block.renderType == RenderType.SPRITE
//                        && block.toughness < 0.5f);
//    }
}