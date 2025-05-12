package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.engine.common.world.World;
import org.joml.Vector3i;

public class ServerChunk extends Chunk {

    public ServerChunk(Vector3i position, FutureChunk futureChunk, float distToPlayer, World world) {
        super(position, futureChunk, distToPlayer, world);
    }

    public ServerChunk(Chunk other, Vector3i position, FutureChunk futureChunk, float distToPlayer) {
        super(other, position, futureChunk, distToPlayer);
    }
}
