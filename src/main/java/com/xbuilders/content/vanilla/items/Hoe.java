package com.xbuilders.content.vanilla.items;

import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.item.Item;
import org.joml.Vector3i;

public class Hoe extends Item {

    public Hoe() {
        super("xbuilders:hoe", "Hoe");
        setIcon("hoe.png");
        tags.add("tool");
        maxDurability = 100;

        this.createClickEvent = (ray, stack) -> {
            Vector3i hit = ray.getHitPos();
            if (Server.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DIRT ||
                    Server.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_GRASS ||
                    Server.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DRY_GRASS ||
                    Server.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_JUNGLE_GRASS ||
                    Server.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_SNOW_GRASS) {
                Server.setBlock(Blocks.BLOCK_FARMLAND, hit.x, hit.y, hit.z);
                stack.durability--;
            }
            return true;
        };
    }

}