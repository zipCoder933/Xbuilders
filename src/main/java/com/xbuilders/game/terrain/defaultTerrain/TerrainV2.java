package com.xbuilders.game.terrain.defaultTerrain;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.items.blocks.trees.AcaciaTreeUtils;
import com.xbuilders.game.items.blocks.trees.JungleTreeUtils;
import com.xbuilders.game.terrain.complexTerrain.ComplexTerrain.Biome;

public class TerrainV2 extends Terrain {

    short fern, deadBush;
    final int WORLD_HEIGHT_OFFSET = 138;
    final int WATER_LEVEL = WORLD_HEIGHT_OFFSET + 20;

    boolean caves = false;
    boolean mountains = true;

    public TerrainV2(boolean caves) {
        super("Default Terrain" + (caves ? " w/ Caves" : ""));
        MIN_HEIGHT = 0;
        MAX_HEIGHT = 257;
        this.caves = caves;
        fern = MyGame.BLOCK_FERN;
        deadBush = MyGame.BLOCK_DEAD_BUSH;
        // utils = new DefaultTerrainUtils(this, WATER_LEVEL);
    }

    public int getHeightmapOfVoxel(int x, int z) {
        return getHeightmapOfVoxel(valley(x, z), x, z);
    }

    public Biome getBiomeOfVoxel(int x, int y, int z) {
        return getBiomeOfVoxel(
                valley(x, z),
                getHeat(x, z),
                getHeightmapOfVoxel(x, z),
                x, y, z);
    }

    final float treeOdds = 0.998f;
    final float jungleTreeOdds = 0.99f;

