/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.items.blocks.trees;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.vanilla.items.Blocks;
import org.joml.Vector3i;

import java.util.Random;

import static com.xbuilders.game.vanilla.items.blocks.trees.TreeUtils.randomInt;


/**
 * @author zipCoder933
 */
public class AcaciaTreeUtils {
    public static final Block.SetBlockEvent setBlockEvent = new Block.SetBlockEvent() {
        @Override
        public void run(int x, int y, int z) {
            if (TreeUtils.readyToGrow(x, y, z))            player_plantTree(new Random(), x, y, z);
        }
    };
    private static void player_treeBush(int x, int y, int z, int bushRadius) {
        TreeUtils.player_roundedSquareLeavesLayer(x, y, z, bushRadius, Blocks.BLOCK_ACACIA_LEAVES);
        TreeUtils.player_diamondLeavesLayer(x, y - 1, z, bushRadius, Blocks.BLOCK_ACACIA_LEAVES);
    }

    public static void player_plantTree(Random rand, int x, int y, int z) {
        int height = randomInt(rand, 3, 4);
        for (int k = 0; k < height; k++) {
            TreeUtils.player_setBlockAndOverride(Blocks.BLOCK_ACACIA_LOG, x, y - (height - 1) + k, z);
        }

        int length = randomInt(rand, 2, 4);
        int xDir = randomInt(rand, -1, 1);
        int zDir = 0;
        if (xDir == 0) {
            zDir = rand.nextBoolean() ? -1 : 1;
        }
        Vector3i vec = TreeUtils.player_generateBranch(x, y - height + 1, z, length, xDir, zDir, Blocks.BLOCK_ACACIA_LOG);
        TreeUtils.player_setBlockAndOverride(Blocks.BLOCK_ACACIA_LOG, vec.x, vec.y - 1, vec.z);
        player_treeBush(vec.x, vec.y - 1, vec.z, randomInt(rand, 3, 4));

        if (rand.nextBoolean()) {
            if (xDir == 0) {
                zDir = 0 - zDir;
            } else {
                xDir = 0 - xDir;
            }
        } else {
            if (xDir == 0) {
                xDir += randomInt(rand, -1, 1);
                zDir = 0;
            } else {
                zDir += randomInt(rand, -1, 1);
                xDir = 0;
            }
        }
        length = randomInt(rand, 3, 4);
        vec = TreeUtils.player_generateBranch(x, y - height + 1, z, length, xDir, zDir, Blocks.BLOCK_ACACIA_LOG);
        TreeUtils.player_setBlockAndOverride(Blocks.BLOCK_ACACIA_LOG, vec.x, vec.y - 1, vec.z);
        player_treeBush(vec.x, vec.y - 1, vec.z, randomInt(rand, 2, 3));
    }

    private static void terrain_treeBush(Terrain.GenSession terrain, Chunk sourceChunk, int x, int y, int z, int bushRadius) {
        TreeUtils.terrain_roundedSquareLeavesLayer(terrain, sourceChunk, x, y, z, bushRadius, Blocks.BLOCK_ACACIA_LEAVES);
        TreeUtils.terrain_diamondLeavesLayer(terrain, sourceChunk, x, y - 1, z, bushRadius, Blocks.BLOCK_ACACIA_LEAVES);
    }

    public static void terrain_plantTree(Terrain.GenSession terrain, Chunk sourceChunk, int x, int y, int z) {
        int height = randomInt(terrain.random, 3, 4);
        for (int k = 0; k < height; k++) {
            terrain.setBlockWorld(Blocks.BLOCK_ACACIA_LOG, x, y - (height - 1) + k, z);
        }

        int length = randomInt(terrain.random, 2, 4);
        int xDir = randomInt(terrain.random, -1, 1);
        int zDir = 0;
        if (xDir == 0) {
            zDir = terrain.random.nextBoolean() ? -1 : 1;
        }
        Vector3i vec = TreeUtils.terrain_generateBranch(terrain, sourceChunk, x, y - height + 1, z, length, xDir, zDir, Blocks.BLOCK_ACACIA_LOG);
        terrain.setBlockWorld(Blocks.BLOCK_ACACIA_LOG, vec.x, vec.y - 1, vec.z);
        terrain_treeBush(terrain, sourceChunk, vec.x, vec.y - 1, vec.z, randomInt(terrain.random, 3, 4));

        if (terrain.random.nextBoolean()) {
            if (xDir == 0) {
                zDir = 0 - zDir;
            } else {
                xDir = 0 - xDir;
            }
        } else {
            if (xDir == 0) {
                xDir += randomInt(terrain.random, -1, 1);
                zDir = 0;
            } else {
                zDir += randomInt(terrain.random, -1, 1);
                xDir = 0;
            }
        }
        length = randomInt(terrain.random, 3, 4);
        vec = TreeUtils.terrain_generateBranch(terrain, sourceChunk, x, y - height + 1, z, length, xDir, zDir, Blocks.BLOCK_ACACIA_LOG);
        terrain.setBlockWorld(Blocks.BLOCK_ACACIA_LOG, vec.x, vec.y - 1, vec.z);
        terrain_treeBush(terrain, sourceChunk, vec.x, vec.y - 1, vec.z, randomInt(terrain.random, 2, 3));
    }

}