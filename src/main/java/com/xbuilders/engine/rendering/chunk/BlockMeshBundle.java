/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.rendering.chunk.mesh.CompactMesh;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.TraditionalVertexSet;
import com.xbuilders.engine.rendering.chunk.meshers.NaiveMesher;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.window.render.Shader;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
public class BlockMeshBundle {


    final TraditionalVertexSet buffer = new TraditionalVertexSet();
    final TraditionalVertexSet transBuffer = new TraditionalVertexSet();
    public final CompactMesh opaqueMesh, transMesh;

    private NaiveMesher naiveMesher;

    public BlockMeshBundle() {
        opaqueMesh = new CompactMesh();
        opaqueMesh.setTextureID(ItemList.blocks.textures.getTexture().id);
        transMesh = new CompactMesh();
        transMesh.setTextureID(ItemList.blocks.textures.getTexture().id);

    }

    public synchronized void init() {
        opaqueMesh.makeEmpty();
        transMesh.makeEmpty();
    }


    public void draw(BlockShader shader) {
        opaqueMesh.draw(shader, true);
        transMesh.draw(shader, true);
    }

    public synchronized void compute(ChunkVoxels voxels) {
        try {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                buffer.reset();
                transBuffer.reset();
                naiveMesher = new NaiveMesher(voxels, new Vector3i(0, 0, 0), true);
                naiveMesher.compute(buffer, buffer, stack, 1, false);

                if (buffer.size() != 0) {
                    buffer.makeVertexSet();
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
        buffer.sendToMesh(opaqueMesh);
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
