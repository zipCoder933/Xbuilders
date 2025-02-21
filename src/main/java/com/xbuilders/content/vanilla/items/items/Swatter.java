//package com.xbuilders.content.vanilla.items.items;
//
//import com.xbuilders.engine.client.visuals.gameScene.GameScene;
//import com.xbuilders.engine.server.entity.LivingEntity;
//import com.xbuilders.engine.server.item.Item;
//import com.xbuilders.engine.utils.MiscUtils;
//
//public class Swatter extends Item {
//
//    float attackDamage = 0;
//
//    public Swatter(int durability, float damage) {
//        super("xbuilders:" + material + "_swatter", MiscUtils.capitalizeWords(material) + " Swatter");
//        setIcon("swatter.png");
//        maxStackSize = 1;
//        tags.add("tool");
//        tags.add("sword");
//        tags.add(material);
//        maxDurability = durability;
//        this.attackDamage = damage;
//        this.miningSpeedMultiplier = 1.5f;
//        destroyClickEvent = (ray, itemStack) -> {
//            if (ray.getEntity() != null && ray.getEntity() instanceof LivingEntity) {
//                LivingEntity entity = (LivingEntity) ray.getEntity();
//                if (!entity.isHostile()) {//only attack hostile entities
//                    entity.currentAction = "swatter";
//                }
//                GameScene.client_hudText(Math.max(0, Math.round(entity.health)) + " / " + entity.maxHealth);
//                return true;
//            }
//            return false;
//        };
//    }
//
//
//}