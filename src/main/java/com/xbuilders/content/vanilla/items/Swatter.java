package com.xbuilders.content.vanilla.items;

import com.xbuilders.content.vanilla.entities.animal.mobile.ActionAnimal;
import com.xbuilders.content.vanilla.entities.animal.mobile.AnimalAction;
import com.xbuilders.content.vanilla.entities.animal.mobile.LandAnimal;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.entity.LivingEntity;
import com.xbuilders.engine.server.item.Item;

public class Swatter extends Item {


    public Swatter(int durability) {
        super("xbuilders:swatter", "Swatter");
        setIcon("swatter.png");
        maxStackSize = 1;
        tags.add("tool");
        maxDurability = durability;
        destroyClickEvent = (ray, itemStack) -> {
            if (ray.getEntity() != null && ray.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) ray.getEntity();
                if (!entity.isHostile()) {
                    if (entity instanceof ActionAnimal) {
                        GameScene.client_hudText("You shooed the animal away!");
                        ActionAnimal animal = (ActionAnimal) entity;
                        animal.walkAwayAndDie();
                    } else {
                        entity.health = -100;
                        GameScene.client_hudText("You swatted the critter away!");
                    }
                }
                return true;
            }
            return false;
        };
    }


}