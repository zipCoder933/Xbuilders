// 
// Decompiled by Procyon v0.5.36
// 

package com.xbuilders.content.vanilla.terrain.complexTerrain;

import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.world.Terrain;
import com.xbuilders.engine.server.model.world.chunk.Chunk;
import com.xbuilders.content.vanilla.items.Blocks;
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

        if (biome == ComplexTerrain.BIOME_BEACH) {
            GameScene.world.setBlock(Blocks.BLOCK_SAND, wx, wy, wz);
            if (terrain.noise.GetValueFractal(wx, wz) >= 0) {
                GameScene.world.setBlock(Blocks.BLOCK_SANDSTONE, wx, wy + 1, wz);
                GameScene.world.setBlock(Blocks.BLOCK_SANDSTONE, wx, wy + 2, wz);
                GameScene.world.setBlock(Blocks.BLOCK_SANDSTONE, wx, wy + 3, wz);
            } else {
                GameScene.world.setBlock(Blocks.BLOCK_GRAVEL, wx, wy + 1, wz);
                GameScene.world.setBlock(Blocks.BLOCK_GRAVEL, wx, wy + 2, wz);
                GameScene.world.setBlock(Blocks.BLOCK_GRAVEL, wx, wy + 3, wz);
            }
        } else if (biome == ComplexTerrain.BIOME_DESERT) {
            if (terrain.noise.GetValueFractal((float) (wx * 4), (float) (wz * 4)) < -0.25f) {
                GameScene.world.setBlock(Blocks.BLOCK_CLAY, wx, wy, wz);
                GameScene.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 1, wz);
                GameScene.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
                GameScene.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
            }
            if (terrain.noise.GetValueFractal((float) (wx / 2), (float) (wz / 2), 1000.0f) > 0.1) {
                GameScene.world.setBlock(Blocks.BLOCK_SAND, wx, wy, wz);
                GameScene.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 1, wz);
                GameScene.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
                GameScene.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
            }
            GameScene.world.setBlock(Blocks.BLOCK_RED_SAND, wx, wy, wz);
            GameScene.world.setBlock(Blocks.BLOCK_SANDSTONE, wx, wy + 1, wz);
            GameScene.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
            GameScene.world.setBlock(Blocks.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
        } else if (biome == ComplexTerrain.BIOME_SNOWY) {
            GameScene.world.setBlock(Blocks.BLOCK_SNOW_GRASS, wx, wy, wz);
            GameScene.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 1, wz);
            GameScene.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 2, wz);
        } else if (biome == ComplexTerrain.BIOME_SAVANNAH) {
            GameScene.world.setBlock(Blocks.BLOCK_DRY_GRASS, wx, wy, wz);
            GameScene.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 1, wz);
            GameScene.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 2, wz);
        } else if (biome == ComplexTerrain.BIOME_JUNGLE) {
            GameScene.world.setBlock(Blocks.BLOCK_JUNGLE_GRASS, wx, wy, wz);
            GameScene.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 1, wz);
            GameScene.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 2, wz);
        } else {
            GameScene.world.setBlock(Blocks.BLOCK_GRASS, wx, wy, wz);
            GameScene.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 1, wz);
            GameScene.world.setBlock(Blocks.BLOCK_DIRT, wx, wy + 2, wz);
            if (session.random.nextFloat() > 0.995) {
                TreeUtils.makeTree(session.random, session, wx, wy + 1, wz);
                session.generatedOutsideOfChunk = true;
            }
        }
    }

}
