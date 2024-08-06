package com.xbuilders.tests;


import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.game.items.entities.animal.mobile.AnimalAction;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MiscTester {
    private static long referenceTime = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        float originalFloat = 0.5f;
        byte[] bf = ByteUtils.floatToBytes(originalFloat);
        float newFloat = ByteUtils.bytesToFloat(bf[0], bf[1], bf[2], bf[3]);
        System.out.println("result: " + newFloat);

        AnimalAction action = new AnimalAction();
        action.type = AnimalAction.ActionType.TURN;
        action.velocity = 0.5f;
        action.duration = 1000;


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        action.toBytes(baos);
        byte[] bytes = baos.toByteArray();

        AnimalAction action2 = (AnimalAction) new AnimalAction().fromBytes(bytes, new AtomicInteger());

        System.out.println(action2.toString());
        System.out.println("Time left " + action2.getDurationLeftMS());
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
