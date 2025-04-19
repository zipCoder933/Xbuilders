package com.xbuilders.content.vanilla.blocks.blocks;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.block.construction.BlockTexture;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.entity.LivingEntity;

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
            GameScene.userPlayer.setSpawnPoint(x, y, z);

            if (GameScene.userPlayer.worldPosition.distance(x, y, z) < 3) {
                if (isDarkOutside()) {
                    //Check for nearby hostile mobs
                    for (Entity e : LocalServer.world.entities.values()) {
                        if (e instanceof LivingEntity) {
                            LivingEntity le = (LivingEntity) e;
                            if (le.isHostile() && le.worldPosition.distance(x, y, z) < 20) {
                                LocalClient.alert("You cannot sleep here, there are enemies nearby!");
                                return;
                            }
                        }
                    }
                    LocalClient.alert("Time set to day");
                    LocalServer.setTimeOfDay(0);
                } else {
                    LocalClient.alert("You can only sleep at night");
                }
            } else LocalClient.alert("You are too far from spawn block to sleep");
        });

        torchlightStartingValue = 10;
        solid = false;
        opaque = false;
    }


    public boolean isDarkOutside() {
        return LocalServer.getTimeOfDay() > 0.36 && LocalServer.getTimeOfDay() < 0.62;
    }

}
