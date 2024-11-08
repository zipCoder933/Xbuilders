/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.terrain;

import com.xbuilders.engine.world.Terrain;
import com.xbuilders.game.Blocks;

import java.util.Random;

/**
 * @author zipCoder933
 */
public class TreeUtils {

    public static void makeTree(Random random, Terrain.GenSession session, int treeX, int treeY, int treeZ) {
        int height = random.nextInt(7) + 3;

        for (int x = 0; x < 5; x++) {
            for (int y = -3; y < 0; y++) {
                for (int z = 0; z < 5; z++) {
                    if ((x == 0 && z == 0)
                            || (x == 4 && z == 4)
                            || (x == 0 && z == 4)
                            || (x == 4 && z == 0)) {
                    } else {
                        session.setBlockWorld(
                                Blocks.BLOCK_OAK_LEAVES, x + treeX - 2, y + treeY - height +2, z + treeZ - 2
                        );
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            session.setBlockWorld(Blocks.BLOCK_OAK_LOG, treeX, y + treeY - height, treeZ);
        }
    }
}
