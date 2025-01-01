package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.engine.server.model.items.item.Item;
import com.xbuilders.engine.utils.MiscUtils;

public class Axe extends Item {

    public Axe(String material, int durability) {
        super("xbuilders:"+ material+"_axe", MiscUtils.capitalizeWords(material) + " Axe");
        setIcon("pp\\"+material + "_axe.png");
        maxStackSize = 1;
        tags.add("tool");
        tags.add("axe");
        tags.add(material);
        maxDurability = durability;
    }
}
