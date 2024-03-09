/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.world.chunk;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.mesh.BufferSet;
import com.xbuilders.engine.mesh.GreedyMesher;
import com.xbuilders.engine.mesh.meshes.CompactMesh;
import com.xbuilders.engine.mesh.NaiveMesher;
import com.xbuilders.engine.utils.ErrorHandler;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author zipCoder933
 */
public class MeshBundle {

    //<editor-fold defaultstate="collapsed" desc="LOD">
    private static int LOD_LEVEL;

    private static List<Integer> listFactors(int number) {
        List<Integer> factors = new ArrayList<>();
        // Include 1 and the number itself if it's greater than 1
        if (number > 1) {
            factors.add(1);
        }
        for (int i = 2; i <= number / 2; i++) {
            if (number % i == 0) {
                factors.add(i);
            }
        }
        if (number > 1) {
            factors.add(number);
        }
        return factors;
    }

    public static void setLOD(int level) {
        List<Integer> factors = listFactors(Chunk.WIDTH);
        //Find the closest factor, and set lodLevel to that
        boolean foundMatch = false;
        for (int i = 0; i < factors.size(); i++) {
            int factor = factors.get(i);
            if (factor >= level) {
                LOD_LEVEL = factor;
                foundMatch = true;
                break;
            }
        }
        //If the lod was larger than the chunk size, iterate again backwards
        if (!foundMatch) {
            for (int i = factors.size() - 1; i >= 0; i--) {
                int factor = factors.get(i);
                if (factor >= level) {
                    LOD_LEVEL = factor;
                    break;
                }
            }
        }
        System.out.println("Set LOD to: " + LOD_LEVEL);
    }

    public static int getLOD() {
        return LOD_LEVEL;
    }
    //</editor-fold>

    static {
        setLOD(4);
        System.out.println("#########################\n\nLOD: " + getLOD());
    }

    private IntBuffer opaqueBuffer, transBuffer;

    Chunk chunk;
    Vector3i position;
    NaiveMesher naiveMesher;
    GreedyMesher greedyMesher;
    public CompactMesh opaque, trans;

    public MeshBundle(int texture, Chunk chunk) {
        this.chunk = chunk;
        opaque = new CompactMesh();
        opaque.setTextureID(texture);
        trans = new CompactMesh();
        trans.setTextureID(texture);

        greedyMesher = new GreedyMesher(chunk.data, ItemList.blocks.getIdMap());
        naiveMesher = new NaiveMesher(chunk.data, ItemList.blocks.getIdMap(), false);
    }

    public synchronized void init(Vector3i position) {
        this.position = position;
        opaque.empty = true;
        trans.empty = true;
    }

    public boolean meshesHaveAllSides;
    final BufferSet buff = new BufferSet();
    final BufferSet transBuff = new BufferSet();

    public synchronized void compute() {
        try {
            try (MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
                meshesHaveAllSides = chunk.neghbors.allFacingNeghborsLoaded;

                buff.clear();
                transBuff.clear();

                greedyMesher.compute(buff, transBuff, position, stack, 1);
                naiveMesher.compute(buff, transBuff, position);

                opaque.empty = buff.isEmpty();
                trans.empty = transBuff.isEmpty();

                if (!buff.isEmpty()) opaqueBuffer = buff.makeVertexSet();
                if (!transBuff.isEmpty()) transBuffer = transBuff.makeVertexSet();
            }
        } catch (Exception ex) {
            ErrorHandler.handleFatalError(ex);
        }
    }

    public synchronized void sendToGPU() {
        if (opaqueBuffer != null) {
            opaque.sendBuffersToGPU(opaqueBuffer);
            MemoryUtil.memFree(opaqueBuffer);
        }
        if (transBuffer != null) {
            trans.sendBuffersToGPU(transBuffer);
            MemoryUtil.memFree(transBuffer);
        }
        opaqueBuffer = null;
        transBuffer = null;
    }

    public boolean isEmpty() {
        return opaque.empty && trans.empty;
    }
}
