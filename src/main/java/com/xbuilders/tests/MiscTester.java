package com.xbuilders.tests;

import com.xbuilders.engine.utils.ByteUtils;
import com.xbuilders.engine.world.chunk.saving.ChunkFile_V1;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class MiscTester {

    public static void main(String[] args) throws IOException {


        long testLong = new SecureRandom().nextLong();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteUtils.writeLong(baos, testLong);
        byte[] bytes = baos.toByteArray();

        long reconstituted = ByteUtils.bytesToLong(bytes, new AtomicInteger(0));

        System.out.println("Original value: " + testLong);
        System.out.println("Bytes: " + Arrays.toString(bytes));
        System.out.println("Reconstituted: " + reconstituted);

//        System.out.println(ChunkSavingLoadingUtils.BLOCK_DATA_MAX_BYTES);
//
//        int origIntValue = 0;
//        byte[] unsignedShortBytes = shortToBytes(origIntValue & 0xffff); // 0xffff is the way to convert from int to unsigned short and vice versa
//        int reconstituted = bytesToShort(unsignedShortBytes[0], unsignedShortBytes[1]) & 0xffff;
//
//        System.out.println("Original value: " + origIntValue);
//        System.out.println("Unsigned short: " + Arrays.toString(unsignedShortBytes));
//        System.out.println("Reconstituted: " + reconstituted);
//
////        BlockData data = new BlockData(new byte[]{1, 2, 98, 12, 79, 1, 2, 3, 4, 1, 2, 98, 12, 79, 1, 2, 3, 4});
////        ByteArrayOutputStream baos = new ByteArrayOutputStream();
////        ChunkSavingLoadingUtils.writeBlockData(data, baos);
////        byte[] bytes2 = baos.toByteArray();
////        BlockData reconstBytes = ChunkSavingLoadingUtils.readBlockData(bytes2, new AtomicInteger(0));
//
//
//        byte[] data = new byte[]{1, 2, 98, 12, 79, 1, 2, 3, 4, 1, 2, 98, 12, 79, 1, 2, 3, 4, 14};
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        ChunkSavingLoadingUtils.writeEntityData(data, baos);
//        byte[] bytes = baos.toByteArray();
//
//        byte[] reconstBytes = ChunkSavingLoadingUtils.readEntityData(bytes, new AtomicInteger(0));
//
//        System.out.println(Arrays.toString(data));
//        System.out.println(Arrays.toString(reconstBytes));
    }

    public static byte[] shortToBytes(final int x) {
        final byte b1 = (byte) x;
        final byte b2 = (byte) (x >> 8);
        return new byte[]{b1, b2};
    }

    public static int bytesToShort(final byte b1, final byte b2) {
        return (b2 << 8 | (b1 & 0xFF));
    }


}
