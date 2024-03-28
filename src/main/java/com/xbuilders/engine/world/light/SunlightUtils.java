package com.xbuilders.engine.world.light;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.ChunkNode;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.List;

public class SunlightUtils {
    public static synchronized void updateFromQueue(List<ChunkNode> opaqueToTransparent, List<ChunkNode> transparentToOpaque) {
        if (transparentToOpaque.size() > 0) {
            HashSet<ChunkNode> adjacentNodes = new HashSet<>();
            SunlightUtils.eraseSection(transparentToOpaque, adjacentNodes);
            if (!adjacentNodes.isEmpty()) {
                transparentToOpaque.clear();
                transparentToOpaque.addAll(opaqueToTransparent);
                transparentToOpaque.addAll(adjacentNodes);
                SunlightUtils.propagateSunlight(transparentToOpaque);
            } else if (opaqueToTransparent.size() > 0) {
                SunlightUtils.propagateSunlight(opaqueToTransparent);
            }
        } else if (opaqueToTransparent.size() > 0) {
            SunlightUtils.propagateSunlight(opaqueToTransparent);
        }
    }

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

    public static synchronized void propagateSunlight(List<ChunkNode> queue) {
//        int originalSize = queue.size();
//        while (queue.size() > 0) {
//            ChunkNode node = queue.remove(0);
//            if (node == null) continue;
//            byte lightValue = node.chunk.data.getSun(node.coords.x, node.coords.y, node.coords.z);
//            SunlightUtils.checkNeighbor(node.chunk, node.coords.x - 1, node.coords.y, node.coords.z, lightValue, queue);
//            SunlightUtils.checkNeighbor(node.chunk, node.coords.x + 1, node.coords.y, node.coords.z, lightValue, queue);
//            SunlightUtils.checkNeighbor(node.chunk, node.coords.x, node.coords.y, node.coords.z + 1, lightValue, queue);
//            SunlightUtils.checkNeighbor(node.chunk, node.coords.x, node.coords.y, node.coords.z - 1, lightValue, queue);
//            SunlightUtils.checkNeighbor(node.chunk, node.coords.x, node.coords.y + 1, node.coords.z, lightValue, queue);
//            SunlightUtils.checkNeighbor(node.chunk, node.coords.x, node.coords.y - 1, node.coords.z, lightValue, queue);
//            float percentage = 1.0f - (float) queue.size() / (float) originalSize;
//          System.out.println("Propagating Sunlight (" + Math.round(percentage * 100.0f) + "%)");
//        }
//        queue.clear();
    }

    private static void checkNeighbor(Chunk chunk, int x, int y, int z, int lightLevel, List<ChunkNode> queue) {
//        byte neighborLevel;
//        Block neigborBlock;
//        if (chunk.inBounds(x, y, z)) {
//            neigborBlock = ItemList.getBlock(chunk.data.getBlock(x, y, z));
//        } else {
//            Vector3i neighboringChunk = WCCi.getNeighboringChunk(chunk.position, x, y, z);
//            Vector3i neighboringChunkBlockCoords = WCCi.normalizeToChunkSpace(x, y, z);
//            chunk = GameScene.world.getChunk(neighboringChunk);
//            x = neighboringChunkBlockCoords.x;
//            y = neighboringChunkBlockCoords.y;
//            z = neighboringChunkBlockCoords.z;
//            if (chunk == null) {
//                return;
//            }
//            neigborBlock = ItemList.getBlock(chunk.data.getBlock(x, y, z));
//        }
//        if (!neigborBlock.opaque && (neighborLevel = chunk.data.getSun(x, y, z)) + 2 <= lightLevel) {
//            chunk.data.setSun(x, y, z, (byte) (lightLevel - 1));
//            queue.add(new ChunkNode(chunk, x, y, z));
//        }
    }
}

