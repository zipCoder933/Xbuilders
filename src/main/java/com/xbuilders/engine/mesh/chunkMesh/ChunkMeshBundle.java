/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.mesh.chunkMesh;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.mesh.chunkMesh.withBakedLight.GreedyMesherWithLight;
import com.xbuilders.engine.mesh.chunkMesh.withBakedLight.NaiveMesherWithLight;
import com.xbuilders.engine.mesh.mesh.CompactMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.chunk.Chunk;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 * @author zipCoder933
 */
public class ChunkMeshBundle {

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
        //We want to find the closest factor of chunk.width to set to the LOD level
        List<Integer> factors = listFactors(Chunk.WIDTH);
        //Find the closest factor, and set lodLevel to that
        for (int i = 0; i < factors.size(); i++) {
            int factor = factors.get(i);
            if (factor >= level) {
                LOD_LEVEL = factor;
                break;
            }
        }
        System.out.println("Chunk LOD set to: " + LOD_LEVEL);
    }

    public static int getLOD() {
        return LOD_LEVEL;
    }
    //</editor-fold>

    static {
        setLOD(4);
    }

    private IntBuffer opaqueBuffer, transBuffer;
    Chunk chunk;
    Vector3i position;
    NaiveMesherWithLight naiveMesher;
    GreedyMesherWithLight greedyMesher;
    public final CompactMesh opaque, trans;

    public ChunkMeshBundle(int texture, Chunk chunk) {
        this.chunk = chunk;
        opaque = new CompactMesh();
        opaque.setTextureID(texture);
        trans = new CompactMesh();
        trans.setTextureID(texture);

        greedyMesher = new GreedyMesherWithLight(chunk.data, ItemList.blocks.getIdMap());
        naiveMesher = new NaiveMesherWithLight(chunk.data, ItemList.blocks.getIdMap(), false);
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

                opaque.empty = buff.size()==0;
                trans.empty = transBuff.size()==0;

                if (buff.size()!=0) {
                    opaqueBuffer = buff.makeVertexSet();
                }
                if (transBuff.size()!=0) {
                    transBuffer = transBuff.makeVertexSet();
                }
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
