/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.blocks.trees;

import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.content.vanilla.items.Blocks;
import com.xbuilders.content.vanilla.items.blocks.PlantUtils;

import java.util.Random;

import static com.xbuilders.content.vanilla.items.blocks.trees.TreeUtils.*;

/**
 * @author zipCoder933
 */
public class SpruceTreeUtils {
    public static final Block.RandomTickEvent randomTickEvent = new Block.RandomTickEvent() {
        @Override
        public boolean run(int x, int y, int z) {
            if (PlantUtils.plantable(x, y, z)) {
                player_plantTree(new Random(), x, y, z);
                return true;
            }
            return false;
        }
    };
    static final int MIN_HEIGHT = 7;
    static final int MAX_HEIGHT = 14;

    public static void player_plantTree(Random rand, int x, int y, int z) {
        int height = randomInt(rand, MIN_HEIGHT, MAX_HEIGHT);
        for (int k = 0; k < height; k++) {
            Server.setBlock(Blocks.BLOCK_SPRUCE_LOG, x, y - k, z);
        }

        int heightVal = 4;
        int layerValue = 2;

        TreeUtils.player_squareLeavesLayer(x, y - height + heightVal, z, layerValue, Blocks.BLOCK_SPRUCE_LEAVES);
        heightVal--;
        TreeUtils.player_diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
        heightVal--;
        layerValue--;

        if (height > 8) {
            layerValue++;
            TreeUtils.player_squareLeavesLayer(x, y - height + heightVal, z, layerValue, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.player_diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }
        if (height > 10) {
            layerValue++;
            TreeUtils.player_squareLeavesLayer(x, y - height + heightVal, z, layerValue, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.player_diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }

        while (layerValue > 0) {
            TreeUtils.player_squareLeavesLayer(x, y - height + heightVal, z, layerValue, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.player_diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
            if (rand.nextBoolean()) {
                TreeUtils.player_diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
            }
            heightVal--;
            layerValue--;
        }
        Server.setBlock(Blocks.BLOCK_SPRUCE_LEAVES, z, x, y - height + heightVal);
    }

    public static void terrain_plantTree(Terrain.GenSession terrain, Chunk source, int x, int y, int z) {
        int height = randomInt(terrain.random, MIN_HEIGHT, MAX_HEIGHT);
        for (int k = 0; k < height; k++) {
            terrain.setBlockWorld(Blocks.BLOCK_SPRUCE_LOG, x, y - k, z);
        }

        int heightVal = 4;
        int layerValue = 2;

        TreeUtils.terrain_squareLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue, Blocks.BLOCK_SPRUCE_LEAVES);
        heightVal--;
        TreeUtils.terrain_diamondLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
        heightVal--;
        layerValue--;

        if (height > 8) {
            layerValue++;
            TreeUtils.terrain_squareLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.terrain_diamondLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }
        if (height > 12) {
            layerValue++;
            TreeUtils.terrain_squareLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.terrain_diamondLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }

        while (layerValue > 0) {
            TreeUtils.terrain_squareLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.terrain_diamondLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue + 1, Blocks.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }
        terrain.setBlockWorld(Blocks.BLOCK_SPRUCE_LEAVES, x, y - height + heightVal, z);
    }
}
