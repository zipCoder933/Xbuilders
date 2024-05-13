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
import com.xbuilders.engine.world.wcc.WCCi;
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

    //Constants
    ChunkVoxels chunkVoxels;
    final int[] dims;
    final Vector3i chunkPosition;
    HashMap<Short, Block> blockMap;
    final static boolean smoothLighting = true;

    //Variables used for greedy meshing
    int j, k, l, u, v, n, side = 0;
    final int[] x = new int[]{0, 0, 0};
    final int[] q = new int[]{0, 0, 0};
    final int[] du = new int[]{0, 0, 0};
    final int[] dv = new int[]{0, 0, 0};
    final int[] normal = new int[]{0, 0, 0};

    //Variables used for quad generaiton
    final static int[] indexes1 = {2, 0, 1, 1, 3, 2}; //Constant
    final static int[] indexes2 = {2, 3, 1, 1, 0, 2}; //Constant
    final Vector3f[] vertices = {new Vector3f(), new Vector3f(), new Vector3f(), new Vector3f()};
    final Vector2f[] uvs = {new Vector2f(), new Vector2f(), new Vector2f(), new Vector2f()};
    final byte[] light = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};
    final Vector3i[] completeVertex = {new Vector3i(), new Vector3i(), new Vector3i(), new Vector3i()};


    public GreedyMesherWithLight(ChunkVoxels voxels, Vector3i chunkPosition,
                                 HashMap<Short, Block> blockMap) {
        this.chunkVoxels = voxels;
        this.blockMap = blockMap;
        this.chunkPosition = chunkPosition;
        dims = new int[]{voxels.size.x, voxels.size.y, voxels.size.z};
    }

    /**
     * Check if 2 voxel faces are the same
     */
    private boolean equals(IntBuffer mask, IntBuffer lightMask, int indx1, int indx2) {
        return mask.get(indx2) == mask.get(indx1) && lightMask.get(indx2) == lightMask.get(indx1);
    }

    public void compute(VertexSet opaqueBuffers, VertexSet transparentBuffers, MemoryStack stack, int lodLevel) {
        /**
         * These are just working variables for the algorithm - almost all taken
         * directly from Mikola Lysenko's javascript implementation.
         */
        //Empty x,q,du and dv
        for (int i = 0; i < 3; i++) {
            x[i] = 0;
            q[i] = 0;
            du[i] = 0;
            dv[i] = 0;
        }
        //Reset all the variables
        int i = 0;
        j = 0;
        k = 0;
        l = 0;
        n = 0;
        u = 0;
        v = 0;
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

                normal[0] = 0;//Normal is similar to q, but if we are on the negative side of the plane, it is negative
                normal[1] = 0;
                normal[2] = 0;
                normal[d] = backFace ? -1 : 1;

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
                final IntBuffer mask = stack.mallocInt(dims[u] * dims[v]); // TODO: Either change this from intbuffer to
                // long buffer or add another mask
                // Implement lightmask
                final IntBuffer lightMask = stack.mallocInt(dims[u] * dims[v]);

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

                for (x[d] = min; x[d] < max; ) {
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

                            light = retrieveLightForThisPlane(thisPlaneVoxel.get(0), backChunk, forwardChunk, block,
                                    d, backFace, x, q);
                            light1 = retrieveLightForNextPlane(nextPlaneVoxel.get(0), backChunk, forwardChunk, block1,
                                    d, backFace, x, q);

                            boolean draw = (thisPlaneVoxel.get(0) == 0 || nextPlaneVoxel.get(0) == 0)
                                    || (block.opaque != block1.opaque);
                            // The opaque check is to prevent transparent mesh from overriding opaque

                            int maskValue = draw
                                    ? (backFace // add the voxel for either this plane or the next plane
                                    // depending on our direction
                                    ? nextPlaneVoxel.get(0)
                                    : thisPlaneVoxel.get(0))
                                    : 0;
                            mask.put(n, maskValue);

                            int lightValue = draw
                                    ? (backFace // add the voxel for either this plane or the next plane
                                    // depending on our direction
                                    ? light1
                                    : light)
                                    : 0;
                            lightMask.put(n, lightValue);
                            n++;
                        }
                    }

                    x[d]++; // move forward

                    /*
                     * Now we generate the mesh for the mask
                     */
                    n = 0;

                    for (j = 0; j < dims[v]; j++) {
                        for (i = 0; i < dims[u]; ) {
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
                                for (quadSize.put(0, 1); i + quadSize.get(0) < dims[u]

                                        && mask.get(n + quadSize.get(0)) != 0 &&

                                        equals(mask, lightMask, n + quadSize.get(0), n);

                                     quadSize.put(0,
                                             quadSize.get(0) + 1))
                                    ;
                                {
                                }
                                // compute the quad height
                                boolean done = false;
                                for (quadSize.put(1, 1); j + quadSize.get(1) < dims[v]; quadSize.put(1,
                                        quadSize.get(1) + 1)) {
                                    for (k = 0; k < quadSize.get(0); k++) {
                                        if (mask.get(n + k + quadSize.get(1) * dims[u]) == 0
                                                ||

                                                !equals(mask, lightMask, n + k + quadSize.get(1) * dims[u], n)) {
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
                                        (short) mask.get(n), lightMask.get(n), backFace, d, side, stack);
                                // </editor-fold>

                                /*
                                 * We zero out the mask
                                 * This is important as we don't want to render the same voxel twice
                                 */
                                for (l = 0; l < quadSize.get(1); ++l) {
                                    for (k = 0; k < quadSize.get(0); ++k) {
                                        mask.put(n + k + l * dims[u], (short) 0);
                                        lightMask.put(n + k + l * dims[u], 0);
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

    // private int getMaskValue(IntBuffer mask, int index, int lodLevel) {
    // return mask.get((index / lodLevel) * lodLevel);
    // }

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

    // d: 0=X,1=Y,2=Z
    protected void Mesher_makeQuad(VertexSet buffers, VertexSet transBuffers, int x[], int du[], int dv[], final int w,
                                   final int h,
                                   short blockVal, int packedLight,
                                   final boolean backFace, final int d, final int side, MemoryStack stack) {

//         packedLight = 0b11111111000000001111111100000000;// For testing purposes

        byte l_lt = (byte) packedLight; // top left
        byte l_rt = (byte) packedLight; // top right
        byte l_lb = (byte) packedLight; // bottom left
        byte l_rb = (byte) packedLight; // bottom right

        if (smoothLighting) { // We need a 32 bit number for the light not 16
            // (leftTop, rightTop, leftBottom, rightBottom)
            l_lt = (byte) ((packedLight >> 8) & 0xF);
            l_rt = (byte) ((packedLight >> 16) & 0xF);
            l_lb = (byte) ((packedLight >> 24) & 0xF);
            l_rb = (byte) ((packedLight >> 32) & 0xF);
        }

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
                        light[0] = l_lb;
                        light[1] = l_rb;
                        light[2] = l_lt;
                        light[3] = l_rt;
                    } else {
                        texture = block.texture.getPOS_X();
                        light[2] = l_rb;
                        light[3] = l_lb;
                        light[0] = l_rt;
                        light[1] = l_lt;
                    }

                    // Z=180 flip
                    uvs[3].set(0, w);
                    uvs[2].set(h, w);
                    uvs[1].set(0, 0);
                    uvs[0].set(h, 0);
                }
                case 1 -> {
                    if (backFace) {
                        texture = block.texture.getPOS_Y();
                        light[0] = l_lb;
                        light[1] = l_rb;
                        light[2] = l_lt;
                        light[3] = l_rt;
                    } else {
                        texture = block.texture.getNEG_Y();
                        light[2] = l_rb;
                        light[3] = l_lb;
                        light[0] = l_rt;
                        light[1] = l_lt;
                    }

                    uvs[0].set(0, w);
                    uvs[1].set(h, w);
                    uvs[2].set(0, 0);
                    uvs[3].set(h, 0);
                }
                default -> {
                    if (backFace) {
                        texture = block.texture.getNEG_Z();
                        light[0] = l_lb;
                        light[1] = l_rb;
                        light[2] = l_lt;
                        light[3] = l_rt;
                    } else {
                        texture = block.texture.getPOS_Z();
                        light[2] = l_rb;
                        light[3] = l_lb;
                        light[0] = l_rt;
                        light[1] = l_lt;
                    }

                    // X=90 flip
                    uvs[1].set(0, h);
                    uvs[3].set(w, h);
                    uvs[0].set(0, 0);
                    uvs[2].set(w, 0);
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

    private byte getLightVal(int pos[]) {
        if (Chunk.inBounds(pos[0], pos[1], pos[2])) {//Center
            return chunkVoxels.getSun(pos[0], pos[1], pos[2]);
        } else {
            Chunk chunk = WCCi.getNeighboringChunk(GameScene.world, chunkPosition, pos[0], pos[1], pos[2]);
            if (chunk != null) {
                return chunk.data.getSun(
                        MathUtils.positiveMod(pos[0], Chunk.WIDTH),
                        MathUtils.positiveMod(pos[1], Chunk.WIDTH),
                        MathUtils.positiveMod(pos[2], Chunk.WIDTH));
            }
        }
        return 0;
    }

    private int[] getCoords(int x[], int xOffset, int yOffset, int[] normal, int u, int v,boolean backFace) {
        int[] pos = {x[0] + normal[0], x[1] + normal[1], x[2] + normal[2]};
        pos[u] += backFace ? -yOffset : yOffset;
        pos[v] += backFace ? -xOffset : xOffset;
        return pos;
    }

    private int retrieveSmoothLight(short thisPlaneVoxel, Chunk backChunk,
                                    Chunk frontChunk, Block block,
                                    boolean backFace,
                                    int x, int y, int z,//origin
                                    int[] normal) {


        byte a1, b1, c1,
                a2, b2, c2,
                a3, b3, c3;

        int origin[] = {x, y, z};

        int[] pos = getCoords(origin, -1, -1, normal, u, v,backFace);
//        System.out.print("a1=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")  ");
        a1 = getLightVal(pos);

        pos = getCoords(origin, 0, -1, normal, u, v,backFace);
        // System.out.print("b1=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")  ");
        b1 = getLightVal(pos);

        pos = getCoords(origin, 1, -1, normal, u, v,backFace);
        // System.out.println("c1=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")");
        c1 = getLightVal(pos);


        pos = getCoords(origin, -1, 0, normal, u, v,backFace);
        // System.out.print("a2=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")  ");
        a2 = getLightVal(pos);

        pos = getCoords(origin, 0, 0, normal, u, v,backFace);
        //  System.out.print("b2=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")  ");
        b2 = getLightVal(pos);

        pos = getCoords(origin, 1, 0, normal, u, v,backFace);
        //System.out.println("c2=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")");
        c2 = getLightVal(pos);


        pos = getCoords(origin, -1, 1, normal, u, v,backFace);
        //  System.out.print("a3=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")  ");
        a3 = getLightVal(pos);

        pos = getCoords(origin, 0, 1, normal, u, v,backFace);
        //   System.out.print("b3=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")  ");
        b3 = getLightVal(pos);

        pos = getCoords(origin, 1, 1, normal, u, v,backFace);
        // System.out.println("c3=(" + pos[0] + " " + pos[1] + " " + pos[2] + ")");
        c3 = getLightVal(pos);

        byte leftTop = 0;
        byte rightTop = 15;//
        byte leftBottom = 15;
        byte rightBottom = 15;//
        leftTop = (byte) ((a1+b1+a2+b2)/4);
        rightTop = (byte) ((b1+c1+b2+c2)/4);
        leftBottom = (byte) ((a2+b2+a3+b3)/4);
        rightBottom = (byte) ((b2+c2+b3+c3)/4);
////
//        if(leftTop!=0||rightTop!=0||leftBottom!=0||rightBottom!=0){
//            System.out.println("Light: " + leftTop + " " + rightTop + " " + leftBottom + " " + rightBottom);
//        }


//        byte avg = (byte) ((a1 + b1 + c1 + a2 + b2 + c2 + a3 + b3 + c3) / 9);
        //The reason why there is variance when the values are the same is that the return of some of the light values are negative
//        leftTop = avg;
//        rightTop = avg;
//        leftBottom = avg;
//        rightBottom = avg;
        // Pack the light values
        return (leftBottom | (rightTop << 8) | (leftTop << 16) | (rightBottom << 24));
    }

    private int retrieveLightForThisPlane(short thisPlaneVoxel,
                                          Chunk backChunk, Chunk forwardChunk, Block block,
                                          int d, boolean backFace, int[] x, int[] q) {
        // //This plane = top face, +X face and +Z face (x and z assuming you are
        // starting from the center of the chunk and moving outwards)
        if (thisPlaneVoxel != 0) {
            if (smoothLighting) {
                return retrieveSmoothLight(thisPlaneVoxel, backChunk, forwardChunk, block, backFace,
                        x[0], x[1], x[2], normal);
            } else {// Flat lighting
                if (!block.opaque) {
                    if (x[d] >= 0) {
                        return chunkVoxels.getPackedLight(x[0], x[1], x[2]);
                    } else if (backChunk != null) {
                        return backChunk.data.getPackedLight(
                                MathUtils.positiveMod(x[0], Chunk.WIDTH),
                                MathUtils.positiveMod(x[1], Chunk.WIDTH),
                                MathUtils.positiveMod(x[2], Chunk.WIDTH));
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

// private void getLightFrag(int[]x, int[]dims, int d, int[]q, Chunk chunk, Chunk forwardChunk){
//     if (x[d] + 1 < dims[d]) {
//         b2 = chunkVoxels.getPackedLight(x[0] + q[0], x[1] + q[1], x[2] + q[2]);
//     } else if (forwardChunk != null) {
//         b2 = forwardChunk.data.getPackedLight(
//                 MathUtils.positiveMod(x[0] + q[0], Chunk.WIDTH),
//                 MathUtils.positiveMod(x[1] + q[1], Chunk.WIDTH),
//                 MathUtils.positiveMod(x[2] + q[2], Chunk.WIDTH));
//     }
// }

    private int retrieveLightForNextPlane(short nextPlaneVoxel, Chunk backChunk, Chunk forwardChunk, Block block1,
                                          int d, boolean backFace, int[] x, int[] q) {
        // next plane = bottom face, -X face and -Z face (x and z assuming you are
        // starting from the center of the chunk and moving outwards)
        if (nextPlaneVoxel != 0) {
            if (smoothLighting) {
                return retrieveSmoothLight(nextPlaneVoxel, backChunk, forwardChunk, block1, backFace,
                        x[0] + q[0], x[1] + q[1], x[2] + q[2], normal);
            } else {// Flat lighting
                if (!block1.opaque) {
                    if (x[d] + 1 < dims[d]) {
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
