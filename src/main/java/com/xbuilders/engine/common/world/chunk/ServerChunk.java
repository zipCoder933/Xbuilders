package com.xbuilders.engine.common.world.chunk;

import com.xbuilders.engine.common.world.ServerWorld;
import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.chunk.saving.ChunkSavingLoadingUtils;
import org.joml.Vector3i;

import java.io.File;
import java.util.logging.Level;

import static com.xbuilders.Main.LOGGER;

public class ServerChunk extends Chunk {

    public float distToPlayer;
    boolean sunGenerated = false;
    boolean terrainGenerated = false;

    public ServerChunk(Vector3i position, FutureChunk futureChunk, ServerWorld world) {
        super(position, futureChunk, world);

    }

    public ServerChunk(Chunk other, Vector3i position, FutureChunk futureChunk, ServerWorld world) {
        super(other, position, futureChunk, world);
    }


    public boolean gen_terrainLoaded() {
        return terrainGenerated;
    }

    public boolean gen_sunLoaded() {
        return sunGenerated;
    }


    public void loadBlocksAndLight(FutureChunk futureChunk) {
        File f = world.getData().getChunkFile(position);
        try {
            //Set blocks

            if (f.exists()) {
                ChunkSavingLoadingUtils.readChunkFromFile(this, f);
                sunGenerated = true;
            } else if (world.terrain.isBelowMinHeight(this.position, 0)) {
                Terrain.GenSession session = world.terrain.createTerrainOnChunk(this);
            }
            if (futureChunk != null) {
                futureChunk.setBlocksInChunk(this);
            }
            terrainGenerated = true;

            for (int x = 0; x < data.size.x; x++) {
                for (int y = 0; y < data.size.y; y++) {
                    for (int z = 0; z < data.size.z; z++) {
                        data.setSun(x, y, z, 15);
                    }
                }
            }


        } catch (Exception ex) {//For some reason we have to catch incoming errors otherwise they wont be visible
            LOGGER.log(Level.WARNING, "error loading chunk", ex);
        }
    }
}
