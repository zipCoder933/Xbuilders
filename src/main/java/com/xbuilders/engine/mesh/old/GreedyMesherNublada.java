/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.mesh.old;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 *
 * @author zipCoder933
 */
public class GreedyMesherNublada {

    private static final int SOUTH = 0;
    private static final int NORTH = 1;
    private static final int EAST = 2;
    private static final int WEST = 3;
    private static final int TOP = 4;
    private static final int BOTTOM = 5;

    public GreedyMesherNublada(ChunkVoxels chunkPreMeshData, HashMap<Short, Block> blockList) {
        this.chunkData = chunkPreMeshData;
        this.blockList = blockList;
        this.dims = new int[]{chunkPreMeshData.size.x, chunkPreMeshData.size.y, chunkPreMeshData.size.x};
    }

    HashMap<Short, Block> blockList;
    private int passes = 0;
    private final int[] dims;
    private ChunkVoxels chunkData;
    private List<Integer> positions;
    private List<Integer> indices;

    private int[] positionsArray;
    private int[] indicesArray;

    public void compute(Vector3i position) {
        this.positions = new ArrayList<>(10000);
        this.indices = new ArrayList<>(6000);

        computeMesh();

        // IMPORTANT - DO NOT DELETE
        // De-references ChunkVoxels to avoid memory leaks
//        this.chunkData = null;
        positionsArray = new int[positions.size()];
        for (int i = 0; i < positions.size(); i++) {
            positionsArray[i] = positions.get(i);
        }
        positions = null;

        indicesArray = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            indicesArray[i] = indices.get(i);
        }
        indices = null;
    }

    private void computeMesh() {
        int i, j, k, l, w, h, u, v, n;

        final int[] x = new int[]{0, 0, 0};
        final int[] q = new int[]{0, 0, 0};
        final int[] du = new int[]{0, 0, 0};
        final int[] dv = new int[]{0, 0, 0};

        int[] mask;

        int voxelFace, voxelFace1;

        for (boolean backFace = true, b = false; b != backFace; backFace = false, b = !b) {

            for (int d = 0; d < 3; d++) {

                u = (d + 1) % 3;
                v = (d + 2) % 3;

                x[0] = 0;
                x[1] = 0;
                x[2] = 0;

                q[0] = 0;
                q[1] = 0;
                q[2] = 0;
                q[d] = 1;

                mask = new int[(dims[u] + 1) * (dims[v] + 1)];

                for (x[d] = -1; x[d] < dims[d];) {

                    n = 0;

                    for (x[v] = 0; x[v] < dims[v]; x[v]++) {
                        for (x[u] = 0; x[u] < dims[u]; x[u]++) {

                            voxelFace = (x[d] >= 0) ? chunkData.getBlock(x[0], x[1], x[2]) : 0;
                            voxelFace1 = (x[d] < dims[d] - 1) ? chunkData.getBlock((x[0] + q[0]), (x[1] + q[1]), (x[2] + q[2])) : 0;

                            if (voxelFace != 0 && !blockList.get((short) voxelFace).opaque) {
                                voxelFace = 0;
                            }

                            if (voxelFace1 != 0 && !blockList.get((short) voxelFace1).opaque) {
                                voxelFace1 = 0;
                            }

                            mask[n++] = ((voxelFace == 0 || voxelFace1 == 0))
                                    ? backFace ? voxelFace1 : voxelFace
                                    : 0;
                        }
                    }

                    x[d]++;

                    n = 0;

                    for (j = 0; j < dims[v]; j++) {
                        for (i = 0; i < dims[u];) {

                            if (mask[n] != 0) {

                                for (w = 1; i + w < dims[u] && mask[n + w] != 0 && mask[n + w] == mask[n]; w++) {
                                }

                                boolean done = false;

                                for (h = 1; j + h < dims[v]; h++) {
                                    for (k = 0; k < w; k++) {
                                        if (mask[n + k + h * dims[u]] == 0 || mask[n + k + h * dims[u]] != mask[n]) {
                                            done = true;
                                            break;
                                        }
                                    }
                                    if (done) {
                                        break;
                                    }
                                }

                                if (mask[n] != 0) {
                                    x[u] = i;
                                    x[v] = j;

                                    du[0] = 0;
                                    du[1] = 0;
                                    du[2] = 0;
                                    du[u] = w;

                                    dv[0] = 0;
                                    dv[1] = 0;
                                    dv[2] = 0;
                                    dv[v] = h;

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
                                    Block block = blockList.get((short) mask[n]);

                                    int[] indexes = backFace ? indexes1 : indexes2;

                                    int[] vertices = new int[8];

                                    for (int index : indexes) {
                                        indices.add(index + passes * 4);
                                    }

                                    vertices[0] = getCompressedData(x[0], x[1], x[2]);

                                    vertices[2] = getCompressedData(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2]);

                                    vertices[4] = getCompressedData(x[0] + du[0], x[1] + du[1], x[2] + du[2]);

                                    vertices[6] = getCompressedData(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1],
                                            x[2] + du[2] + dv[2]);

                                    // Texture re-orientation based on the direction
                                    if (d == 2) {
                                        if (!backFace) {
                                            // 2
                                            vertices[1] = getCompressedData(0, h, 0);

                                            // 0
                                            vertices[3] = getCompressedData(0, 0, 0);

                                            // 3
                                            vertices[5] = getCompressedData(w, h, 0);

                                            // 1
                                            vertices[7] = getCompressedData(w, 0, 0);
                                        } else {
                                            // 3
                                            vertices[1] = getCompressedData(w, h, 0);

                                            // 1
                                            vertices[3] = getCompressedData(w, 0, 0);

                                            // 2
                                            vertices[5] = getCompressedData(0, h, 0);

                                            // 0
                                            vertices[7] = getCompressedData(0, 0, 0);
                                        }
                                    } else if (d == 0) {
                                        if (backFace) {
                                            // 2
                                            vertices[1] = getCompressedData(0, w, 0);

                                            // 3
                                            vertices[3] = getCompressedData(h, w, 0);

                                            // 0
                                            vertices[5] = getCompressedData(0, 0, 0);

                                            // 1
                                            vertices[7] = getCompressedData(h, 0, 0);
                                        } else {
                                            // 2
                                            vertices[1] = getCompressedData(h, w, 0);

                                            // 3
                                            vertices[3] = getCompressedData(0, w, 0);

                                            // 1
                                            vertices[5] = getCompressedData(h, 0, 0);

                                            // 0
                                            vertices[7] = getCompressedData(0, 0, 0);
                                        }
                                    } else {
                                        if (!backFace) {
                                            // 0
                                            vertices[1] = getCompressedData(0, 0, 0);

                                            // 1
                                            vertices[3] = getCompressedData(h, 0, 0);

                                            // 2
                                            vertices[5] = getCompressedData(0, w, 0);

                                            // 3
                                            vertices[7] = getCompressedData(h, w, 0);
                                        } else {
                                            // 1
                                            vertices[1] = getCompressedData(h, 0, 0);

                                            // 0
                                            vertices[3] = getCompressedData(0, 0, 0);

                                            // 3
                                            vertices[5] = getCompressedData(h, w, 0);

                                            // 2
                                            vertices[7] = getCompressedData(0, w, 0);
                                        }
                                    }

                                    positions.add(vertices[0]);
                                    positions.add(vertices[1]);
                                    positions.add(vertices[2]);
                                    positions.add(vertices[3]);
                                    positions.add(vertices[4]);
                                    positions.add(vertices[5]);
                                    positions.add(vertices[6]);
                                    positions.add(vertices[7]);
                                    passes++;

                                    // quad(x[0], x[1], x[2],
                                    // x[0] + du[0], x[1] + du[1], x[2] + du[2],
                                    // x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2],
                                    // x[0] + dv[0], x[1] + dv[1], x[2] + dv[2],
                                    // w,
                                    // h,
                                    // blockList.get((short)mask[n]),
                                    // backFace,
                                    // d);
                                }

                                for (l = 0; l < h; ++l) {
                                    for (k = 0; k < w; ++k) {
                                        mask[n + k + l * dims[u]] = 0;
                                    }
                                }

                                i += w;
                                n += w;

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

    private static final int[] indexes1 = new int[]{2, 0, 1, 1, 3, 2};
    private static final int[] indexes2 = new int[]{2, 3, 1, 1, 0, 2};

    public static int getCompressedData(int x, int y, int z) {
        return z | x << 9 | y << 18;
    }

}
