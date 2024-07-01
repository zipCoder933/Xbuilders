// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.FastNoise;
import com.xbuilders.engine.utils.math.PerlinNoise;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public abstract class Terrain {

    public static final FastNoise noise = new FastNoise();
    public static final PerlinNoise perlinNoise = new PerlinNoise(0, 150);
    private int seed = 0;
    public final String name;
    public int MAX_SURFACE_HEIGHT = 10;
    public int MIN_SURFACE_HEIGHT = -100;
    HashMap<String, Boolean> options = new HashMap<>();

    public Terrain(String name) {
        this.name = name;
    }

    public void init(int seed) {
        noise.SetSeed(seed);
        perlinNoise.setSeed(((double) seed / Integer.MAX_VALUE) * 255);
        this.seed = seed;
    }

    public class GenSession {

        public final HashSet<Chunk> modifiedMeshedChunks = new HashSet<>();
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

        public GenSession(Chunk chunk) {
            this.homeChunk = chunk;
            random.setSeed(FastNoise.Hash3D(seed, chunk.position.x, chunk.position.y, chunk.position.z));
        }

        public void setBlockWorld(int x, int y, int z, short block) {
            Chunk chunk = GameScene.world.setBlock(block, x, y, z);//The world.setBlock automatically sets the block on a future chunk if it doesnt exist
            if (chunk != null && !homeChunk.position.equals(chunk.position)) {
                modifiedMeshedChunks.add(chunk);
            }
        }
    }

    public final GenSession createTerrainOnChunk(final Chunk chunk) {
        GenSession session = new GenSession(chunk);
        this.generateChunkInner(chunk, session);
        return session;
    }

    protected abstract void generateChunkInner(final Chunk p0, GenSession session);

    //    public abstract int getHeightmapOfVoxel(final int p0, final int p1);
    public boolean spawnRulesApply(float PLAYER_HEIGHT, World world,
                                   int playerFeetX, int playerFeetY, int playerFeetZ) {
        WCCi wcc = new WCCi();
        wcc.set(playerFeetX, playerFeetY - 3, playerFeetZ);
        Chunk chunk = world.chunks.get(wcc.chunk);
        if (chunk != null && chunk.data.getSun(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z) < 5) {
            return false;//We cant spawn in darkness
        }
        Block footBlock = world.getBlock(playerFeetX, playerFeetY, playerFeetZ);
        return footBlock.solid && footBlock.opaque
                && !world.getBlock(playerFeetX, playerFeetY - 1, playerFeetZ).solid
                && !world.getBlock(playerFeetX, playerFeetY - 2, playerFeetZ).solid
                && !world.getBlock(playerFeetX, playerFeetY - 3, playerFeetZ).solid;
    }

    @Override
    public String toString() {
        return "Terrain{" + "seed=" + seed + ", name=" + name + '}';
    }

}
