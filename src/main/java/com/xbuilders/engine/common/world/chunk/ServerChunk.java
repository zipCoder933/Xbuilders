package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.engine.common.world.ServerWorld;
import com.xbuilders.engine.common.world.World;
import org.joml.Vector3i;

public class ServerChunk extends Chunk {

    public float distToPlayer;

    public ServerChunk(Vector3i position, FutureChunk futureChunk, ServerWorld world) {
        super(position, futureChunk, world);

    }

    public ServerChunk(Chunk other, Vector3i position, FutureChunk futureChunk, ServerWorld world) {
        super(other, position, futureChunk,world);
    }
}
