package com.xbuilders.engine.player.raycasting;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.utils.math.MathUtils;
import java.util.ArrayList;
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
        return MathUtils.floatToInt(hitPostition);
    }

    public Vector3i getHitNormalAsInt() {
        return MathUtils.floatToInt(hitNormal);
    }

    /**
     * @return the hitPosPlusNormal
     */
    public Vector3i getHitPosPlusNormal() {
        return MathUtils.floatToInt(hitPostition).add((int) hitNormal.x, (int) hitNormal.y, (int) hitNormal.z);
    }

    public Ray() {
        hitTarget = false;
        hitPostition = new Vector3f();
        hitNormal = new Vector3f();
        origin = new Vector3f();
        direction = new Vector3f();
    }

    public boolean hitTarget;
    public final Vector3f origin;
    public final Vector3f direction;
    public final Vector3f hitPostition;
    public final Vector3f hitNormal;
    public float distanceTraveled;
    public Entity entity;
    public ArrayList<AABB> cursorBoxes;

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
