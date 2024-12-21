/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.game.model.world.chunk.pillar;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.Registrys;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.utils.BFS.ChunkNode;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.game.model.world.chunk.Chunk;
import com.xbuilders.engine.game.model.world.wcc.WCCi;
import org.joml.Vector3i;

import java.util.ArrayList;

import static com.xbuilders.engine.game.model.world.chunk.Chunk.WIDTH;

/**
 * @author zipCoder933
 */
public class ChunkSunlightGenerator {

    private static boolean isNeghboringDarkness(Chunk chunk, int x, int y, int z) {
        if (x - 1 >= 0) {
            if (chunk.data.getSun(x - 1, y, z) == 0 && chunk.data.getBlock(x - 1, y, z) == 0) {
                return true;
            }
        } else {
            Chunk neghboringChunk = GameScene.world
                    .getChunk(new Vector3i(chunk.position.x - 1, chunk.position.y, chunk.position.z));
            if (neghboringChunk != null && neghboringChunk.data.getSun(neghboringChunk.data.size.x - 1, y, z) == 0) {
                return true;
            }
        }
        if (x + 1 < WIDTH) {
            if (chunk.data.getSun(x + 1, y, z) == 0) {
                return true;
            }
        } else {
            Chunk neghboringChunk = GameScene.world
                    .getChunk(new Vector3i(chunk.position.x + 1, chunk.position.y, chunk.position.z));
            if (neghboringChunk != null && neghboringChunk.data.getSun(0, y, z) == 0) {
                return true;
            }
        }
        if (z - 1 >= 0) {
            if (chunk.data.getSun(x, y, z - 1) == 0) {
                return true;
            }
        } else {
            Chunk neghboringChunk = GameScene.world
                    .getChunk(new Vector3i(chunk.position.x, chunk.position.y, chunk.position.z - 1));
            if (neghboringChunk != null && neghboringChunk.data.getSun(x, y, neghboringChunk.data.size.z - 1) == 0) {
                return true;
            }
        }
        if (z + 1 < WIDTH) {
            if (chunk.data.getSun(x, y, z + 1) == 0) {
                return true;
            }
        } else {
            Chunk neghboringChunk = GameScene.world
                    .getChunk(new Vector3i(chunk.position.x, chunk.position.y, chunk.position.z + 1));
            if (neghboringChunk != null && neghboringChunk.data.getSun(x, y, 0) == 0) {
                return true;
            }
        }
        return false;
    }


    private static void addAnyBrightNeighbors(ArrayList<ChunkNode> queue, Chunk chunk, int x, int y, int z) {
        //Negative x
        if (x - 1 >= 0) {
            if (chunk.data.getSun(x - 1, y, z) > 0) {
                addNodeToQueue(queue, chunk, x - 1, y, z);
            }
        } else {
            Chunk neghboringChunk = GameScene.world
                    .getChunk(new Vector3i(chunk.position.x - 1, chunk.position.y, chunk.position.z));
            if (neghboringChunk != null && neghboringChunk.gen_sunLoaded() && neghboringChunk.data.getSun(neghboringChunk.data.size.x - 1, y, z) > 0) {
                addNodeToInitialQueue(queue, neghboringChunk, neghboringChunk.data.size.x - 1, y, z, neghboringChunk);
            }
        }

        //Positive x
        if (x + 1 < WIDTH) {
            if (chunk.data.getSun(x + 1, y, z) > 0) {
                addNodeToQueue(queue, chunk, x + 1, y, z);
            }
        } else {
            Chunk neghboringChunk = GameScene.world
                    .getChunk(new Vector3i(chunk.position.x + 1, chunk.position.y, chunk.position.z));
            if (neghboringChunk != null && neghboringChunk.gen_sunLoaded() && neghboringChunk.data.getSun(0, y, z) > 0) {
                addNodeToInitialQueue(queue, neghboringChunk, 0, y, z, neghboringChunk);
            }
        }

        //Negative z
        if (z - 1 >= 0) {
            if (chunk.data.getSun(x, y, z - 1) > 0) {
                addNodeToQueue(queue, chunk, x, y, z - 1);
            }
        } else {
            Chunk neghboringChunk = GameScene.world
                    .getChunk(new Vector3i(chunk.position.x, chunk.position.y, chunk.position.z - 1));
            if (neghboringChunk != null && neghboringChunk.gen_sunLoaded() && neghboringChunk.data.getSun(x, y, neghboringChunk.data.size.z - 1) > 0) {
                addNodeToInitialQueue(queue, neghboringChunk, x, y, neghboringChunk.data.size.z - 1, neghboringChunk);
            }
        }

        //Positive z
        if (z + 1 < WIDTH) {
            if (chunk.data.getSun(x, y, z + 1) > 0) {
                addNodeToQueue(queue, chunk, x, y, z + 1);
            }
        } else {
            Chunk neghboringChunk = GameScene.world
                    .getChunk(new Vector3i(chunk.position.x, chunk.position.y, chunk.position.z + 1));
            if (neghboringChunk != null && neghboringChunk.gen_sunLoaded() && neghboringChunk.data.getSun(x, y, 0) > 0) {
                addNodeToInitialQueue(queue, neghboringChunk, x, y, 0, neghboringChunk);
            }
        }
    }


