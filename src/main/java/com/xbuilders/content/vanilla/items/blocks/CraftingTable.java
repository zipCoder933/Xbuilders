package com.xbuilders.content.vanilla.items.blocks;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.game.model.items.block.construction.BlockTexture;
import com.xbuilders.engine.game.model.world.chunk.BlockData;
import com.xbuilders.engine.game.model.world.chunk.Chunk;
import com.xbuilders.engine.game.model.world.wcc.WCCi;

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
            BlockData data = GameScene.world.getBlockData(x, y, z);
            if (data == null) {
                data = new BlockData(0);
                GameScene.world.setBlockData(data, x, y, z);
            }
            WCCi wcc = new WCCi().set(x, y, z);
            Chunk chunk = GameScene.world.getChunk(wcc.chunk);
            MainWindow.game.craftingUI.setOpen(true);
        });
    }
}
