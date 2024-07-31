/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.trees;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.MyGame;

import java.util.Random;

import static com.xbuilders.game.items.blocks.trees.TreeUtils.WAIT_TIME;
import static com.xbuilders.game.items.blocks.trees.TreeUtils.randomInt;

/**
 * @author zipCoder933
 */
public class BirchTreeUtils {

    public static final Block.SetBlockEvent setBlockEvent = new Block.SetBlockEvent() {
        @Override
        public void run(int x, int y, int z) {
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            plantTree(new Random(), x, y, z);
        }
    };

    public static void plantTree(Random rand, int x, int y, int z) {
        int height = randomInt(rand, 5, 7);
        for (int k = 0; k < height; k++) {
            TreeUtils.setBlockAndOverride(MyGame.BLOCK_BIRCH_LOG, x, y - k, z);
        }

        TreeUtils.roundedSquareLeavesLayer(x, y - height + 2, z, 2, MyGame.BLOCK_BIRCH_LEAVES);
        TreeUtils.roundedSquareLeavesLayer(x, y - height + 1, z, 2, MyGame.BLOCK_BIRCH_LEAVES);
        TreeUtils.diamondLeavesLayer(x, y - height, z, 2, MyGame.BLOCK_BIRCH_LEAVES);
        if (rand.nextDouble() > 0.8) {
            TreeUtils.diamondLeavesLayer(x, y - height - 1, z, 2, MyGame.BLOCK_BIRCH_LEAVES);
        }
    }

    public static void plantTree(Terrain.GenSession terrain, Chunk source, int x, int y, int z) {
        int height = randomInt(terrain.random, 5, 7);
        for (int k = 0; k < height; k++) {
            terrain.setBlockWorld(x, y - k, z, MyGame.BLOCK_BIRCH_LOG);
        }

        TreeUtils.roundedSquareLeavesLayer(terrain, source, x, y - height + 2, z, 2, MyGame.BLOCK_BIRCH_LEAVES);
        TreeUtils.roundedSquareLeavesLayer(terrain, source, x, y - height + 1, z, 2, MyGame.BLOCK_BIRCH_LEAVES);
        TreeUtils.diamondLeavesLayer(terrain, source, x, y - height, z, 2, MyGame.BLOCK_BIRCH_LEAVES);
        if (terrain.random.nextDouble() > 0.8) {
            TreeUtils.diamondLeavesLayer(terrain, source, x, y - height - 1, z, 2, MyGame.BLOCK_BIRCH_LEAVES);
        }
    }
}
