package com.xbuilders.content.vanilla.propagation;

import com.xbuilders.content.vanilla.blocks.RenderType;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockType;
import com.xbuilders.engine.server.builtinMechanics.liquid.LiquidPropagationTask;
import com.xbuilders.content.vanilla.Blocks;

public class LavaPropagation extends LiquidPropagationTask {

    public LavaPropagation() {
        super(Registrys.getBlock(Blocks.BLOCK_LAVA), 900);
    }

}
