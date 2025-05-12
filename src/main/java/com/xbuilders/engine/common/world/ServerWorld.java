package com.xbuilders.engine.common.world;

import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.common.world.chunk.ClientChunk;
import com.xbuilders.engine.common.world.chunk.FutureChunk;
import org.joml.Vector3i;

public class ServerWorld extends World {

    /**
     * For a local server, we just want to share unused chunks for memory manegment
     */
    public ServerWorld(ClientWorld otherWorld) {
        this.data = new WorldData(otherWorld.data); //Everything except for the chunks is its own instance
    }

    @Override
    protected Chunk createChunk(Chunk recycleChunk, final Vector3i coords, boolean isTopLevel, FutureChunk futureChunk, float distToPlayer) {
        if (recycleChunk != null) return new Chunk(coords, isTopLevel, futureChunk, distToPlayer, recycleChunk);
        else return new Chunk(coords, isTopLevel, futureChunk, distToPlayer, data, terrain);
    }
}
