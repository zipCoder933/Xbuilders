package com.xbuilders.engine.items.loot;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;

public class Loot {
    ItemStack item;
    float chance;

    public Loot(ItemStack item, float chance) {
        this.item = item;
        this.chance = chance;
    }
}
