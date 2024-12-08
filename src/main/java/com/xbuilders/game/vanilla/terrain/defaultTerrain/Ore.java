package com.xbuilders.game.vanilla.terrain.defaultTerrain;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;

public class Ore {
    public int peakCommonYLevel;
    public int commonRange = 20;
    public float rarity;
    public short block;

    public Ore(int peakCommonYLevel, float rarity, short block) {
        this.peakCommonYLevel = peakCommonYLevel;
        this.rarity = rarity;
        this.block = block;
    }

    public void placeOre(Chunk chunk, int x, int y, int z, int wx, int wy, int wz, float caveFractal, Terrain.GenSession session) {
        float distance = Math.abs(wy - peakCommonYLevel);
        if(distance < commonRange) {
            session.random.
            chunk.data.setBlock(x, y, z, block);
        }
    }
}
