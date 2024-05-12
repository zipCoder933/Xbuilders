/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk.withBakedLight;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

/**
 * @author zipCoder933
 */
public class GreedyMesherWithLight {
    // Nublada does not actually use indexed meshing, it is just regular meshes with
    // indexes attached to vertices
    // If I want to do indexed meshes, I need some sort of hashmap like thing, and
    // than compute the indexes later

    ChunkVoxels chunkVoxels;
    final int[] dims;
    HashMap<Short, Block> blockMap;

    public GreedyMesherWithLight(ChunkVoxels voxels, HashMap<Short, Block> blockMap) {
        this.chunkVoxels = voxels;
        this.blockMap = blockMap;
        dims = new int[] { voxels.size.x, voxels.size.y, voxels.size.z };
    }

    /**
     * Check if 2 voxel faces are the same
     */
    private boolean equals(int val1, int val2) {
        return val1 == val2;
    }

    public void compute(VertexSet opaqueBuffers, VertexSet transparentBuffers,
            Vector3i chunkPosition, MemoryStack stack, int lodLevel) {

        /**
         * These are just working variables for the algorithm - almost all taken
         * directly from Mikola Lysenko's javascript implementation.
         */
        int i, j, k, l, u, v, n, side = 0;

        final int[] x = new int[] { 0, 0, 0 };
        final int[] q = new int[] { 0, 0, 0 };
        final int[] du = new int[] { 0, 0, 0 };
        final int[] dv = new int[] { 0, 0, 0 };
        final IntBuffer quadSize = stack.mallocInt(2);

        /**
         * These are just working variables to hold two faces during comparison.
         */
        Vector3i voxelPos = new Vector3i(stack.mallocInt(3));

        ShortBuffer thisPlaneVoxel = stack.mallocShort(1);
        ShortBuffer nextPlaneVoxel = stack.mallocShort(1);
        int light, light1;
        Block block, block1;

        for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) {
            for (int d = 0; d < 3; d++) {

                u = (d + 1) % 3;// v and u are the perpendicular dimensions to d
                v = (d + 2) % 3;

                x[0] = 0;// X is the position vector, X[d] advances forward in the given dimension
                x[1] = 0;
                x[2] = 0;

                q[0] = 0;// Q is the dimension vector, for example, if d=1, q = [0,1,0]
                q[1] = 0;
                q[2] = 0;
                q[d] = 1;

                Chunk forwardChunk = GameScene.world
                        .getChunk(new Vector3i(chunkPosition.x + q[0], chunkPosition.y + q[1], chunkPosition.z + q[2]));
                Chunk backChunk = GameScene.world
                        .getChunk(new Vector3i(chunkPosition.x - q[0], chunkPosition.y - q[1], chunkPosition.z - q[2]));

                if (d == 0) {
                    side = backFace ? BlockType.NEG_X : BlockType.POS_X;
                } else if (d == 1) {
                    side = backFace ? BlockType.POS_Y : BlockType.NEG_Y;
                } else if (d == 2) {
                    side = backFace ? BlockType.NEG_Z : BlockType.POS_Z;
                }

                // We create the mask here (We MUST use it this way IF the chunk dimensions are
                // not cubic)
                final IntBuffer mask = stack.mallocInt(dims[u] * dims[v]);
                // final IntBuffer lightMask = stack.mallocInt(dims[u] * dims[v]); //TODO: Implement lightmask

                /*
                 * We move through the d from front to back
                 */
                int min = 0;// -1 (We changed this to 0, so chunks dont overlap. The dim starts at 0 and
                            // ends up covering the next chunks faces)
                int max = dims[d];

                // If the chunk is not terrainLoaded, dont draw the side that it is on
                if (forwardChunk == null || forwardChunk.generationStatus < Chunk.GEN_TERRAIN_LOADED) {
                    max = dims[d] - 1;
                }
                if (backChunk == null || backChunk.generationStatus < Chunk.GEN_TERRAIN_LOADED) {
                    min = 0;
                }

                for (x[d] = min; x[d] < max;) {
                    /**
                     * -------------------------------------------------------------------
                     * We compute the mask
                     * -------------------------------------------------------------------
                     */
                    n = 0;

                    for (x[v] = 0; x[v] < dims[v]; x[v]++) {// v and u are the perpendicular dimensions to d, we sweep
                                                            // across the plane of V and U
                        for (x[u] = 0; x[u] < dims[u]; x[u]++) {
                            retrieveMaskVoxels(x, q, d, backChunk, forwardChunk, voxelPos,
                                    thisPlaneVoxel, nextPlaneVoxel, lodLevel);
                            block = blockMap.get(thisPlaneVoxel.get(0));
                            block1 = blockMap.get(nextPlaneVoxel.get(0));

                            if (block == null || block.isAir() || block.type != BlockList.DEFAULT_BLOCK_TYPE_ID) {
                                thisPlaneVoxel.put(0, (short) 0);
                            }
                            if (block1 == null || block1.isAir() || block1.type != BlockList.DEFAULT_BLOCK_TYPE_ID) {
                                nextPlaneVoxel.put(0, (short) 0);
                            }

                            light = retrieveLightForThisPlane(thisPlaneVoxel.get(0), backChunk, forwardChunk, block, d,
                                    x, q);
                            light1 = retrieveLightForNextPlane(nextPlaneVoxel.get(0), backChunk, forwardChunk, block1,
                                    d, x, q);

                            //The first 16 bits are for the block ID, we also need an additional 32 bits for the light
                            //The light has 8 bits for torch and sun, x4= 32 bits
                            int thisPlanePacked = (thisPlaneVoxel.get(0) << 16) | (light & 0xFFFF);
                            int nextPlanePacked = (nextPlaneVoxel.get(0) << 16) | (light1 & 0xFFFF);

                            int maskValue = (thisPlaneVoxel.get(0) == 0 || nextPlaneVoxel.get(0) == 0)
                                    || (block.opaque != block1.opaque)
                                            // The opaque check is to prevent transparent mesh from overriding opaque
                                            // one
                                            ? (backFace // add the voxel for either this plane or the next plane
                                                        // depending on our direction
                                                    ? nextPlanePacked
                                                    : thisPlanePacked)
                                            : 0;
                            mask.put(n++, maskValue);
                        }
                    }

                    x[d]++; // move forward

                    /*
                     * Now we generate the mesh for the mask
                     */
                    n = 0;

                    for (j = 0; j < dims[v]; j++) {
                        for (i = 0; i < dims[u];) {
                            if (mask.get(n) != 0) {

                                /*
                                 * Add quad
                                 */
                                x[u] = i;
                                x[v] = j;
                                du[0] = 0;
                                du[1] = 0;
                                du[2] = 0;
                                dv[0] = 0;
                                dv[1] = 0;
                                dv[2] = 0;

                                // <editor-fold defaultstate="collapsed" desc="make the quad">
                                // Compute the quad width
                                for (quadSize.put(0, 1); i + quadSize.get(0) < dims[u] && mask.get(n
                                        + quadSize.get(0)) != 0 && equals(mask.get(n + quadSize.get(0)),
                                                mask.get(n)); quadSize.put(0, quadSize.get(0) + 1))
                                    ;
                                {
                                }
                                // compute the quad height
                                boolean done = false;
                                for (quadSize.put(1, 1); j + quadSize.get(1) < dims[v]; quadSize.put(1,
                                        quadSize.get(1) + 1)) {
                                    for (k = 0; k < quadSize.get(0); k++) {
                                        if (mask.get(n + k + quadSize.get(1) * dims[u]) == 0
                                                || mask.get(n + k + quadSize.get(1) * dims[u]) != mask.get(n)) {
                                            done = true;
                                            break;
                                        }
                                    }
                                    if (done) {
                                        break;
                                    }
                                }
                                du[u] = quadSize.get(0);
                                dv[v] = quadSize.get(1);

                                /*
                                 * And here we call the quad function in order to render a merged quad in the
                                 * scene.
                                 *
                                 * We pass mask[n] to the function, which is an instance of the VoxelFace class
                                 * containing
                                 * all the attributes of the face - which allows for variables to be passed to
                                 * shaders - for
                                 * example lighting values used to create ambient occlusion.
                                 */
                                Mesher_makeQuad(opaqueBuffers, transparentBuffers, x, du, dv, quadSize.get(0),
                                        quadSize.get(1),
                                        mask.get(n), backFace, d, side, stack);
                                // </editor-fold>

                                /*
                                 * We zero out the mask
                                 * This is important as we don't want to render the same voxel twice
                                 */
                                for (l = 0; l < quadSize.get(1); ++l) {
                                    for (k = 0; k < quadSize.get(0); ++k) {
                                        mask.put(n + k + l * dims[u], (short) 0);
                                    }
                                }
                                /*
                                 * And then finally increment the counters and continue
                                 */
                                i += quadSize.get(0);
                                n += quadSize.get(0);
                            } else {
                                i++;
                                n++;
                            }
                        }
                    }
                }
            }
        }
    }

    private int getMaskValue(IntBuffer mask, int index, int lodLevel) {
        return mask.get((index / lodLevel) * lodLevel);
    }

    private short getBlockLOD(ChunkVoxels data, int x, int y, int z, int lodLevel) {
        if (lodLevel > 1) {// might be redundant
            // We want to make the coordinates align with the LOD level,
            // for example, if lod level is 2, the X coordinate would be as follows:
            // Real X: 0,1,2,3,4,5,6,7,8,9,10
            // LOD X: 0,0,2,2,4,4,6,6,8,8,10
            // x = (x / lodLevel) * 2;
            x = (x / lodLevel) * lodLevel;
            y = (y / lodLevel) * lodLevel;
            z = (z / lodLevel) * lodLevel;
        }

        return data.getBlock(x, y, z);
    }

    private void retrieveMaskVoxels(int[] x, int[] q, int d, Chunk backChunk, Chunk forwardChunk,
            Vector3i voxelPos, ShortBuffer thisPlaneVoxel, ShortBuffer nextPlaneVoxel, int lodLevel) {
        // Here we retrieve two voxel faces for comparison.
        // thisPlaneVoxel literaly faces forward, while nextPlaneVoxel faces backward
        if (x[d] >= 0) { // Calculate the voxel of THIS plane
            thisPlaneVoxel.put(0, getBlockLOD(chunkVoxels, x[0], x[1], x[2], lodLevel));
        } else {// If we are out of bounds for this chunk:
            if (backChunk == null) {
                thisPlaneVoxel.put(0, (short) 0);
            } else {
                voxelPos.set(MathUtils.positiveMod(x[0], Chunk.WIDTH), MathUtils.positiveMod(x[1], Chunk.WIDTH),
                        MathUtils.positiveMod(x[2], Chunk.WIDTH));
                thisPlaneVoxel.put(0, getBlockLOD(backChunk.data, voxelPos.x, voxelPos.y, voxelPos.z, lodLevel));
            }
        }
        if (x[d] < dims[d] - 1) { // calculate the voxel of the NEXT plane
            voxelPos.set(x[0] + q[0], x[1] + q[1], x[2] + q[2]);
            nextPlaneVoxel.put(0, getBlockLOD(chunkVoxels, voxelPos.x, voxelPos.y, voxelPos.z, lodLevel));
        } else {// If we are out of bounds for this chunk:
            if (forwardChunk == null) {
                nextPlaneVoxel.put(0, (short) 0);
            } else {
                voxelPos.set(MathUtils.positiveMod(x[0] + q[0], Chunk.WIDTH),
                        MathUtils.positiveMod(x[1] + q[1], Chunk.WIDTH),
                        MathUtils.positiveMod(x[2] + q[2], Chunk.WIDTH));
                nextPlaneVoxel.put(0, getBlockLOD(forwardChunk.data, voxelPos.x, voxelPos.y, voxelPos.z, lodLevel));
            }
        }
    }

    final int[] indexes1 = { 2, 0, 1, 1, 3, 2 };
    final int[] indexes2 = { 2, 3, 1, 1, 0, 2 };

    final Vector3f[] vertices = { new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f() };
    final Vector2f[] uvs = { new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f() };
    final byte[] light = { (byte) 0, (byte) 0, (byte) 0, (byte) 0 };
    final Vector3i[] completeVertex = { new Vector3i(), new Vector3i(), new Vector3i(), new Vector3i() };
    boolean smoothLighting = false;

    // d: 0=X,1=Y,2=Z
    protected void Mesher_makeQuad(VertexSet buffers, VertexSet transBuffers, int x[], int du[], int dv[], final int w,
            final int h,
            final int voxel, final boolean backFace, final int d, final int side, MemoryStack stack) {

        short blockVal = (short) ((voxel >> 16) & 0xFFFF);// Block ID
        int packedLight = (byte) (voxel & 0xFFFF); // Light value (packed)
        // Get the 4 values of light (each value is 4 bits)

        // packedLight = 0b11111111000000001111111100000000;//For testing purposes

        byte l_lt = (byte) packedLight; // top left
        byte l_rt = (byte) packedLight; // top right
        byte l_lb = (byte) packedLight; // bottom left
        byte l_rb = (byte) packedLight; // bottom right

        // if (smoothLighting) { //We need a 32 bit number for the light not 16
        //     l_lt = (byte) ((packedLight >> 8) & 0xF);
        //     l_rt = (byte) ((packedLight >> 16) & 0xF);
        //     l_lb = (byte) ((packedLight >> 24) & 0xF);
        //     l_rb = (byte) ((packedLight >> 32) & 0xF);
        // }


        Block block = blockMap.get(blockVal);

        if (block != null && block.texture != null) {
            int[] indexes = backFace ? indexes1 : indexes2;
            BlockTexture.FaceTexture texture;

            // The ONLY difference betweent this method and nublada is that nublada uses
            // packed single integer coordinates,
            // and that uvs and vertex positions are combined
            vertices[0].set(x[0], x[1], x[2]);
            vertices[1].set(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2]);
            vertices[2].set(x[0] + du[0], x[1] + du[1], x[2] + du[2]);
            vertices[3].set(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2]);

            switch (d) {
                case 0 -> {
                    if (backFace) {
                        texture = block.texture.getNEG_X();
                    } else {
                        texture = block.texture.getPOS_X();
                    }

                    // Z=180 flip
                    uvs[3].set(0, w);
                    uvs[2].set(h, w);
                    uvs[1].set(0, 0);
                    uvs[0].set(h, 0);

                    light[3] = l_lb;
                    light[2] = l_rb;
                    light[1] = l_lt;
                    light[0] = l_rt;
                }
                case 1 -> {
                    if (!backFace) {
                        texture = block.texture.getNEG_Y();
                    } else {
                        texture = block.texture.getPOS_Y();
                    }

                    uvs[0].set(0, w);
                    uvs[1].set(h, w);
                    uvs[2].set(0, 0);
                    uvs[3].set(h, 0);

                    light[0] = l_lb;
                    light[1] = l_rb;
                    light[2] = l_lt;
                    light[3] = l_rt;
                }
                default -> {
                    if (backFace) {
                        texture = block.texture.getNEG_Z();
                    } else {
                        texture = block.texture.getPOS_Z();
                    }

                    // X=90 flip
                    uvs[1].set(0, h);
                    uvs[3].set(w, h);
                    uvs[0].set(0, 0);
                    uvs[2].set(w, 0);

                    light[0] = l_lb;
                    light[1] = l_rb;
                    light[2] = l_lt;
                    light[3] = l_rt;
                }
            }

            for (int i = 0; i < 4; i++) {
                Vector3f vertex = vertices[i];
                //
                // int sun = 3 & 0xF;
                // int torch = 0 & 0xF;
                // byte light2 = ((sun << 4) | torch);

                completeVertex[i].set(
                        VertexSet.packFirstInt(vertex.x, vertex.y, (byte) side, texture.animationLength),
                        VertexSet.packSecondInt(vertex.z, uvs[i].x, uvs[i].y),
                        VertexSet.packThirdInt(texture.id, light[i]));
            }

            if (block.opaque) {
                for (int i = 0; i < indexes.length; i++) {
                    int j = indexes[i];
                    buffers.vertex(0, completeVertex[j].x, completeVertex[j].y, completeVertex[j].z);
                }
            } else {
                for (int i = 0; i < indexes.length; i++) {
                    int j = indexes[i];
                    transBuffers.vertex(0, completeVertex[j].x, completeVertex[j].y, completeVertex[j].z);
                }
            }
        }
    }



    private int retrieveLightForThisPlane(short thisPlaneVoxel, Chunk backChunk, Chunk forwardChunk, Block block,
            int d, int[] x, int[] q) {
        // //This plane = top face, +X face and +Z face (x and z assuming you are
        // starting from the center of the chunk and moving outwards)
        if (thisPlaneVoxel != 0) {
            if (smoothLighting) {
                byte leftTop = 15;
                byte rightTop = 0;
                byte leftBottom = 0;
                byte rightBottom = 15;
                //Pack the light values
                return (leftTop | (rightTop << 8) | (leftBottom << 16) | (rightBottom << 24));
            } else {// Flat lighting
                if (!block.opaque) {
                    if (x[d] >= 0) {
                        return chunkVoxels.getPackedLight(x[0], x[1], x[2]);
                    } else if (backChunk != null) {
                        return backChunk.data.getPackedLight(MathUtils.positiveMod(x[0], Chunk.WIDTH),
                                MathUtils.positiveMod(x[1], Chunk.WIDTH), MathUtils.positiveMod(x[2], Chunk.WIDTH));
                    }
                } else {
                    if (x[d] + 1 < dims[d]) {
                        return chunkVoxels.getPackedLight(x[0] + q[0], x[1] + q[1], x[2] + q[2]);
                    } else if (forwardChunk != null) {
                        return forwardChunk.data.getPackedLight(MathUtils.positiveMod(x[0] + q[0], Chunk.WIDTH),
                                MathUtils.positiveMod(x[1] + q[1], Chunk.WIDTH),
                                MathUtils.positiveMod(x[2] + q[2], Chunk.WIDTH));
                    }
                }
            }
        }
        return 0;
    }

    private int retrieveLightForNextPlane(short nextPlaneVoxel, Chunk backChunk, Chunk forwardChunk, Block block1,
            int d, int[] x, int[] q) {
        // next plane = bottom face, -X face and -Z face (x and z assuming you are
        // starting from the center of the chunk and moving outwards)
        if (nextPlaneVoxel != 0) {
            if (smoothLighting) {
                byte leftTop = 15;
                byte rightTop = 0;
                byte leftBottom = 0;
                byte rightBottom = 15;
                //Pack the light values
                return (leftTop | (rightTop << 8) | (leftBottom << 16) | (rightBottom << 24));
            } else {// Flat lighting
                if (!block1.opaque) {
                    if (x[d] + 1 < dims[d]) { // we changed <dims[d]-1 to dims[d]. Investigate if we need to switch this
                                              // in
                                              // the voxel plane as well
                        return chunkVoxels.getPackedLight(x[0] + q[0], x[1] + q[1], x[2] + q[2]);
                    } else if (forwardChunk != null) {
                        return forwardChunk.data.getPackedLight(MathUtils.positiveMod(x[0] + q[0], Chunk.WIDTH),
                                MathUtils.positiveMod(x[1] + q[1], Chunk.WIDTH),
                                MathUtils.positiveMod(x[2] + q[2], Chunk.WIDTH));
                    }
                } else {
                    if (x[d] >= 0) {
                        return chunkVoxels.getPackedLight(x[0], x[1], x[2]);
                    } else if (backChunk != null) {
                        return backChunk.data.getPackedLight(MathUtils.positiveMod(x[0], Chunk.WIDTH),
                                MathUtils.positiveMod(x[1], Chunk.WIDTH), MathUtils.positiveMod(x[2], Chunk.WIDTH));
                    }
                }
            }
        }
        return 0;
    }
}
