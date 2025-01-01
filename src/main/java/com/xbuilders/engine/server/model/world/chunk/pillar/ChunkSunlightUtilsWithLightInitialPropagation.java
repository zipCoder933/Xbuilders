//package com.xbuilders.engine.game.model.world.chunk.pillar;
//
//import com.xbuilders.engine.game.model.GameScene;
//import com.xbuilders.engine.game.model.items.ItemList;
//import com.xbuilders.engine.game.model.items.block.Block;
//import com.xbuilders.engine.utils.BFS.ChunkNode;
//import com.xbuilders.engine.utils.math.MathUtils;
//import com.xbuilders.engine.game.model.world.Terrain;
//import com.xbuilders.engine.game.model.world.chunk.Chunk;
//import com.xbuilders.engine.game.model.world.wcc.WCCi;
//import org.joml.Vector3i;
//
//import java.util.ArrayList;
//
//import static com.xbuilders.engine.game.model.world.chunk.Chunk.WIDTH;
//
///**
// * This class assumes that we START with darkness and add the light nodes before the BFS
// * @author zipCoder933
// */
//public class ChunkSunlightUtilsWithLightInitialPropagation {
//
//    private static boolean isNeghboringDarkness(Chunk chunk, int x, int y, int z) {
//        if (x - 1 >= 0) {
//            if (chunk.data.getSun(x - 1, y, z) == 0 && chunk.data.getBlock(x - 1, y, z) == 0) {
//                return true;
//            }
//        } else {
//            Chunk neghboringChunk = GameScene.world
//                    .getChunk(new Vector3i(chunk.position.x - 1, chunk.position.y, chunk.position.z));
//            if (neghboringChunk != null && neghboringChunk.data.getSun(neghboringChunk.data.size.x - 1, y, z) == 0) {
//                return true;
//            }
//        }
//        if (x + 1 < WIDTH) {
//            if (chunk.data.getSun(x + 1, y, z) == 0) {
//                return true;
//            }
//        } else {
//            Chunk neghboringChunk = GameScene.world
//                    .getChunk(new Vector3i(chunk.position.x + 1, chunk.position.y, chunk.position.z));
//            if (neghboringChunk != null && neghboringChunk.data.getSun(0, y, z) == 0) {
//                return true;
//            }
//        }
//        if (z - 1 >= 0) {
//            if (chunk.data.getSun(x, y, z - 1) == 0) {
//                return true;
//            }
//        } else {
//            Chunk neghboringChunk = GameScene.world
//                    .getChunk(new Vector3i(chunk.position.x, chunk.position.y, chunk.position.z - 1));
//            if (neghboringChunk != null && neghboringChunk.data.getSun(x, y, neghboringChunk.data.size.z - 1) == 0) {
//                return true;
//            }
//        }
//        if (z + 1 < WIDTH) {
//            if (chunk.data.getSun(x, y, z + 1) == 0) {
//                return true;
//            }
//        } else {
//            Chunk neghboringChunk = GameScene.world
//                    .getChunk(new Vector3i(chunk.position.x, chunk.position.y, chunk.position.z + 1));
//            if (neghboringChunk != null && neghboringChunk.data.getSun(x, y, 0) == 0) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private static void addNodeToChunk(ArrayList<ChunkNode> queue, ChunkNode node, Chunk neghbor) {
//        queue.add(node);
////        if (neghbor.gen_sunLoaded()) queue.add(node);
////        else
////            neghbor.lightQueue.add(node); //The neghbor will alwyas be the pillar because we are propigating from the pillar
//    }
//
//    private static void addAnyBrightNeighbors(ArrayList<ChunkNode> queue, Chunk chunk, int x, int y, int z) {
//        //Negative x
//        if (x - 1 >= 0) {
//            if (chunk.data.getSun(x - 1, y, z) > 0) {
//                queue.add(new ChunkNode(chunk, x - 1, y, z));
//            }
//        } else {
//            Chunk neghboringChunk = GameScene.world
//                    .getChunk(new Vector3i(chunk.position.x - 1, chunk.position.y, chunk.position.z));
//            if (neghboringChunk != null && neghboringChunk.data.getSun(neghboringChunk.data.size.x - 1, y, z) > 0) {
//                ChunkNode node = new ChunkNode(neghboringChunk, neghboringChunk.data.size.x - 1, y, z);
//                addNodeToChunk(queue, node, neghboringChunk);
//            }
//        }
//
//        //Positive x
//        if (x + 1 < WIDTH) {
//            if (chunk.data.getSun(x + 1, y, z) > 0) {
//                queue.add(new ChunkNode(chunk, x + 1, y, z));
//            }
//        } else {
//            Chunk neghboringChunk = GameScene.world
//                    .getChunk(new Vector3i(chunk.position.x + 1, chunk.position.y, chunk.position.z));
//            if (neghboringChunk != null && neghboringChunk.data.getSun(0, y, z) > 0) {
//                ChunkNode node = (new ChunkNode(neghboringChunk, 0, y, z));
//                addNodeToChunk(queue, node, neghboringChunk);
//            }
//        }
//
//        //Negative z
//        if (z - 1 >= 0) {
//            if (chunk.data.getSun(x, y, z - 1) > 0) {
//                queue.add(new ChunkNode(chunk, x, y, z - 1));
//            }
//        } else {
//            Chunk neghboringChunk = GameScene.world
//                    .getChunk(new Vector3i(chunk.position.x, chunk.position.y, chunk.position.z - 1));
//            if (neghboringChunk != null && neghboringChunk.data.getSun(x, y, neghboringChunk.data.size.z - 1) > 0) {
//                ChunkNode node = (new ChunkNode(neghboringChunk, x, y, neghboringChunk.data.size.z - 1));
//                addNodeToChunk(queue, node, neghboringChunk);
//            }
//        }
//
//        //Positive z
//        if (z + 1 < WIDTH) {
//            if (chunk.data.getSun(x, y, z + 1) > 0) {
//                queue.add(new ChunkNode(chunk, x, y, z + 1));
//            }
//        } else {
//            Chunk neghboringChunk = GameScene.world
//                    .getChunk(new Vector3i(chunk.position.x, chunk.position.y, chunk.position.z + 1));
//            if (neghboringChunk != null && neghboringChunk.data.getSun(x, y, 0) > 0) {
//                ChunkNode node = (new ChunkNode(neghboringChunk, x, y, 0));
//                addNodeToChunk(queue, node, neghboringChunk);
//            }
//        }
//    }
//
//    static boolean generateSunlight(ArrayList<ChunkNode> queue, Chunk pillarChunk1, Terrain terrain) {
//
//        /**
//         * Placing nodes:
//         * - We cant add neghbors of dark nodes because we dont know what chunks or parts of a chunk have been lighted.
//         *   - We could leave sun at 15 by default and darken it
//         *      - if we add nodes that are 15 but should be darker, it doesnt matter because we are only propagating existing light, and another chunk should handle its own light
//         *     TODO: - If we add a light node on an uninitialized light chunk, that chunk should own that node, not us.
//         *      - Only fix this if we see a problem
//         */
//
//        //Add nodes in a separate loop so that most of the dark nodes are added first
//        /**
//         * If we add nodes in a separate loop, we
//         * + significantly reduce the initial number of nodes
//         * - have to iterate twice
//         */
//        for (int x = 0; x < WIDTH; x++) {
//            for (int z = 0; z < WIDTH; z++) {
//                boolean addSun = true;
//                for (Chunk chunk : pillarChunk1.pillarInformation.chunks) {// Go DOWN from Y
//                    for (int y = 0; y < Chunk.WIDTH; y++) {
//                        if (addSun) {
//                            Block block = ItemList.getBlock(chunk.data.getBlock(x, y, z));
//                            if (block.opaque) {
//                                WCCi wcc = new WCCi().setNeighboring(chunk.position, x, y - 1, z);
//                                queue.add(new ChunkNode(wcc, GameScene.world));
//                                addSun = false;
//                            } else chunk.data.setSun(x, y, z, (byte) 15);
//                        }
//                    }
//                }
//            }
//        }
//
//
//        while (!queue.isEmpty()) {
//            ChunkNode node = queue.remove(0);
////            node.chunk.data.setSun(node.x, node.y, node.z, (byte) 10); //KEEP. used to visualize where the nodes are placed initially
//
//            byte lightValue = node.chunk.data.getSun(node.x, node.y, node.z);
//
//            //Checking neighbors in the queue might be faster?
////            if (lightValue == 0) {
////                checkForLightNeighbor(node.chunk, node.x - 1, node.y, node.z, queue);
////                checkForLightNeighbor(node.chunk, node.x + 1, node.y, node.z, queue);
////                checkForLightNeighbor(node.chunk, node.x, node.y, node.z + 1, queue);
////                checkForLightNeighbor(node.chunk, node.x, node.y, node.z - 1, queue);
////                checkForLightNeighbor(node.chunk, node.x, node.y + 1, node.z, queue);
////                checkForLightNeighbor(node.chunk, node.x, node.y - 1, node.z, queue);
////            } else {
//            checkNeighbor(node.chunk, node.x - 1, node.y, node.z, lightValue, queue, false);
//            checkNeighbor(node.chunk, node.x + 1, node.y, node.z, lightValue, queue, false);
//            checkNeighbor(node.chunk, node.x, node.y, node.z + 1, lightValue, queue, false);
//            checkNeighbor(node.chunk, node.x, node.y, node.z - 1, lightValue, queue, false);
//            checkNeighbor(node.chunk, node.x, node.y + 1, node.z, lightValue, queue, true);
//            checkNeighbor(node.chunk, node.x, node.y - 1, node.z, lightValue, queue, false);
////            }
//        }
//
//        return true;
//    }
//
//    //DO NOT DELETE THIS CODE!!! KEPT FOR TESTING AND POSSIBLY FUTURE USE
////    private static void checkForLightNeighbor(Chunk chunk, int x, int y, int z, ArrayList<ChunkNode> queue) {
////        int neighborLevel = 0;
////        if (Chunk.inBounds(x, y, z)) {
////            neighborLevel = chunk.data.getSun(x, y, z);
////        } else {//If out of bounds, check the neighboring chunk
////            final Vector3i neighboringChunk = new Vector3i();
////            WCCi.getNeighboringChunk(neighboringChunk, chunk.position, x, y, z);
////
////            chunk = GameScene.world.getChunk(neighboringChunk);
////            if (chunk != null && chunk.gen_sunLoaded()) {
////                x = MathUtils.positiveMod(x, Chunk.WIDTH);
////                y = MathUtils.positiveMod(y, Chunk.WIDTH);
////                z = MathUtils.positiveMod(z, Chunk.WIDTH);
////                neighborLevel = chunk.data.getSun(x, y, z);
////            }
////        }
////        if (neighborLevel > 1) {
////            queue.add(new ChunkNode(chunk, x, y, z));
////        }
////    }
//
//    private static synchronized void checkNeighbor(Chunk chunk, int x, int y, int z, final byte lightLevel,
//                                                   final ArrayList<ChunkNode> queue, boolean isBelow) {
//        Block neigborBlock;
//        if (Chunk.inBounds(x, y, z)) {
//            neigborBlock = ItemList.getBlock(chunk.data.getBlock(x, y, z));
//        } else {
//            final Vector3i neighboringChunk = new Vector3i();
//            WCCi.getNeighboringChunk(neighboringChunk, chunk.position, x, y, z);
//
//            chunk = GameScene.world.getChunk(neighboringChunk);
//            if (chunk != null) {
//                x = MathUtils.positiveMod(x, Chunk.WIDTH);
//                y = MathUtils.positiveMod(y, Chunk.WIDTH);
//                z = MathUtils.positiveMod(z, Chunk.WIDTH);
//                neigborBlock = ItemList.getBlock(chunk.data.getBlock(x, y, z));
//            } else {
//                return;
//            }
//        }
//
//        if (!neigborBlock.opaque) {
//            if (isBelow && lightLevel == 15) {
//                chunk.data.setSun(x, y, z, (byte) 15);
//                queue.add(new ChunkNode(chunk, x, y, z));
//            } else {
//                final int neighborLevel = chunk.data.getSun(x, y, z);
//                if (neighborLevel + 2 <= lightLevel) {
//                    chunk.data.setSun(x, y, z, (byte) (lightLevel - 1));
//                    queue.add(new ChunkNode(chunk, x, y, z));
//                }
//            }
//        }
//    }
//
//}
