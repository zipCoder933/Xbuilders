/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.math.FastNoise;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.random.CustomRandom;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zipCoder933
 */
public class AnimalRandom {


    public int nextInt(int lowerBound, int upperBound) {
        return random.nextInt(upperBound - lowerBound) + lowerBound;
    }

    public float nextFloat(float lowerBound, float upperBound) {
        return (random.nextFloat() * upperBound - lowerBound) + lowerBound;
    }

    public long nextLong(long lowerBound, long upperBound) {
        return (long) ((random.nextFloat() * upperBound - lowerBound) + lowerBound);
    }

    public boolean nextBoolean() {
        return random.nextBoolean();
    }

    public float nextFloat() {
        return random.nextFloat();
    }

    public int nextInt() {
        return random.nextInt();
    }

    public int nextInt(int val) {
        return random.nextInt(val);
    }

    private FastNoise noise;
    private CustomRandom random;

    public AnimalRandom() {
        super();
        noiseIndex = 0;
        noise = new FastNoise();
        random = new CustomRandom();
    }

    public void setSeed(long seed) {
        noiseIndex = 0;
        noiseSeed = (int) (seed);
        random.setSeed(seed);
        noise.SetSeed((int) (seed));
    }


    private int noiseSeed;

    //These parameters represent the state of this entire class
    int noiseIndex;
    //random.getTrueSeed();

    public void writeState(ByteArrayOutputStream baos) throws IOException {
        ByteUtils.writeLong(baos, random.getTrueSeed().get());
        ByteUtils.writeInt(baos, noiseIndex);
    }

    public void readState(byte[] state, AtomicInteger start) {
        random.getTrueSeed().set(ByteUtils.bytesToLong(state, start));
        noiseIndex = ByteUtils.bytesToInt(state, start);
    }

    public float noise(float frequency) {
        return noise.GetValueFractal(noiseSeed, (noiseIndex * frequency) - noiseSeed);
    }

    public float noise(float frequency, float min, float max) {
        return MathUtils.map(noise(frequency), -1, 1, min, max);
    }
}
