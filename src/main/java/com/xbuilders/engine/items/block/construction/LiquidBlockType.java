package com.xbuilders.engine.items.block.construction;

import com.xbuilders.engine.items.block.Block;

public class LiquidBlockType extends DefaultBlockType {
    public LiquidBlockType() {
        super();
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = false;
        };
    }

    @Override
    public boolean sideIsVisible(Block block, Block NEG_X) {
        return NEG_X.isAir();
    }
}
