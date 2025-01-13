package com.xbuilders.engine.server.items.loot.output;

import com.xbuilders.engine.server.items.item.ItemStack;

import java.util.function.Supplier;

public class Loot {
    public String item;
    public float chance;
    public int maxItems = 1;

    public Loot(String item, float chance) {
        this.item = item;
        this.chance = chance;
        this.maxItems = 1;
    }

    public Loot(String item, float chance, int maxItems) {
        this.item = item;
        this.chance = chance;
        this.maxItems = maxItems;
    }
}
