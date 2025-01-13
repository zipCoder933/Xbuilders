package com.xbuilders.engine.server.items.loot.animalFeed;

import com.xbuilders.engine.server.items.loot.output.LootList;

public class AnimalFeedLoot {
    public String animalID;
    public String foodID;
    public LootList output;

    public AnimalFeedLoot(String animalID, String foodId, LootList out) {
        this.animalID = animalID;
        this.foodID = foodId;
        this.output = out;
    }

    public AnimalFeedLoot() {
    }
}
