/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.mesh.chunkMesh;

import java.nio.IntBuffer;
import java.util.ArrayList;

import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.world.chunk.Chunk;
import java.util.List;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

/**
 * @author zipCoder933
 */
public class BufferSet {

    public final static float maxMult10bits = (float) ((Math.pow(2, 10) / Chunk.WIDTH) - 1);
    public final static float maxMult12bits = (float) ((Math.pow(2, 12) / Chunk.WIDTH) - 1);
    public final static float MAX_BLOCK_ANIMATION_SIZE = 2 ^ 5 - 1;
    public static final int VECTOR_ELEMENTS = 3;

    public static int packFirstInt(float vertX, float vertY, byte normals, byte animation) {
        // Scale and convert vertX and vertY to 12-bit integers
        int a = (int) (vertX * maxMult12bits); // A and b are 12 bits (0-4095)
        int b = (int) (vertY * maxMult12bits);

        // Pack the values into a single integer
        // VertX is shifted to the left by 20 bits, VertY by 8 bits, Normals by 5 bits, and Animation by 0 bits
        // Normals take up 3 bits, and animation takes up 5 bits
        return (a << 20) | (b << 8) | ((normals & 0b111) << 5) | (animation & 0b11111);
    }

    public static int packSecondInt(float vertZ, float u, float v) {
        int a = (int) (vertZ * maxMult12bits); //12 bits
        int b = (int) (u * maxMult10bits); //10 bits
        int c = (int) (v * maxMult10bits); //10 bits

        //a = 2^12 (0-4095), b and c = 2^10 (0-1023)
        return (a << 20) | (b << 10) | c;
    }

    public static int packThirdInt(int texture, int light) {
        // First 16 bits for texture
        int textureBits = (texture & 0xFFFF) << 16;
        //Remaining 16 bits for animation
        int animationBits = (light & 0xFFFF);

        return textureBits | animationBits;
    }

    //-----------------------------------------------------------------------------
    /**
     * Packs three integer coordinates into a single integer.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param z The z-coordinate.
     * @return The packed integer.
     */
    public static int packCoords(int x, int y, int z) {
        // Each value is reduced to 10 bits, this integer is only wasting 2 bits
        return z | (x << 10) | (y << 20);
    }

    /**
     * Unpacks an integer into a Vector3f representing coordinates.
     *
     * @param vec The Vector3f to store the unpacked coordinates.
     * @param packed The packed integer containing x, y, and z coordinates.
     */
    public static void unpackCoords(Vector3f vec, int packed) {
        float y = (packed >> 20);
        float x = (packed >> 10) & 0x3FF;  // Use bitmask to getVert 10 bits
        float z = packed & 0x3FF;  // Use bitmask to getVert 10 bits
        vec.set(x, y, z);
    }

    /**
     * This is the primary memory contributor in the greedy mesher
     */
    //<editor-fold desc="Buffer Set (Reusable IntBuffer version)">
//    private IntBuffer verts;
//    int indx = 0;
//
//    public boolean isEmpty() {
//        return indx == 0;
//    }
//
//    public void clear() {
//        MemoryUtil.memFree(verts);
//        indx = 0;
//    }
//
//    public BufferSet() {
//        indx = 0;
//        verts = MemoryUtil.memAllocInt(1);
//    }
//
//    public IntBuffer makeVertexSet() {
//        verts = MemoryUtil.memRealloc(verts, indx);
//        return verts;
////        return null;
//    }
//
//    public void addVert(int firstInt, int secondInt, int thridInt) {
//        if (indx + 3 > verts.capacity()) verts = MemoryUtil.memRealloc(verts, indx + 30);
//        verts.put(indx, firstInt);
//        verts.put(indx + 1, secondInt);
//        verts.put(indx + 2, thridInt);
//        indx += 3;
//    }
//</editor-fold>
    //<editor-fold desc="Buffer Set (IntBuffer version)">
//    private IntBuffer verts;
//    int indx;
//
//    public boolean isEmpty() {
//        return indx == 0;
//    }
//
//    public BufferSet() {
//        indx = 0;
//        verts = MemoryUtil.memAllocInt(1);
//    }
//
//    public IntBuffer makeVertexSet() {
//        verts = MemoryUtil.memRealloc(verts, indx);
//        return verts;
////        return null;
//    }
//
//    public void addVert(int firstInt, int secondInt, int thridInt) {
//        if (indx + 3 > verts.capacity()) verts = MemoryUtil.memRealloc(verts, indx + 30);
//        verts.put(indx, firstInt);
//        verts.put(indx + 1, secondInt);
//        verts.put(indx + 2, thridInt);
//        indx += 3;
//    }
//
//
//    private IntBuffer verts;
//    int indx;
//    public final static int VECTOR_ELEMENTS = 3;
//
//    public boolean isEmpty() {
//        return indx == 0;
////        return verts.isEmpty();
//    }
//</editor-fold>
    //<editor-fold desc="Buffer Set (ArrayList version)">
    public Buffer[] verts = {new Buffer()};
    private IntBuffer buffer;

    public int size() {
        int size = 0;
        for (Buffer vert : verts) {
            size += vert.size();
        }
        return size;
    }

    public void clear() {
        for (Buffer vert : verts) {
            vert.clear();
        }
    }

    public IntBuffer makeVertexSet() {
        //The main contributor to the memory usage is the IntBuffer that gets created here
        int vertIndex = 0;
        buffer = MemoryUtil.memAllocInt(size() * VECTOR_ELEMENTS);

        for (int buffIndex = 0; buffIndex < verts.length; buffIndex++) {
            for (int i = 0; i < verts[buffIndex].size(); i++) {
                IntBuffer vertex = verts[buffIndex].getVert(i);
                buffer.put(vertIndex, vertex.get(0));
                buffer.put(vertIndex + 1, vertex.get(1));
                buffer.put(vertIndex + 2, vertex.get(2));
                MemoryUtil.memFree(vertex);
                vertIndex += 3;
            }
        }
        clear();
        return buffer;
    }
    //</editor-fold>

    public void vertex(float x, float y, float z,
            float uvX, float uvY, byte normal,
            BlockTexture.FaceTexture texture, byte light) {
        verts[0].addVert(packFirstInt(x, y, normal, texture.animationLength),
                packSecondInt(z, uvX, uvY),
                packThirdInt(texture.id, light));
    }

    public void vertex(float x, float y, float z,
            float uvX, float uvY, int normal,
            BlockTexture.FaceTexture texture, byte light) {
        verts[0].addVert(packFirstInt(x, y, (byte) normal, texture.animationLength),
                packSecondInt(z, uvX, uvY),
                packThirdInt(texture.id, light));
    }

    public void vertex(float x, float y, float z,
            float uvX, float uvY,
            BlockTexture.FaceTexture texture, byte light) {
        verts[0].addVert(packFirstInt(x, y, (byte) 0, texture.animationLength),
                packSecondInt(z, uvX, uvY),
                packThirdInt(texture.id, light));
    }

    public void vertex(float x, float y, float z,
            float uvX, float uvY, byte normal,
            int texture, byte animationLength, byte light) {
        verts[0].addVert(packFirstInt(x, y, normal, animationLength),
                packSecondInt(z, uvX, uvY),
                packThirdInt(texture, light));
    }

}
