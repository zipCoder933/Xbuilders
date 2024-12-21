package com.xbuilders.content.vanilla.items.blocks;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.content.vanilla.items.Blocks;
import com.xbuilders.content.vanilla.terrain.complexTerrain.ComplexTerrain;

public class PlantUtils {


    public static void addPlantGrowthEvents(final Block... stages) {
        for (int i = 0; i < stages.length - 1; i++) {
            Block b = stages[i];
            final int finalI = i;
            b.randomTickEvent = (x, y, z) -> {
                if (cropPlantable(x, y, z)) {
                    GameScene.setBlock(stages[finalI + 1].id, x, y, z);
                    return true;
                }
                return false;
            };
        }
    }

    public static boolean deepPlantable(final int x, final int y, final int z) {
        boolean val = blockIsGrassSnowOrDirt(GameScene.world.getBlock(x, y + 1, z))
                && blockIsGrassSnowOrDirt(GameScene.world.getBlock(x, y + 2, z));
        return val;
    }

    public static boolean cropPlantable(final int x, final int y, final int z) {
        return GameScene.world.getBlockID(x, y + 1, z) == Blocks.BLOCK_FARMLAND;
    }

    public static boolean plantable(final int x, final int y, final int z) {
        return blockIsGrassSnowOrDirt(GameScene.world.getBlock(x, y + 1, z));
    }

    public static boolean isGrass(short thisBlock) {
        return thisBlock == Blocks.BLOCK_GRASS ||
                thisBlock == Blocks.BLOCK_SNOW_GRASS ||
                thisBlock == Blocks.BLOCK_JUNGLE_GRASS ||
                thisBlock == Blocks.BLOCK_DRY_GRASS;
    }

    public static boolean blockIsGrassSnowOrDirt(Block block) {
        return block.id == Blocks.BLOCK_FARMLAND
                || block.id == Blocks.BLOCK_DIRT
                || isGrass(block.id);
    }

    public static short getGrassBlockOfBiome(int wx, int wy, int wz) {
        int biome = GameScene.world.terrain.getBiomeOfVoxel(wx, wy, wz);
        switch (biome) {
            case ComplexTerrain.BIOME_SNOWY -> {
                return Blocks.BLOCK_SNOW_GRASS;
            }
            case ComplexTerrain.BIOME_JUNGLE -> {
                return Blocks.BLOCK_JUNGLE_GRASS;
            }
            case ComplexTerrain.BIOME_SAVANNAH, ComplexTerrain.BIOME_DESERT -> {
                return Blocks.BLOCK_DRY_GRASS;
            }
            default -> {
                return Blocks.BLOCK_GRASS;
            }
        }
    }


}