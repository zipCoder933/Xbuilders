package com.xbuilders.engine.items.loot;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;

import java.util.function.Supplier;

public class Loot {
    public Supplier<ItemStack> itemSupplier;
    public float chance;
    public int maxItems = 1;

    public Loot(Supplier<ItemStack> itemSupplier, float chance) {
        this.itemSupplier = itemSupplier;
        this.chance = chance;
        this.maxItems = 1;
    }

    public Loot(Supplier<ItemStack> itemSupplier, float chance, int maxItems) {
        this.itemSupplier = itemSupplier;
        this.chance = chance;
        this.maxItems = maxItems;
    }
}
