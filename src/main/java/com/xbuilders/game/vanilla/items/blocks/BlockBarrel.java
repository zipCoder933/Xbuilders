package com.xbuilders.game.items.blocks;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;

public class BlockBarrel extends Block {
    public BlockBarrel(int id, String name) {
        super(id, name, new BlockTexture("barrel_top.png", "barrel_top.png", "barrel_side.png"));
        clickEvent(false, (x, y, z) -> {
            BlockData data = GameScene.world.getBlockData(x, y, z);
            if (data == null) {
                data = new BlockData(0);
                GameScene.world.setBlockData(data, x, y, z);
            }
            WCCi wcc = new WCCi().set(x, y, z);
            Chunk chunk = GameScene.world.getChunk(wcc.chunk);
            MainWindow.game.barrelUI.openUI(data, chunk);
        });
    }


}
