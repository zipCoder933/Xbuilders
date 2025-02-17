package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.utils.MiscUtils;

public class Shovel extends Item {

    public Shovel(String material, int durability) {
        super("xbuilders:" + material + "_shovel", MiscUtils.capitalizeWords(material) + " Shovel");
        setIcon("pp\\" + material + "_shovel.png");
        maxStackSize = 1;
        tags.add("shovel");
        tags.add("tool");
        tags.add(material);
        maxDurability = durability;
    }
}
