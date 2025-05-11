package com.xbuilders.engine.common.world;

public class ServerWorld extends World {

    /**
     * For a local server, we just want to share unused chunks for memory manegment
     */
    public ServerWorld(ClientWorld otherWorld) {
        this.unusedChunks = otherWorld.unusedChunks;
        this.data = new WorldData(otherWorld.data); //Everything except for the chunks is its own instance
    }

}
