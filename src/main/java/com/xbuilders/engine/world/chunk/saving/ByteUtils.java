package com.xbuilders.engine.world.chunk.saving;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

 class ByteUtils {



    public static byte[] shortToBytes(final int x) {
        final byte b1 = (byte) x;
        final byte b2 = (byte) (x >> 8);
        return new byte[]{b1, b2};
    }

    public static short bytesToShort(final byte b1, final byte b2) {
        final short result = (short) (b2 << 8 | (b1 & 0xFF));
        return result;
    }

    public static void writeShort(OutputStream out, final short x) throws IOException {
        out.write((byte) (x & 0xFF));
        out.write((byte) ((x >> 8) & 0xFF));
    }

    public static void writeUnsignedShort(OutputStream out, final int x) throws IOException {
        out.write((byte) (x & 0xFF));
        out.write((byte) ((x >> 8) & 0xFF));
    }


    public static int readShort(final byte b1, final byte b2) {
        return (b2 << 8 | (b1 & 0xFF));
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= Byte.SIZE;
        }
        return result;
    }

    public static long bytesToLong(final byte[] b) {
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (b[i] & 0xFF);
        }
        return result;
    }
}