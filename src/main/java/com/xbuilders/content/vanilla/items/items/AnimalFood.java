package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.content.vanilla.items.entities.animal.mobile.Animal;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.items.Registrys;
import com.xbuilders.engine.server.items.item.Item;
import com.xbuilders.engine.server.items.item.ItemStack;
import com.xbuilders.engine.server.items.loot.LootTableRegistry;
import org.joml.Vector3f;

public class AnimalFood {
    public static void makeAnimalFood(Item food) {
        Item.OnClickEvent event = (ray, itemStack) -> {
            System.out.println("Animal food clicked");
            if (ray.getEntity() != null && ray.getEntity() instanceof Animal animal) {
                if (animal.tamed) {
                    itemStack.stackSize--;
                    animal.markAsModifiedByUser();

                    Vector3f dropPos = new Vector3f(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z);

                    LootTableRegistry.animalFeedLootTables.getLoot(animal.getId(), food.id).forEach((loot) -> {
                        Item out = Registrys.getItem(loot.item);
                        if (out != null) {
                            ItemStack stack = new ItemStack(out);
                            Server.placeItemDrop(dropPos, stack, false);
                        }
                    });

                }
            }
            return true;
        };
        food.createClickEvent = event;
        food.destroyClickEvent = event;
    }
}
