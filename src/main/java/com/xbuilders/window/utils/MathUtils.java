/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lwjgltest.utils;

/**
 *
 * @author sampw
 */
public class MathUtils {

    /**
     * Maps a variable from range A to range B
     *
     * @param value the variable
     * @param start1 range A minimum
     * @param stop1 range A maximum
     * @param start2 range B minimum
     * @param stop2 range B maximum
     * @return the new variable
     */
    static public final double map(double value,
            double start1, double stop1,
            double start2, double stop2) {
        double outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
        return outgoing;
    }

    /**
     * Maps a variable from range A to range B
     *
     * @param value the variable
     * @param start1 range A minimum
     * @param stop1 range A maximum
     * @param start2 range B minimum
     * @param stop2 range B maximum
     * @return the new variable
     */
    static public final float mapFloat(float value,
            float start1, float stop1,
            float start2, float stop2) {
        float outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
        return outgoing;
    }

    static public final double mapAndClamp(double value,
            double start1, double stop1,
            double start2, double stop2) {
        double outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
        return MathUtils.clamp(outgoing, start2, stop2);
    }

    static public final float mapAndClampFloat(float value,
            float start1, float stop1,
            float start2, float stop2) {
        float outgoing = start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
        return MathUtils.clamp(outgoing, start2, stop2);
    }

    /**
     * Creates a value based on a range and sets the value somewhere in between
     * that range.
     *
     * @param start the range min
     * @param stop the range max
     * @param amt where to place the value within the range.
     * @return the value
     */
    static public final double lerp(double start, double stop, double amt) {
        return start + (stop - start) * amt;
    }

    /**
     * Creates a value based on a range and sets the value somewhere in between
     * that range.
     *
     * @param start the range min
     * @param stop the range max
     * @param amt where to place the value within the range.
     * @return the value
     */
    static public final float lerp(float start, float stop, float amt) {
        return start + (stop - start) * amt;
    }

    /**
     * Gradually changes val over time to target
     *
     * @param val the variable
     * @param target the target position
     * @param speed how long to get there (lower numbers means slower
     * transition)
     * @return the variable
     */
    public static double curve(double val, double target, double speed) {
        if (val < target) {
            val += (target - val) * speed;
        } else {
            val -= (val - target) * speed;
        }
        return val;
    }

    public static double linearTravel(double val, double target, double speed) {
        if (val < target) {
            val += speed;
            if (val > target) {
                val = target;
            }

        } else {
            val -= speed;
            if (val < target) {
                val = target;
            }
        }

        return val;
    }

    /**
     * Clamp a value from minimum to maximum
     *
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static double clamp(double val, double min, double max) {
        if (val > max) {
            return max;
        } else if (val < min) {
            return min;
        }

        return val;
    }

    /**
     * Clamp a value from minimum to maximum
     *
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static float clamp(float val, float min, float max) {
        if (val > max) {
            return max;
        } else if (val < min) {
            return min;
        }

        return val;
    }

    /**
     * Clamp a value from minimum to maximum
     *
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static int clamp(int val, int min, int max) {
        if (val > max) {
            return max;
        } else if (val < min) {
            return min;
        }

        return val;
    }

    /**
     * Clamp a value from minimum to maximum
     *
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static long clamp(long val, long min, long max) {
        if (val > max) {
            return max;
        } else if (val < min) {
            return min;
        }

        return val;
    }

    static public final double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * ( begin auto-generated from dist.getX()ml )
     *
     * Calculates the distance between two points.
     *
     * ( end auto-generated )
     *
     * @webref math:calculation
     * @param x1 x-coordinate of the first point
     * @param y1 y-coordinate of the first point
     * @param z1 z-coordinate of the first point
     * @param x2 x-coordinate of the second point
     * @param y2 y-coordinate of the second point
     * @param z2 z-coordinate of the second point
     */
    static public final double dist(double x1, double y1, double z1,
            double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
    }

    public static final double HALF_PI = 1.5707963267948966192313215;
    public static final double PI = 3.141592653589793238462643;
    public static final double TWO_PI = 6.283185307179586476925286;

    /**
     *
     * @param array the array
     * @return the index of the lowest item in the array
     */
    public static int min(double... array) {
        int minIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     *
     * @param array the array
     * @return the index of the highest item in the array
     */
    public static int max(double... array) {
        int minIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    public static double dist(double dist1, double dist2) {
        if (dist1 - dist2 >= 0) {
            return dist1 - dist2;
        } else {
            return dist2 - dist1;
        }
    }

    /**
     * Calculates the angle from centerPt to targetPt in degrees.<br>
     * This method only works for 2 dimensions
     *
     * @param centerPtX the center point
     * @param centerPtY the center point
     * @param targetPtX the target point
     * @param targetPtY the target point
     * @return angle in degrees. This is the angle from centerPt to targetPt.
     */
    public static double calcRotationAngleInDegrees(double centerPtX, double centerPtY, double targetPtX, double targetPtY) {
        double theta = Math.atan2(targetPtY - centerPtY, targetPtX - centerPtX);
        return Math.toDegrees(theta);
    }

    /**
     * Calculates the angle from centerPt to targetPt in radians. This method
     * only works for 2 dimensions
     *
     * @param centerPtX the center point
     * @param centerPtY the center point
     * @param targetPtX the target point
     * @param targetPtY the target point
     * @return angle in radians. This is the angle from centerPt to targetPt. (π
     * radians = 180 degrees)
     */
    public static double calcRotationAngleInRadiants(double centerPtX, double centerPtY, double targetPtX, double targetPtY) {
        double theta = Math.atan2(targetPtY - centerPtY, targetPtX - centerPtX);
        return theta;
    }

    /**
     *
     * @param number the number
     * @param min the minimum bound
     * @param max the maximum bound
     * @return if the number is within range
     */
    public static boolean inRange(double number, double min, double max) {
        return number >= max && number <= min;
    }

    public double degreesToRadiants(double degrees) {
        return degrees * (Math.PI / 180);
    }

    public double radiantsToDegrees(double radiants) {
        return (radiants * 180) / PI;
    }

    /**
     * Java uses negative modulus for negative numbers. See
     * https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.17.3
     * https://stackoverflow.com/questions/4403542/how-does-java-do-modulus-calculations-with-negative-numbers
     *
     * @return positive modulus operation result
     */
    public static float positiveMod(float dividend, float divisor) {
        return ((dividend % divisor) + divisor) % divisor;
    }

    /**
     * Java uses negative modulus for negative numbers. See
     * https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.17.3
     * https://stackoverflow.com/questions/4403542/how-does-java-do-modulus-calculations-with-negative-numbers
     *
     * @return positive modulus operation result
     */
    public static int positiveMod(int dividend, int divisor) {
        return ((dividend % divisor) + divisor) % divisor;
//         int modulus = dividend % divisor;
//        if (modulus < 0) {
//            modulus += divisor;
//        }
    }

}