    private void plantSod(GenSession session, int x, int y, int z, int wx, int wy, int wz, float alpha, Biome biome,
            Chunk chunk) {
        if (y - 2 < 0 || y+1>=Chunk.WIDTH)
            return;
        float f = session.random.nextFloat();
        boolean makePlants = true;
        if (f < 0.02 && wy < WATER_LEVEL - 1) {
            chunk.data.setBlock(x, y - 1, z, deadBush);
            makePlants = false;
        }

        switch (biome) {
            case DEFAULT -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_GRASS);
                if (makePlants) {
                    if (f > treeOdds) {
                        DefaultTerrainUtils.plantBirchOrOakTree(session, chunk, wx, y, wz);
                    } else if (f > 0.95) {
                        chunk.data.setBlock(x, y - 1, z, fern);
                    } else if (f > 0.9) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_PLANT_GRASS);
                    } else if (f > 0.89) {
                        DefaultTerrainUtils.randomFlower(session, chunk, x, y - 1, z);
                    }
                }
            }
            case SNOWY -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_SNOW);
                if (makePlants) {
                    if (f > treeOdds) {
                        DefaultTerrainUtils.plantBirchOrOakTree(session, chunk, wx, y, wz);
                    } else if (f > 0.98) {
                        chunk.data.setBlock(x, y - 1, z, fern);
                    } else if (f > 0.96) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_PLANT_GRASS);
                    }
                }
            }
            case BEACH -> {
                if (alpha > 0) {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_SAND);
                    chunk.data.setBlock(x, y + 1, z, MyGame.BLOCK_SAND);
                } else {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_GRAVEL);
                    chunk.data.setBlock(x, y + 1, z, MyGame.BLOCK_GRAVEL);
                }
                if (wy > WATER_LEVEL + 2) {
                    if (session.random.nextFloat() > 0.9) {
                        switch (session.random.nextInt(6)) {
                            case 0 -> {
                                chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_FIRE_CORAL_FAN);
                            }
                            case 1 -> {
                                chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_HORN_CORAL_FAN);
                            }
                            case 2 -> {
                                chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_BUBBLE_CORAL_FAN);
                            }
                            case 3 -> {
                                chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_TUBE_CORAL_FAN);
                            }
                            default -> {
                                // chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_SEA_GRASS.id); //TODO: Add
                                // seagrass as JSON block
                                // When we load seagrass without putting it int he block list, the chunk cant
                                // load because it doesnt know what kind of block it is
                            }
                        }
                    }
                } else if (makePlants && wy < WATER_LEVEL - 2) {
                    float rand = session.random.nextFloat();
                    if (rand > 0.99) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_BAMBOO);
                        chunk.data.setBlock(x, y - 2, z, MyGame.BLOCK_BAMBOO);
                        chunk.data.setBlock(x, y - 3, z, MyGame.BLOCK_BAMBOO);
                    } else if (rand > 0.98) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_MINI_CACTUS);
                    }
                }
            }
            case DESERT -> {
                if (alpha > 0) {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_SAND);
                    chunk.data.setBlock(x, y + 1, z, MyGame.BLOCK_SAND);
                } else {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_RED_SAND);
                    chunk.data.setBlock(x, y + 1, z, MyGame.BLOCK_RED_SAND);
                }
                if (makePlants) {
                    if (session.random.nextFloat() > 0.99 && y > 4 && y < 140) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_CACTUS);
                        chunk.data.setBlock(x, y - 2, z, MyGame.BLOCK_CACTUS);
                        chunk.data.setBlock(x, y - 3, z, MyGame.BLOCK_CACTUS);
                    }
                }
            }
            case SAVANNAH -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_DRY_GRASS);
                if (makePlants) {
                    if (f > treeOdds) {
                        AcaciaTreeUtils.plantTree(session, chunk, wx, y, wz);
                    } else if (f < 0.15) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_DRY_GRASS_PLANT);
                    } else if (f > 0.99) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_TALL_DRY_GRASS);
                        chunk.data.setBlock(x, y - 2, z, MyGame.BLOCK_TALL_DRY_GRASS_TOP);
                    }
                }
            }
            case JUNGLE -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_JUNGLE_GRASS);
                if (makePlants) {
                    if (f > jungleTreeOdds) {
                        JungleTreeUtils.plantTree(session, chunk, wx, y, wz);
                    } else if (f < 0.15) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_JUNGLE_GRASS_PLANT);
                    } else if (f > 0.98) {
                        chunk.data.setBlock(x, y - 1, z, MyGame.BLOCK_TALL_GRASS);
                        chunk.data.setBlock(x, y - 2, z, MyGame.BLOCK_TALL_GRASS_TOP);
                    }
                }
            }
        }
    }

    final float OCEAN_THRESH = 25;
    final float MOUNTAIN_THRESH = 20;

    public float valley(final int wx, final int wz) {
        return getValueFractal((float) wz - 10000, (float) wx);
    }

    public int getHeightmapOfVoxel(float valley, final int wx, final int wz) {
        int val = (int) (getValueFractal((float) wx, (float) wz) * 50f);

        if (val > OCEAN_THRESH) {
            val = (int) (((val - OCEAN_THRESH) * 1.5f) + OCEAN_THRESH);
        }
        // else if (val < -MOUNTAIN_THRESH) {
        // val = (int) (((val + MOUNTAIN_THRESH) * 2f) - MOUNTAIN_THRESH);
        // }
        if (val < 0) {// If the height value is less than 0, normalize it
            val = (int) MathUtils.map(valley, -1, 1, val, val / 10f);// Normalize for valleys
        }

        return WORLD_HEIGHT_OFFSET + val;
    }

    public float getHeat(int x, int z) {
        return (float) (getValueFractal((float) x / 5, (float) z / 5) + 1) / 2.0f;
    }

    public Biome getBiomeOfVoxel(float valley, float heat, int heightmap, final int x, final int y, final int z) {
        if (y > WATER_LEVEL - 10) {
            return Biome.BEACH;
        }

        if (heat > 0.55f && heightmap > WORLD_HEIGHT_OFFSET - 8 - (heat * 5)) {// 0.6 - 1
            // We lower down the minimum temperature of desert to compensate for it only
            // being at the bottom of the terrain
            return Biome.DESERT;
        } else if (heat > 0.2f) {// 0.2 - 0.6
            return Biome.SAVANNAH;
        } else if (heat > -0.2f) {// -0.2 - 0.2
            return Biome.DEFAULT;
        } else if (heat > -0.6f) {// -0.6 - -0.2
            return Biome.JUNGLE;
        } else {// -1 - -0.6
            return Biome.SNOWY;
        }
    }

    private float getFrequency() {
        return 2.0f;
    }

    private float getValueFractal(float x, float y, float z) {
        return noise.GetValueFractal(x * getFrequency(), y * getFrequency(), z * getFrequency());
    }

    private float getValueFractal(float x, float y) {
        return noise.GetValueFractal(x * getFrequency(), y * getFrequency());
    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {

        final float caveFrequency = MathUtils.clamp(3.9f * getFrequency(), 7.0f, 7.5f);
        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int z = 0; z < Chunk.WIDTH; z++) {
                final int wx = x + chunk.position.x * Chunk.WIDTH;
                final int wz = z + chunk.position.z * Chunk.WIDTH;
                final float valley = valley(wx, wz);
                final int heightmap = getHeightmapOfVoxel(valley, wx, wz);
                boolean placeWater = true;
                float heat = getHeat(wx, wz);
                Biome biome = Biome.DEFAULT;

                for (int y = 0; y < Chunk.WIDTH; y++) {
                    int wy = y + (chunk.position.y * Chunk.WIDTH);

                    if (wy > 252) {
                        chunk.data.setBlock(x, y, z, MyGame.BLOCK_BEDROCK);
                    } /*
                       * else if (wy >= heightmap) {
                       * chunk.data.setBlock(x, y, z, MyGame.BLOCK_STONE);
                       * }
                       */

                    else if (wy == heightmap && wy > 1) {// Place sod
                        biome = getBiomeOfVoxel(valley, heat, heightmap, wx, y, wz);
                        final float alpha = getValueFractal((float) wx * 3, (float) wz * 3 -
                                500.0f);
                        plantSod(session, x, y, z, wx, wy, wz, alpha, biome, chunk);
                    } else if (wy > heightmap && wy < heightmap + 3) {
                        if (chunk.data.getBlock(x, y, z) == BlockList.BLOCK_AIR.id) {
                            chunk.data.setBlock(x, y, z, MyGame.BLOCK_DIRT);
                        }
                    } else if (wy > heightmap &&
                            (!caves || getValueFractal(wx * caveFrequency, wy * 14.0f, wz *
                                    caveFrequency) <= 0.25)) {
                        chunk.data.setBlock(x, y, z, MyGame.BLOCK_STONE);
                        placeWater = false;
                    } else if (wy == WATER_LEVEL && heat < -0.6f) {
                        chunk.data.setBlock(x, y, z, MyGame.BLOCK_ICE_BLOCK);
                    } else if (wy > WATER_LEVEL && placeWater) {
                        chunk.data.setBlock(x, y, z, MyGame.BLOCK_WATER);
                    }

                }
            }
        }

    }
}