/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk.meshers;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.chunk.Chunk;

import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
public class NaiveMesherWithLight extends Mesher {

    ChunkVoxels data;
    boolean generateAll;

    public NaiveMesherWithLight(ChunkVoxels chunkPreMeshData, Vector3i chunkPositionOffset, boolean generateAll) {
        super(chunkPreMeshData, chunkPositionOffset);
        this.generateAll = generateAll;
        this.data = chunkPreMeshData;
    }

    Block[] neighbors = new Block[6];
    byte[] lightNeghbors = new byte[6];
    Chunk negXNeghbor;
    Chunk posXNeghbor;
    Chunk negYNeghbor;
    Chunk posYNeghbor;
    Chunk negZNeghbor;
    Chunk posZNeghbor;
    BlockType type;

    public void compute(VertexSet opaqueBuffers, VertexSet transparentBuffers,
                        MemoryStack stack, int lodLevel, boolean smoothShading) {
        Block block = null;
        BlockData blockData = null;

        for (int i = 0; i < 6; i++) {
            neighbors[i] = BlockList.BLOCK_AIR;
            lightNeghbors[i] = 0;
        }

        negXNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x - 1, chunkPosition.y, chunkPosition.z));
        posXNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x + 1, chunkPosition.y, chunkPosition.z));
        negYNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x, chunkPosition.y - 1, chunkPosition.z));
        posYNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x, chunkPosition.y + 1, chunkPosition.z));
        negZNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x, chunkPosition.y, chunkPosition.z - 1));
        posZNeghbor = GameScene.world
                .getChunk(new Vector3i(chunkPosition.x, chunkPosition.y, chunkPosition.z + 1));

        for (int x = 0; x < data.size.x; ++x) {
            for (int y = 0; y < data.size.y; ++y) {
                for (int z = 0; z < data.size.z; ++z) {

                    block = ItemList.getBlock(data.getBlock(x, y, z));
                    byte centerLight = data.getPackedLight(x, y, z);


                    if (block != null && !block.isAir()
                            && (generateAll ||
                            !ItemList.blocks.getBlockType(block.type).useInGreedyMesher)
                    ) {


                        //The code that assigns neighbors produces the most memory:
                        //THE REASON, It could be hashmap.get()
                        // This is the main bottleneck of naive mesher but not all of the bottleneck
                        if (true) {
                            if (x > 0) {
                                neighbors[BlockType.NEG_X] = ItemList.getBlock(data.getBlock(x - 1, y, z));
                                lightNeghbors[BlockType.NEG_X] = block.opaque ? data.getPackedLight(x - 1, y, z)
                                        : centerLight;
                            } else if (negXNeghbor != null) {
                                neighbors[BlockType.NEG_X] = ItemList
                                        .getBlock(negXNeghbor.data.getBlock(negXNeghbor.data.size.x - 1, y, z));
                                lightNeghbors[BlockType.NEG_X] = block.opaque ? negXNeghbor.data
                                        .getPackedLight(negXNeghbor.data.size.x - 1, y, z)
                                        : centerLight;
                            } else {
                                neighbors[BlockType.NEG_X] = BlockList.BLOCK_AIR;
                                lightNeghbors[BlockType.NEG_X] = 15;
                            }

                            if (x < data.size.x - 1) {
                                neighbors[BlockType.POS_X] = ItemList.getBlock(data.getBlock(x + 1, y, z));
                                lightNeghbors[BlockType.POS_X] = block.opaque ? data.getPackedLight(x + 1, y, z)
                                        : centerLight;
                            } else if (posXNeghbor != null) {
                                neighbors[BlockType.POS_X] = ItemList.getBlock(posXNeghbor.data.getBlock(0, y, z));
                                lightNeghbors[BlockType.POS_X] = block.opaque ? posXNeghbor.data.getPackedLight(0, y, z)
                                        : centerLight;
                            } else {
                                neighbors[BlockType.POS_X] = BlockList.BLOCK_AIR;
                                lightNeghbors[BlockType.POS_X] = 15;
                            }

                            if (y > 0) {
                                neighbors[BlockType.POS_Y] = ItemList.getBlock(data.getBlock(x, y - 1, z));
                                lightNeghbors[BlockType.POS_Y] = block.opaque ? data.getPackedLight(x, y - 1, z)
                                        : centerLight;
                            } else if (negYNeghbor != null) {
                                neighbors[BlockType.POS_Y] = ItemList
                                        .getBlock(negYNeghbor.data.getBlock(x, negYNeghbor.data.size.y - 1, z));
                                lightNeghbors[BlockType.POS_Y] = block.opaque ? negYNeghbor.data.getPackedLight(x,
                                        negYNeghbor.data.size.y - 1, z)
                                        : centerLight;
                            } else {
                                neighbors[BlockType.POS_Y] = BlockList.BLOCK_AIR;
                                lightNeghbors[BlockType.POS_Y] = 15;
                            }

                            if (y < data.size.y - 1) {
                                neighbors[BlockType.NEG_Y] = ItemList.getBlock(data.getBlock(x, y + 1, z));
                                lightNeghbors[BlockType.NEG_Y] = block.opaque ? data.getPackedLight(x, y + 1, z)
                                        : centerLight;
                            } else if (posYNeghbor != null) {
                                neighbors[BlockType.NEG_Y] = ItemList.getBlock(posYNeghbor.data.getBlock(x, 0, z));
                                lightNeghbors[BlockType.NEG_Y] = block.opaque ? posYNeghbor.data.getPackedLight(x, 0, z)
                                        : centerLight;
                            } else {
                                neighbors[BlockType.NEG_Y] = BlockList.BLOCK_AIR;
                                lightNeghbors[BlockType.NEG_Y] = 15;
                            }

                            if (z > 0) {
                                neighbors[BlockType.NEG_Z] = ItemList.getBlock(data.getBlock(x, y, z - 1));
                                lightNeghbors[BlockType.NEG_Z] = block.opaque ? data.getPackedLight(x, y, z - 1)
                                        : centerLight;
                            } else if (negZNeghbor != null) {
                                neighbors[BlockType.NEG_Z] = ItemList
                                        .getBlock(negZNeghbor.data.getBlock(x, y, negZNeghbor.data.size.z - 1));
                                lightNeghbors[BlockType.NEG_Z] = block.opaque ? negZNeghbor.data.getPackedLight(x, y,
                                        negZNeghbor.data.size.z - 1)
                                        : centerLight;
                            } else {
                                neighbors[BlockType.NEG_Z] = BlockList.BLOCK_AIR;
                                lightNeghbors[BlockType.NEG_Z] = 15;
                            }

                            if (z < data.size.z - 1) {
                                neighbors[BlockType.POS_Z] = ItemList.getBlock(data.getBlock(x, y, z + 1));
                                lightNeghbors[BlockType.POS_Z] = block.opaque ? data.getPackedLight(x, y, z + 1)
                                        : centerLight;
                            } else if (posZNeghbor != null) {
                                neighbors[BlockType.POS_Z] = ItemList
                                        .getBlock(posZNeghbor.data.getBlock(x, y, 0));
                                lightNeghbors[BlockType.POS_Z] = block.opaque ? posZNeghbor.data.getPackedLight(x, y, 0)
                                        : centerLight;
                            } else {
                                neighbors[BlockType.POS_Z] = BlockList.BLOCK_AIR;
                                lightNeghbors[BlockType.POS_Z] = 15;
                            }
                        }

                        blockData = data.getBlockData(x, y, z);
                        type = ItemList.blocks.getBlockType(block.type);
                        try {
                            if (block.opaque) {
                                type.constructBlock(opaqueBuffers, block, blockData, neighbors, lightNeghbors, x, y, z);
                            } else {
                                type.constructBlock(transparentBuffers, block, blockData, neighbors, lightNeghbors, x,
                                        y, z);
                            }
                        } catch (Exception e) {
                            ErrorHandler.saveErrorToLogFile(e);
                        }
                    }
                }
            }
        }
    }

}
