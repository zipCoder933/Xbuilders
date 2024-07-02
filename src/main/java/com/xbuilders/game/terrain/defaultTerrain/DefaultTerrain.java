package com.xbuilders.game.terrain.defaultTerrain;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.items.blocks.trees.AcaciaTreeUtils;
import com.xbuilders.game.items.blocks.trees.JungleTreeUtils;
import com.xbuilders.game.terrain.complexTerrain.ComplexTerrain.Biome;

import java.util.HashMap;

public class DefaultTerrain extends Terrain {

    short fern, deadBush;
    final int WORLD_HEIGHT_OFFSET = 138;
    final int OCEAN_LEVEL = 25; //Used to deepen lakes in heightmap generation
    final int WATER_LEVEL = WORLD_HEIGHT_OFFSET + (OCEAN_LEVEL - 5); //5 blocks above ocean level
    final float MOUNTAIN_THRESH = 20;

    boolean caves = false;
//    boolean mountains = true;

    public DefaultTerrain() {
        super("Default Terrain");

        MIN_SURFACE_HEIGHT = 100;
        MAX_SURFACE_HEIGHT = 230;

        TERRAIN_MIN_HEIGHT = 100;

        fern = MyGame.BLOCK_FERN;
        deadBush = MyGame.BLOCK_DEAD_BUSH;
        options.put("Generate Caves", true);
    }



    @Override
    public void loadWorld(HashMap<String, Boolean> options, int version) {
        caves = options.get("Generate Caves");
    }

    public int getTerrainHeight(int x, int z) {
        return getTerrainHeight(valley(x, z), x, z);
    }

    public Biome getBiomeOfVoxel(int x, int y, int z) {
        return getBiomeOfVoxel(
                valley(x, z),
                getHeat(x, z),
                getTerrainHeight(x, z),
                x, y, z);
    }

    final float treeOdds = 1;
    final float jungleTreeOdds = 0.99f;

