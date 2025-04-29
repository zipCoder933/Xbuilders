package com.xbuilders.content.vanilla.items;

import com.xbuilders.Main;
import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.common.utils.MiscUtils;
import org.joml.Vector3i;

public class Hoe extends Item {

    public Hoe(String material, int durability) {
        super("xbuilders:" + material + "_hoe", MiscUtils.capitalizeWords(material) + " Hoe");
        setIcon("pp\\"+material + "_hoe.png");
        tags.add("tool");
        tags.add("hoe");
        maxDurability = durability;
        maxStackSize=1;
        miningSpeedMultiplier = 0.01f;

        this.createClickEvent = (ray, stack) -> {
            Vector3i hit = ray.getHitPos();
            if (LocalClient.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DIRT ||
                    LocalClient.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_GRASS ||
                    LocalClient.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_DRY_GRASS ||
                    LocalClient.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_JUNGLE_GRASS ||
                    LocalClient.world.getBlockID(hit.x, hit.y, hit.z) == Blocks.BLOCK_SNOW_GRASS) {
                Main.getServer().setBlock(Blocks.BLOCK_FARMLAND, hit.x, hit.y, hit.z);
                stack.durability--;
            }
            return true;
        };
    }

}