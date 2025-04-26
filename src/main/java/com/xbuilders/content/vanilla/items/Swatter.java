package com.xbuilders.content.vanilla.items;

import com.xbuilders.Main;
import com.xbuilders.content.vanilla.entities.animal.mobile.ActionAnimal;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.entity.ItemDrop;
import com.xbuilders.engine.server.entity.LivingEntity;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.server.item.ItemStack;

public class Swatter extends Item {


    public Swatter(int durability) {
        super("xbuilders:swatter", "Swatter");
        setIcon("swatter.png");
        maxStackSize = 1;
        tags.add("tool");
        maxDurability = durability;
        destroyClickEvent = (ray, itemStack) -> {
            if (ray.getEntity() != null) {
                if (ray.getEntity() instanceof LivingEntity) {
                    LivingEntity entity = (LivingEntity) ray.getEntity();
                    if (!entity.isHostile()) {
                        if (entity instanceof ActionAnimal) {
                            Main.getClient().window.gameScene.client_hudText("You shooed the animal away!");
                            ActionAnimal animal = (ActionAnimal) entity;
                            animal.walkAwayAndDie();
                        } else {
                            entity.destroy();
                            entity.health = -100;
                            Main.getClient().window.gameScene.client_hudText("You swatted the critter away!");
                        }
                    }
                } else if (!(ray.getEntity() instanceof ItemDrop)) {
                    Entity entity = ray.getEntity();
                    Item item = Registrys.getItem(entity);
                    if (item != null) { //Remove the item and place an item drop
                        entity.destroy();
                        Main.getServer().placeItemDrop(entity.worldPosition,
                                new ItemStack(item),
                                false);
                        //GameScene.client_hudText("You swatted the " + item.name + " away!");
                    }
                }
                return true;
            }
            return false;
        };
    }


}