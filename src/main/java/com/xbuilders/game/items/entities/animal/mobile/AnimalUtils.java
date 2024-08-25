package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.Entity;

public class AnimalUtils {
    public static boolean inWater(Entity entity) {
        if (GameScene.world.getBlock(
                (int) entity.worldPosition.x,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z
        ).isLiquid()
                || GameScene.world.getBlock(
                (int) entity.worldPosition.x - 1,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z
        ).isLiquid()
                || GameScene.world.getBlock(
                (int) entity.worldPosition.x + 1,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z
        ).isLiquid()
                || GameScene.world.getBlock(
                (int) entity.worldPosition.x,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z - 1
        ).isLiquid()) {
            return true;
        }
        return (GameScene.world.getBlock(
                (int) entity.worldPosition.x,
                (int) entity.worldPosition.y,
                (int) entity.worldPosition.z + 1
        ).isLiquid());
    }
}
