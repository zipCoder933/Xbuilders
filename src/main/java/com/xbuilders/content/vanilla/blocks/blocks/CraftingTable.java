package com.xbuilders.content.vanilla.blocks.blocks;

import com.xbuilders.Main;
import com.xbuilders.engine.client.Client;
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
            BlockData data = Client.world.getBlockData(x, y, z);
            if (data == null) {
                data = new BlockData(0);
                Client.world.setBlockData(data, x, y, z);
            }
            WCCi wcc = new WCCi().set(x, y, z);
            Chunk chunk = Client.world.getChunk(wcc.chunk);
            Main.game.craftingUI.setOpen(true);
        });
    }
}
