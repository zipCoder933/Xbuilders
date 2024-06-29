/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk.meshers;

import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.engine.items.BlockList;

import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
public class NaiveMesher extends Mesher {

    boolean generateAll;

    public NaiveMesher(ChunkVoxels data, Vector3i position, boolean generateAll) {
        super(data, position);
        this.generateAll = generateAll;
    }

    final byte[] light = {15, 15, 15, 15, 15, 15};

    public void compute(VertexSet opaqueBuffers,
                        VertexSet transparentBuffers,
                        MemoryStack stack, int lodLevel, boolean smoothShading) {
        Block block;
        Block[] neighbors = new Block[6];

        BlockData blockData;
        for (int x = 0; x < chunkVoxels.size.x; ++x) {
            for (int y = 0; y < chunkVoxels.size.y; ++y) {
                for (int z = 0; z < chunkVoxels.size.z; ++z) {

                    block = ItemList.getBlock(chunkVoxels.getBlock(x, y, z));

                    if (!block.isAir() && (generateAll || block.type != BlockList.DEFAULT_BLOCK_TYPE_ID)) {

                        if (x > 0) {
                            neighbors[BlockType.NEG_X] = ItemList.getBlock(chunkVoxels.getBlock(x - 1, y, z));
                        } else {
                            neighbors[BlockType.NEG_X] = BlockList.BLOCK_AIR;
//                            nx = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x - 1, subChunk.getPosition().getY() * SubChunk.WIDTH + y, subChunk.getPosition().getZ() * SubChunk.WIDTH + z);
                        }

                        if (x < chunkVoxels.size.x - 1) {
                            neighbors[BlockType.POS_X] = ItemList.getBlock(chunkVoxels.getBlock(x + 1, y, z));
                        } else {
                            neighbors[BlockType.POS_X] = BlockList.BLOCK_AIR;
//                            px = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x + 1, subChunk.getPosition().getY() * SubChunk.WIDTH + y, subChunk.getPosition().getZ() * SubChunk.WIDTH + z);
                        }

                        if (y > 0) {
                            neighbors[BlockType.POS_Y] = ItemList.getBlock(chunkVoxels.getBlock(x, y - 1, z));
                        } else {
                            neighbors[BlockType.POS_Y] = BlockList.BLOCK_AIR;
//                            ny = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x, subChunk.getPosition().getY() * SubChunk.WIDTH + y - 1, subChunk.getPosition().getZ() * SubChunk.WIDTH + z);
                        }

                        if (y < chunkVoxels.size.y - 1) {
                            neighbors[BlockType.NEG_Y] = ItemList.getBlock(chunkVoxels.getBlock(x, y + 1, z));
                        } else {
                            neighbors[BlockType.NEG_Y] = BlockList.BLOCK_AIR;
//                            py = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x, subChunk.getPosition().getY() * SubChunk.WIDTH + y + 1, subChunk.getPosition().getZ() * SubChunk.WIDTH + z);
                        }

                        if (z > 0) {
                            neighbors[BlockType.NEG_Z] = ItemList.getBlock(chunkVoxels.getBlock(x, y, z - 1));
                        } else {
                            neighbors[BlockType.NEG_Z] = BlockList.BLOCK_AIR;
//                            nz = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x, subChunk.getPosition().getY() * SubChunk.WIDTH + y, subChunk.getPosition().getZ() * SubChunk.WIDTH + z - 1);
                        }

                        if (z < chunkVoxels.size.z - 1) {
                            neighbors[BlockType.POS_Z] = ItemList.getBlock(chunkVoxels.getBlock(x, y, z + 1));
                        } else {
                            neighbors[BlockType.POS_Z] = BlockList.BLOCK_AIR;
//                            pz = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x, subChunk.getPosition().getY() * SubChunk.WIDTH + y, subChunk.getPosition().getZ() * SubChunk.WIDTH + z + 1);
                        }

                        blockData = chunkVoxels.getBlockData(x, y, z);
                        BlockType type = ItemList.blocks.getBlockType(block.type);
                        if (block.opaque) {
                            type.constructBlock(opaqueBuffers, block, blockData, neighbors, light, x, y, z);
                        } else {
                            type.constructBlock(transparentBuffers, block, blockData, neighbors, light, x, y, z);
                        }
                    }

                }
            }
        }
    }

}
