/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.meshers;

import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.mesh.CompactMesh;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.mesh.CompactOcclusionMesh;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.meshers.bufferSet.vertexSet.TraditionalVertexSet;
import com.xbuilders.engine.client.visuals.gameScene.rendering.chunk.occlusionCulling.BoundingBoxMesh;
import com.xbuilders.engine.common.utils.ErrorHandler;
import com.xbuilders.engine.common.math.AABB;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.List;

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
    Terrain terrain;

    Chunk_NaiveMesher naiveMesher; //These meshers are not thread safe. They should only be used to generate 1 mesh at a time
    Chunk_GreedyMesherWithLight greedyMesher;
    public final BoundingBoxMesh boundMesh;
    public final CompactOcclusionMesh opaqueMesh;
    public final CompactMesh transMesh;

    public ChunkMeshBundle(int texture, Chunk chunk, Terrain terrain) {
        this.chunk = chunk;
        this.terrain = terrain;
        boundMesh = new BoundingBoxMesh();
        opaqueMesh = new CompactOcclusionMesh(boundMesh);
        opaqueMesh.setTextureID(texture);
        transMesh = new CompactMesh(); //We only need transparent mesh to be an occlusion If we have to check it if it is occluding the opaque mesh
        transMesh.setTextureID(texture);

        greedyMesher = new Chunk_GreedyMesherWithLight(chunk.data, chunk.position);
        naiveMesher = new Chunk_NaiveMesher(chunk, false);
    }

    public synchronized void init(AABB bounds) {
        opaqueMesh.makeEmpty();
        transMesh.makeEmpty();
        boundMesh.setBounds(bounds);
    }

    public boolean meshesHaveAllSides;


    //This compute function is thread safe
    public synchronized void compute() {
        try {
            try (MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
                meshesHaveAllSides = chunk.neghbors.allFacingNeghborsLoaded;

                //Check if the blocks of this chunk or its neighbors are empty
                boolean blocksAreEmpty = chunk.data.blocksAreEmpty;
                if (blocksAreEmpty) for (int i = 0; i < 6; i++) {
                    if (chunk.neghbors.neighbors[i] != null &&
                            !chunk.neghbors.neighbors[i].data.blocksAreEmpty) {
                        blocksAreEmpty = false;
                        break;
                    }
                }

                //We should guarantee that the buffers get sent to the mesh, because we determine
                //if a mesh is empty by the size of the verteces
                opaqueBuffer.reset();
                transBuffer.reset();

                if (!blocksAreEmpty) {//We wont check if we are below terrain because a loaded file chunk could be there
                    UseGM_BooleanBuffer buffer = new UseGM_BooleanBuffer(stack);//Allocate the boolean buffer on the stack
                    naiveMesher.buffer_shouldUseGreedyMesher = buffer;
                    naiveMesher.compute(opaqueBuffer, transBuffer, stack, 1, true); //This contributes as well, but im saving it for later since it plays a small role in memory when not generating the whole mesh

                    greedyMesher.buffer_shouldUseGreedyMesher = buffer;
                    greedyMesher.compute(opaqueBuffer, transBuffer, stack, 1, true);
                }

                opaqueBuffer.makeVertexSet(); //Buffer wont make verteces if it is empty
                transBuffer.makeVertexSet();

            }
        } catch (Exception ex) {
            ErrorHandler.log(ex);
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