   public static boolean generateSunlight(Chunk pillarChunk1) {
        ArrayList<ChunkNode> queue = new ArrayList<>();
        /**
         * Placing nodes:
         * - We cant add neghbors of dark nodes because we dont know what chunks or parts of a chunk have been lighted.
         *   - We could leave sun at 15 by default and darken it
         *      - if we add nodes that are 15 but should be darker, it doesnt matter because we are only propagating existing light, and another chunk should handle its own light
         *     TODO: - If we add a light node on an uninitialized light chunk, that chunk should own that node, not us.
         *      - Only fix this if we see a problem
         * - we can skip checking nodes after the first one if we continue BFS downward
         */

        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {
                boolean addSun = true;
                for (Chunk chunk : pillarChunk1.pillarInformation.chunks) {// Go DOWN from Y
//                    if(terrain.isBelowMinHeight(chunk.position,-2)) //Lets wait before implementing this. I dont want issues to be caused with saved chunks
                    for (int y = 0; y < Chunk.WIDTH; y++) {
                        Block block = Registrys.getBlock(chunk.data.getBlock(x, y, z));
                        if (addSun) {
                            if (block.opaque) {
                                // Place a node just above the opaque block
//                                int worldY = (chunk.position.y * Chunk.WIDTH) + y - 1;
//                                int blockY = positiveMod(worldY, Chunk.WIDTH);
//                                Chunk adjChunk = GameScene.world.getChunk(new Vector3i(
//                                        chunk.position.x,
//                                        chunkDiv(worldY),
//                                        chunk.position.z));
//                                if (adjChunk != null) {
//                                    queue.add(new ChunkNode(adjChunk, x, blockY, z));
//                                }
                                chunk.data.setSun(x, y, z, (byte) 0);
                                addSun = false;
                            }
                        } else {
                            chunk.data.setSun(x, y, z, (byte) 0);
//                            if (!block.opaque) { //Adding nodes here is ok, but incredibly redundant! because too many nodes get added because the chunk isnt finished
//                                addAnyBrightNeighbors(queue, chunk, x, y, z);
//                            }
                        }
                    }
                }
            }
        }

        //Add nodes in a separate loop so that most of the dark nodes are added first
        /**
         * If we add nodes in a separate loop, we
         * + significantly reduce the initial number of nodes
         * - have to iterate twice
         */
        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {

                yLoop:
                for (Chunk chunk : pillarChunk1.pillarInformation.chunks) {// Go DOWN from Y
                    for (int y = 0; y < Chunk.WIDTH; y++) {
                        Block block = Registrys.getBlock(chunk.data.getBlock(x, y, z));
                        if (!block.opaque) {
                            int sun = chunk.data.getSun(x, y, z);
                            if (sun == 0) {
//                                addNodeToQueue(queue,chunk, x, y, z));
                                addAnyBrightNeighbors(queue, chunk, x, y, z);
                                break yLoop; //If we propagate downward via BFS, we dont have to check the rest of the chunk.
                                // This also saves time because addAnyBrightNeighbors() takes a relatively fair amount of time
                            }
                        }
                    }
                }
            }
        }


        while (!queue.isEmpty()) {
            ChunkNode node = removeNode(queue);
//            node.chunk.data.setSun(node.x, node.y, node.z, (byte) 10); //KEEP. used to visualize where the nodes are placed initially

            byte lightValue = node.chunk.data.getSun(node.x, node.y, node.z);

            //Checking neighbors in the queue might be faster?
//            if (lightValue == 0) {
//                checkForLightNeighbor(node.chunk, node.x - 1, node.y, node.z, queue);
//                checkForLightNeighbor(node.chunk, node.x + 1, node.y, node.z, queue);
//                checkForLightNeighbor(node.chunk, node.x, node.y, node.z + 1, queue);
//                checkForLightNeighbor(node.chunk, node.x, node.y, node.z - 1, queue);
//                checkForLightNeighbor(node.chunk, node.x, node.y + 1, node.z, queue);
//                checkForLightNeighbor(node.chunk, node.x, node.y - 1, node.z, queue);
//            } else {
            checkNeighbor(node.chunk, node.x - 1, node.y, node.z, lightValue, queue, false);
            checkNeighbor(node.chunk, node.x + 1, node.y, node.z, lightValue, queue, false);
            checkNeighbor(node.chunk, node.x, node.y, node.z + 1, lightValue, queue, false);
            checkNeighbor(node.chunk, node.x, node.y, node.z - 1, lightValue, queue, false);
            checkNeighbor(node.chunk, node.x, node.y + 1, node.z, lightValue, queue, true);
            checkNeighbor(node.chunk, node.x, node.y - 1, node.z, lightValue, queue, false);
//            }
        }

        return true;
    }

    //DO NOT DELETE THIS CODE!!! KEPT FOR TESTING AND POSSIBLY FUTURE USE
