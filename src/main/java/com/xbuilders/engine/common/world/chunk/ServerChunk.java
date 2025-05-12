package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.World;
import com.xbuilders.engine.common.world.WorldData;
import org.joml.Vector3i;

public class ServerChunk extends Chunk{

    public ServerChunk(Vector3i position, boolean isTopChunk, FutureChunk futureChunk, float distToPlayer, World world) {
        super(position, isTopChunk, futureChunk, distToPlayer, world);
    }

    public ServerChunk(Chunk other, Vector3i position, boolean isTopChunk, FutureChunk futureChunk, float distToPlayer) {
        super(other, position, isTopChunk, futureChunk, distToPlayer);
    }
}
