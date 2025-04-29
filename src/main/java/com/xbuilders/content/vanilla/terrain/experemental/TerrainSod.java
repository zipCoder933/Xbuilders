// 
// Decompiled by Procyon v0.5.36
// 

package com.xbuilders.content.vanilla.terrain.experemental;

import com.xbuilders.content.vanilla.terrain.defaultTerrain.DefaultTerrain;
import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.common.world.Terrain;
import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.content.vanilla.terrain.TreeUtils;

public class TerrainSod {

    private static short randomFlower(Terrain.GenSession session) {
        short block = 0;
        switch (session.random.nextInt(4)) {
            case 0 -> {
                block = Blocks.BLOCK_ROSES;
            }
            case 1 -> {
                block = Blocks.BLOCK_PANSIES;
            }
            case 2 -> {
                block = Blocks.BLOCK_AZURE_BLUET;
            }
            case 3 -> {
                block = Blocks.BLOCK_DANDELION;
            }
            default -> {
                block = Blocks.BLOCK_BLUE_ORCHID;
            }
        }
        return block;
    }

    public static void placeSod(Terrain terrain, final Terrain.GenSession session, final Chunk chunk,
                                   final int cx, int cy, final int cz,
                                   final int wx, final int wy, final int wz,
                                   int biome,
                                   float valleyLikelyhood, float dryness, float make) {

        if (biome == DefaultTerrain.BIOME_BEACH) {
            Client.world.setBlock(Blocks.BLOCK_SAND, wx, wy, wz);
            if (terrain.fastNoise.GetValueFractal(wx, wz) >= 0) {
                Client.world.setBlock(Blocks.BLOCK_SANDSTONE, wx, wy + 1, wz);
                Client.world.setBlock(Blocks.BLOCK_SANDSTONE, wx, wy + 2, wz);
                Client.world.setBlock(Blocks.BLOCK_SANDSTONE, wx, wy + 3, wz);
            } else {
                Client.world.setBlock(Blocks.BLOCK_GRAVEL, wx, wy + 1, wz);
                Client.world.setBlock(Blocks.BLOCK_GRAVEL, wx, wy + 2, wz);
                Client.world.setBlock(Blocks.BLOCK_GRAVEL, wx, wy + 3, wz);
            }
        } else if (biome == DefaultTerrain.BIOME_DESERT) {
            if (terrain.fastNoise.GetValueFractal((float) (wx * 4), (float) (wz * 4)) < -0.25f) {
                Client.world.setBlock(Blocks.BLOCK_CLAY, wx, wy, wz);
                Client.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 1, wz);
                Client.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
                Client.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
            }
            if (terrain.fastNoise.GetValueFractal((float) (wx / 2), (float) (wz / 2), 1000.0f) > 0.1) {
                Client.world.setBlock(Blocks.BLOCK_SAND, wx, wy, wz);
                Client.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 1, wz);
                Client.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
                Client.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
            }
            Client.world.setBlock(Blocks.BLOCK_RED_SAND, wx, wy, wz);
            Client.world.setBlock(Blocks.BLOCK_SANDSTONE, wx, wy + 1, wz);
            Client.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
            Client.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
        } else if (biome == DefaultTerrain.BIOME_SNOWY) {
            Client.world.setBlock(Blocks.BLOCK_SNOW_GRASS, wx, wy, wz);
            Client.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 1, wz);
            Client.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 2, wz);
        } else if (biome == DefaultTerrain.BIOME_SAVANNAH) {
            Client.world.setBlock(Blocks.BLOCK_DRY_GRASS, wx, wy, wz);
            Client.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 1, wz);
            Client.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 2, wz);
        } else if (biome == DefaultTerrain.BIOME_JUNGLE) {
            Client.world.setBlock(Blocks.BLOCK_JUNGLE_GRASS, wx, wy, wz);
            Client.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 1, wz);
            Client.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 2, wz);
        } else {
            Client.world.setBlock(Blocks.BLOCK_GRASS, wx, wy, wz);
            Client.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 1, wz);
            Client.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 2, wz);
            if (session.random.nextFloat() > 0.995) {
                TreeUtils.makeTree(session.random, session, wx, wy + 1, wz);
                session.generatedOutsideOfChunk = true;
            }
        }
    }

}
