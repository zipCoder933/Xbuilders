package com.xbuilders.engine.rendering.entity.block.meshers;

import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

public abstract class BlockMesher<T extends VertexSet> {

    public BlockMesher(ChunkVoxels voxels, Vector3i chunkPositionOffset) {
        this.data = voxels;
        this.chunkPosition = chunkPositionOffset;
    }

    public final ChunkVoxels data;
    public final Vector3i chunkPosition;

    public abstract void compute(
            T opaqueBuffers, T transparentBuffers,
            MemoryStack stack, int lodLevel, boolean smoothShading);
}
