package com.xbuilders.game.items.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;

public class Hoe extends Item {

    public Hoe() {
        super(3, "Hoe");
        setIcon("hoe.png");
        this.createClickEvent = (ray) -> {
            Vector3i hit = ray.getHitPos();
            if (GameScene.world.getBlockID(hit.x, hit.y, hit.z) == MyGame.BLOCK_DIRT ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == MyGame.BLOCK_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == MyGame.BLOCK_DRY_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == MyGame.BLOCK_JUNGLE_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == MyGame.BLOCK_SNOW_GRASS) {
                GameScene.player.setBlock(MyGame.BLOCK_FARMLAND, hit.x, hit.y, hit.z);
            }
            return true;
        };
    }

}