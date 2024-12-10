package com.xbuilders.game.vanilla.items.blocks;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.player.UserControlledPlayer;

public class BlockSpawn extends Block {
    public BlockSpawn(short id) {
        super(id, "xbuilders:spawn_block",
                new BlockTexture(
                        "symbols/destination top",
                        "symbols/destination top",
                        "symbols/destination",
                        "symbols/destination",
                        "symbols/destination",
                        "symbols/destination"));
        setBlockEvent(false, (x, y, z) -> {
            GameScene.player.status_spawnPosition.set(x, y, z);
            GameScene.alert("Spawn set to " + x + ", " + y + ", " + z);
        });
        torchlightStartingValue = 15;
        solid = false;
        opaque = false;
    }

}
