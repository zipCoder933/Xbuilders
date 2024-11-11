package com.xbuilders.game.items.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.game.items.Blocks;
import org.joml.Vector3i;

public class Hoe extends Item {

    public Hoe() {
        super("xbuilders:hoe", "Hoe");
        setIcon("hoe.png");
        this.createClickEvent = (ray) -> {
            Vector3i hit = ray.getHitPos();
            if (GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DIRT ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DRY_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_JUNGLE_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_SNOW_GRASS) {
                GameScene.player.setBlock(Blocks.BLOCK_FARMLAND, hit.x, hit.y, hit.z);
            }
            return true;
        };
    }

}