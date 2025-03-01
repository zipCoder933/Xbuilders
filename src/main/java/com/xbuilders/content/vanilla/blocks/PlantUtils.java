package com.xbuilders.content.vanilla.blocks;

import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.content.vanilla.terrain.complexTerrain.ComplexTerrain;
import com.xbuilders.engine.server.item.ItemStack;
import com.xbuilders.engine.server.loot.AllLootTables;
import com.xbuilders.engine.server.loot.LootTableRegistry;
import com.xbuilders.engine.server.loot.block.BlockLootRegistry;
import org.joml.Vector3f;

public class PlantUtils {


    public static void addPlantGrowthEvents(final Block... stages) {
        for (int i = 0; i < stages.length - 1; i++) {
            Block b = stages[i];
            final int finalI = i;
            b.randomTickEvent = (x, y, z) -> {
                if (cropPlantable(x, y, z)) {
                    Server.setBlock(stages[finalI + 1].id, x, y, z);
                    return true;
                }
                return false;
            };
        }
    }

    public static boolean deepPlantable(final int x, final int y, final int z) {
        boolean val = blockIsGrassSnowOrDirt(Server.world.getBlock(x, y + 1, z))
                && blockIsGrassSnowOrDirt(Server.world.getBlock(x, y + 2, z));
        return val;
    }

    public static boolean cropPlantable(final int x, final int y, final int z) {
        return Server.world.getBlockID(x, y + 1, z) == Blocks.BLOCK_FARMLAND;
    }

    public static boolean plantable(final int x, final int y, final int z) {
        return blockIsGrassSnowOrDirt(Server.world.getBlock(x, y + 1, z));
    }

    public static boolean sandPlantable(final int x, final int y, final int z) {
        return blockIsSandOrGravel(Server.world.getBlock(x, y + 1, z));
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

    public static boolean blockIsSandOrGravel(Block block) {
        return block.id == Blocks.BLOCK_SAND
                || block.id == Blocks.BLOCK_RED_SAND
                || block.id == Blocks.BLOCK_GRAVEL;
    }

    public static short getGrassBlockOfBiome(int wx, int wy, int wz) {
        int biome = Server.world.terrain.getBiomeOfVoxel(wx, wy, wz);
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


    public static void makeStalk(Block stalk, Block sapling, int maxHeight) {
        stalk.randomTickEvent = (x, y, z) -> {
            return growStalk(x, y, z, stalk, sapling, maxHeight);
        };
        if (sapling != null) sapling.randomTickEvent = (x, y, z) -> {
            return growStalk(x, y, z, stalk, sapling, maxHeight);
        };

        //We have to manually remove blocks above the stalk and add item drops
        stalk.removeBlockEvent(false, ((x, y, z, history) -> {
            for (int i = 0; i < 50; i++) {
                Block block = Server.world.getBlock(x, y - i, z);
                if (block != null && block.id == stalk.id) {
                    //Remove the block
                    Server.setBlock(Blocks.BLOCK_AIR, x, y - i, z);

                    if (Server.getGameMode() == GameMode.ADVENTURE) {//Drop loot tables
                        final int blockY = y - i;
                        AllLootTables.blockLootTables.getLoot(stalk.alias).randomItems((itemStack) -> {
                            Server.placeItemDrop(new Vector3f(x, blockY, z), itemStack, false);
                        });
                    }
                }
            }
        }));
    }

    private static boolean growStalk(int x, int y, int z, Block stalkBlock, Block stalkSapling, int maxHeight) {
        if (sandPlantable(x, y, z)) {//If this is the bottom of a stalk
            for (int i = 0; i > -maxHeight; i--) {//Go up -20 blocks max
                Block stalk = Server.world.getBlock(x, y + i, z);
                if ((stalk != null && stalk.id == stalkBlock.id)) {//If this is a stalk
                } else if (stalk.isAir() || stalk.isLiquid() || (stalkSapling != null && stalk.id == stalkSapling.id)) {//If this is air, liquid or sapling
                    Server.setBlock(stalkBlock.id, x, y + i, z); //Set bamboo
                    return true;
                } else {//this is not a stalk or air
                    return false;
                }
            }
        }
        return false;
    }
}