    private void plantSod(GenSession session,
                          int x, int y, int z,
                          int wx, int wy, int wz,
                          float alpha, Biome biome,
                          Chunk chunk) {

        float f = session.random.nextFloat();
        boolean makePlants = true;
        if (f < 0.02 && wy < WATER_LEVEL - 1) {
            session.setBlockWorld(wx, wy - 1, wz, deadBush);
            makePlants = false;
        }

        switch (biome) {
            case DEFAULT -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_GRASS);
                if (makePlants) {
                    if (f > treeOdds) {
                        DefaultTerrainUtils.plantBirchOrOakTree(session, chunk, wx, y, wz);
                    } else if (f > 0.95) {
                        session.setBlockWorld(wx, wy - 1, wz, fern);
                    } else if (f > 0.9) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_PLANT_GRASS);
                    } else if (f > 0.89) {
                        session.setBlockWorld(wx, wy - 1, wz, DefaultTerrainUtils.randomFlower(session));
                    }
                }
            }
            case SNOWY -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_SNOW);
                if (makePlants) {
                    if (f > treeOdds) {
                        DefaultTerrainUtils.plantBirchOrOakTree(session, chunk, wx, y, wz);
                    } else if (f > 0.98) {
                        session.setBlockWorld(wx, wy - 1, wz, fern);
                    } else if (f > 0.96) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_PLANT_GRASS);
                    }
                }
            }
            case BEACH -> {
                if (alpha > 0) {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_SAND);
                    session.setBlockWorld(wx, wy + 1, wz, MyGame.BLOCK_SAND);
                } else {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_GRAVEL);
                    session.setBlockWorld(wx, wy + 1, wz, MyGame.BLOCK_GRAVEL);
                }
                if (wy > WATER_LEVEL + 2) {
                    if (session.random.nextFloat() > 0.9) {
                        switch (session.random.nextInt(6)) {
                            case 0 -> {
                                session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_FIRE_CORAL_FAN);
                            }
                            case 1 -> {
                                session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_HORN_CORAL_FAN);
                            }
                            case 2 -> {
                                session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_BUBBLE_CORAL_FAN);
                            }
                            case 3 -> {
                                session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_TUBE_CORAL_FAN);
                            }
                            default -> {
                                // session.setBlockWorld(wx,wy-1,wz, MyGame.BLOCK_SEA_GRASS.id); //TODO: Add
                                // seagrass as JSON block
                                // When we load seagrass without putting it int he block list, the chunk cant
                                // load because it doesnt know what kind of block it is
                            }
                        }
                    }
                } else if (makePlants && wy < WATER_LEVEL - 2) {
                    float rand = session.random.nextFloat();
                    if (rand > 0.99) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_BAMBOO);
                        session.setBlockWorld(wx, wy - 2, wz, MyGame.BLOCK_BAMBOO);
                        session.setBlockWorld(wx, wy - 3, wz, MyGame.BLOCK_BAMBOO);
                    } else if (rand > 0.98) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_MINI_CACTUS);
                    }
                }
            }
            case DESERT -> {
                if (alpha > 0) {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_SAND);
                    session.setBlockWorld(wx, wy + 1, wz, MyGame.BLOCK_SAND);
                } else {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_RED_SAND);
                    session.setBlockWorld(wx, wy + 1, wz, MyGame.BLOCK_RED_SAND);
                }
                if (makePlants) {
                    if (session.random.nextFloat() > 0.99 && y > 4 && y < 140) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_CACTUS);
                        session.setBlockWorld(wx, wy - 2, wz, MyGame.BLOCK_CACTUS);
                        session.setBlockWorld(wx, wy - 3, wz, MyGame.BLOCK_CACTUS);
                    }
                }
            }
            case SAVANNAH -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_DRY_GRASS);
                if (makePlants) {
                    if (f > treeOdds) {
                        AcaciaTreeUtils.plantTree(session, chunk, wx, y, wz);
                    } else if (f < 0.15) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_DRY_GRASS_PLANT);
                    } else if (f > 0.99) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_TALL_DRY_GRASS);
                        session.setBlockWorld(wx, wy - 2, wz, MyGame.BLOCK_TALL_DRY_GRASS_TOP);
                    }
                }
            }
            case JUNGLE -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_JUNGLE_GRASS);
                if (makePlants) {
                    if (f > jungleTreeOdds) {
                        JungleTreeUtils.plantTree(session, chunk, wx, y, wz);
                    } else if (f < 0.15) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_JUNGLE_GRASS_PLANT);
                    } else if (f > 0.98) {
                        session.setBlockWorld(wx, wy - 1, wz, MyGame.BLOCK_TALL_GRASS);
                        session.setBlockWorld(wx, wy - 2, wz, MyGame.BLOCK_TALL_GRASS_TOP);
                    }
                }
            }
        }
    }

    final float caveFrequency = 5.0f;

    public float valley(final int wx, final int wz) {
        float val = getValueFractal((float) (wz * 0.5) - 10000, (float) (wx * 0.5));
//        if (val < 0) { //We want less valley to be more common
//            val *= 4;
//            if (val < -1) val = -1;
//        }
        return val;
    }

    public int getTerrainHeight(float valley, final int wx, final int wz) {
        int val = (int) (getValueFractal((float) wx, (float) wz) * 50f);

        if (val > OCEAN_LEVEL) { //Deepen the oceans and lakes
            val = (int) (((val - OCEAN_LEVEL) * 1.7f) + OCEAN_LEVEL);
        }
        // else if (val < -MOUNTAIN_THRESH) {
        // val = (int) (((val + MOUNTAIN_THRESH) * 2f) - MOUNTAIN_THRESH);
        // }

        if (valley > 0) { //Valley only applies if the value is greater than 0
            val = (int) MathUtils.map(valley,
                    0, 1, //From 0-1
                    val, //Regular terrain
                    (val / 5f) - 5); //Raised, flattened terrain
        }
        return WORLD_HEIGHT_OFFSET + val;
    }

    public float getHeat(int x, int z) {
        return (float) (getValueFractal((float) x / 5, (float) z / 5) + 1) / 2.0f;
    }

    public Biome getBiomeOfVoxel(float valley, float heat, int heightmap, final int wx, final int wy, final int wz) {
        if (wy > WATER_LEVEL - 10) {
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
        return 0.9f;
    }

    private float getValueFractal(float x, float y, float z) {
        return noise.GetValueFractal(x * getFrequency(), y * getFrequency(), z * getFrequency());
    }

    private float getValueFractal(float x, float y) {
        return noise.GetValueFractal(x * getFrequency(), y * getFrequency());
    }


    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {
        int wx, wy, wz, heightmap; //IMPORTANT: We cant put this outside generateChunkInner() because multiple chunks are generated at the same time
        float valley, heat;
        Biome biome = Biome.DEFAULT;

        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int z = 0; z < Chunk.WIDTH; z++) {
                wx = x + chunk.position.x * Chunk.WIDTH;
                wz = z + chunk.position.z * Chunk.WIDTH;
                valley = valley(wx, wz);
                heightmap = getTerrainHeight(valley, wx, wz);
                boolean placeWater = true;
                heat = getHeat(wx, wz);

                for (int y = 0; y < Chunk.WIDTH; y++) {
                    wy = y + (chunk.position.y * Chunk.WIDTH);

                    if (wy > 252) {
                        chunk.data.setBlock(x, y, z, MyGame.BLOCK_BEDROCK);
                    } /*
                     * else if (wy >= heightmap) {
                     * chunk.data.setBlock(x, y, z, MyGame.BLOCK_STONE);
                     * }
                     */ else if (wy == heightmap && wy > 1) {// Place sod
                        biome = getBiomeOfVoxel(valley, heat, heightmap, wx, wy, wz);
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
                    } else if (wy <= heightmap) {
                        if (wy == WATER_LEVEL && heat < -0.6f) {
                            chunk.data.setBlock(x, y, z, MyGame.BLOCK_ICE_BLOCK);
                        } else if (wy > WATER_LEVEL && placeWater) {
                            chunk.data.setBlock(x, y, z, MyGame.BLOCK_WATER);
                        }
                    }
                }
            }
        }
    }
}