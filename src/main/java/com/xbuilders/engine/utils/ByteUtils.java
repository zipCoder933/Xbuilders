package com.xbuilders.engine.utils;

import java.io.IOException;
import java.io.OutputStream;

public class ByteUtils {

//Short

    public static byte[] shortToBytes(final int x) {
        final byte b1 = (byte) x;
        final byte b2 = (byte) (x >> 8);
        return new byte[]{b1, b2};
    }
    public static int bytesToShort(final byte b1, final byte b2) {
        return (b2 << 8 | (b1 & 0xFF));
    }


    public static void writeShort(OutputStream out, final short x) throws IOException {
        out.write((byte) (x & 0xFF));
        out.write((byte) ((x >> 8) & 0xFF));
    }

    public static void writeUnsignedShort(OutputStream out, final int x) throws IOException {
        out.write((byte) (x & 0xFF));
        out.write((byte) ((x >> 8) & 0xFF));
    }


    //Integer
    public static byte[] intToBytes(int x) {
        final byte b1 = (byte) x;
        final byte b2 = (byte) (x >> 8);
        final byte b3 = (byte) (x >> 16);
        final byte b4 = (byte) (x >> 24);
        return new byte[]{b1, b2, b3, b4};
    }

    public static void writeInt(OutputStream out, final int x) throws IOException {
        out.write((byte) (x & 0xFF));
        out.write((byte) ((x >> 8) & 0xFF));
        out.write((byte) ((x >> 16) & 0xFF));
        out.write((byte) ((x >> 24) & 0xFF));
    }

    public static int bytesToInt(final byte b1, final byte b2, final byte b3, final byte b4) {
        final int result = b4 << 24 | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF);
        return result;
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

     public static byte[] floatToBytes(float value) {
         int intBits =  Float.floatToIntBits(value);
         return new byte[] {
                 (byte) (intBits >> 24),
                 (byte) (intBits >> 16),
                 (byte) (intBits >> 8),
                 (byte) (intBits) };
     }

     public static float bytesToFloat(byte[] bytes) {
         int intBits =
                 bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
         return Float.intBitsToFloat(intBits);
     }


}