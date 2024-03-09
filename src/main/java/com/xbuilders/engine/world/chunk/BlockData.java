/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.world.chunk;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author zipCoder933
 */
public class BlockData {

    public final ByteBuffer buff;

    public BlockData(int size) {
        /**
         * The size of block data is designed to be a fixed value. If another
         * block replaces this block data, it must be replaced with a new
         * blockData
         */
        buff = MemoryUtil.memAlloc(size);

    }

    public void setSize(int size) {
        MemoryUtil.memRealloc(buff, size);
    }

    public BlockData(byte[] bytes) {
        buff = ByteBuffer.allocate(bytes.length);
        buff.put(bytes);
    }

    public BlockData(List<Byte> bytes) {
        buff = ByteBuffer.allocate(bytes.size());
        for (int i = 0; i < bytes.size(); i++) {
            buff.put(i, bytes.get(i));
        }
    }

    public byte get(int i) {
        return buff.get(i);

    }

    public void set(int i, byte val) {
        buff.put(i, val);
    }

    public int size() {
        return buff.capacity();
    }

    @Override
    public String toString() {
        return Arrays.toString(buff.array());
    }
}
