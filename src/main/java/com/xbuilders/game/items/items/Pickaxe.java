package com.xbuilders.game.items.items;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.utils.MiscUtils;

public class Pickaxe extends Item {

    public Pickaxe(String id, int durability) {
        super("xbuilders:"+ id+"-pickaxe", MiscUtils.capitalizeWords(id) + " Pickaxe");
        setIcon("pp\\"+id + "_pickaxe.png");
        maxStackSize = 1;
        tags.add("tool");
        tags.add("pickaxe");
        maxDurability = durability;
    }
}