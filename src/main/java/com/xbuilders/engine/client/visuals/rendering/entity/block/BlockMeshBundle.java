/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.rendering.entity.block;

import com.xbuilders.engine.game.model.items.Registrys;
import com.xbuilders.engine.client.visuals.rendering.entity.block.meshers.Block_NaiveMesher;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh_ArrayTexture;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.game.model.world.chunk.ChunkVoxels;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
public class BlockMeshBundle {


    /**
     * The chunk shader only suports a mesh of size 32 or less
     */

    final BlockVertexSet buffer = new BlockVertexSet();
    final BlockVertexSet transBuffer = new BlockVertexSet();
    public final EntityMesh_ArrayTexture opaqueMesh, transMesh;
    public final int texture;
    private Block_NaiveMesher naiveMesher;

    public BlockMeshBundle() {
        opaqueMesh = new EntityMesh_ArrayTexture();
        texture = (Registrys.blocks.textures.getTexture().id);
        transMesh = new EntityMesh_ArrayTexture();

    }

    public synchronized void init() {
        opaqueMesh.reset();
        transMesh.reset();
    }

    public void draw() {
        opaqueMesh.draw(true, texture);
        transMesh.draw(true, texture);
    }

    public synchronized void compute(ChunkVoxels voxels) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            buffer.reset();
            transBuffer.reset();
            naiveMesher = new Block_NaiveMesher(voxels, new Vector3i(0, 0, 0), true);
            naiveMesher.compute(buffer, buffer, stack, 1, false);

            if (buffer.size() != 0) {
                buffer.makeVertexSet();
            }
            if (transBuffer.size() != 0) {
                transBuffer.makeVertexSet();
            }
        } catch (Exception ex) {
            ErrorHandler.log(ex);
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
