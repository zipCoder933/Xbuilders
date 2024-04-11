package com.xbuilders.engine.world.light;


import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.ChunkNode;
import com.xbuilders.engine.world.wcc.WCCi;

import java.util.*;

public class TorchUtils {

    //TODO: Make multi falloff rate work with only 1 torch channel

    public static void transparentToOpaque(HashSet<Chunk> affectedChunks, final Chunk chunk, final int x, final int y, final int z) {
        final List<ChunkNode> queue = new ArrayList<>();
        queue.add(new ChunkNode(chunk, x, y, z));
        final HashSet<ChunkNode> edges = eraseSection(affectedChunks, queue);
        queue.clear();
        queue.addAll(edges);
        continueBFS(affectedChunks, queue, (byte) 1);
    }

    public static void opaqueToTransparent(HashSet<Chunk> affectedChunks, final Chunk chunk, final int x, final int y, final int z) {
        final List<ChunkNode> queue = new ArrayList<>();
        startingNodesForPropagation(queue, chunk, x, y, z);
        continueBFS(affectedChunks, queue, (byte) 1);
    }

    protected static void startingNodesForPropagation(List<ChunkNode> queue, final Chunk chunk, final int x, final int y, final int z) {
        findNeg2(chunk, x + 1, y, z, queue);
        findNeg2(chunk, x - 1, y, z, queue);
        findNeg2(chunk, x, y + 1, z, queue);
        findNeg2(chunk, x, y - 1, z, queue);
        findNeg2(chunk, x, y, z + 1, queue);
        findNeg2(chunk, x, y, z - 1, queue);
    }

    private static void findNeg2(Chunk chunk, int x, int y, int z, final List<ChunkNode> queue) {
        if (!Chunk.inBounds(x, y, z)) {//Correct coordinates if out of bounds
            final WCCi wcc = new WCCi().setNeighboring(chunk.position, x, y, z);
            chunk = wcc.getChunk(GameScene.world);
            if (chunk == null) {
                return;
            }
            x = wcc.chunkVoxel.x;
            y = wcc.chunkVoxel.y;
            z = wcc.chunkVoxel.z;
        }
        if (chunk.data.getTorch(x, y, z) > 0) {
            queue.add(new ChunkNode(chunk, x, y, z));
        }
    }


    //------------------------------------------------------------------
    //------------------------------------------------------------------
    //General utilities
    //------------------------------------------------------------------
    //------------------------------------------------------------------

    public static boolean isTorchAtThisPosition(final Chunk chunk, final int x, final int y, final int z) {
        return chunk.data.getTorch(x, y, z) == 15;
    }

    public static void setTorch(HashSet<Chunk> affectedChunks, final Chunk chunk, final int x, final int y, final int z, byte lightFallof) {
        if (!isTorchAtThisPosition(chunk, x, y, z)) {
            lightFallof = (byte) MathUtils.clamp(lightFallof, 1, 15);
            final List<ChunkNode> queue = new ArrayList<>();
            queue.add(new ChunkNode(chunk, x, y, z));
            chunk.data.setTorch(x, y, z, (byte) 15);
            affectedChunks.add(chunk);
            continueBFS(affectedChunks, queue, lightFallof);
        }
    }

    public static void removeTorchlight(HashSet<Chunk> affectedChunks, final Chunk chunk, final int x, final int y, final int z, final byte lightFalloff) {
        if (chunk.data.getTorch(x, y, z) == 15) {
            final List<ChunkNode> queue = new ArrayList<>();
            queue.add(new ChunkNode(chunk, x, y, z));
            final HashSet<ChunkNode> edges = eraseSection(affectedChunks, queue);
            queue.clear();
            queue.addAll(edges);
            continueBFS(affectedChunks, queue, lightFalloff);
        }
    }


