package com.xbuilders.content.vanilla.items;

import com.xbuilders.Main;
import com.xbuilders.engine.server.entity.LivingEntity;
import com.xbuilders.engine.server.item.Item;

public class AnimalFeed extends Item {
    public AnimalFeed() {
        super("xbuilders:animal_feed", "Animal Feed");
        setIcon("animal_feed.png");
        tags.add("tool");

        OnClickEvent event = (ray, itemStack) -> {
            System.out.println("Animal Feed clicked");
            if (ray.getEntity() != null && ray.getEntity() instanceof LivingEntity animal) {
                itemStack.stackSize--;
                animal.tamed = true;
                Main.getClient().consoleOut("You have tamed an animal!");
                animal.markAsModifiedByUser();
            }
            return true;
        };

        createClickEvent = event;
        destroyClickEvent = event;
    }
}
