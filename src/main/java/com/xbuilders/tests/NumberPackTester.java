package com.xbuilders.tests;

import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.world.chunk.ChunkVoxels;

public class NumberPackTester {

    public static void main(String[] args) {
//        ChunkVoxels voxels = new ChunkVoxels(16, 16, 16);
//
//short voxel = 1234;
//        voxels.setSun(0,0,0,(byte)5);
//        voxels.setTorch(0,0,0,(byte)0,(byte)3);
//
//        System.out.println("Packing test");
//        byte packed = voxels.getPackedSunAndTorch(0,0,0);
//        int[] unpacked = voxels.unpack4BitValues(packed);
//        System.out.println("Unpacked: "+unpacked[0] + ", " + unpacked[1]);
//        System.out.println("Original: "+voxels.getSun(0,0,0) + ", " + voxels.getTorch(0,0,0));
//
//        System.out.println("\nMesh packing test");
//        int thisPlanePacked = (voxel << 8) | (packed & 0xFF);
//        //Unpack to test and print the results
//
//        int voxelUnpacked = thisPlanePacked >> 8;
//        byte sunUnpacked = (byte) (thisPlanePacked & 0xFF);
//        System.out.println("Voxel: " + voxelUnpacked+" original: "+voxel);
//        System.out.println("Light: " + sunUnpacked+" original: "+packed);
//        System.out.println("Light unpacked: " + voxels.unpack4BitValues(sunUnpacked)[0] + ", " + voxels.unpack4BitValues(sunUnpacked)[1]);


        System.out.println("\nThird int test");
        //The mistake was that the bits were not shifted correctly
        //TTTTTTTT TTTTTTTT 00000000 SSSSTTTT

        int sun = 15 & 0x0F;
        int torch = 0 & 0x0F;
        byte light2 = (byte)((sun << 4) | torch);
        int thirdInt = VertexSet.packThirdInt(524, light2);
        System.out.println("third int: " + intToString(thirdInt,8));

        //Unpack it
        // Extract the texture value (16 bits) by shifting right by 16 bits
        int textureID = (thirdInt >> 16) & 0x0FFFF;


        // Extract the next 4 bits as sunlight and the rest of the bits as torchlight
        int packedSun = (thirdInt >> 4) & 0x0F;
        int packedTorch = thirdInt & 0x0F;
        System.out.println("Unpacked texture: " + textureID);
        System.out.println("Unpacked sunlight: " + packedSun);
        System.out.println("Unpacked torchlight: " + packedTorch);
    }

    /**
     * Converts an integer to a 32-bit binary string
     * @param number
     *      The number to convert
     * @param groupSize
     *      The number of bits in a group
     * @return
     *      The 32-bit long bit string
     */
    public static String intToString(int number, int groupSize) {
        StringBuilder result = new StringBuilder();

        for(int i = 31; i >= 0 ; i--) {
            int mask = 1 << i;
            result.append((number & mask) != 0 ? "1" : "0");

            if (i % groupSize == 0)
                result.append(" ");
        }
        result.replace(result.length() - 1, result.length(), "");

        return result.toString();
    }

    public int[] unpack4BitValues(byte packedByte) {
        int high = (packedByte >> 4) & 0x0F; // Shift right to get the high 4 bits and mask
        int low = packedByte & 0x0F;          // Mask to get the low 4 bits

        return new int[]{high, low};
    }
}
