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
 * @author zipCoder933
 */
public class CollisionData {

    //Information only useful to collision handler
    protected Vector3i collisionNormal;//just a pointer to one of the 6 possible faces
    protected final FloatBuffer penetration;//penetration amount
    protected final FloatBuffer distances; //a list of distances
    protected final Vector3f penPerAxes= new Vector3f(); //penetration per axes

    //Information useful everywhere
    public final Vector3f totalPenPerAxes = new Vector3f(); //sum of all penetrations per axes
    public final Vector3f block_penPerAxes = new Vector3f(); //penetration per axes
    public final Vector3f entity_penPerAxes = new Vector3f(); //sum of all penetrations per axes

    public CollisionData() {
        distances = MemoryUtil.memAllocFloat(6);
        penetration = MemoryUtil.memAllocFloat(1);
        collisionNormal = null;
    }

    public static final Vector3i[] faces = {
            new Vector3i(-1, 0, 0), new Vector3i(1, 0, 0),
            new Vector3i(0, -1, 0), new Vector3i(0, 1, 0),
            new Vector3i(0, 0, -1), new Vector3i(0, 0, 1),};

    public void calculateCollision(AABB thisBox, AABB other, boolean otherBoxIsEntity) {
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

        totalPenPerAxes.add(penPerAxes);
        if(otherBoxIsEntity){
            entity_penPerAxes.add(penPerAxes);
        }else{
            block_penPerAxes.add(penPerAxes);
        }
    }

    public void reset() {
        block_penPerAxes.set(0, 0, 0);
        entity_penPerAxes.set(0, 0, 0);
        totalPenPerAxes.set(0, 0, 0);
    }
}
