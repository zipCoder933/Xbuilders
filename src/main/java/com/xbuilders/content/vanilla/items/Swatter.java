package com.xbuilders.content.vanilla.items;

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
        this.miningSpeedMultiplier = 1.5f;
        destroyClickEvent = (ray, itemStack) -> {
            if (ray.getEntity() != null && ray.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) ray.getEntity();
                if (entity instanceof LandAnimal) {
                    LandAnimal landAnimal = (LandAnimal) entity;
                    if (!entity.isHostile()) {
                        landAnimal.currentAction = new AnimalAction(AnimalAction.ActionType.RUN_AWAY, 10000);
                    }
                }
                return true;
            }
            return false;
        };
    }


}