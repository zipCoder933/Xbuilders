package com.xbuilders.content.vanilla.blocks.blocks;

import com.xbuilders.MainClient;
import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockTexture;
import com.xbuilders.engine.server.world.chunk.BlockData;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.wcc.WCCi;

public class CraftingTable extends Block {
    public CraftingTable(short id) {
        super(id, "xbuilders:crafting_table",new BlockTexture(
                "crafting_table_top.png",
                "crafting_table_top.png",
                "crafting_table_side.png",
                "crafting_table_side.png",
                "crafting_table_side.png",
                "crafting_table_front.png"));
        easierMiningTool_tag = "axe";

        clickEvent(false, (x, y, z) -> {
            BlockData data = LocalClient.world.getBlockData(x, y, z);
            if (data == null) {
                data = new BlockData(0);
                LocalClient.world.setBlockData(data, x, y, z);
            }
            WCCi wcc = new WCCi().set(x, y, z);
            Chunk chunk = LocalClient.world.getChunk(wcc.chunk);
            MainClient.game.craftingUI.setOpen(true);
        });
    }
}
