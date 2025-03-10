package com.xbuilders.content.vanilla.items;

import com.xbuilders.engine.server.entity.LivingEntity;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.item.Item;

public class AnimalFeed extends Item {
    public AnimalFeed() {
        super("xbuilders:animal_feed", "Animal Feed");
        setIcon("animal_feed.png");

        OnClickEvent event = (ray, itemStack) -> {
            System.out.println("Animal Feed clicked");
            if (ray.getEntity() != null && ray.getEntity() instanceof LivingEntity animal) {
                itemStack.stackSize--;
                animal.tamed = true;
                Server.alertClient("You have tamed an animal!");
                animal.markAsModifiedByUser();
            }
            return true;
        };

        createClickEvent = event;
        destroyClickEvent = event;
    }
}
