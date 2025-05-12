package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.WorldData;

public class ClientChunk extends Chunk {
    /**
     * A chunk is a reusable class
     *
     * @param info
     * @param terrain
     */
    public ClientChunk(WorldData info, Terrain terrain) {
        super(info, terrain);
    }
}
