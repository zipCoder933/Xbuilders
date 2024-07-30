package com.xbuilders.tests;

import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.saving.ChunkSavingLoadingUtils;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class MiscTester {

    public static void main(String[] args) throws IOException {

        System.out.println(ChunkSavingLoadingUtils.BLOCK_DATA_MAX_BYTES);

        int origIntValue = 0;
        byte[] unsignedShortBytes = shortToBytes(origIntValue & 0xffff); // 0xffff is the way to convert from int to unsigned short and vice versa
        int reconstituted = bytesToShort(unsignedShortBytes[0], unsignedShortBytes[1]) & 0xffff;

        System.out.println("Original value: " + origIntValue);
        System.out.println("Unsigned short: " + Arrays.toString(unsignedShortBytes));
        System.out.println("Reconstituted: " + reconstituted);

        BlockData data = new BlockData(new byte[]{1, 2, 98, 12, 79, 1, 2, 3, 4, 1, 2, 98, 12, 79, 1, 2, 3, 4});
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ChunkSavingLoadingUtils.writeBlockData(data, baos);
        byte[] bytes2 = baos.toByteArray();
        BlockData data2 = ChunkSavingLoadingUtils.readBlockData(bytes2, new AtomicInteger(0));

        System.out.println(data);
        System.out.println(data2);
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
