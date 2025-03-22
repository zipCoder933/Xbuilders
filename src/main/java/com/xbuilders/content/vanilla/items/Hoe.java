package com.xbuilders.content.vanilla.items;

import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.utils.MiscUtils;
import org.joml.Vector3i;

public class Hoe extends Item {

    public Hoe(String material, int durability) {
        super("xbuilders:" + material + "_hoe", MiscUtils.capitalizeWords(material) + " Hoe");
        setIcon("pp\\"+material + "_hoe.png");
        tags.add("tool");
        maxDurability = durability;
        miningSpeedMultiplier = 0.01f;

        this.createClickEvent = (ray, stack) -> {
            Vector3i hit = ray.getHitPos();
            if (LocalServer.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DIRT ||
                    LocalServer.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_GRASS ||
                    LocalServer.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DRY_GRASS ||
                    LocalServer.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_JUNGLE_GRASS ||
                    LocalServer.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_SNOW_GRASS) {
                LocalServer.setBlock(Blocks.BLOCK_FARMLAND, hit.x, hit.y, hit.z);
                stack.durability--;
            }
            return true;
        };
    }

}