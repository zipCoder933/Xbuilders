package com.xbuilders.engine.items.block.construction.BlockTypeModel;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.mesh.BufferSet;

import java.util.function.Consumer;

public class ModelSide {
    public final ModelVertex[] vertices;
    public int textureSide = 0;

    public ModelSide(int vertexCount) {
        vertices = new ModelVertex[vertexCount];
    }


    public void render(int x, int y, int z, BufferSet buff, BlockTexture.FaceTexture texture) {
//System.out.println("RENDERING SIDE: " + texture+", "+vertices.length);
        for (int i = 0; i < vertices.length; i++) {
            ModelVertex vertex = vertices[i];
            buff.vertex(vertex.position.x + x, vertex.position.y + y, vertex.position.z + z, vertex.u, vertex.v, texture);
        }
    }
}
