/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.trees;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.BFS.TravelNode;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * @author zipCoder933
 */
class TreeUtils {

    public static final long WAIT_TIME = 60 * 1000;

    public static int randomInt(Random rand, int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public static void squareLeavesLayer(int x, int y, int z, int radius, short leaves) {
        int lowerBoundX = x - radius;
        int upperBoundX = x + radius;
        int lowerBoundZ = z - radius;
        int upperBoundZ = z + radius;

        for (int x2 = lowerBoundX; x2 <= upperBoundX; x2++) {
            for (int z2 = lowerBoundZ; z2 <= upperBoundZ; z2++) {

                setBlock(leaves, x2, y, z2);

            }
        }
    }

    public static void squareLeavesLayer(Terrain.GenSession terrain, Chunk source, int x, int y, int z, int radius, short leaves) {
        int lowerBoundX = x - radius;
        int upperBoundX = x + radius;
        int lowerBoundZ = z - radius;
        int upperBoundZ = z + radius;

        for (int x2 = lowerBoundX; x2 <= upperBoundX; x2++) {
            for (int z2 = lowerBoundZ; z2 <= upperBoundZ; z2++) {
                if (!GameScene.world.getBlock(x2, y, z2).solid) {
                    terrain.setBlockWorld(x2, y, z2, leaves);
                }
            }
        }
    }

    public static void roundedSquareLeavesLayer(int x, int y, int z, int radius, short leaves) {
        int lowerBoundX = x - radius;
        int upperBoundX = x + radius;
        int lowerBoundZ = z - radius;
        int upperBoundZ = z + radius;

        for (int x2 = lowerBoundX; x2 <= upperBoundX; x2++) {
            for (int z2 = lowerBoundZ; z2 <= upperBoundZ; z2++) {

                if (!((x2 == lowerBoundX && z2 == lowerBoundZ)
                        || (x2 == upperBoundX && z2 == upperBoundZ)
                        || (x2 == lowerBoundX && z2 == upperBoundZ)
                        || (x2 == upperBoundX && z2 == lowerBoundZ))) {
                    setBlock(leaves, x2, y, z2);
                }
            }
        }
    }

    public static void roundedSquareLeavesLayer(Terrain.GenSession terrain, Chunk source, int x, int y, int z, int radius, short leaves) {
        int lowerBoundX = x - radius;
        int upperBoundX = x + radius;
        int lowerBoundZ = z - radius;
        int upperBoundZ = z + radius;

        for (int x2 = lowerBoundX; x2 <= upperBoundX; x2++) {
            for (int z2 = lowerBoundZ; z2 <= upperBoundZ; z2++) {

                if (!((x2 == lowerBoundX && z2 == lowerBoundZ)
                        || (x2 == upperBoundX && z2 == upperBoundZ)
                        || (x2 == lowerBoundX && z2 == upperBoundZ)
                        || (x2 == upperBoundX && z2 == lowerBoundZ))) {
                    if (!GameScene.world.getBlock(x2, y, z2).solid) {
                        terrain.setBlockWorld(x2, y, z2, leaves);
                    }
                }
            }
        }
    }

    public static void setBlock(short id, int x, int y, int z) {
        if (!GameScene.world.getBlock(x, y, z).solid) {
            GameScene.player.setBlock(id, x, y, z);
        }
    }

    public static void setBlockAndOverride(short id, int x, int y, int z) {
        GameScene.player.setBlock(id, x, y, z);
    }

    //Terrain generators create terrains in world space,
    //so there is nothing we have to change here.
    public static void diamondLeavesLayer(int x, int y, int z, int travelDist, short leaves) {
        HashSet<TravelNode> exploredNodes = new HashSet<>();
        ArrayList<TravelNode> queue = new ArrayList<>();
        queue.add(new TravelNode(x, y, z, 0));

        while (!queue.isEmpty()) {
            TravelNode node = queue.remove(0);
            if (!exploredNodes.contains(node) && node.travel < travelDist) {
                setBlock(leaves, node.x, node.y, node.z);
                queue.add(new TravelNode(node.x + 1, node.y, node.z, node.travel + 1));
                queue.add(new TravelNode(node.x, node.y, node.z + 1, node.travel + 1));
                queue.add(new TravelNode(node.x - 1, node.y, node.z, node.travel + 1));
                queue.add(new TravelNode(node.x, node.y, node.z - 1, node.travel + 1));
                exploredNodes.add(node);
            }
        }
    }


    public static void diamondLeavesLayer(Terrain.GenSession terrain, Chunk source, int x, int y, int z, int travelDist, short leaves) {
        ArrayList<TravelNode> queue = new ArrayList<>();
        queue.add(new TravelNode(x, y, z, 0));

        while (!queue.isEmpty()) {
            TravelNode node = queue.remove(0);
            Block block = GameScene.world.getBlock(node.x, node.y, node.z);

            if (!block.equals(leaves) && node.travel < travelDist) {
                if (!block.solid) {
                    terrain.setBlockWorld(node.x, node.y, node.z, leaves);
                }
                queue.add(new TravelNode(node.x + 1, node.y, node.z, node.travel + 1));
                queue.add(new TravelNode(node.x, node.y, node.z + 1, node.travel + 1));
                queue.add(new TravelNode(node.x - 1, node.y, node.z, node.travel + 1));
                queue.add(new TravelNode(node.x, node.y, node.z - 1, node.travel + 1));
            }
        }
    }

    public static Vector3i generateBranch(int x, int y, int z, int length, int xDir, int zDir, short logType) {
        for (int i = 0; i < length; i++) {
            x += xDir;
            z += zDir;
            y--;
            setBlock(logType, x, y, z);
        }
        return new Vector3i(x, y, z);
    }

    public static Vector3i generateBranch(Terrain.GenSession terrain, Chunk source, int x, int y, int z, int length, int xDir, int zDir, short logType) {
        for (int i = 0; i < length; i++) {
            x += xDir;
            z += zDir;
            y--;
            if (!GameScene.world.getBlock(x, y, z).solid) {
                terrain.setBlockWorld(x, y, z, logType);
            }
        }
        return new Vector3i(x, y, z);
    }
}
