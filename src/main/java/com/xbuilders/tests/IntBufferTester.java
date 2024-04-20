package com.xbuilders.tests;

import com.xbuilders.engine.items.block.construction.BlockTypeModel.ObjToBlockModel;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.ResizableIntArray;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.ResizableIntBuffer;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.world.chunk.Chunk;

public class IntBufferTester {

    public final static float maxMult12bits = (float) ((Math.pow(2, 12) / (Chunk.WIDTH)) - 6);


    public static void main(String[] args) {
        System.out.println("2^12 = " + Math.pow(2, 12));

        for (float val = -1; val < 34; val += 0.5f) {
            int converted = (int) ((val + 1) * maxMult12bits);
            float unconverted = ((float) (converted) / maxMult12bits) - 1;

            System.out.println(val + "\t  unconv: " + unconverted + "\t  is over: " + (converted >= Math.pow(2, 12)));
        }


//        System.out.println("Hello World!");
//        ResizableIntArray ria = new ResizableIntArray(0);
//
//        for(int i = 0; i < 100; i++) {
//            ria.add(i);
//            System.out.println(ria.toString()+" \t capacity: "+ria.getArray().length);
//        }
//
//
////        ResizableIntBuffer rib = new ResizableIntBuffer(5);
////
////        for(int i = 0; i < 10000; i++) {
////            rib.add(i);
////            System.out.println(rib.toString()+" \t capacity: "+rib.getBuffer().capacity());
////        }

    }
}
