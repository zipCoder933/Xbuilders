package com.xbuilders.game.vanilla.items.items;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.utils.MiscUtils;

public class Axe extends Item {

    public Axe(String id, int durability) {
        super("xbuilders:"+ id+"-axe", MiscUtils.capitalizeWords(id) + " Axe");
        setIcon("pp\\"+id + "_axe.png");
        maxStackSize = 1;
        tags.add("tool");
        tags.add("axe");
        maxDurability = durability;
    }
}
