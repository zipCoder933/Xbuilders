/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.utils.math;

import java.util.Random;

/**
 *
 * @author zipCoder933
 */
public class RandomUtils {

    static Random random = new Random();

    public static int randInt(int lowerBound, int upperBound) {
        return random.nextInt(upperBound - lowerBound) + lowerBound;
    }

    public static int randInt(Random random, int lowerBound, int upperBound) {
        return random.nextInt(upperBound - lowerBound) + lowerBound;
    }

    public static float randFloat(Random random, float lowerBound, float upperBound) {
        return (random.nextFloat() * upperBound - lowerBound) + lowerBound;
    }
}
