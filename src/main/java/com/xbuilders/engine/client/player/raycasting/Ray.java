package com.xbuilders.engine.client.player.raycasting;

import com.xbuilders.engine.server.model.items.entity.Entity;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.AABB;

import java.util.List;

import org.joml.Vector3f;
import org.joml.Vector3i;

public class Ray {

    /**
     * @return the hitTarget
     */
    public boolean isHitTarget() {
        return hitTarget;
    }

    /**
     * @return the hitNormal
     */
    public Vector3f getHitNormal() {
        return hitNormal;
    }

    /**
     * @return the hitPostition
     */
    public Vector3f getHitPosition() {
        return hitPostition;
    }

    public Vector3i getHitPositionAsInt() {
        hitPositionInt.set((int) hitPostition.x, (int) hitPostition.y, (int) hitPostition.z);
        return hitPositionInt;
    }

    public Vector3i getHitNormalAsInt() {
        hitNormalInt.set((int) hitNormal.x, (int) hitNormal.y, (int) hitNormal.z);
        return hitNormalInt;
    }

    /**
     * @return the hitPosPlusNormal
     */
    public Vector3i getHitPosPlusNormal() {
        hitPosPlusNormal.set((int) hitPostition.x, (int) hitPostition.y, (int) hitPostition.z);
        hitPosPlusNormal.add((int) hitNormal.x, (int) hitNormal.y, (int) hitNormal.z);
        return hitPosPlusNormal;
    }

    public Ray() {
        hitTarget = false;
    }

    public boolean hitTarget;
    public final Vector3f origin = new Vector3f();
    public final Vector3f direction = new Vector3f();
    public final Vector3f hitPostition = new Vector3f();
    public final Vector3f hitNormal = new Vector3f();

    public final Vector3i hitPosPlusNormal = new Vector3i();
    public final Vector3i hitPositionInt = new Vector3i();
    public final Vector3i hitNormalInt = new Vector3i();


    public float distanceTraveled;
    public Entity entity;
    public List<AABB> cursorBoxes;

    @Override
    public String toString() {
        return "Ray{" + "hitTarget=" + hitTarget + ", origin=" + MiscUtils.printVector(origin) + ", direction=" + MiscUtils.printVector(direction) + ",\n"
                + "hitPostition=" + MiscUtils.printVector(hitPostition) + ", hitNormal=" + MiscUtils.printVector(hitNormal) + ", distanceTraveled=" + distanceTraveled + ",\n"
                + "entity=" + entity + ", cursorBoxes=" + cursorBoxes + '}';
    }

    protected void reset() {
        distanceTraveled = 0;
        hitTarget = false;
        hitNormal.set(0, 0, 0);
        hitPostition.set(0, 0, 0);
        cursorBoxes = null;
        entity = null;
    }

}
