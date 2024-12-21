package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.item.Item;
import com.xbuilders.content.vanilla.items.Blocks;
import org.joml.Vector3i;

public class Hoe extends Item {

    public Hoe() {
        super("xbuilders:hoe", "Hoe");
        setIcon("hoe.png");
        tags.add("tool");
        maxDurability = 100;

        this.createClickEvent = (ray, stack) -> {
            Vector3i hit = ray.getHitPos();
            if (GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DIRT ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DRY_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_JUNGLE_GRASS ||
                    GameScene.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_SNOW_GRASS) {
                GameScene.setBlock(Blocks.BLOCK_FARMLAND, hit.x, hit.y, hit.z);
                stack.durability--;
            }
            return true;
        };
    }

}