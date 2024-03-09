/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.math;

import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.worldInteraction.collision.CollisionData;
import java.nio.FloatBuffer;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author zipCoder933
 */
public class AABB {

    /**
     * @return the xLength
     */
    public float getXLength() {
        return maxPoint.x - minPoint.x;
    }

    /**
     * @return the yLength
     */
    public float getYLength() {
        return maxPoint.y - minPoint.y;
    }

    /**
     * @return the zLength
     */
    public float getZLength() {
        return maxPoint.z - minPoint.z;
    }

    public void setX(float x) {
        maxPoint.x = x + getXLength();
        minPoint.x = x;
    }

    public void setY(float y) {
        maxPoint.y = y + getYLength();
        minPoint.y = y;
    }

    public void setZ(float z) {
        maxPoint.z = z + getZLength();
        minPoint.z = z;
    }

    public Vector3f minPoint, maxPoint;

    public AABB(MemoryStack stack) {
        minPoint = new Vector3f(stack.mallocFloat(3));
        maxPoint = new Vector3f(stack.mallocFloat(3));
    }

    public AABB(FloatBuffer minPoint2, FloatBuffer maxPoint2) {
        minPoint = new Vector3f(minPoint2);
        maxPoint = new Vector3f(maxPoint2);
    }

    public AABB() {
        minPoint = new Vector3f(0);
        maxPoint = new Vector3f(0);
    }

    public AABB setPosAndSize(float x, float y, float z, float xLength, float yLength, float zLength) {
        minPoint.set(x, y, z);
        maxPoint.set(x + xLength, y + yLength, z + zLength);
        return this;
    }

    public void set(AABB box) {
        minPoint.set(box.minPoint);
        maxPoint.set(box.maxPoint);
    }

    public AABB(AABB aabb) {
        this.minPoint = new Vector3f(aabb.minPoint);
        this.maxPoint = new Vector3f(aabb.maxPoint);
    }

    public boolean intersects(AABB other) {
        return maxPoint.x > other.minPoint.x && minPoint.x < other.maxPoint.x
                && maxPoint.y > other.minPoint.y && minPoint.y < other.maxPoint.y
                && maxPoint.z > other.minPoint.z && minPoint.z < other.maxPoint.z;
    }



    /**
     *
     * @param other the other AABB
     * @return the amount that the other box is intersecting with this box on
     * the X axis, or in other words, <b>the amount of displacement on the X
     * axis to make the other bounding box not colliding with this bounding
     * box.</b>
     */
    public float getXPenetrationDepth(AABB other) {
        if (minPoint.x < other.minPoint.x) {
            return getXLength() - (other.minPoint.x - minPoint.x);
        } else if (minPoint.x > other.minPoint.x) {
            return 0 - (other.getXLength() - (minPoint.x - other.minPoint.x));
        } else {
            return 0;
        }
    }

    /**
     * @param other the other AABB
     * @return the amount that the other box is intersecting with this box on
     * the Y axis, or in other words, <b>the amount of displacement on the Y
     * axis to make the other bounding box not colliding with this bounding
     * box.</b>
     */
    public float getYPenetrationDepth(AABB other) {
        if (minPoint.y < other.minPoint.y) {
            return getYLength() - (other.minPoint.y - minPoint.y);
        } else if (minPoint.y > other.minPoint.y) {
            return 0 - (other.getYLength() - (minPoint.y - other.minPoint.y));
        } else {
            return 0;
        }
    }

    /**
     * @param other the other AABB
     * @return the amount that the other box is intersecting with this box on
     * the Z axis, or in other words, <b>the amount of displacement on the Z
     * axis to make the other bounding box not colliding with this bounding
     * box.</b>
     */
    public float getZPenetrationDepth(AABB other) {
        if (minPoint.z < other.minPoint.z) {
            return getZLength() - (other.minPoint.z - minPoint.z);
        } else if (minPoint.z > other.minPoint.z) {
            return 0 - (other.getZLength() - (minPoint.z - other.minPoint.z));
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "AABB{" + MiscUtils.printVector(minPoint) + ", " + MiscUtils.printVector(maxPoint) + "}";
    }

}
