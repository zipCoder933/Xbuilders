package com.xbuilders.content.vanilla.blocks.blocks;

import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.content.vanilla.blocks.RenderType;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockTexture;

public class BlockFarmland extends Block {

    public BlockFarmland(short id) {
        super(id, "xbuilders:farmland",
                new BlockTexture(
                        "farmland",
                        "farmland",
                        "dirt",
                        "dirt",
                        "dirt",
                        "dirt"));

        randomTickEvent = (x, y, z) -> {

            //If we are directly touching water
            if (LocalServer.world.getBlockID(x, y + 1, z) == Blocks.BLOCK_WATER ||
                    //
                    LocalServer.world.getBlockID(x + 1, y, z) == Blocks.BLOCK_WATER ||
                    LocalServer.world.getBlockID(x - 1, y, z) == Blocks.BLOCK_WATER ||
                    LocalServer.world.getBlockID(x, y, z + 1) == Blocks.BLOCK_WATER ||
                    LocalServer.world.getBlockID(x, y, z - 1) == Blocks.BLOCK_WATER) {

                LocalServer.world.setBlock(Blocks.BLOCK_WET_FARMLAND, x, y, z);

                // Server.world.setBlockData(new BlockData(new byte[]{0}), x, y, z); //Set the data to 0 since we are touching water
                return true;
            }

            Block above = LocalServer.world.getBlock(x, y - 1, z);
            //Decay the farmland if there is something solid on top or if there are no crops
            if (above.solid || above.type != RenderType.SPRITE) {
                LocalServer.world.setBlock(Blocks.BLOCK_DIRT, x, y, z);
            }

            return false;
        };
    }
}
