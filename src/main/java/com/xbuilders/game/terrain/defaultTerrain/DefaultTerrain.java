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
    final float CAVE_FREQUENCY = 5.0f;

    boolean caves;
    boolean trees;
    boolean mountains;

    private void setTerrainBounds(int minSurfaceHeight) {
        MIN_SURFACE_HEIGHT = minSurfaceHeight;
        MAX_SURFACE_HEIGHT = 230;
        TERRAIN_MIN_GEN_HEIGHT = minSurfaceHeight;
    }

    public DefaultTerrain() {
        super("Default Terrain");
        fern = MyGame.BLOCK_FERN;
        deadBush = MyGame.BLOCK_DEAD_BUSH;
    }

    public void initOptions() {
        setTerrainBounds(100);
        //Default setup
        version = 1;//The latest version
        caves = false;
        trees = false;
        mountains = false;

        options.put("Generate Caves", caves);
        options.put("Generate Trees", trees);
        options.put("Generate Mountains", mountains);
    }

    @Override
    public void loadWorld(HashMap<String, Boolean> options, int version) {
        if (options.containsKey("Generate Caves")) {
            caves = options.get("Generate Caves");
        }
        if (options.containsKey("Generate Trees")) {
            trees = options.get("Generate Trees");
        }
        if (options.containsKey("Generate Mountains")) {
            mountains = options.get("Generate Mountains");
        }
        setTerrainBounds(mountains ? 0 : 100);
    }

    public int getTerrainHeight(int x, int z) {
        return getTerrainHeight(valley(x, z), x, z);
    }

    public Biome getBiomeOfVoxel(int x, int y, int z) {
        return getBiomeOfVoxelV2(
                valley(x, z),
                getHeat(x, z),
                getTerrainHeight(x, z),
                x, y, z);
    }


    final float savannahTreeOdds = .995f;
    final float treeOdds = .994f;
    final float jungleTreeOdds = .99f;

    private void plantSod(GenSession session,
                          int x, int y, int z,
                          int wx, int wy, int wz,
                          float alpha, Biome biome,
                          Chunk chunk) {

        float f = session.random.nextFloat();
        boolean makePlants = true;
        if (f < 0.02 && wy < WATER_LEVEL - 1) {
            session.setBlockWorld(deadBush, wx, wy - 1, wz);
            makePlants = false;
        }

        switch (biome) {
            case DEFAULT -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_GRASS);
                if (makePlants) {
                    if (trees && f > treeOdds) {
                        DefaultTerrainUtils.plantRandomTree(session, alpha, chunk, wx, wy, wz);
                    } else if (f > 0.95) {
                        session.setBlockWorld(fern, wx, wy - 1, wz);
                    } else if (f > 0.9) {
                        session.setBlockWorld(MyGame.BLOCK_PLANT_GRASS, wx, wy - 1, wz);
                    } else if (f > 0.89) {
                        session.setBlockWorld(DefaultTerrainUtils.randomFlower(session), wx, wy - 1, wz);
                    }
                }
            }
            case SNOWY -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_SNOW);
                if (makePlants) {
                    if (trees && f > treeOdds) {
                        DefaultTerrainUtils.plantRandomTree(session, alpha, chunk, wx, wy, wz);
                    } else if (f > 0.98) {
                        session.setBlockWorld(fern, wx, wy - 1, wz);
                    } else if (f > 0.96) {
                        session.setBlockWorld(MyGame.BLOCK_PLANT_GRASS, wx, wy - 1, wz);
                    }
                }
            }
            case BEACH -> {
                if (alpha > 0) {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_SAND);
                    session.setBlockWorld(MyGame.BLOCK_SAND, wx, wy + 1, wz);
                } else {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_GRAVEL);
                    session.setBlockWorld(MyGame.BLOCK_GRAVEL, wx, wy + 1, wz);
                }
                if (wy > WATER_LEVEL + 2) {
                    if (session.random.nextFloat() > 0.9) {
                        switch (session.random.nextInt(6)) {
                            case 0 -> {
                                session.setBlockWorld(MyGame.BLOCK_FIRE_CORAL_FAN, wx, wy - 1, wz);
                            }
                            case 1 -> {
                                session.setBlockWorld(MyGame.BLOCK_HORN_CORAL_FAN, wx, wy - 1, wz);
                            }
                            case 2 -> {
                                session.setBlockWorld(MyGame.BLOCK_BUBBLE_CORAL_FAN, wx, wy - 1, wz);
                            }
                            case 3 -> {
                                session.setBlockWorld(MyGame.BLOCK_TUBE_CORAL_FAN, wx, wy - 1, wz);
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
                        session.setBlockWorld(MyGame.BLOCK_BAMBOO, wx, wy - 1, wz);
                        session.setBlockWorld(MyGame.BLOCK_BAMBOO, wx, wy - 2, wz);
                        session.setBlockWorld(MyGame.BLOCK_BAMBOO, wx, wy - 3, wz);
                    } else if (rand > 0.98) {
                        session.setBlockWorld(MyGame.BLOCK_MINI_CACTUS, wx, wy - 1, wz);
                    }
                }
            }
            case DESERT -> {
                if (alpha > 0) {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_SAND);
                    session.setBlockWorld(MyGame.BLOCK_SAND, wx, wy + 1, wz);
                } else {
                    chunk.data.setBlock(x, y, z, MyGame.BLOCK_RED_SAND);
                    session.setBlockWorld(MyGame.BLOCK_RED_SAND, wx, wy + 1, wz);
                }
                if (makePlants) {
                    if (session.random.nextFloat() > 0.99 && y > 4 && y < 140) {
                        session.setBlockWorld(MyGame.BLOCK_CACTUS, wx, wy - 1, wz);
                        session.setBlockWorld(MyGame.BLOCK_CACTUS, wx, wy - 2, wz);
                        session.setBlockWorld(MyGame.BLOCK_CACTUS, wx, wy - 3, wz);
                    }
                }
            }
            case SAVANNAH -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_DRY_GRASS);
                if (makePlants) {
                    if (trees && f > savannahTreeOdds) {
                        AcaciaTreeUtils.terrain_plantTree(session, chunk, wx, wy, wz);
                    } else if (f < 0.15) {
                        session.setBlockWorld(MyGame.BLOCK_DRY_GRASS_PLANT, wx, wy - 1, wz);
                    } else if (f > 0.99) {
                        session.setBlockWorld(MyGame.BLOCK_TALL_DRY_GRASS, wx, wy - 1, wz);
                        session.setBlockWorld(MyGame.BLOCK_TALL_DRY_GRASS_TOP, wx, wy - 2, wz);
                    }
                }
            }
            case JUNGLE -> {
                chunk.data.setBlock(x, y, z, MyGame.BLOCK_JUNGLE_GRASS);
                if (makePlants) {
                    if (trees && f > jungleTreeOdds) {
                        JungleTreeUtils.terrain_plantTree(session, chunk, wx, wy, wz);
                    } else if (f < 0.15) {
                        session.setBlockWorld(MyGame.BLOCK_TALL_GRASS, wx, wy - 1, wz);
                        session.setBlockWorld(MyGame.BLOCK_TALL_GRASS_TOP, wx, wy - 2, wz);
                    } else if (f > 0.98) {
                        session.setBlockWorld(MyGame.BLOCK_JUNGLE_GRASS_PLANT, wx, wy - 1, wz);
                    }
                }
            }
        }
    }


    public float valley(final int wx, final int wz) {
        //Scale: -1 to 1. higher values = more valley
        float valley = getValueFractal((float) (wz * 0.5) - 10000, (float) (wx * 0.5));
        if (version >= 1) {//Causes more valley to appear
            valley += 0.15f;
        }

        return valley;
    }

    final float MOUNTAIN_THRESH = 0.2f;
    final float MOUNTAIN_HEIGHT = 2.5f;

    public int getTerrainHeight(float valley, final int wx, final int wz) {
        double val = getValueFractal((float) wx, (float) wz) * 50f;

        if (val > OCEAN_LEVEL) { //Deepen the oceans and lakes
            val = ((val - OCEAN_LEVEL) * 1.7f) + OCEAN_LEVEL;
        } else if (mountains && val < -MOUNTAIN_THRESH) {
            val = ((val + MOUNTAIN_THRESH) * MOUNTAIN_HEIGHT) - MOUNTAIN_THRESH;
        }

        if (valley > 0) { //Valley only applies if the value is greater than 0
            val = MathUtils.map(valley,
                    0, 1, //From 0-1
                    val, //Regular terrain
                    (val / 5f) - 5); //Raised, flattened terrain
        }


        return (int) (WORLD_HEIGHT_OFFSET + val);
    }

    public float getHeat(int x, int z) {
        return (getValueFractal((float) x / 5, (float) z / 5) + 1) / 2.0f;
    }

    public Biome getBiomeOfVoxelV2(float valley, float heat, int heightmap, final int wx, final int wy, final int wz) {
        if (heat > 0.65f  // We lower the temp to compensate for being at the bottom of the terrain
                && wy > WORLD_HEIGHT_OFFSET - 8 - (heat * 5) &&
                wy < WATER_LEVEL - 1) {
            return Biome.DESERT;
        }

        if (wy > WATER_LEVEL - 10) {
            return Biome.BEACH;
        }
        //Heat will stay within 0-1 range
        //Noise tends to be more biased towards the center, meaning we either have to normalize the noise function
        //somehow to produce even distribution, or we have to favor the edges more

        if (heat > 0.52f) {
            return Biome.SAVANNAH;
        } else if (heat > 0.39f) {
            return Biome.DEFAULT;
        } else if (heat > 0.25f) {
            return Biome.JUNGLE;
        } else {
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
                        biome = getBiomeOfVoxelV2(valley, heat, heightmap, wx, wy, wz);

                        //Alpha is a high frequency noise value, from -1 to 1
                        final float alpha = getValueFractal((float) wx * 3, (float) wz * 3 - 500.0f);
                        plantSod(session, x, y, z, wx, wy, wz, alpha, biome, chunk);
                    } else if (wy > heightmap && wy < heightmap + 2) {
                        if (chunk.data.getBlock(x, y, z) == BlockList.BLOCK_AIR.id) {
                            chunk.data.setBlock(x, y, z, MyGame.BLOCK_DIRT);
                        }
                    } else if (wy > heightmap &&
                            (!caves || //If caves are disabled
                                    wy < heightmap + 10 || //Or we arent below the ground enough
                                    getValueFractal(wx * CAVE_FREQUENCY, wy * 14.0f, wz * CAVE_FREQUENCY) <= 0.25)
                    ) {
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