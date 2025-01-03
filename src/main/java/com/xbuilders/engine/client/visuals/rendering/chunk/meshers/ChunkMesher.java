package com.xbuilders.engine.client.visuals.rendering.chunk.meshers;

import com.xbuilders.engine.client.visuals.rendering.VertexSet;
import com.xbuilders.engine.server.model.world.chunk.ChunkVoxels;
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
