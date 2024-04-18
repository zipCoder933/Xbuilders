/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.trees;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.MyGame;

import java.util.Random;

import static com.xbuilders.engine.items.trees.TreeUtils.randomInt;

/**
 * @author zipCoder933
 */
public class SpruceTreeUtils {

    public static void plantTree(Random rand, int x, int y, int z) {

        int height = randomInt(rand, 7, 14);
        for (int k = 0; k < height; k++) {
            GameScene.player.setBlock(MyGame.BLOCK_SPRUCE_LOG, x, y - k, z);
        }

        int heightVal = 4;
        int layerValue = 2;

        TreeUtils.squareLeavesLayer(x, y - height + heightVal, z, layerValue, MyGame.BLOCK_SPRUCE_LEAVES);
        heightVal--;
        TreeUtils.diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
        heightVal--;
        layerValue--;

        if (height > 8) {
            layerValue++;
            TreeUtils.squareLeavesLayer(x, y - height + heightVal, z, layerValue, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }
        if (height > 10) {
            layerValue++;
            TreeUtils.squareLeavesLayer(x, y - height + heightVal, z, layerValue, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }

        while (layerValue > 0) {
            TreeUtils.squareLeavesLayer(x, y - height + heightVal, z, layerValue, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
            if (rand.nextBoolean()) {
                TreeUtils.diamondLeavesLayer(x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
            }
            heightVal--;
            layerValue--;
        }
        GameScene.player.setBlock(MyGame.BLOCK_SPRUCE_LEAVES, z, x, y - height + heightVal);
    }

    public static void plantTree(Terrain.GenSession terrain, Chunk source, int x, int y, int z) {
        int height = randomInt(terrain.random, 7, 14);
        for (int k = 0; k < height; k++) {
            GameScene.player.setBlock(MyGame.BLOCK_SPRUCE_LOG, z, x, y - k);
        }

        int heightVal = 2;
        int layerValue = 2;

        TreeUtils.squareLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue, MyGame.BLOCK_SPRUCE_LEAVES);
        heightVal--;
        TreeUtils.diamondLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
        heightVal--;
        layerValue--;

        if (height > 8) {
            layerValue++;
            TreeUtils.squareLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.diamondLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }
//        if (height > 10) {
//            layerValue++;
//            TreeUtils.squareLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue, MyGame.BLOCK_SPRUCE_LEAVES);
//            heightVal--;
//            TreeUtils.diamondLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
//            heightVal--;
//            layerValue--;
//        }

        while (layerValue > 0) {
            TreeUtils.squareLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            TreeUtils.diamondLeavesLayer(terrain, source, x, y - height + heightVal, z, layerValue + 1, MyGame.BLOCK_SPRUCE_LEAVES);
            heightVal--;
            layerValue--;
        }
        TreeUtils.setBlockAndOverride(MyGame.BLOCK_SPRUCE_LEAVES, x, y - height + heightVal, z);
    }
}
