package com.xbuilders.tests;


import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.utils.math.random.CustomRandom;

import java.io.*;

public class MiscTester {
    private static long referenceTime = System.currentTimeMillis();

    public static void main(String[] args) throws InterruptedException {
        // Generate a shared seed
        byte[] sharedSeed = ByteUtils.longToByteArray(15);

        // Create a SecureRandom instance using the shared seed
        CustomRandom rand = new CustomRandom();


        long seed = rand.getTrueSeed().get();

        for (int i = 0; i < 15; i++) {
            int randomInt = rand.nextInt();
            float randomFloat = rand.nextFloat();
            System.out.println(randomInt + "\t\t  " + randomFloat);
        }

        rand.getTrueSeed().set(seed); //The true seed is the real state of the random generator
        System.out.println();

        for (int i = 0; i < 15; i++) {
            int randomInt = rand.nextInt();
            double randomFloat = rand.nextGaussian();
            System.out.println(randomInt + "\t\t  " + randomFloat);
        }
    }

    //We could also just use a default random number genereator and serialzie it to get its complete state

    public static byte[] serialize(Serializable object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        oos.close();
        return baos.toByteArray();
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
