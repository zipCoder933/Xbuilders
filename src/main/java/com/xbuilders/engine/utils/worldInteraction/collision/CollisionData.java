/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.worldInteraction.collision;

import com.xbuilders.engine.utils.math.AABB;
import java.nio.FloatBuffer;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author zipCoder933
 */
public class CollisionData {

    public Vector3i collisionNormal;//just a pointer to one of the 6 possible faces
    public final FloatBuffer penetration;//penetration amount
    public final FloatBuffer distances; //a list of distances
    public final Vector3f penPerAxes;//penetration per axes

    public CollisionData() {
        distances = MemoryUtil.memAllocFloat(6);
        penetration = MemoryUtil.memAllocFloat(1);
        collisionNormal = null;
        penPerAxes = new Vector3f();
    }

    public static final Vector3i[] faces = {
        new Vector3i(-1, 0, 0), new Vector3i(1, 0, 0),
        new Vector3i(0, -1, 0), new Vector3i(0, 1, 0),
        new Vector3i(0, 0, -1), new Vector3i(0, 0, 1),};

    public void calculateCollision(AABB thisBox, AABB other) {
        Vector3f boxAPos = thisBox.min;
        Vector3f boxBPos = other.min;
        Vector3f maxA = new Vector3f(boxAPos).add(thisBox.getXLength(), thisBox.getYLength(), thisBox.getZLength());
        Vector3f maxB = new Vector3f(boxBPos).add(other.getXLength(), other.getYLength(), other.getZLength());
        penetration.put(0, Float.MAX_VALUE);

        distances.put(0, (maxB.x - boxAPos.x));// distance of box 'b' to 'left ' of 'a '.
        distances.put(1, (maxA.x - boxBPos.x));// distance of box 'b' to 'right ' of 'a '.
        distances.put(2, (maxB.y - boxAPos.y));// distance of box 'b' to 'bottom ' of 'a '.
        distances.put(3, (maxA.y - boxBPos.y));// distance of box 'b' to 'top ' of 'a '.
        distances.put(4, (maxB.z - boxAPos.z));// distance of box 'b' to 'far ' of 'a '.
        distances.put(5, (maxA.z - boxBPos.z)); // distance of box 'b' to 'near ' of 'a '.

        for (int i = 0; i < 6; i++) {
            if (distances.get(i) < penetration.get(0)) {
                penetration.put(0, distances.get(i));
                collisionNormal = faces[i];
            }
        }
        penPerAxes.set(collisionNormal).mul(penetration.get(0));
    }
}
