package com.xbuilders.game.items.blocks;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;

public class Plant extends Block {

    public Plant(int id, String name, BlockTexture texture) {
        super(id, name, texture);
        solid = false;
        opaque = false;
        this.type = RenderType.SPRITE;
    }
}