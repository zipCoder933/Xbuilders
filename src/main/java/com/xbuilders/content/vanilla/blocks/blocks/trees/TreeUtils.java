/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.blocks.blocks.trees;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.utils.BFS.TravelNode;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.engine.server.world.chunk.Chunk;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * @author zipCoder933
 */
class TreeUtils {



    public static int randomInt(Random rand, int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public static void player_squareLeavesLayer(int x, int y, int z, int radius, short leaves) {
        int lowerBoundX = x - radius;
        int upperBoundX = x + radius;
        int lowerBoundZ = z - radius;
        int upperBoundZ = z + radius;

        for (int x2 = lowerBoundX; x2 <= upperBoundX; x2++) {
            for (int z2 = lowerBoundZ; z2 <= upperBoundZ; z2++) {
                player_setBlock(leaves, x2, y, z2);
            }
        }
    }

    public static void terrain_squareLeavesLayer(Terrain.GenSession terrain, Chunk source, int x, int y, int z, int radius, short leaves) {
        int lowerBoundX = x - radius;
        int upperBoundX = x + radius;
        int lowerBoundZ = z - radius;
        int upperBoundZ = z + radius;

        for (int x2 = lowerBoundX; x2 <= upperBoundX; x2++) {
            for (int z2 = lowerBoundZ; z2 <= upperBoundZ; z2++) {
                if (!LocalClient.world.getBlock(x2, y, z2).solid) {
                    terrain.setBlockWorld(x2, y, z2, leaves);
                }
            }
        }
    }

    public static void player_roundedSquareLeavesLayer(int x, int y, int z, int radius, short leaves) {
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
                    player_setBlock(leaves, x2, y, z2);
                }
            }
        }
    }

    public static void terrain_roundedSquareLeavesLayer(Terrain.GenSession terrain, Chunk source, int x, int y, int z, int radius, short leaves) {
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
                    if (!LocalClient.world.getBlock(x2, y, z2).solid) {
                        terrain.setBlockWorld(x2, y, z2, leaves);
                    }
                }
            }
        }
    }

    public static void player_setBlock(short id, int x, int y, int z) {
        if (!LocalClient.world.getBlock(x, y, z).solid) {
            LocalServer.setBlock(id, x, y, z);
        }
    }

    public static void player_setBlockAndOverride(short id, int x, int y, int z) {
        LocalServer.setBlock(id, x, y, z);
    }

    //Terrain generators create terrains in world space,
    //so there is nothing we have to change here.
    public static void player_diamondLeavesLayer(int x, int y, int z, int travelDist, short leaves) {
        HashSet<TravelNode> exploredNodes = new HashSet<>();
        ArrayList<TravelNode> queue = new ArrayList<>();
        queue.add(new TravelNode(x, y, z, 0));

        while (!queue.isEmpty()) {
            TravelNode node = queue.remove(0);
            if (!exploredNodes.contains(node) && node.travel < travelDist) {
                player_setBlock(leaves, node.x, node.y, node.z);
                queue.add(new TravelNode(node.x + 1, node.y, node.z, node.travel + 1));
                queue.add(new TravelNode(node.x, node.y, node.z + 1, node.travel + 1));
                queue.add(new TravelNode(node.x - 1, node.y, node.z, node.travel + 1));
                queue.add(new TravelNode(node.x, node.y, node.z - 1, node.travel + 1));
                exploredNodes.add(node);
            }
        }
    }


    public static void terrain_diamondLeavesLayer(Terrain.GenSession terrain, Chunk source, int x, int y, int z, int travelDist, short leaves) {
        ArrayList<TravelNode> queue = new ArrayList<>();
        queue.add(new TravelNode(x, y, z, 0));

        while (!queue.isEmpty()) {
            TravelNode node = queue.remove(0);
            Block block = LocalClient.world.getBlock(node.x, node.y, node.z);

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

    public static Vector3i player_generateBranch(int x, int y, int z, int length, int xDir, int zDir, short logType) {
        for (int i = 0; i < length; i++) {
            x += xDir;
            z += zDir;
            y--;
            player_setBlock(logType, x, y, z);
        }
        return new Vector3i(x, y, z);
    }

    public static Vector3i terrain_generateBranch(Terrain.GenSession terrain, Chunk source, int x, int y, int z, int length, int xDir, int zDir, short logType) {
        for (int i = 0; i < length; i++) {
            x += xDir;
            z += zDir;
            y--;
            if (!LocalClient.world.getBlock(x, y, z).solid) {
                terrain.setBlockWorld(x, y, z, logType);
            }
        }
        return new Vector3i(x, y, z);
    }
}
