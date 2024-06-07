package com.xbuilders.engine.rendering.chunk.meshers;

import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

public abstract class Mesher {

    public Mesher(ChunkVoxels voxels, Vector3i chunkPositionOffset) {
        this.chunkVoxels = voxels;
        this.chunkPosition = chunkPositionOffset;
    }

    public final ChunkVoxels chunkVoxels;
    public final Vector3i chunkPosition;

    public abstract void compute(
            VertexSet opaqueBuffers, VertexSet transparentBuffers,
            MemoryStack stack, int lodLevel, boolean smoothShading);
}
