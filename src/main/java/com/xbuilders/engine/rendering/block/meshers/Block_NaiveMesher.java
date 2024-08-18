/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.block.meshers;

import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.engine.items.BlockList;

import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

/**
 * This one is designed for static block rendering, (but can technically still be used for chunk rendering)
 * @author zipCoder933
 */
public class Block_NaiveMesher extends BlockMesher<VertexSet> {

    boolean generateAll;

    public Block_NaiveMesher(ChunkVoxels data, Vector3i position, boolean generateAll) {
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
        for (int x = 0; x < data.size.x; ++x) {
            for (int y = 0; y < data.size.y; ++y) {
                for (int z = 0; z < data.size.z; ++z) {

                    block = ItemList.getBlock(data.getBlock(x, y, z));

                    if (!block.isAir() && (generateAll || block.type != BlockList.DEFAULT_BLOCK_TYPE_ID)) {

                        if (x > 0) {
                            neighbors[BlockType.NEG_X] = ItemList.getBlock(data.getBlock(x - 1, y, z));
                        } else {
                            neighbors[BlockType.NEG_X] = BlockList.BLOCK_AIR;
//                            nx = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x - 1, subChunk.getPosition().getY() * SubChunk.WIDTH + y, subChunk.getPosition().getZ() * SubChunk.WIDTH + z);
                        }

                        if (x < data.size.x - 1) {
                            neighbors[BlockType.POS_X] = ItemList.getBlock(data.getBlock(x + 1, y, z));
                        } else {
                            neighbors[BlockType.POS_X] = BlockList.BLOCK_AIR;
//                            px = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x + 1, subChunk.getPosition().getY() * SubChunk.WIDTH + y, subChunk.getPosition().getZ() * SubChunk.WIDTH + z);
                        }

                        if (y > 0) {
                            neighbors[BlockType.POS_Y] = ItemList.getBlock(data.getBlock(x, y - 1, z));
                        } else {
                            neighbors[BlockType.POS_Y] = BlockList.BLOCK_AIR;
//                            ny = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x, subChunk.getPosition().getY() * SubChunk.WIDTH + y - 1, subChunk.getPosition().getZ() * SubChunk.WIDTH + z);
                        }

                        if (y < data.size.y - 1) {
                            neighbors[BlockType.NEG_Y] = ItemList.getBlock(data.getBlock(x, y + 1, z));
                        } else {
                            neighbors[BlockType.NEG_Y] = BlockList.BLOCK_AIR;
//                            py = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x, subChunk.getPosition().getY() * SubChunk.WIDTH + y + 1, subChunk.getPosition().getZ() * SubChunk.WIDTH + z);
                        }

                        if (z > 0) {
                            neighbors[BlockType.NEG_Z] = ItemList.getBlock(data.getBlock(x, y, z - 1));
                        } else {
                            neighbors[BlockType.NEG_Z] = BlockList.BLOCK_AIR;
//                            nz = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x, subChunk.getPosition().getY() * SubChunk.WIDTH + y, subChunk.getPosition().getZ() * SubChunk.WIDTH + z - 1);
                        }

                        if (z < data.size.z - 1) {
                            neighbors[BlockType.POS_Z] = ItemList.getBlock(data.getBlock(x, y, z + 1));
                        } else {
                            neighbors[BlockType.POS_Z] = BlockList.BLOCK_AIR;
//                            pz = ph.getWorld().getBlock(subChunk.getPosition().getX() * SubChunk.WIDTH + x, subChunk.getPosition().getY() * SubChunk.WIDTH + y, subChunk.getPosition().getZ() * SubChunk.WIDTH + z + 1);
                        }

                        blockData = data.getBlockData(x, y, z);
                        BlockType type = ItemList.blocks.getBlockType(block.type);
                        if (block.opaque) {
                            type.constructBlock(opaqueBuffers, block, blockData, neighbors, null, light, null, x, y, z);
                        } else {
                            type.constructBlock(transparentBuffers, block, blockData, neighbors, null, light, null, x, y, z);
                        }
                    }

                }
            }
        }
    }

}
