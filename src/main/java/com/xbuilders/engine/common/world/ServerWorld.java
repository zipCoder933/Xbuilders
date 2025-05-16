package com.xbuilders.engine.common.world;

import com.xbuilders.Main;
import com.xbuilders.engine.common.packets.ChunkDataPacket;
import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.common.world.chunk.FutureChunk;
import com.xbuilders.engine.common.world.chunk.ServerChunk;
import org.joml.Vector3i;

public class ServerWorld extends World<ServerChunk> {

    /**
     * For a local server, we just want to share unused chunks for memory manegment
     */
    public ServerWorld(ClientWorld otherWorld) {
        this.setData(new WorldData(otherWorld.getData())); //Everything except for the chunks is its own instance
    }

    @Override
    protected ServerChunk internal_createChunkObject(Chunk recycleChunk, final Vector3i coords, FutureChunk futureChunk) {
        if (recycleChunk != null) return new ServerChunk(recycleChunk, coords, futureChunk, this);
        else return new ServerChunk(coords, futureChunk, this);
    }

    public ServerChunk addChunk(final Vector3i coords) {
        ServerChunk chunk = super.addChunk(coords);
        if (chunk != null) {

            chunk.loadFuture = generationService.submit(chunk.distToPlayer, () -> {
                try {
                    System.out.println("Chunk Blocks...");
                    chunk.loadBlocksAndLight(futureChunks.remove(coords));

                    System.out.println("Sending chunk...");
                    Main.getServer().writeAndFlushToAllPlayers(new ChunkDataPacket(chunk, Main.getServer().runningLocally()));
                    return false;
                } finally {
                    newGameTasks.incrementAndGet();
                }
            });
        }
        return chunk;
    }

}
