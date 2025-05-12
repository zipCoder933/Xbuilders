package com.xbuilders.engine.common.world;

import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.common.world.chunk.FutureChunk;
import com.xbuilders.engine.common.world.chunk.ServerChunk;
import org.joml.Vector3i;

public class ServerWorld extends World<ServerChunk> {

    /**
     * For a local server, we just want to share unused chunks for memory manegment
     */
    public ServerWorld(ClientWorld otherWorld) {
        this.data = new WorldData(otherWorld.data); //Everything except for the chunks is its own instance
    }

    @Override
    protected ServerChunk createChunk(Chunk recycleChunk, final Vector3i coords, FutureChunk futureChunk, float distToPlayer) {
        if (recycleChunk != null) return new ServerChunk(recycleChunk, coords,  futureChunk, distToPlayer);
        else return new ServerChunk(coords,  futureChunk, distToPlayer, this);
    }
}
