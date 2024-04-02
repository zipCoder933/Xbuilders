package com.xbuilders.tests;

import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.ResizableIntArray;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.ResizableIntBuffer;

public class IntBufferTester {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        ResizableIntArray ria = new ResizableIntArray(0);

        for(int i = 0; i < 100; i++) {
            ria.add(i);
            System.out.println(ria.toString()+" \t capacity: "+ria.getArray().length);
        }


//        ResizableIntBuffer rib = new ResizableIntBuffer(5);
//
//        for(int i = 0; i < 10000; i++) {
//            rib.add(i);
//            System.out.println(rib.toString()+" \t capacity: "+rib.getBuffer().capacity());
//        }

    }
}
