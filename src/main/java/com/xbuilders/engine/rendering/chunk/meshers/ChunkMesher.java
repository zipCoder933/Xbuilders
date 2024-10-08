package com.xbuilders.engine.rendering.chunk.meshers;

import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import org.joml.Vector3i;

public abstract class ChunkMesher<T extends VertexSet> {

    public ChunkMesher(ChunkVoxels voxels, Vector3i chunkPositionOffset) {
        this.data = voxels;
        this.chunkPosition = chunkPositionOffset;
    }

   UseGM_BooleanBuffer buffer_shouldUseGreedyMesher;
    public final ChunkVoxels data;
    public final Vector3i chunkPosition;
}
