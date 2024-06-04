/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.TraditionalVertexSet;
import com.xbuilders.engine.rendering.chunk.withBakedLight.GreedyMesherWithLight;
import com.xbuilders.engine.rendering.chunk.withBakedLight.NaiveMesherWithLight;
import com.xbuilders.engine.rendering.chunk.mesh.CompactMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

import com.xbuilders.game.Main;
import org.lwjgl.system.MemoryStack;

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

    final TraditionalVertexSet opaqueBuffer = new TraditionalVertexSet();
    final TraditionalVertexSet transBuffer = new TraditionalVertexSet();

    Chunk chunk;

    NaiveMesherWithLight naiveMesher; //These meshers are not thread safe. They should only be used to generate 1 mesh at a time
    GreedyMesherWithLight greedyMesher;
    public final CompactMesh opaqueMesh, transMesh;

    public ChunkMeshBundle(int texture, Chunk chunk) {
        this.chunk = chunk;
        opaqueMesh = new CompactMesh();
        opaqueMesh.setTextureID(texture);
        transMesh = new CompactMesh();
        transMesh.setTextureID(texture);

        greedyMesher = new GreedyMesherWithLight(chunk.data, chunk.position);
        naiveMesher = new NaiveMesherWithLight(chunk.data);
    }

    public synchronized void init() {
        opaqueMesh.makeEmpty();
        transMesh.makeEmpty();
    }

    public boolean meshesHaveAllSides;


    public synchronized void compute() {
        try {
            try (MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
                meshesHaveAllSides = chunk.neghbors.allFacingNeghborsLoaded;

                //We should guarantee that the buffers get sent to the mesh, because we determine
                //if a mesh is empty by the size of the verteces
                opaqueBuffer.reset();
                transBuffer.reset();

//                if(Main.devkeyF4) {
                    greedyMesher.compute(opaqueBuffer, transBuffer, stack, 1, true);
                    naiveMesher.compute(opaqueBuffer, transBuffer, chunk.position, false);
//                }

                opaqueBuffer.makeVertexSet(); //Buffer will automatically not make verteces if it is empty
                transBuffer.makeVertexSet();

            }
        } catch (Exception ex) {
            ErrorHandler.saveErrorToLogFile(ex);
            ex.printStackTrace();
        }
    }

    public synchronized void sendToGPU() {
        opaqueBuffer.sendToMesh(opaqueMesh);
        transBuffer.sendToMesh(transMesh);
    }

    public boolean isEmpty() {
        return opaqueMesh.isEmpty() && transMesh.isEmpty();
    }

    @Override
    public String toString() {
        return "ChunkMeshBundle{ \n" + "opaque=" + opaqueMesh + ",\n trans=" + transMesh + " }";
    }
}
