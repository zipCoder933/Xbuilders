package com.xbuilders.content.vanilla.items;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.entity.LivingEntity;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.utils.MiscUtils;

public class Sword extends Item {

    float attackDamage = 0;

    public Sword(String material, int durability, float damage) {
        super("xbuilders:" + material + "_sword", MiscUtils.capitalizeWords(material) + " Sword");
        setIcon(material + "_sword.png");
        maxStackSize = 1;
        miningSpeedMultiplier = 0.1f;
        tags.add("tool");
        tags.add("sword");
        tags.add(material);
        maxDurability = durability;
        this.attackDamage = damage;
        this.miningSpeedMultiplier = 1.5f;
        destroyClickEvent = (ray, itemStack) -> {
            if (ray.getEntity() != null && ray.getEntity() instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) ray.getEntity();
                if (entity.isHostile() || LocalClient.DEV_MODE) {//only attack hostile entities
                    entity.damage(attackDamage);
                } else entity.damage(attackDamage / 3); //attack non-hostile entities, but less damage
                GameScene.client_hudText(Math.max(0, Math.round(entity.health)) + " / " + entity.maxHealth);
                return true;
            }
            return false;
        };
    }


}