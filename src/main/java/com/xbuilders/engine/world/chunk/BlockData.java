/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.world.chunk;

import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * @author zipCoder933
 */
public class BlockData {

    //The buffer must always be private, we want the flexibility of changing the data structure
    //    private final ByteBuffer buff;
    private byte[] buff;

    public BlockData(int size) {
//        buff = MemoryUtil.memAlloc(size);
        buff = new byte[size];
    }

    public BlockData(byte[] bytes) {
//        buff = ByteBuffer.allocate(bytes.length);
//        buff.put(bytes);
        buff = bytes;
    }

    public BlockData(List<Byte> bytes) {
//        buff = ByteBuffer.allocate(bytes.size());
        buff = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
//            buff.put(i, bytes.get(i));
            buff[i] = bytes.get(i);
        }
    }


    /**
     * The size of block data is designed to be a fixed value. If another
     * block replaces this block data, it must be replaced with a new
     * blockData
     * <p>
     * Unlike dynamic data structures (e.g., ArrayList), ByteBuffer doesn’t inherently support resizing. Once you allocate a ByteBuffer, its capacity remains fixed.
     * If you need to resize a ByteBuffer, you typically create a new one with a larger capacity and copy the data from the old buffer to the new one.
     * <p>
     */
//    public void setSize(int size) {
//        buff = Arrays.copyOf(buff, size);
////        MemoryUtil.memRealloc(buff, size);
//    }


    public byte get(int i) {
//        return buff.get(i);
        return buff[i];
    }

    public void set(int i, byte val) {
//        buff.put(i, val);
        buff[i] = val;
    }

    public int size() {
//        return buff.capacity();
        return buff.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("[");

        for (int i = 0; i < buff.length; i++) {
            sb.append(buff[i]);
            if (i < buff.length - 1) {
                sb.append(", ");
            }
        }
//        for (int i = 0; i < buff.capacity(); i++) {
//            sb.append(buff.get(i));
//            if (i < buff.capacity() - 1) {
//                sb.append(", ");
//            }
//        }
        return sb.append("]").toString();
    }

    public void write(final OutputStream out) throws IOException {
        out.write(buff);
//        for (int i = 0; i < size(); i++) {
//            byte b = get(i);
//            out.write(b);
//        }
    }
}
