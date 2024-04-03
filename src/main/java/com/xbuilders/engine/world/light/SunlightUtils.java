package com.xbuilders.engine.world.light;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.ChunkNode;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SunlightUtils {
    public static void addBrightestNeighboringNode(List<ChunkNode> queue, final Chunk c, final int x, final int y, final int z) {
        AtomicInteger brightestSunlight = new AtomicInteger(0);
        ChunkNode brightestNode = null;
        WCCi wcc = new WCCi();
//        System.out.println("Searching for brightest node " + x + " " + y + " " + z);

        wcc.getNeighboring(c.position, x, y - 1, z);
        ChunkNode node = checkNeighbor_getBrightestNeighboringNode(wcc, brightestSunlight);
        if (node != null) {
            brightestNode = node;
        }

        wcc.getNeighboring(c.position, x - 1, y, z);
        node = checkNeighbor_getBrightestNeighboringNode(wcc, brightestSunlight);
        if (node != null) {
            brightestNode = node;
        }

        wcc.getNeighboring(c.position, x, y, z - 1);
        node = checkNeighbor_getBrightestNeighboringNode(wcc, brightestSunlight);
        if (node != null) {
            brightestNode = node;
        }

        wcc.getNeighboring(c.position, x + 1, y, z);
        node = checkNeighbor_getBrightestNeighboringNode(wcc, brightestSunlight);
        if (node != null) {
            brightestNode = node;
        }

        wcc.getNeighboring(c.position, x, y, z + 1);
        node = checkNeighbor_getBrightestNeighboringNode(wcc, brightestSunlight);
        if (node != null) {
            brightestNode = node;
        }

        wcc.getNeighboring(c.position, x, y + 1, z);
        node = checkNeighbor_getBrightestNeighboringNode(wcc, brightestSunlight);
        if (node != null) {
            brightestNode = node;
        }


        if (brightestNode != null) {
//            System.out.println("Found brightest node: " + brightestNode + " light: " + brightestSunlight.get());
            queue.add(brightestNode);
        }
    }

    private static ChunkNode checkNeighbor_getBrightestNeighboringNode(WCCi coords,
                                                                       AtomicInteger brightestSunlight) {

//        System.out.println("\tChecking: " + coords.getChunk(GameScene.world) + " " + coords.toString());
        Chunk coordsChunk = coords.getChunk(GameScene.world);
        if (coordsChunk != null) {
            int lightVal = coordsChunk.data.getSun(coords.chunkVoxel.x, coords.chunkVoxel.y, coords.chunkVoxel.z);
//            System.out.println("\t\tNeighboring: " + MiscUtils.printVector(coordsChunk.position) + "): " + lightVal + " brightest: " + brightestSunlight.get());
            if (lightVal > brightestSunlight.get()) {
//                System.out.println("\t\t\tNew brightest: " + lightVal);
                brightestSunlight.set(lightVal);
                return new ChunkNode(coordsChunk, coords.chunkVoxel.x, coords.chunkVoxel.y, coords.chunkVoxel.z);
            }
        }
        return null;
    }

//    public static synchronized void updateFromQueue(List<ChunkNode> opaqueToTransparent, List<ChunkNode> transparentToOpaque) {
//        if (transparentToOpaque.size() > 0) {
//            HashSet<ChunkNode> adjacentNodes = new HashSet<>();
//            SunlightUtils.eraseSection(transparentToOpaque, adjacentNodes);
//            if (!adjacentNodes.isEmpty()) {
//                transparentToOpaque.clear();
//                transparentToOpaque.addAll(opaqueToTransparent);
//                transparentToOpaque.addAll(adjacentNodes);
//                SunlightUtils.propagateSunlight(transparentToOpaque);
//            } else if (opaqueToTransparent.size() > 0) {
//                SunlightUtils.propagateSunlight(opaqueToTransparent);
//            }
//        } else if (opaqueToTransparent.size() > 0) {
//            SunlightUtils.propagateSunlight(opaqueToTransparent);
//        }
//    }

    public static synchronized void eraseSection(List<ChunkNode> queue, HashSet<ChunkNode> totalNodes) {
        long timeStart = System.currentTimeMillis();
        while (queue.size() > 0 && System.currentTimeMillis() - timeStart < 10000L) {
            ChunkNode node = queue.remove(0);
            if (node == null) continue;
            byte lightValue = node.chunk.data.getSun(node.coords.x, node.coords.y, node.coords.z);
            node.chunk.data.setSun(node.coords.x, node.coords.y, node.coords.z, (byte) 0);
            totalNodes.remove(node);
            SunlightUtils.checkNeighborErase(node.chunk, node.coords.x - 1, node.coords.y, node.coords.z, lightValue, queue, totalNodes);
            SunlightUtils.checkNeighborErase(node.chunk, node.coords.x + 1, node.coords.y, node.coords.z, lightValue, queue, totalNodes);
            SunlightUtils.checkNeighborErase(node.chunk, node.coords.x, node.coords.y, node.coords.z + 1, lightValue, queue, totalNodes);
            SunlightUtils.checkNeighborErase(node.chunk, node.coords.x, node.coords.y, node.coords.z - 1, lightValue, queue, totalNodes);
            SunlightUtils.checkNeighborErase(node.chunk, node.coords.x, node.coords.y + 1, node.coords.z, lightValue, queue, totalNodes);
            SunlightUtils.checkNeighborErase(node.chunk, node.coords.x, node.coords.y - 1, node.coords.z, lightValue, queue, totalNodes);
        }
        queue.clear();
    }

    private static void checkNeighborErase(Chunk chunk, int x, int y, int z, int centerLightLevel, List<ChunkNode> queue, HashSet<ChunkNode> totalNodes) {
//        byte thisLevel;
//        if (chunk.inBounds(x, y, z)) {
//            thisLevel = chunk.data.getSun(x, y, z);
//        } else {
//            WCCi newCoords = WCCi.getNeighboringChunk(chunk.position, x, y, z);
//            chunk = GameScene.world.getChunk(newCoords.chunk);
//            x = newCoords.chunkVoxel.x;
//            y = newCoords.chunkVoxel.y;
//            z = newCoords.chunkVoxel.z;
//            if (chunk == null) {
//                return;
//            }
//            thisLevel = chunk.data.getSun(x, y, z);
//        }
//        if (thisLevel > 0) {
//            ChunkNode node = new ChunkNode(chunk, x, y, z);
//            if (centerLightLevel >= thisLevel && thisLevel < 15) {
//                queue.add(node);
//            } else if (thisLevel > 1) {
//                totalNodes.add(node);
//            }
//        }
    }

    public static void propagateSunlight(List<ChunkNode> queue, HashSet<Chunk> affectedChunks) {
        int originalSize = queue.size();
        while (queue.size() > 0) {
            ChunkNode node = queue.remove(0);
            if (node == null) continue;
            byte lightValue = node.chunk.data.getSun(node.coords.x, node.coords.y, node.coords.z);
            SunlightUtils.checkNeighbor(node.chunk, node.coords.x - 1, node.coords.y, node.coords.z, lightValue, queue, affectedChunks, false);
            SunlightUtils.checkNeighbor(node.chunk, node.coords.x + 1, node.coords.y, node.coords.z, lightValue, queue, affectedChunks, false);
            SunlightUtils.checkNeighbor(node.chunk, node.coords.x, node.coords.y, node.coords.z + 1, lightValue, queue, affectedChunks, false);
            SunlightUtils.checkNeighbor(node.chunk, node.coords.x, node.coords.y, node.coords.z - 1, lightValue, queue, affectedChunks, false);
            SunlightUtils.checkNeighbor(node.chunk, node.coords.x, node.coords.y + 1, node.coords.z, lightValue, queue, affectedChunks, true);
            SunlightUtils.checkNeighbor(node.chunk, node.coords.x, node.coords.y - 1, node.coords.z, lightValue, queue, affectedChunks, false);
//            System.out.println("Queue size: " + queue.size());
        }
        queue.clear();
    }

    private static void checkNeighbor(Chunk chunk, int x, int y, int z, int lightLevel,
                                      List<ChunkNode> queue, HashSet<Chunk> affectedChunks, boolean isBelowNode) {

        Block neigborBlock;
        if (chunk.inBounds(x, y, z)) {
            neigborBlock = ItemList.getBlock(chunk.data.getBlock(x, y, z));
        } else {
            final Vector3i neighboringChunk = new Vector3i();
            WCCi.getNeighboringChunk(neighboringChunk, chunk.position, x, y, z);

            chunk = GameScene.world.getChunk(neighboringChunk);
            if (chunk != null) {
                x = MathUtils.positiveMod(x, Chunk.WIDTH);
                y = MathUtils.positiveMod(y, Chunk.WIDTH);
                z = MathUtils.positiveMod(z, Chunk.WIDTH);
                neigborBlock = ItemList.getBlock(chunk.data.getBlock(x, y, z));
            } else return;
        }
        if (!neigborBlock.opaque) {
            if (isBelowNode && lightLevel == 15) {
                chunk.data.setSun(x, y, z, (byte) 15);
                affectedChunks.add(chunk);
                queue.add(new ChunkNode(chunk, x, y, z));
            } else if (chunk.data.getSun(x, y, z) + 2 <= lightLevel) {
                chunk.data.setSun(x, y, z, (byte) (lightLevel - 1));
                affectedChunks.add(chunk);
                queue.add(new ChunkNode(chunk, x, y, z));
            }
        }
    }
}