//    private static void checkForLightNeighbor(Chunk chunk, int x, int y, int z, ArrayList<ChunkNode> queue) {
//        int neighborLevel = 0;
//        if (Chunk.inBounds(x, y, z)) {
//            neighborLevel = chunk.data.getSun(x, y, z);
//        } else {//If out of bounds, check the neighboring chunk
//            final Vector3i neighboringChunk = new Vector3i();
//            WCCi.getNeighboringChunk(neighboringChunk, chunk.position, x, y, z);
//
//            chunk = GameScene.world.getChunk(neighboringChunk);
//            if (chunk != null && chunk.gen_sunLoaded()) {
//                x = MathUtils.positiveMod(x, Chunk.WIDTH);
//                y = MathUtils.positiveMod(y, Chunk.WIDTH);
//                z = MathUtils.positiveMod(z, Chunk.WIDTH);
//                neighborLevel = chunk.data.getSun(x, y, z);
//            }
//        }
//        if (neighborLevel > 1) {
//            queue.add(new ChunkNode(chunk, x, y, z));
//        }
//    }

    private static synchronized void checkNeighbor(Chunk chunk, int x, int y, int z, final byte lightLevel,
                                                   final ArrayList<ChunkNode> queue, boolean isBelow) {
        Block neigborBlock;
        if (Chunk.inBounds(x, y, z)) {
            neigborBlock = Registrys.getBlock(chunk.data.getBlock(x, y, z));
        } else {
            final Vector3i neighboringChunk = new Vector3i();
            WCCi.getNeighboringChunk(neighboringChunk, chunk.position, x, y, z);

            chunk = GameScene.world.getChunk(neighboringChunk);
            if (chunk != null) {
                x = MathUtils.positiveMod(x, Chunk.WIDTH);
                y = MathUtils.positiveMod(y, Chunk.WIDTH);
                z = MathUtils.positiveMod(z, Chunk.WIDTH);
                neigborBlock = Registrys.getBlock(chunk.data.getBlock(x, y, z));
            } else {
                return;
            }
        }

        if (!neigborBlock.opaque) {
            if (isBelow && lightLevel == 15) {
                chunk.data.setSun(x, y, z, (byte) 15);
                addNodeToQueue(queue, chunk, x, y, z);
            } else {
                final int neighborLevel = chunk.data.getSun(x, y, z);
                if (neighborLevel + 2 <= lightLevel) {
                    chunk.data.setSun(x, y, z, (byte) (lightLevel - 1));
                    addNodeToQueue(queue, chunk, x, y, z);
                }
            }
        }
    }

    private static void addNodeToInitialQueue(ArrayList<ChunkNode> queue,
                                              Chunk chunk, int x, int y, int z,
                                              Chunk neghbor) {

//        if (Main.devkeyF4) {
            queue.add(new ChunkNode(chunk, x, y, z));
//        } else {
//
////Add initially before BFS propagation
//            if (unusedNodes.isEmpty()) {
//                queue.add(new ChunkNode(chunk, x, y, z));
//                Main.frameTester.count("New sun nodes", 1);
//            } else {
//                ChunkNode node = unusedNodes.remove(0);
//                node.set(chunk, x, y, z);
//                queue.add(node);
//            }
//        }
    }

    private static void addNodeToQueue(ArrayList<ChunkNode> queue, Chunk chunk, int x, int y, int z) {
//        if (Main.devkeyF4) {
            queue.add(new ChunkNode(chunk, x, y, z));
//        } else {
//            if (unusedNodes.isEmpty()) {
//                queue.add(new ChunkNode(chunk, x, y, z));
//                Main.frameTester.count("New sun nodes", 1);
//            } else {
//                ChunkNode node = unusedNodes.remove(0);
//                node.set(chunk, x, y, z);
//                queue.add(node);
//            }
//        }
    }

//    private static final List<ChunkNode> unusedNodes = Collections.synchronizedList(new ArrayList<ChunkNode>());

    private static ChunkNode removeNode(ArrayList<ChunkNode> queue) {
        ChunkNode node = queue.remove(0);
//        if (!Main.devkeyF4) unusedNodes.add(node);
        return node;
    }

}
