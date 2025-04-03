// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.server.world;

import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.utils.math.FastNoise;
import com.xbuilders.engine.utils.math.PerlinNoise;
import com.xbuilders.engine.server.world.chunk.Chunk;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Random;

import static com.xbuilders.engine.server.players.Player.PLAYER_HEIGHT;

public abstract class Terrain {

    public static final FastNoise fastNoise = new FastNoise();
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
        fastNoise.SetSeed(seed);
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

        public void setBlockWorld(int x, int y, int z, short block) {
            Chunk chunk = LocalServer.world.setBlock(block, x, y, z);//The world.setBlock automatically sets the block on a future chunk if it doesnt exist
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
    public boolean canSpawnHere(World world,
                                int playerWorldPosX,
                                int playerWorldPosY,
                                int playerWorldPosZ) {

        //We are looking at the player foot
        int playerFeetY = (int) (playerWorldPosY + PLAYER_HEIGHT);

        Block footBlock = world.getBlock(playerWorldPosX, playerFeetY, playerWorldPosZ);
        Block bodyBlock1 = world.getBlock(playerWorldPosX, playerFeetY - 1, playerWorldPosZ);
        Block bodyBlock2 = world.getBlock(playerWorldPosX, playerFeetY - 2, playerWorldPosZ);
        Block bodyBlock3 = world.getBlock(playerWorldPosX, playerFeetY - 3, playerWorldPosZ);

        return footBlock.solid //Ground is solid
                && !bodyBlock1.solid //The player can move
                && !bodyBlock2.solid
                && !bodyBlock3.solid
                //The ground and air is safe to stand in
                && footBlock.enterDamage < 0.01
                && bodyBlock1.enterDamage < 0.01
                && bodyBlock2.enterDamage < 0.01
                && bodyBlock3.enterDamage < 0.01;
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
