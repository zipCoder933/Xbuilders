/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.world.chunk;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.world.wcc.ChunkNode;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.BFS.HashQueue;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.Terrain;

import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;

import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

/**
 * @author zipCoder933
 */
class ChunkSunlightUtils {

    private static void addNodes(HashQueue<ChunkNode> queue, Chunk chunk, int x, int y, int z) {
        if (x - 1 >= 0 && chunk.data.getSun(x - 1, y, z) > 0) {
            queue.add(new ChunkNode(chunk, x - 1, y, z));
        }
        if (x + 1 < WIDTH && chunk.data.getSun(x + 1, y, z) > 0) {
            queue.add(new ChunkNode(chunk, x + 1, y, z));
        }
        if (z - 1 >= 0 && chunk.data.getSun(x, y, z - 1) > 0) {
            queue.add(new ChunkNode(chunk, x, y, z - 1));
        }
        if (z + 1 < WIDTH && chunk.data.getSun(x, y, z + 1) > 0) {
            queue.add(new ChunkNode(chunk, x, y, z + 1));
        }
    }

    static boolean generateSunlight(Chunk chunk, Terrain terrain) {
//        HashQueue<ChunkNode> queue = new HashQueue<ChunkNode>();
        for (int x = 0; x < WIDTH; x++) {
            for (int z = 0; z < WIDTH; z++) {
                byte sunVal = 15;
                for (Chunk pillarChunk : chunk.pillarInformation.chunks) {//Go DOWN from Y
                    for (int y = 0; y < Chunk.WIDTH; y++) {
                        Block block = ItemList.getBlock(pillarChunk.data.getBlock(x, y, z));
                        if (block.opaque) {
                            sunVal = 0;
                        }
                        pillarChunk.data.setSun(x, y, z, sunVal);
//                        if(sunVal == 15) {
//                            queue.add(new ChunkNode(pillarChunk, x, y, z));
//                        }
                    }
                }
            }
        }

//            while (!queue.isEmpty()) {
//                ChunkNode node = queue.getAndRemove();
//                byte lightValue = node.chunk.data.getSun(node.coords.x, node.coords.y, node.coords.z);
//                if (lightValue > 0) {
//                    checkNeighbor(node.chunk, node.coords.x - 1, node.coords.y, node.coords.z, lightValue, queue);
//                    checkNeighbor(node.chunk, node.coords.x + 1, node.coords.y, node.coords.z, lightValue, queue);
//                    checkNeighbor(node.chunk, node.coords.x, node.coords.y, node.coords.z + 1, lightValue, queue);
//                    checkNeighbor(node.chunk, node.coords.x, node.coords.y, node.coords.z - 1, lightValue, queue);
//                    checkNeighbor(node.chunk, node.coords.x, node.coords.y + 1, node.coords.z, lightValue, queue);
//                    checkNeighbor(node.chunk, node.coords.x, node.coords.y - 1, node.coords.z, lightValue, queue);
//                }
//            }


        return true;
    }

    private static synchronized void checkNeighbor(Chunk chunk, int x, int y, int z, final byte lightLevel,
                                                   final HashQueue<ChunkNode> queue) {
        Block neigborBlock = null;
        if (Chunk.inBounds(x, y, z)) {
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
            }
        }
        if (neigborBlock != null && !neigborBlock.opaque) {
            final int neighborLevel = chunk.data.getSun(x, y, z);
            if (neighborLevel + 2 <= lightLevel) {
                chunk.data.setSun(x, y, z, (byte) (lightLevel - 1));
                queue.add(new ChunkNode(chunk, x, y, z));
            }
        }
    }

}
