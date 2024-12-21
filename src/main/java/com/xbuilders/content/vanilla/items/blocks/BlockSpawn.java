package com.xbuilders.content.vanilla.items.blocks;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.game.model.items.block.construction.BlockTexture;

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

        clickEvent(false, (x, y, z) -> {
            if (GameScene.player.status_spawnPosition.distance(x, y, z) < 1) {
//                GameScene.alert("Time set to day");
//                GameScene.setTimeOfDay(0);
            }
            GameScene.alert("Spawn set to " + x + ", " + y + ", " + z);
            GameScene.player.status_spawnPosition.set(x, y, z);
        });

        torchlightStartingValue = 15;
        solid = false;
        opaque = false;
    }

}
