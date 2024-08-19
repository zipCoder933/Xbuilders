// 
// Decompiled by Procyon v0.5.36
// 

package com.xbuilders.game.terrain.complexTerrain;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.terrain.TreeUtils;

public class TerrainSod {

    private static short randomFlower(Terrain.GenSession session) {
        short block = 0;
        switch (session.random.nextInt(4)) {
            case 0 -> {
                block = MyGame.BLOCK_ROSES;
            }
            case 1 -> {
                block = MyGame.BLOCK_PANSIES;
            }
            case 2 -> {
                block = MyGame.BLOCK_AZURE_BLUET;
            }
            case 3 -> {
                block = MyGame.BLOCK_DANDELION;
            }
            default -> {
                block = MyGame.BLOCK_BLUE_ORCHID;
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
            GameScene.world.setBlock(MyGame.BLOCK_SAND, wx, wy, wz);
            if (terrain.noise.GetValueFractal(wx, wz) >= 0) {
                GameScene.world.setBlock(MyGame.BLOCK_SANDSTONE, wx, wy + 1, wz);
                GameScene.world.setBlock(MyGame.BLOCK_SANDSTONE, wx, wy + 2, wz);
                GameScene.world.setBlock(MyGame.BLOCK_SANDSTONE, wx, wy + 3, wz);
            } else {
                GameScene.world.setBlock(MyGame.BLOCK_GRAVEL, wx, wy + 1, wz);
                GameScene.world.setBlock(MyGame.BLOCK_GRAVEL, wx, wy + 2, wz);
                GameScene.world.setBlock(MyGame.BLOCK_GRAVEL, wx, wy + 3, wz);
            }
        } else if (biome == ComplexTerrain.BIOME_DESERT) {
            if (terrain.noise.GetValueFractal((float) (wx * 4), (float) (wz * 4)) < -0.25f) {
                GameScene.world.setBlock(MyGame.BLOCK_CLAY, wx, wy, wz);
                GameScene.world.setBlock(MyGame.BLOCK_RED_SANDSTONE, wx, wy + 1, wz);
                GameScene.world.setBlock(MyGame.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
                GameScene.world.setBlock(MyGame.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
            }
            if (terrain.noise.GetValueFractal((float) (wx / 2), (float) (wz / 2), 1000.0f) > 0.1) {
                GameScene.world.setBlock(MyGame.BLOCK_SAND, wx, wy, wz);
                GameScene.world.setBlock(MyGame.BLOCK_RED_SANDSTONE, wx, wy + 1, wz);
                GameScene.world.setBlock(MyGame.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
                GameScene.world.setBlock(MyGame.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
            }
            GameScene.world.setBlock(MyGame.BLOCK_RED_SAND, wx, wy, wz);
            GameScene.world.setBlock(MyGame.BLOCK_SANDSTONE, wx, wy + 1, wz);
            GameScene.world.setBlock(MyGame.BLOCK_RED_SANDSTONE, wx, wy + 2, wz);
            GameScene.world.setBlock(MyGame.BLOCK_RED_SANDSTONE, wx, wy + 3, wz);
        } else if (biome == ComplexTerrain.BIOME_SNOWY) {
            GameScene.world.setBlock(MyGame.BLOCK_SNOW_GRASS, wx, wy, wz);
            GameScene.world.setBlock(MyGame.BLOCK_DIRT, wx, wy + 1, wz);
            GameScene.world.setBlock(MyGame.BLOCK_DIRT, wx, wy + 2, wz);
        } else if (biome == ComplexTerrain.BIOME_SAVANNAH) {
            GameScene.world.setBlock(MyGame.BLOCK_DRY_GRASS, wx, wy, wz);
            GameScene.world.setBlock(MyGame.BLOCK_DIRT, wx, wy + 1, wz);
            GameScene.world.setBlock(MyGame.BLOCK_DIRT, wx, wy + 2, wz);
        } else if (biome == ComplexTerrain.BIOME_JUNGLE) {
            GameScene.world.setBlock(MyGame.BLOCK_JUNGLE_GRASS, wx, wy, wz);
            GameScene.world.setBlock(MyGame.BLOCK_DIRT, wx, wy + 1, wz);
            GameScene.world.setBlock(MyGame.BLOCK_DIRT, wx, wy + 2, wz);
        } else {
            GameScene.world.setBlock(MyGame.BLOCK_GRASS, wx, wy, wz);
            GameScene.world.setBlock(MyGame.BLOCK_DIRT, wx, wy + 1, wz);
            GameScene.world.setBlock(MyGame.BLOCK_DIRT, wx, wy + 2, wz);
            if (session.random.nextFloat() > 0.995) {
                TreeUtils.makeTree(session.random, session, wx, wy + 1, wz);
                session.generatedOutsideOfChunk = true;
            }
        }
    }

}
