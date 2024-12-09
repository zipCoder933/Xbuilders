// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.FastNoise;
import com.xbuilders.engine.utils.math.PerlinNoise;
import com.xbuilders.engine.world.chunk.Chunk;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Random;

public abstract class Terrain {

    public static final FastNoise noise = new FastNoise();
    public static final PerlinNoise perlinNoise = new PerlinNoise(0, 150);
    private int seed = 0;
    public final String name;

    public int MAX_SURFACE_HEIGHT = 10;
    public int MIN_SURFACE_HEIGHT = -100;
    public int TERRAIN_MIN_GEN_HEIGHT = 0;  //Anything above this is considered air

    public HashMap<String, Boolean> options = new HashMap<>();
    public int version = 0;

    public Terrain(String name) {
        this.name = name;
    }

    public final void initForWorld(int seed, HashMap<String, Boolean> terrainOptions, int terrainVersion) {
        noise.SetSeed(seed);
        perlinNoise.setSeed(((double) seed / Integer.MAX_VALUE) * 255);
        this.seed = seed;
        if (terrainOptions == null) terrainOptions = new HashMap<>();
        this.options = terrainOptions;
        this.version = terrainVersion;
        loadWorld(options, version);
    }

    public int getBiomeOfVoxel(int x, int y, int z) {
        return 0;
    }

    public abstract void loadWorld(HashMap<String, Boolean> options, int version);

    public boolean isBelowMinHeight(Vector3i position, int offset) {
        //If the bottom of the chunk is below the minimum height, we need to generate the terrain
        return (position.y * Chunk.HEIGHT) + Chunk.HEIGHT >= TERRAIN_MIN_GEN_HEIGHT + offset;
    }

    public void initOptions() {

    }

    public class GenSession {

        //        public final HashSet<Chunk> modifiedMeshedChunks = new HashSet<>();
        public final Random random = new Random();
        public final Chunk homeChunk;
        public boolean generatedOutsideOfChunk = false;

        public int randomInt(int lowerBound, int upperBound) {
            return random.nextInt(upperBound - lowerBound) + lowerBound;
        }

        public float randomFloat(float lowerBound, float upperBound) {
            return (random.nextFloat() * upperBound - lowerBound) + lowerBound;
        }

        public double randomDouble(double lowerBound, double upperBound) {
            return (random.nextDouble() * upperBound - lowerBound) + lowerBound;
        }

        /**
         * Generates a random boolean with the specified probability.
         *
         * @param probability The probability of returning true (0.0 to 1.0).
         * @return true with the given probability, false otherwise.
         */
        public boolean randBoolWithProbability(float probability) {
            return random.nextFloat() < probability;
        }

        public GenSession(Chunk chunk) {
            this.homeChunk = chunk;
            random.setSeed(FastNoise.Hash3D(seed, chunk.position.x, chunk.position.y, chunk.position.z));
        }

        public void setBlockWorld(short block, int x, int y, int z) {
            Chunk chunk = GameScene.world.setBlock(block, x, y, z);//The world.setBlock automatically sets the block on a future chunk if it doesnt exist
//            if (chunk != null && !homeChunk.position.equals(chunk.position)) {
//                modifiedMeshedChunks.add(chunk);
//            }
        }
    }

    public final GenSession createTerrainOnChunk(final Chunk chunk) {
        GenSession session = new GenSession(chunk);
        this.generateChunkInner(chunk, session);
        return session;
    }

    protected abstract void generateChunkInner(final Chunk p0, GenSession session);

    //    public abstract int getHeightmapOfVoxel(final int p0, final int p1);
    public boolean canSpawnHere(float PLAYER_HEIGHT,
                                World world,
                                int playerFeetX, int playerFeetY, int playerFeetZ) {

        Block footBlock = world.getBlock(playerFeetX, playerFeetY, playerFeetZ);
        return footBlock.solid
                && !world.getBlock(playerFeetX, playerFeetY - 1, playerFeetZ).solid
                && !world.getBlock(playerFeetX, playerFeetY - 2, playerFeetZ).solid
                && !world.getBlock(playerFeetX, playerFeetY - 3, playerFeetZ).solid;
    }

    @Override
    public String toString() {
        return "Terrain{" +
                "version=" + version +
                ", options=" + options +
                ", name='" + name + '\'' +
                ", seed=" + seed +
                '}';
    }
}
