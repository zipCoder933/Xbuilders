package com.xbuilders.content.vanilla.blocks;

import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.content.vanilla.Blocks;
import com.xbuilders.content.vanilla.terrain.complexTerrain.ComplexTerrain;
import com.xbuilders.engine.server.block.BlockRegistry;
import com.xbuilders.engine.server.loot.AllLootTables;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class PlantBlockUtils {
    public final List<Short> snowyDefaultGrowth = new ArrayList<>();
    //    public final List<Short> grass = new ArrayList<>();
    Random random = new Random();

    public PlantBlockUtils() {
        //Reeds
        snowyDefaultGrowth.add(Blocks.BLOCK_GRASS_PLANT);
        snowyDefaultGrowth.add(Blocks.BLOCK_FERN);
        snowyDefaultGrowth.add(Blocks.BLOCK_DEAD_BUSH);
        snowyDefaultGrowth.add(Blocks.BLOCK_PANSIES);
        snowyDefaultGrowth.add(Blocks.BLOCK_ROSES);
        snowyDefaultGrowth.add(Blocks.BLOCK_DANDELION);
        snowyDefaultGrowth.add(Blocks.BLOCK_AZURE_BLUET);
        snowyDefaultGrowth.add(Blocks.BLOCK_ORANGE_TULIP);
        System.out.println("SnowDefaultGrowth: " + snowyDefaultGrowth.toString());
    }

    public void addDecayTickEvent(Block block) {
        if (block == null) return;
        block.randomTickEvent = (x, y, z) -> {
//            System.out.println("Decaying " + block.id + " at " + x + ", " + y + ", " + z);
            Server.setBlock(Blocks.BLOCK_AIR, x, y, z);
            return true;
        };
    }

    public void addPlantGrowthEvents(final Block... stages) {
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

    public boolean deepPlantable(final int x, final int y, final int z) {
        boolean val = blockIsGrassSnowOrDirt(Server.world.getBlock(x, y + 1, z))
                && blockIsGrassSnowOrDirt(Server.world.getBlock(x, y + 2, z));
        return val;
    }

    public boolean cropPlantable(final int x, final int y, final int z) {
        return Server.world.getBlockID(x, y + 1, z) == Blocks.BLOCK_FARMLAND;
    }

    public boolean plantable(final int x, final int y, final int z) {
        return blockIsGrassSnowOrDirt(Server.world.getBlock(x, y + 1, z));
    }


    public boolean isGrass(short thisBlock) {
        return thisBlock == Blocks.BLOCK_GRASS ||
                thisBlock == Blocks.BLOCK_SNOW_GRASS ||
                thisBlock == Blocks.BLOCK_JUNGLE_GRASS ||
                thisBlock == Blocks.BLOCK_DRY_GRASS;
    }

    public boolean blockIsGrassSnowOrDirt(Block block) {
        return block.id == Blocks.BLOCK_FARMLAND
                || block.id == Blocks.BLOCK_DIRT
                || isGrass(block.id);
    }

    public boolean blockIsSandOrGravel(Block block) {
        return block.id == Blocks.BLOCK_SAND
                || block.id == Blocks.BLOCK_RED_SAND
                || block.id == Blocks.BLOCK_GRAVEL;
    }

    public short getGrassBlockOfBiome(int wx, int wy, int wz) {
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


    public void makeStalk(Block stalk, Block sapling, int maxHeight, Predicate<Block> canGrow) {
        stalk.randomTickEvent = (x, y, z) -> {
            return growStalk(x, y, z, stalk, sapling, maxHeight, canGrow);
        };
        if (sapling != null) sapling.randomTickEvent = (x, y, z) -> {
            return growStalk(x, y, z, stalk, sapling, maxHeight, canGrow);
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

    private boolean growStalk(int x, int y, int z,
                              Block stalkBlock, Block stalkSapling, int maxHeight,
                              Predicate<Block> plantable) {

        Block belowBlock = (Server.world.getBlock(x, y + 1, z));
        if (plantable.test(belowBlock)) {//If this is the bottom of a stalk
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


    public short growPlants(short thisBlock, Block aboveBlock) {
        /**
         * Grow grass
         */
        if (thisBlock == Blocks.BLOCK_GRASS || thisBlock == Blocks.BLOCK_SNOW_GRASS) {
            if (aboveBlock == BlockRegistry.BLOCK_AIR) {
                int indx = random.nextInt(snowyDefaultGrowth.size());
                return snowyDefaultGrowth.get(indx);
            }
        } else if (thisBlock == Blocks.BLOCK_DRY_GRASS) {
            if (aboveBlock == BlockRegistry.BLOCK_AIR) {
                return Blocks.BLOCK_DRY_GRASS_PLANT;
            } else if (aboveBlock.id == Blocks.BLOCK_DRY_GRASS_PLANT) {
                return Blocks.BLOCK_TALL_DRY_GRASS;
            }
        } else if (thisBlock == Blocks.BLOCK_JUNGLE_GRASS) {
            if (aboveBlock == BlockRegistry.BLOCK_AIR) {
                return Blocks.BLOCK_JUNGLE_GRASS_PLANT;
            } else if (aboveBlock.id == Blocks.BLOCK_JUNGLE_GRASS_PLANT) {
                return Blocks.BLOCK_TALL_GRASS;
            }
        }
        return -1;
    }


}