    public static void continueBFS(HashSet<Chunk> affectedChunks, final List<ChunkNode> queue, final byte falloff) {
        while (queue.size() > 0) {
            final ChunkNode node = queue.remove(0);
            final int lightValue = node.chunk.data.getTorch(node.x, node.y, node.z);
            checkNeighborCont(node.chunk, node.x - 1, node.y, node.z, lightValue, queue, falloff, affectedChunks);
            checkNeighborCont(node.chunk, node.x + 1, node.y, node.z, lightValue, queue, falloff, affectedChunks);
            checkNeighborCont(node.chunk, node.x, node.y, node.z + 1, lightValue, queue, falloff, affectedChunks);
            checkNeighborCont(node.chunk, node.x, node.y, node.z - 1, lightValue, queue, falloff, affectedChunks);
            checkNeighborCont(node.chunk, node.x, node.y + 1, node.z, lightValue, queue, falloff, affectedChunks);
            checkNeighborCont(node.chunk, node.x, node.y - 1, node.z, lightValue, queue, falloff, affectedChunks);
        }
    }

    private static void checkNeighborCont(Chunk chunk, int x, int y, int z, final int lightLevel,
                                          final List<ChunkNode> queue, final byte lightFallof, HashSet<Chunk> affectedChunks) {
        if (!Chunk.inBounds(x, y, z)) {//Correct coordinates if out of bounds
            final WCCi wcc = new WCCi().setNeighboring(chunk.position, x, y, z);
            chunk = wcc.getChunk(GameScene.world);
            if (chunk == null) {
                return;
            }
            x = wcc.chunkVoxel.x;
            y = wcc.chunkVoxel.y;
            z = wcc.chunkVoxel.z;
        }
        final int neighborLevel = chunk.data.getTorch(x, y, z);
        final Block block = ItemList.getBlock(chunk.data.getBlock(x, y, z));

        if ((!block.opaque || block.luminous) && neighborLevel + (lightFallof + 1) <= lightLevel) {
            chunk.data.setTorch(x, y, z, (byte) (lightLevel - lightFallof));
            affectedChunks.add(chunk);
            queue.add(new ChunkNode(chunk, x, y, z));
        }
    }

    public static HashSet<ChunkNode> eraseSection(HashSet<Chunk> affectedChunks, final List<ChunkNode> queue) {
        final HashSet<ChunkNode> edgeNodes = new HashSet<ChunkNode>();
        while (queue.size() > 0) {
            final ChunkNode node = queue.remove(0);
            final int lightValue = node.chunk.data.getTorch(node.x, node.y, node.z);
            affectedChunks.add(node.chunk);
            node.chunk.data.setTorch(node.x, node.y, node.z, (byte) 0);
            checkNeighborErase(node.chunk, node.x - 1, node.y, node.z, lightValue, queue, edgeNodes);
            checkNeighborErase(node.chunk, node.x + 1, node.y, node.z, lightValue, queue, edgeNodes);
            checkNeighborErase(node.chunk, node.x, node.y, node.z + 1, lightValue, queue, edgeNodes);
            checkNeighborErase(node.chunk, node.x, node.y, node.z - 1, lightValue, queue, edgeNodes);
            checkNeighborErase(node.chunk, node.x, node.y + 1, node.z, lightValue, queue, edgeNodes);
            checkNeighborErase(node.chunk, node.x, node.y - 1, node.z, lightValue, queue, edgeNodes);
        }
        return edgeNodes;
    }

    private static void checkNeighborErase(Chunk chunk, int x, int y, int z, final int lightLevel,
                                           final List<ChunkNode> queue, final HashSet<ChunkNode> edgeNodes) {
        if (!Chunk.inBounds(x, y, z)) {//Correct coordinates if out of bounds
            final WCCi wcc = new WCCi().setNeighboring(chunk.position, x, y, z);
            chunk = wcc.getChunk(GameScene.world);
            if (chunk == null) {
                return;
            }
            x = wcc.chunkVoxel.x;
            y = wcc.chunkVoxel.y;
            z = wcc.chunkVoxel.z;
        }
        final int neighborLevel = chunk.data.getTorch(x, y, z);
        if (neighborLevel < lightLevel) {
            queue.add(new ChunkNode(chunk, x, y, z));
        } else {
            edgeNodes.add(new ChunkNode(chunk, x, y, z));
        }
    }
}