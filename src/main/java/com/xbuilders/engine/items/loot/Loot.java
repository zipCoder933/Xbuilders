package com.xbuilders.engine.items.loot;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;

import java.util.function.Supplier;

public class Loot {
    Supplier<ItemStack> itemSupplier;
    float chance;

    public Loot(Supplier<ItemStack> itemSupplier, float chance) {
        this.itemSupplier = itemSupplier;
        this.chance = chance;
    }
}
