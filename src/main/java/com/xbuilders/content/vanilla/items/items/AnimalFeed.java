package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.content.vanilla.items.entities.animal.mobile.Animal;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.item.Item;

public class AnimalFeed extends Item {
    public AnimalFeed() {
        super("xbuilders:animal_feed", "Animal Feed");
        setIcon("animal_feed.png");
        createClickEvent = (ray, itemStack) -> {
            System.out.println("Animal Feed clicked");
            if (ray.getEntity() != null && ray.getEntity() instanceof Animal animal) {
                itemStack.stackSize--;
                animal.tamed = true;
                GameScene.alert("You have tamed an animal!");
                animal.markAsModifiedByUser();
            }
            return true;
        };
        destroyClickEvent = (ray, itemStack) -> {
            System.out.println("Animal Feed clicked");
            if (ray.getEntity() != null && ray.getEntity() instanceof Animal animal) {
                itemStack.stackSize--;
                animal.tamed = true;
                GameScene.alert("You have tamed an animal!");
                animal.markAsModifiedByUser();
            }
            return true;
        };
    }
}
