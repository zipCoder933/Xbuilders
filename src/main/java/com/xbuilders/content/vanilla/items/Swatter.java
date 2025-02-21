package com.xbuilders.content.vanilla.items;

import com.xbuilders.content.vanilla.entities.animal.mobile.AnimalAction;
import com.xbuilders.content.vanilla.entities.animal.mobile.LandAnimal;
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
                    if (entity instanceof LandAnimal) {
                        LandAnimal landAnimal = (LandAnimal) entity;
                        landAnimal.walkAwayAndDie();
                    }
                }
                return true;
            }
            return false;
        };
    }


}