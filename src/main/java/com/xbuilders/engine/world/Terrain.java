// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.world;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.FastNoise;
import com.xbuilders.engine.utils.math.PerlinNoise;
import com.xbuilders.engine.world.chunk.Chunk;

import java.util.HashSet;
import java.util.Random;

public abstract class Terrain {

    public final static PerlinNoise noise = new PerlinNoise();
    private int seed = 0;
    public final String name;
    public int MAX_HEIGHT = 10;
    public int MIN_HEIGHT = -100;

    public Terrain(String name) {
        this.name = name;
    }

    public class GenSession {

        public final HashSet<Chunk> modifiedMeshedChunks = new HashSet<>();
        public final Random random = new Random();
        public final Chunk homeChunk;

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

        public void setBlockWorld(int x, int y, int z, Block block) {
            Chunk chunk = GameScene.world.setBlock(x, y, z, block.id);
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

    protected abstract boolean generateChunkInner(final Chunk p0, GenSession session);

//    public abstract int getHeightmapOfVoxel(final int p0, final int p1);
    public boolean spawnRulesApply(float PLAYER_HEIGHT, World chunks, int x, int y, int z) {
        return chunks.getBlock(x, y, z).isSolid()
                && !chunks.getBlock(x, y + 1, z).isSolid()
                && !chunks.getBlock(x, y + 2, z).isSolid();
    }

}
