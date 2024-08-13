package com.xbuilders.tests;


import com.xbuilders.engine.utils.BooleanBuffer;
import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.game.items.entities.animal.mobile.AnimalAction;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MiscTester {
    private static long referenceTime = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        try (MemoryStack stack = MemoryStack.stackPush()) {

            BooleanBuffer booleanBuffer = new BooleanBuffer(10, stack);
            System.out.println(booleanBuffer.toString());

            booleanBuffer.set(0, true);
            booleanBuffer.set(1, false);
            booleanBuffer.set(2, true);
            booleanBuffer.set(3, false);
            booleanBuffer.set(4, true);
            booleanBuffer.set(5, false);
            booleanBuffer.set(6, true);
            booleanBuffer.set(7, false);
            booleanBuffer.set(8, true);
            booleanBuffer.set(9, false);

            //Print the booleanBuffer
            System.out.println(booleanBuffer.toString());


            booleanBuffer.set(16, false);
        }
    }

}
