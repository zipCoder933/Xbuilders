package com.xbuilders.game.items.blocks;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;

public class BlockBarrel extends Block {
    public BlockBarrel(int id, String name) {
        super(id, name, new BlockTexture("barrel_top.png", "barrel_top.png", "barrel_side.png"));
        clickEvent(false, (x, y, z) -> {
            GameScene.alert("barrel");
        });
    }


}
