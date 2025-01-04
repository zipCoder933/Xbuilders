package com.xbuilders.content.vanilla.items.blocks;

import com.xbuilders.engine.server.items.block.Block;
import com.xbuilders.engine.server.items.block.construction.BlockTexture;

public class Furnace extends Block {
    public Furnace(short id) {
        super(id, "xbuilders:furnace", new BlockTexture(
                "furnace_top.png",
                "furnace_top.png",
                "furnace_side.png",
                "furnace_side.png",
                "furnace_side.png",
                "furnace_front.png"));
    }
}
