package com.xbuilders.engine.common.world.chunk.pillar;

import com.xbuilders.engine.common.utils.BFS.ChunkNode;
import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.World;
import com.xbuilders.engine.common.world.WorldData;
import com.xbuilders.engine.common.world.chunk.Chunk;

import java.util.ArrayList;

import static com.xbuilders.engine.common.world.World.generationService;
import static com.xbuilders.engine.common.world.World.lightService;

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

    // TODO: We need to change the lighting algorithm to generate sunlight from the
    // top chunk downward instead of letting each chunk generate its own sunlight
    // We are shifting gears from making an infinite world height, to instead,
    // making the world height finite to accomindate Xbuilders 2 world height and
    // chunk configuration
    // We are not making super chunks of any kind, we just want to limit the world
    // boundaries and tell the top chunk that it is the top chunk, so that it knows
    // to generate its own sunlight
    // Also, we need to check the entire pillar before generating sunlight.
    public void initLighting(ArrayList<ChunkNode> queue, Terrain terrain, float dist) {
        try {
            lightService.submit(dist, () -> {
                // System.err.println("Started loading sunlight");
                // World.frameTester.startProcess();
                ChunkSunlightGenerator.generateSunlight(chunks[0]);
                // Generate all meshes
                for (Chunk c : chunks) {
                    if (c.getGenerationStatus() >= Chunk.GEN_TERRAIN_LOADED)
                        c.setGenerationStatus(Chunk.GEN_SUN_LOADED);
                }
                World.newGameTasks.incrementAndGet();
                // int timeMS = World.frameTester.endProcess("green Generate SUNLIGHT");
                // System.out.println("Elapsed sun gen MS: " + timeMS);
                // System.err.println("Done.");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadChunks(Terrain terrain, WorldData info) {
        generationService.submit(chunks[0].client_distToPlayer, () -> {
            for (Chunk c : chunks) {
                c.loadChunk(null);
            }
        });
    }
}
