/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.trees;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Random;

import static com.xbuilders.engine.utils.math.RandomUtils.randInt;

/**
 *
 * @author zipCoder933
 */
public class JungleTreeUtils {
    public static final Block.SetBlockEvent setBlockEvent = new Block.SetBlockEvent() {
        @Override
        public void run(int x, int y, int z) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            plantTree(new Random(), x, y, z);
        }
    };
    static class VineBranchPair {

        public ArrayList<Vector3i> vines;
        public ArrayList<Vector3i> branches;
    }

    private static VineBranchPair setVinesAndBranches(Random random, int x, int z, int radius) {
        int lowerBoundX = x - radius;
        int upperBoundX = x + radius;
        int lowerBoundZ = z - radius;
        int upperBoundZ = z + radius;
        VineBranchPair ret = new VineBranchPair();

        ret.vines = new ArrayList<>();
        ret.branches = new ArrayList<>();

        for (int x2 = lowerBoundX; x2 <= upperBoundX; x2++) {
            for (int z2 = lowerBoundZ; z2 <= upperBoundZ; z2++) {

                if (!((x2 == lowerBoundX && z2 == lowerBoundZ)
                        || (x2 == upperBoundX && z2 == upperBoundZ)
                        || (x2 == lowerBoundX && z2 == upperBoundZ)
                        || (x2 == upperBoundX && z2 == lowerBoundZ))) {
                    if (!(x2 == 0 && z2 == 0)) {
                        if (random.nextFloat() > 0.95) {
                            ret.branches.add(new Vector3i(x2, 0, z2));
                        } else if (random.nextFloat() > 0.96) {
                            ret.vines.add(new Vector3i(x2, 0, z2));
                        }
                    }
                }
            }
        }
        return ret;
    }

    public static void plantTree(Random rand, int x, int y, int z) {
        int height = randInt(rand, 5, 13);
        int firstLayerWide = 0;
        firstLayerWide = randInt(rand, 2, 4);
        TreeUtils.roundedSquareLeavesLayer(x, (y - height + 2), z, firstLayerWide, MyGame.BLOCK_JUNGLE_LEAVES);
        TreeUtils.roundedSquareLeavesLayer(x + randInt(rand, -1, 1),
                (y - height + 1),
                z + randInt(rand, -1, 1),
                firstLayerWide, MyGame.BLOCK_JUNGLE_LEAVES);

        VineBranchPair vb = setVinesAndBranches(rand, x, z, firstLayerWide);
        int h4 = (int) (height * 0.4);
        for (int k = 0; k < height; k++) {
         GameScene.player.setBlock(MyGame.BLOCK_JUNGLE_LOG,x, y - k, z);
            if (k < height - 1) {
                if (k > h4) {
                    for (Vector3i branch : vb.branches) {
                        TreeUtils.setBlock(MyGame.BLOCK_JUNGLE_LEAVES, branch.x, y - k, branch.z);
                    }
                }
                for (Vector3i vine : vb.vines) {
                    TreeUtils.setBlock(MyGame.BLOCK_VINES, vine.x, y - k, vine.z);
                }
            }
        }

        TreeUtils.diamondLeavesLayer(x, y - height, z, 3, MyGame.BLOCK_JUNGLE_LEAVES);
        if (rand.nextDouble() > 0.8) {
            TreeUtils.diamondLeavesLayer(x, y - height - 1, z, 2, MyGame.BLOCK_JUNGLE_LEAVES);
        }
    }

    public static void plantTree(Terrain.GenSession terrain, Chunk source, int x, int y, int z) {
        int height = randInt(terrain.random, 5, 13);
        int firstLayerWide = 0;
        firstLayerWide = randInt(terrain.random, 2, 4);
        TreeUtils.roundedSquareLeavesLayer(terrain, source, x, (y - height + 2), z, firstLayerWide, MyGame.BLOCK_JUNGLE_LEAVES);
        TreeUtils.roundedSquareLeavesLayer(terrain, source,
                x + randInt(terrain.random, -1, 1),
                (y - height + 1),
                z + randInt(terrain.random, -1, 1),
                firstLayerWide, MyGame.BLOCK_JUNGLE_LEAVES);

        VineBranchPair vb = setVinesAndBranches(terrain.random, x, z, firstLayerWide);
        int h4 = (int) (height * 0.4);
        for (int k = 0; k < height; k++) {
           terrain.setBlockWorld(x, y - k, z, MyGame.BLOCK_JUNGLE_LOG);
            if (k < height - 1) {
                if (k > h4) {
                    for (Vector3i branch : vb.branches) {
                        if (branch.x != x && branch.z != z) {
                            terrain.setBlockWorld(branch.x, y - k, branch.z, MyGame.BLOCK_JUNGLE_LEAVES);
                        }
                    }
                }
                for (Vector3i vine : vb.vines) {
                    if (vine.x != x && vine.z != z) {
                        terrain.setBlockWorld(vine.x, y - k, vine.z, MyGame.BLOCK_VINES);
                    }
                }
            }
        }

        TreeUtils.diamondLeavesLayer(terrain, source, x, y - height, z, 3, MyGame.BLOCK_JUNGLE_LEAVES);
        if (terrain.random.nextDouble() > 0.8) {
            TreeUtils.diamondLeavesLayer(terrain, source, x, y - height - 1, z, 2, MyGame.BLOCK_JUNGLE_LEAVES);
        }
    }
}
