package com.xbuilders.game.vanilla.items.items;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.game.vanilla.items.Items;

import java.util.HashMap;

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
