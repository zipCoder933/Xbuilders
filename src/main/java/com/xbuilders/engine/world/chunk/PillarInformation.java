package com.xbuilders.engine.world.chunk;

import com.xbuilders.engine.world.DistanceScheduledExecutor.PriorityCallable;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.WorldInfo;

import static com.xbuilders.engine.world.World.generationService;
import static com.xbuilders.engine.world.World.lightService;

public class PillarInformation {
    public final static int CHUNKS_IN_PILLAR = World.BOTTOM_Y_CHUNK - World.TOP_Y_CHUNK + 1;


    //This list includes the chunk itself
    Chunk[] chunks;

    public PillarInformation(Chunk[] pillarChunks) {
        this.chunks = pillarChunks;
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

    public boolean isPillarLoaded() {
        for (Chunk chunk : chunks) {
            chunk.neghbors.cacheNeighbors();
            if (!chunk.gen_terrainLoaded()) {
                return false;
            }
        }
        return true;
    }


    //TODO: We need to change the lighting algorithm to generate sunlight from the top chunk downward instead of letting each chunk generate its own sunlight
    //We are shifting gears from making an infinite world height, to instead, making the world height finite to accomindate Xbuilders 2 world height and chunk configuration
    //We are not making super chunks of any kind, we just want to limit the world boundaries and tell the top chunk that it is the top chunk, so that it knows to generate its own sunlight
    //Also, we need to check the entire pillar before generating sunlight.
    public void initLighting(Terrain terrain, float dist) {
        try {
            lightService.submit(dist, () -> {
                ChunkSunlightUtils.generateSunlight(chunks[0], terrain);
                //Generate all meshes
                for (Chunk c : chunks) {
                    c.generationStatus = Chunk.GEN_SUN_LOADED;
                }
                World.newGameTasks.incrementAndGet();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadChunks(Terrain terrain, WorldInfo info) {
        generationService.submit(chunks[0].distToPlayer, () -> {
            for (Chunk c : chunks) {
                c.loadChunk(info, terrain, null);
            }
        });
    }
}

