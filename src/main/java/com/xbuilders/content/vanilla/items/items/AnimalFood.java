package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.engine.server.entity.LivingEntity;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.server.loot.AllLootTables;
import com.xbuilders.engine.server.loot.output.LootList;
import org.joml.Vector3f;

public class AnimalFood {
    public static void makeAnimalFood(Item food, Item.OnClickEvent defaultEvent) {
        Item.OnClickEvent event = (ray, itemStack) -> {
            System.out.println("Animal food clicked");
            if (ray.getEntity() != null && ray.getEntity() instanceof LivingEntity animal) {
                itemStack.stackSize--;

                if (!animal.tamed) {
                    Server.alertClient("Animal is not tamed");
                    return true;
                } else if (animal.tryToConsume(itemStack)) {
                    animal.markAsModifiedByUser();
                    Server.alertClient("Food consumed");
                    Vector3f dropPos = new Vector3f(animal.worldPosition.x, animal.worldPosition.y, animal.worldPosition.z);
                    LootList lootList = AllLootTables.animalFeedLootTables.getLoot(animal.getId(), food.id);
                    if (lootList.isEmpty()) System.out.println("No loot for " + animal);
                    lootList.randomItems((stack) -> {
                        Server.placeItemDrop(dropPos, stack, false);
                    });
                }
                return true;
            } else if (defaultEvent != null) return defaultEvent.run(ray, itemStack);
            else return false;
        };
        food.createClickEvent = event;
        food.destroyClickEvent = defaultEvent;
    }
}
