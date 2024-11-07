package com.xbuilders.game.items.tools;

import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemType;

public class AnimalFeed extends Item {
    public AnimalFeed() {
        super(0, "Animal Feed", ItemType.ITEM);
        setIcon("animal_feed.png");
    }
}
