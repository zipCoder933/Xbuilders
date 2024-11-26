package com.xbuilders.game.items.items;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.game.items.Items;

import java.util.HashMap;

public class Shovel extends Item {

    public Shovel(String id, int durability) {
        super("xbuilders:"+ id+"-shovel", MiscUtils.capitalizeWords(id) + " Shovel");
        setIcon("pp\\"+id + "_shovel.png");
        maxStackSize = 1;
        tags.add("shovel");
        tags.add("tool");
        maxDurability = durability;
    }
}
