/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.rendering.chunk.mesh.CompactMesh;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.TraditionalVertexSet;
import com.xbuilders.engine.rendering.chunk.withBakedLight.GreedyMesherWithLight;
import com.xbuilders.engine.rendering.chunk.withBakedLight.NaiveMesherWithLight;
import com.xbuilders.engine.rendering.chunk.withoutBakedLight.GreedyMesher;
import com.xbuilders.engine.rendering.chunk.withoutBakedLight.NaiveMesher;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zipCoder933
 */
public class BlockMeshBundle {


    final TraditionalVertexSet opaqueBuffer = new TraditionalVertexSet();
    final TraditionalVertexSet transBuffer = new TraditionalVertexSet();


    static final NaiveMesher naiveMesher = new NaiveMesher(true);
//    static final GreedyMesher greedyMesher = new GreedyMesher();

    public final CompactMesh opaqueMesh, transMesh;

    public BlockMeshBundle() {
        opaqueMesh = new CompactMesh();
        opaqueMesh.setTextureID(ItemList.blocks.textures.getTexture().id);
        transMesh = new CompactMesh();
        transMesh.setTextureID(ItemList.blocks.textures.getTexture().id);
    }

    public synchronized void init() {
        opaqueMesh.reset();
        transMesh.reset();
    }


    public synchronized void compute(ChunkVoxels voxels) {
        try {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                opaqueBuffer.reset();
                transBuffer.reset();

                naiveMesher.compute(voxels, opaqueBuffer, transBuffer, new Vector3i(0, 0, 0));
//                greedyMesher.compute(voxels, opaqueBuffer, transBuffer, new Vector3i(0, 0, 0), stack, 1);

                if (opaqueBuffer.size() != 0) {
                    opaqueBuffer.makeVertexSet();
                }
                if (transBuffer.size() != 0) {
                    transBuffer.makeVertexSet();
                }
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
