package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.WorldData;
import org.joml.Vector3i;

public class ClientChunk extends Chunk {

    public ClientChunk(Vector3i position, boolean isTopChunk, FutureChunk futureChunk, float distToPlayer, WorldData info, Terrain terrain) {
        super(position, isTopChunk, futureChunk, distToPlayer, info, terrain);
    }

    public ClientChunk(Vector3i position, boolean isTopChunk, FutureChunk futureChunk, float distToPlayer, Chunk other) {
        super(position, isTopChunk, futureChunk, distToPlayer, other);
    }
}
