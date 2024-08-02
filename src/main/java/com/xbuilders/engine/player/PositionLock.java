package com.xbuilders.engine.player;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class PositionLock {

    public PositionLock(Entity lock, float yOffset) {
        this.entity = lock;
        System.out.println(lock.aabb.offset.x + " " + lock.aabb.offset.y + " " + lock.aabb.offset.z);
        this.playerDisplacement = new Matrix4f();
        setOffset(yOffset);
    }

    public void setOffset(float yOffset) {
        playerDisplacement.identity().translate(
                0,
                yOffset - GameScene.player.aabb.size.y,
                0);
    }

    public void setOffset(float xOffset, float yOffset, float zOffset) {
        playerDisplacement.identity().translate(
                xOffset,
                yOffset - GameScene.player.aabb.size.y,
                zOffset);
    }

    public final Entity entity;
    public final Matrix4f playerDisplacement;//Meant to be updated by the entity every frame or so
    private final Vector3f translation = new Vector3f();//Calculated from displacement, used to position the player relatively to the entity
    private final Vector3f position = new Vector3f();

    public Vector3f getPosition() {
        position.set(entity.worldPosition).add(playerDisplacement.getTranslation(translation));
        return position;
    }

}
