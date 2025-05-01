package com.xbuilders.content.vanilla.blocks.blocks;

import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockTexture;

public class BlockWetFarmland extends Block {

    public BlockWetFarmland(short id) {
        super(id, "xbuilders:wet_farmland",
                new BlockTexture(
                        "wet_farmland",
                        "wet_farmland",
                        "mud",
                        "mud",
                        "mud",
                        "mud"));

        randomTickEvent = (x, y, z) -> {
            if (Client.world.getBlockID(x, y + 1, z) == Blocks.BLOCK_WATER ||
                    //
                    Client.world.getBlockID(x + 1, y, z) == Blocks.BLOCK_WATER ||
                    Client.world.getBlockID(x - 1, y, z) == Blocks.BLOCK_WATER ||
                    Client.world.getBlockID(x, y, z + 1) == Blocks.BLOCK_WATER ||
                    Client.world.getBlockID(x, y, z - 1) == Blocks.BLOCK_WATER) {
                return false;
            } else { //If we are not directly touching water, switch back
                Client.world.setBlock(Blocks.BLOCK_FARMLAND, x, y, z);
                return true;
            }
        };
    }
}
