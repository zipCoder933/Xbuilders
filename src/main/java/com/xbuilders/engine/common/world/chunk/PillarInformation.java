package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.engine.common.world.World;

public class PillarInformation {

    public final static int CHUNKS_IN_PILLAR = World.BOTTOM_Y_CHUNK - World.TOP_Y_CHUNK + 1;

    // This list includes the chunk itself
    public final Chunk[] chunks;
    public boolean pillarLightLoaded = false;

    public PillarInformation(Chunk[] pillarChunks) {
        //Setup occurs here
        this.chunks = pillarChunks;
        pillarLightLoaded = false;
    }

    public boolean isPillarAndAllNeghborsLoaded() {
        for (Chunk chunk : chunks) {
            chunk.neghbors.cacheNeighbors();
            if (!chunk.gen_terrainLoaded() || !chunk.neghbors.allNeghborsLoaded) {
                return false;
            }
        }
        return true;
    }

    public boolean isTopChunk(Chunk me){
        return getTopPillar() == me;
    }

    public Chunk getTopPillar() {
        return chunks[0];
    }

    public boolean isPillarLoaded() {
        for (Chunk chunk : chunks) {
            if (!chunk.gen_terrainLoaded()) {
                return false;
            }
        }
        return true;
    }
}
