/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.utils.math.FastNoise;
import com.xbuilders.engine.utils.math.MathUtils;

import java.util.Random;

/**
 * @author zipCoder933
 */
public class AnimalRandom {

    /**
     * @return the noise
     */
    public FastNoise getNoise() {
        return noise;
    }

    /**
     * @return the random
     */
    public Random getRandom() {
        return random;
    }

    /**
     * @return the seed
     */
    public int getSeed() {
        return seed;
    }

    public int nextInt(int lowerBound, int upperBound) {
        return getRandom().nextInt(upperBound - lowerBound) + lowerBound;
    }

    public float nextFloat(float lowerBound, float upperBound) {
        return (getRandom().nextFloat() * upperBound - lowerBound) + lowerBound;
    }

    public long nextLong(long lowerBound, long upperBound) {
        return (long) ((getRandom().nextFloat() * upperBound - lowerBound) + lowerBound);
    }

    public boolean nextBoolean() {
        return getRandom().nextBoolean();
    }

    public float nextFloat() {
        return getRandom().nextFloat();
    }

    public int nextInt() {
        return getRandom().nextInt();
    }

    public int nextInt(int val) {
        return getRandom().nextInt(val);
    }

    private FastNoise noise;
    private Random random;

    public AnimalRandom(int seed) {
        super();
        noiseInt = 0;
        noise = new FastNoise();
        random = new Random();
        setSeed(seed);
    }

    public void setSeed(int seed) {
        noiseInt = 0;
        this.seed = seed;
        getRandom().setSeed(seed);
        getNoise().SetSeed((int) seed);
    }

    int noiseInt;
    private int seed = 0;

    protected void updateNoiseSeed() {
        noiseInt += 1;
        if (noiseInt > 100000) {
            noiseInt = 0;
        }
    }

    public float noise(float frequency) {
        return getNoise().GetValueFractal(seed, (noiseInt * frequency) - getSeed());
    }

    public float noise(float frequency, float min, float max) {
        return MathUtils.map(noise(frequency), -1, 1, min, max);
    }

}
