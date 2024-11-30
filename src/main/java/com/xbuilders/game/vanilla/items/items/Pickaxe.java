package com.xbuilders.game.vanilla.items.items;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.utils.MiscUtils;

public class Pickaxe extends Item {

    public Pickaxe(String id, int durability, float miningSpeedMultiplier) {
        super("xbuilders:"+ id+"_pickaxe", MiscUtils.capitalizeWords(id) + " Pickaxe");
        setIcon("pp\\"+id + "_pickaxe.png");
        maxStackSize = 1;
        tags.add("tool");
        tags.add("pickaxe");
        maxDurability = durability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
    }
}
