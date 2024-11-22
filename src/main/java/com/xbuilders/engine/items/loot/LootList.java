package com.xbuilders.engine.items.loot;

import java.util.ArrayList;

public class LootList extends ArrayList<Loot> {
    public LootList(Loot... loot) {
        super(new ArrayList<>(java.util.Arrays.asList(loot)));
    }

    public LootList(ArrayList<Loot> loot) {

    }
}
