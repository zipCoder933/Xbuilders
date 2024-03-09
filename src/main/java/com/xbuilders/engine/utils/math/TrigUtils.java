/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.math;

import static com.xbuilders.engine.utils.math.MathUtils.HALF_PI;
import static com.xbuilders.engine.utils.math.MathUtils.TWO_PI;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 *
 * @author zipCoder933
 */
public class TrigUtils {

    /**
     *
     * @param x the z position of the center-point
     * @param z the x position of the center-point
     * @param rot the angle (in degrees)
     * @param d the distance from the center-point
     * @return the point in space from the center point given the angle
     */
    public static Vector2f getCircumferencePoint(double rot, double d) {
        rot = ((float) rot / 360) * TWO_PI;
        float tX = (float) (Math.cos(rot + HALF_PI) * d);
        float tY = (float) (Math.sin(rot + HALF_PI) * d);
        return new Vector2f(tX, tY);
    }

    /**
     *
     * @param targetX
     * @param targetZ
     * @param thisX
     * @param thisZ
     * @return the angle between two 2D points (in degrees)
     */
    public static float getAngleOfPoints(float thisX, float thisZ, float targetX, float targetZ) {
        float angle = (float) Math.toDegrees(Math.atan2(targetX - thisX, targetZ - thisZ));

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    /**
     * @return the Shortest distance (angular) between two angles. It will be in
     * range [0, 180].
     */
    public static float getSignedAngleDistance(float targetA, float sourceA) {
        float a = targetA - sourceA;
        a = (a + 180) % 360 - 180;
        return a;
    }
}
