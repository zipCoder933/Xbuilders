package com.xbuilders.engine.common.world;

import com.xbuilders.engine.common.world.chunk.Chunk;

public class ServerWorld extends World {

    /**
     * For a local server, we just want to share unused chunks for memory manegment
     */
    public ServerWorld(ClientWorld otherWorld) {
        this.unusedChunks = otherWorld.unusedChunks;
        this.data = new WorldData(otherWorld.data); //Everything except for the chunks is its own instance
    }

    @Override
    protected Chunk createChunk() {
        return new Chunk(data, terrain);
    }
}
