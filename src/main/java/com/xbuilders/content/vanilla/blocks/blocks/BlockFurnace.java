package com.xbuilders.content.vanilla.blocks.blocks;

import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockTexture;

public class BlockFurnace extends Block {
    public BlockFurnace(short id) {
        super(id, "xbuilders:furnace", new BlockTexture(
                "furnace_top.png",
                "furnace_top.png",
                "furnace_side.png",
                "furnace_side.png",
                "furnace_side.png",
                "furnace_front.png"));
    }
}
