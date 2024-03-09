// 
// Decompiled by Procyon v0.5.36
// 

package com.xbuilders.game.terrain.defaultTerrain;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.MyGame;

public class TerrainSod {

    private static  Block randomFlower(Terrain.GenSession session) {
        Block block = null;
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
                                ComplexTerrain.Biome biome,
                                float valleyLikelyhood, float dryness, float make) {

        if (biome == ComplexTerrain.Biome.BEACH) {
            GameScene.world.setBlock(wx, wy, wz, MyGame.BLOCK_SAND.id);
            if (terrain.noise.noise(wx, wz) >= 0) {
                GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_SANDSTONE.id);
                GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_SANDSTONE.id);
                GameScene.world.setBlock(wx, wy + 3, wz, MyGame.BLOCK_SANDSTONE.id);
            } else {
                GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_GRAVEL.id);
                GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_GRAVEL.id);
                GameScene.world.setBlock(wx, wy + 3, wz, MyGame.BLOCK_GRAVEL.id);
            }
        } else if (biome == ComplexTerrain.Biome.DESERT) {
            if (terrain.noise.noise((float) (wx * 4), (float) (wz * 4)) < -0.25f) {
                GameScene.world.setBlock(wx, wy, wz, MyGame.BLOCK_CLAY.id);
                GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_RED_SANDSTONE.id);
                GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_RED_SANDSTONE.id);
                GameScene.world.setBlock(wx, wy + 3, wz, MyGame.BLOCK_RED_SANDSTONE.id);
            }
            if (terrain.noise.noise((float) (wx / 2), (float) (wz / 2), 1000.0f) > 0.1) {
                GameScene.world.setBlock(wx, wy, wz, MyGame.BLOCK_SAND.id);
                GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_RED_SANDSTONE.id);
                GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_RED_SANDSTONE.id);
                GameScene.world.setBlock(wx, wy + 3, wz, MyGame.BLOCK_RED_SANDSTONE.id);
            }
            GameScene.world.setBlock(wx, wy, wz, MyGame.BLOCK_RED_SAND.id);
            GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_SANDSTONE.id);
            GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_RED_SANDSTONE.id);
            GameScene.world.setBlock(wx, wy + 3, wz, MyGame.BLOCK_RED_SANDSTONE.id);
        } else if (biome == ComplexTerrain.Biome.SNOWY) {
            GameScene.world.setBlock(wx, wy, wz, MyGame.BLOCK_SNOW.id);
            GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_DIRT.id);
            GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_DIRT.id);
        } else if (biome == ComplexTerrain.Biome.SAVANNAH) {
            GameScene.world.setBlock(wx, wy, wz, MyGame.BLOCK_DRY_GRASS.id);
            GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_DIRT.id);
            GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_DIRT.id);
        } else if (biome == ComplexTerrain.Biome.JUNGLE) {
            GameScene.world.setBlock(wx, wy, wz, MyGame.BLOCK_JUNGLE_GRASS.id);
            GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_DIRT.id);
            GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_DIRT.id);
        } else {
            GameScene.world.setBlock(wx, wy, wz, MyGame.BLOCK_GRASS.id);
            GameScene.world.setBlock(wx, wy + 1, wz, MyGame.BLOCK_DIRT.id);
            GameScene.world.setBlock(wx, wy + 2, wz, MyGame.BLOCK_DIRT.id);
        }
    }

}
