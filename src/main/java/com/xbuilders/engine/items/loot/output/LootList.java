package com.xbuilders.engine.items.loot.output;

import com.xbuilders.engine.items.item.ItemStack;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Consumer;

public class LootList extends ArrayList<Loot> {
    private static final Random random = new Random();

    public LootList(Loot... loot) {
        super(new ArrayList<>(java.util.Arrays.asList(loot)));
    }

    public LootList(ArrayList<Loot> loot) {
        super(loot);
    }

    public LootList() {
        super();
    }

    public void randomItems(Consumer<ItemStack> output) {
        for (Loot loot : this) {
            if (loot == null) continue;
            int itemCount = random.nextInt(loot.maxItems) + 1; //random number between 1 and maxItems
            for (int i = 0; i < itemCount; i++) {
                if (random.nextFloat() < loot.chance) {
                    output.accept(loot.itemSupplier.get());
                }
            }
        }
    }
}
