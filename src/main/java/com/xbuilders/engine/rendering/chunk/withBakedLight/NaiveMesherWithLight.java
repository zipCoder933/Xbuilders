/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk.withBakedLight;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.world.chunk.Chunk;

import java.util.HashMap;

import org.joml.Vector3i;

/**
 * @author zipCoder933
 */
public class NaiveMesherWithLight {

    ChunkVoxels data;

    int[] dims;
    HashMap<Short, Block> blockList;
    boolean generateAll;

    public NaiveMesherWithLight(ChunkVoxels chunkPreMeshData, HashMap<Short, Block> blockList,
            boolean generateAll) {
        this.generateAll = generateAll;
        this.blockList = blockList;
        this.data = chunkPreMeshData;
        dims = new int[] { data.size.x, data.size.y, data.size.z };
    }

    public void compute(VertexSet opaqueBuffers, VertexSet transparentBuffers, Vector3i chunkPosition) {
        Block block;
        Block[] neighbors = new Block[6];
        byte[] lightNeghbors = new byte[6];
        BlockData blockData;

        Chunk negXNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x - 1, chunkPosition.y, chunkPosition.z));
        Chunk posXNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x + 1, chunkPosition.y, chunkPosition.z));
        Chunk negYNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x, chunkPosition.y - 1, chunkPosition.z));
        Chunk posYNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x, chunkPosition.y + 1, chunkPosition.z));
        Chunk negZNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x, chunkPosition.y, chunkPosition.z - 1));
        Chunk posZNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x, chunkPosition.y, chunkPosition.z + 1));

        for (int x = 0; x < data.size.x; ++x) {
            for (int y = 0; y < data.size.y; ++y) {
                for (int z = 0; z < data.size.z; ++z) {

                    block = ItemList.getBlock(data.getBlock(x, y, z));
                    byte centerLight = data.getPackedLight(x, y, z);

                    if (!block.isAir() && (block.type != BlockList.DEFAULT_BLOCK_TYPE_ID || generateAll)) {

                        if (x > 0) {
                            neighbors[BlockType.NEG_X] = ItemList.getBlock(data.getBlock(x - 1, y, z));
                            lightNeghbors[BlockType.NEG_X] = block.opaque ? data.getPackedLight(x - 1, y, z) : centerLight;
                        } else if (negXNeghbor != null) {
                            neighbors[BlockType.NEG_X] = ItemList
                                    .getBlock(negXNeghbor.data.getBlock(negXNeghbor.data.size.x - 1, y, z));
                            lightNeghbors[BlockType.NEG_X] = negXNeghbor.data.getPackedLight(negXNeghbor.data.size.x - 1, y, z);
                        } else {
                            neighbors[BlockType.NEG_X] = BlockList.BLOCK_AIR;
                        }

                        if (x < data.size.x - 1) {
                            neighbors[BlockType.POS_X] = ItemList.getBlock(data.getBlock(x + 1, y, z));
                            lightNeghbors[BlockType.POS_X] = block.opaque ? data.getPackedLight(x + 1, y, z) : centerLight;
                        } else if (posXNeghbor != null) {
                            neighbors[BlockType.POS_X] = ItemList.getBlock(posXNeghbor.data.getBlock(0, y, z));
                            lightNeghbors[BlockType.POS_X] = posXNeghbor.data.getPackedLight(0, y, z);
                        } else {
                            neighbors[BlockType.POS_X] = BlockList.BLOCK_AIR;
                        }

                        if (y > 0) {
                            neighbors[BlockType.NEG_Y] = ItemList.getBlock(data.getBlock(x, y - 1, z));
                            lightNeghbors[BlockType.NEG_Y] = block.opaque ? data.getPackedLight(x, y - 1, z) : centerLight;
                        } else if (negYNeghbor != null) {
                            neighbors[BlockType.NEG_Y] = ItemList.getBlock(negYNeghbor.data.getBlock(x, negYNeghbor.data.size.y - 1, z));
                            lightNeghbors[BlockType.NEG_Y] = negYNeghbor.data.getPackedLight(x, negYNeghbor.data.size.y - 1, z);
                        }else {
                            neighbors[BlockType.NEG_Y] = BlockList.BLOCK_AIR;
                        }

                        if (y < data.size.y - 1) {
                            neighbors[BlockType.POS_Y] = ItemList.getBlock(data.getBlock(x, y + 1, z));
                            lightNeghbors[BlockType.POS_Y] = block.opaque ? data.getPackedLight(x, y + 1, z) : centerLight;
                        } else if (posYNeghbor != null) {
                            neighbors[BlockType.POS_Y] = ItemList.getBlock(posYNeghbor.data.getBlock(x, 0, z));
                            lightNeghbors[BlockType.POS_Y] = posYNeghbor.data.getPackedLight(x, 0, z);
                        } else {
                            neighbors[BlockType.POS_Y] = BlockList.BLOCK_AIR;
                        }

                        if (z > 0) {
                            neighbors[BlockType.NEG_Z] = ItemList.getBlock(data.getBlock(x, y, z - 1));
                            lightNeghbors[BlockType.NEG_Z] = block.opaque ? data.getPackedLight(x, y, z - 1) : centerLight;
                        } else if (negZNeghbor != null) {
                            neighbors[BlockType.NEG_Z] = ItemList.getBlock(negZNeghbor.data.getBlock(x, y, negZNeghbor.data.size.z - 1));
                            lightNeghbors[BlockType.NEG_Z] = negZNeghbor.data.getPackedLight(x, y, negZNeghbor.data.size.z - 1);
                        }else {
                            neighbors[BlockType.NEG_Z] = BlockList.BLOCK_AIR;
                        }

                        if (z < data.size.z - 1) {
                            neighbors[BlockType.POS_Z] = ItemList.getBlock(data.getBlock(x, y, z + 1));
                            lightNeghbors[BlockType.POS_Z] = block.opaque ? data.getPackedLight(x, y, z + 1) : centerLight;
                        } else if (posZNeghbor != null) {
                            neighbors[BlockType.POS_Z] = ItemList
                                    .getBlock(posZNeghbor.data.getBlock(x, y, 0));
                            lightNeghbors[BlockType.POS_Z] = posZNeghbor.data.getPackedLight(x, y, 0);
                        } else {
                            neighbors[BlockType.POS_Z] = BlockList.BLOCK_AIR;
                        }

                        blockData = data.getBlockData(x, y, z);
                        BlockType type = ItemList.blocks.getBlockType(block.type);
                        if (type == null) {
                            // System.err.println("NaiveMesherWithLight: BlockType " + block.type + " not found");
                            continue;
                        }
                        if (block.opaque) {
                            type.constructBlock(opaqueBuffers, block, blockData, neighbors, lightNeghbors, x, y, z);
                        } else {
                            type.constructBlock(transparentBuffers, block, blockData, neighbors, lightNeghbors, x, y,
                                    z);
                        }
                    }

                }
            }
        }
    }

}
