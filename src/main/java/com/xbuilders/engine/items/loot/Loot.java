package com.xbuilders.engine.items.loot;

import com.xbuilders.engine.items.item.Item;

public class Loot {
    Item item;
    float chance;

    public Loot(Item item, float chance) {
        this.item = item;
        this.chance = chance;
    }
}
