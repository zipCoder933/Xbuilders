package com.xbuilders.game.items.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Tool;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;

public class Hoe extends Tool {

    public Hoe() {
        super(3, "Hoe");
        setIcon("hoe.png");
        setClickEvent((ray, creationMode) -> {
            Vector3i hit = ray.getHitPos();
            if (GameScene.world.getBlockID(hit.x, hit.y, hit.z) == MyGame.BLOCK_DIRT) {
                GameScene.player.setBlock(MyGame.BLOCK_FARMLAND, hit.x, hit.y, hit.z);
            }
        });
    }

}