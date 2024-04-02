///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.engine.mesh;
//
//import com.xbuilders.engine.gameScene.GameScene;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.items.block.BlockList;
//import static com.xbuilders.engine.mesh.old.GreedyMesherNublada.getCompressedData;
//import com.xbuilders.engine.utils.math.MathUtils;
//import com.xbuilders.engine.world.WCC;
//import com.xbuilders.engine.world.World;
//import com.xbuilders.engine.world.chunk.Chunk;
//import com.xbuilders.engine.world.chunk.ChunkVoxels;
//import com.xbuilders.game.MyGame;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import org.joml.Vector2f;
//import org.joml.Vector3f;
//import org.joml.Vector3i;
//
///**
// *
// * @author zipCoder933
// */
//public class Old_GreedyMesher implements Mesher {
//
//    /*
//     * These are just constants to keep track of which face we're dealing with - their actual 
//     * values are unimportantly - only that they're constant.
//     */
//    private static final int NEG_X = 0;
//    private static final int POS_X = 1;
//
//    private static final int NEG_Z = 2;
//    private static final int POS_Z = 3;
//
//    private static final int POS_Y = 4;
//    private static final int NEG_Y = 5;
//
//    public Mesh mesh;
//    private ChunkVoxels voxels;
//    int[] dims;
//    public List<Float> position, uv;
//    private float[] posBuffer, uvBuffer;
//    HashMap<Short, Block> blockList;
//
//    public Old_GreedyMesher(ChunkVoxels chunkPreMeshData, Mesh mesh, HashMap<Short, Block> blockList) {
//        this.mesh = mesh;
//        this.blockList = blockList;
//        this.voxels = chunkPreMeshData;
//        dims = new int[]{voxels.size.x, voxels.size.y, voxels.size.z};
//        for (int i = 0; i < 4; i++) {
//            vertices[i] = new Vector3f();
//            uvs[i] = new Vector2f();
//        }
////        this.position = new ArrayList<>(10000);
////        this.uv = new ArrayList<>(10000);
//    }
//
//    private short getVoxelFace(final int x, final int y, final int z, boolean opaqueMesh) {
//        short voxelFace = voxels.getBlock(x, y, z);
//        if (voxelFace != 0 && blockList.get((short) voxelFace).isTransparent()) {
//            voxelFace = 0;
//        }
//        return voxelFace;
//    }
//
//    private final Chunk[] neighboringChunks = new Chunk[6];
//
//    private short getNeighborVoxelFace(Vector3i chunkPosition,
//            final int x, final int y, final int z, boolean opaqueMesh) {
//
////        int chunkIndex = 0;
////        if (x < 0) {
////            chunkIndex = NEG_X;
////        } else if (x >= Chunk.WIDTH) {
////            chunkIndex = POS_X;
////        }
////        if (y < 0) {
////            chunkIndex = NEG_Y;
////        } else if (y >= Chunk.WIDTH) {
////            chunkIndex = POS_Y;
////        }
////        if (z < 0) {
////            chunkIndex = NEG_Z;
////        } else if (z >= Chunk.WIDTH) {
////            chunkIndex = POS_Z;
////        }
////        Chunk chunk = neighboringChunks[chunkIndex];
//        Chunk chunk = GameScene.world.getChunk(WCC.getNeighboringChunk(chunkPosition, x, y, z));
//        short voxelFace = 0;
//
//        if (chunk == null) {
//            return 0;
//        } else {
//            Vector3i voxel = WCC.getCoord_ChunkVoxelSpace(x, y, z);
//            voxelFace = chunk.data.getBlock(voxel.x, voxel.y, voxel.z);
//        }
//
//        if (voxelFace != 0 && blockList.get((short) voxelFace).isTransparent()) {
//            voxelFace = 0;
//        }
//        return voxelFace;
//    }
//
//    /**
//     * Check if 2 voxel faces are the same
//     */
//    private boolean equals(int val1, int val2) {
//        return val1 == val2;
//    }
//
//    public static int getCompressedData(int x, int y, int z) {
//        return z | x << 9 | y << 18;
//    }
//
//    public static float[] unPackCompressed(int vertex) {
//        float y = (vertex >> 18) & 0x1FF;
//        float x = (vertex >> 9) & 0x1FF;
//        float z = (vertex) & 0x1FF;
//        return new float[]{x, y, z};
//    }
//
//    private Chunk getNeighborBasedOnSide(int i, Vector3i pos) {
//        switch (i) {
//            case 0 -> {//NEG_X = 0;
//                return GameScene.world.getChunk(new Vector3i(pos.x - 1, pos.y + 0, pos.z + 0));
//            }
//            case 1 -> {//POS_X = 1;
//                return GameScene.world.getChunk(new Vector3i(pos.x + 1, pos.y + 0, pos.z + 0));
//            }
//            case 2 -> {//NEG_Z = 2;
//                return GameScene.world.getChunk(new Vector3i(pos.x + 0, pos.y + 0, pos.z - 1));
//            }
//            case 3 -> {//POS_Z = 3;
//                return GameScene.world.getChunk(new Vector3i(pos.x + 0, pos.y + 0, pos.z + 1));
//            }
//            case 4 -> {//POS_Y = 4;
//                return GameScene.world.getChunk(new Vector3i(pos.x + 0, pos.y + 1, pos.z + 0));
//            }
//            case 5 -> {//NEG_Y = 5;
//                return GameScene.world.getChunk(new Vector3i(pos.x + 0, pos.y - 1, pos.z + 0));
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public synchronized Mesher compute(Vector3i pos) {
//
//        this.position = new ArrayList<>(10000);
//        this.uv = new ArrayList<>(10000);
//        generateMesh(pos, true);
//
//        posBuffer = new float[position.size()];
//        uvBuffer = new float[uv.size()];
//        mesh.empty = position.isEmpty();
//
//        for (int i = 0; i < position.size(); i++) {
//            posBuffer[i] = position.get(i);
//        }
//        for (int i = 0; i < uv.size(); i++) {
//            uvBuffer[i] = uv.get(i);
//        }
////        position.clear();
////        uv.clear();
//        position = null;
//        uv = null;
//        return this;
//    }
//
//    private void generateMesh(Vector3i pos, boolean opaqueMesh) {
//        boolean allNeghborsAreHere = false;
//        for (int i = 0; i < neighboringChunks.length; i++) {
//            Chunk chunk = getNeighborBasedOnSide(i, pos);
//            neighboringChunks[i] = chunk;
//            if (chunk == null) {
//                allNeghborsAreHere = false;
//            }
//        }
//
//        /*
//         * These are just working variables for the algorithm - almost all taken 
//         * directly from Mikola Lysenko's javascript implementation.
//         */
//        int i, j, k, l, w, h, u, v, n, side = 0;
//
//        final int[] x = new int[]{0, 0, 0};
//        final int[] q = new int[]{0, 0, 0};
//        final int[] du = new int[]{0, 0, 0};
//        final int[] dv = new int[]{0, 0, 0};
//
//        /*
//         * We create a mask - this will contain the groups of matching voxel faces 
//         * as we proceed through the chunk in 6 directions - once for each face.
//         */
////        for (int i2 = 0; i2 < mask.length; i2++) {
////            mask[i2] = 0;
////        }
//        int[] mask = new int[voxels.size.x * voxels.size.y];
//
//
//        /*
//         * These are just working variables to hold two faces during comparison.
//         */
//        short voxelFace;
//        short voxelFace1;
//
//        /**
//         * We start with the lesser-spotted boolean for-loop (also known as the
//         * old flippy floppy).
//         *
//         * The variable backFace will be TRUE on the first iteration and FALSE
//         * on the second - this allows us to track which direction the indices
//         * should run during creation of the quad.
//         *
//         * This loop runs twice, and the inner loop 3 times - totally 6
//         * iterations - one for each voxel face.
//         */
//        for (boolean backFace = true, b = false; b != backFace; backFace = backFace && b, b = !b) {
//
//            /*
//             * We sweep over the 3 ds - most of what follows is well described by Mikola Lysenko 
//             * in his post - and is ported from his Javascript implementation.  Where this implementation 
//             * diverges, I've added commentary.
//             */
//            for (int d = 0; d < 3; d++) {
//
//                u = (d + 1) % 3;
//                v = (d + 2) % 3;
//
//                x[0] = 0;
//                x[1] = 0;
//                x[2] = 0;
//
//                q[0] = 0;
//                q[1] = 0;
//                q[2] = 0;
//                q[d] = 1;
//                //We can also just create the mask here
////                mask = new int[(dims[u] + 1) * (dims[v] + 1)];
//
//                /*
//                 * We move through the d from front to back
//                 */
//                for (x[d] = -1; x[d] < dims[d];) {
//                    /*
//                     * -------------------------------------------------------------------
//                     *   We compute the mask
//                     * -------------------------------------------------------------------
//                     */
//                    n = 0;
//
//                    boolean outOfChunkBounds = x[d] < 0 || x[d] > dims[d] - 1;
////                    if (d == 0) {
////                        side = x[d] < 0 ? NEG_X : POS_X;
////                    } else if (d == 1) {
////                        side = x[d] < 0 ? NEG_Y : POS_Y;
////                    } else if (d == 2) {
////                        side = x[d] < 0 ? NEG_Z : POS_Z;
////                    }
//
//                    for (x[v] = 0; x[v] < dims[v]; x[v]++) {
//                        for (x[u] = 0; x[u] < dims[u]; x[u]++) {
//                            if (outOfChunkBounds) {
//                                //If there are still chunks showing up after this, it is probbably because of the blocks being added to chunk
//                                mask[n++] = 0;
//                            } else {
//                                //Here we retrieve two voxel faces for comparison.
//                                voxelFace = (x[d] >= 0)
//                                        ? getVoxelFace(x[0], x[1], x[2], opaqueMesh)
//                                        : getNeighborVoxelFace(pos, x[0], x[1], x[2], opaqueMesh);
//
//                                voxelFace1 = (x[d] < dims[d] - 1)
//                                        ? getVoxelFace(x[0] + q[0], x[1] + q[1], x[2] + q[2], opaqueMesh)
//                                        : getNeighborVoxelFace(pos, x[0], x[1], x[2], opaqueMesh);
//
//                                mask[n++] = ((voxelFace == 0 || voxelFace1 == 0))
//                                        ? backFace ? voxelFace1 : voxelFace
//                                        : 0;
//                            }
//                        }
//                    }
//
//                    x[d]++;
//
//                    /*
//                     * Now we generate the mesh for the mask
//                     */
//                    n = 0;
//
//                    for (j = 0; j < dims[v]; j++) {
//                        for (i = 0; i < dims[u];) {
//                            if (mask[n] != 0) {//Make the quad
//                                /*
//                                 * We compute the w
//                                 */
//                                for (w = 1; i + w < dims[u] && mask[n + w] != 0 && equals(mask[n + w], mask[n]); w++) {
//                                }
//
//                                /*
//                                 * Then we compute h
//                                 */
//                                boolean done = false;
//
//                                for (h = 1; j + h < dims[v]; h++) {
//                                    for (k = 0; k < w; k++) {
//                                        if (mask[n + k + h * dims[u]] == 0 || mask[n + k + h * dims[u]] != mask[n]) {
//                                            done = true;
//                                            break;
//                                        }
//                                    }
//                                    if (done) {
//                                        break;
//                                    }
//                                }
//
//                                /*
//                                 * Here we check the "transparent" attribute in the VoxelFace class to ensure that we don't mesh 
//                                 * any culled faces.
//                                 */
//                                if (mask[n] != 0) {
//                                    /*
//                                     * Add quad
//                                     */
//                                    x[u] = i;
//                                    x[v] = j;
//
//                                    du[0] = 0;
//                                    du[1] = 0;
//                                    du[2] = 0;
//                                    du[u] = w;
//
//                                    dv[0] = 0;
//                                    dv[1] = 0;
//                                    dv[2] = 0;
//                                    dv[v] = h;
//
//                                    /*
//                                     * And here we call the quad function in order to render a merged quad in the scene.
//                                     * 
//                                     * We pass mask[n] to the function, which is an instance of the VoxelFace class containing 
//                                     * all the attributes of the face - which allows for variables to be passed to shaders - for 
//                                     * example lighting values used to create ambient occlusion.
//                                     */
//                                    Mesher_makeQuad(
//                                            x, du, dv, w, h,
//                                            mask[n],
//                                            backFace, d);
//                                }
//
//                                /*
//                                 * We zero out the mask
//                                 */
//                                for (l = 0; l < h; ++l) {
//                                    for (k = 0; k < w; ++k) {
//                                        mask[n + k + l * dims[u]] = 0;
//                                    }
//                                }
//
//                                /*
//                                 * And then finally increment the counters and continue
//                                 */
//                                i += w;
//                                n += w;
//                            } else {
//                                i++;
//                                n++;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /*
//     * This function renders a single quad in the scene. This quad may represent
//     * many adjacent voxel faces - so in order to create the illusion of many
//     * faces, you might consider using a tiling function in your voxel shader.
//     * For this reason I've included the quad w and h as parameters.
//     * 
//     * For example, if your texture coordinates for a single voxel face were 0 -
//     * 1 on a given axis, they should now be 0 - w or 0 - h. Then you
//     * can calculate the correct texture coordinate in your fragement shader
//     * using coord.xy = fract(coord.xy).
//     * 
//     * 
//     */
//    final int[] indexes1 = {2, 0, 1, 1, 3, 2};
//    final int[] indexes2 = {2, 3, 1, 1, 0, 2};
//
//    final Vector3f[] vertices = new Vector3f[4];
//    final Vector2f[] uvs = new Vector2f[4];
//
//    protected void Mesher_makeQuad(int x[], int du[], int dv[],
//            final int w, final int h,
//            final int voxel, final boolean backFace, final int d) {
//
//        // d: 0=X,1=Y,2=Z
//        float layer = 0;
//        int[] indexes = backFace ? indexes1 : indexes2;
//        Block block = blockList.get((short) voxel);
//
////<editor-fold defaultstate="collapsed" desc="nublada quad method">
////        int[] vertices = new int[8];
////        vertices[0] = getCompressedData(x[0], x[1], x[2]);
////        vertices[2] = getCompressedData(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2]);
////        vertices[4] = getCompressedData(x[0] + du[0], x[1] + du[1], x[2] + du[2]);
////        vertices[6] = getCompressedData(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2]);
////        if (d == 2) {
////            if (!backFace) {
////                vertices[1] = getCompressedData(0, h, 0);
////                vertices[3] = getCompressedData(0, 0, 0);
////                vertices[5] = getCompressedData(w, h, 0);
////                vertices[7] = getCompressedData(w, 0, 0);
////            } else {
////                vertices[1] = getCompressedData(w, h, 0);
////                vertices[3] = getCompressedData(w, 0, 0);
////                vertices[5] = getCompressedData(0, h, 0);
////                vertices[7] = getCompressedData(0, 0, 0);
////            }
////        } else if (d == 0) {
////            if (backFace) {
////                vertices[1] = getCompressedData(0, w, 0);
////                vertices[3] = getCompressedData(h, w, 0);
////                vertices[5] = getCompressedData(0, 0, 0);
////                vertices[7] = getCompressedData(h, 0, 0);
////            } else {
////                vertices[1] = getCompressedData(h, w, 0);
////                vertices[3] = getCompressedData(0, w, 0);
////                vertices[5] = getCompressedData(h, 0, 0);
////                vertices[7] = getCompressedData(0, 0, 0);
////            }
////        } else {
////            if (!backFace) {
////                vertices[1] = getCompressedData(0, 0, 0);
////                vertices[3] = getCompressedData(h, 0, 0);
////                vertices[5] = getCompressedData(0, w, 0);
////                vertices[7] = getCompressedData(h, w, 0);
////            } else {
////                vertices[1] = getCompressedData(h, 0, 0);
////                vertices[3] = getCompressedData(0, 0, 0);
////                vertices[5] = getCompressedData(h, w, 0);
////                vertices[7] = getCompressedData(0, w, 0);
////            }
////        }
////        position.add((float) vertices[0]);
////        position.add((float) vertices[1]);
////        position.add((float) vertices[2]);
////        position.add((float) vertices[3]);
////        position.add((float) vertices[4]);
////        position.add((float) vertices[5]);
////        position.add((float) vertices[6]);
////        position.add((float) vertices[7]);
////</editor-fold>
////The ONLY difference betweent this method and nublada is that nublada uses packed single integer coordinates, and that uvs and vertex positions are combined
//        vertices[0].set(x[0], x[1], x[2]);
//        vertices[1].set(x[0] + dv[0], x[1] + dv[1], x[2] + dv[2]);
//        vertices[2].set(x[0] + du[0], x[1] + du[1], x[2] + du[2]);
//        vertices[3].set(x[0] + du[0] + dv[0], x[1] + du[1] + dv[1], x[2] + du[2] + dv[2]);
//        if (block != null && block.texture != null) {
//            int texture = 0;
//            switch (d) {
//                case 0 -> {
//                    if (backFace) {
//                        texture = block.texture.getNEG_X();
//                    } else {
//                        texture = block.texture.getPOS_X();
//                    }
//                    layer = BlockList.calculateTextureLayer(texture);
//
//                    // Z=180 flip
//                    uvs[3].set(0, w);
//                    uvs[2].set(h, w);
//                    uvs[1].set(0, 0);
//                    uvs[0].set(h, 0);
//                }
//                case 1 -> {
//                    if (backFace) {
//                        texture = block.texture.getNEG_Y();
//                    } else {
//                        texture = block.texture.getPOS_Y();
//                    }
//                    layer = BlockList.calculateTextureLayer(texture);
//
//                    uvs[0].set(0, w);
//                    uvs[1].set(h, w);
//                    uvs[2].set(0, 0);
//                    uvs[3].set(h, 0);
//                }
//                default -> {
//                    if (backFace) {
//                        texture = block.texture.getNEG_Z();
//                    } else {
//                        texture = block.texture.getPOS_Z();
//                    }
//                    layer = BlockList.calculateTextureLayer(texture);
//
//                    // X=90 flip
//                    uvs[1].set(0, h);
//                    uvs[3].set(w, h);
//                    uvs[0].set(0, 0);
//                    uvs[2].set(w, 0);
//                }
//            }
//
//        }
//
//        for (int i : indexes) {
//            Vector3f vertex = vertices[i];
//            position.add(vertex.x);
//            position.add(vertex.y);
//            position.add(vertex.z);
//            uv.add(uvs[i].x);
//            uv.add(-uvs[i].y);
//            uv.add(layer);
//        }
//        //----------------------------------------------------
//    }
//
//    @Override
//    public void loadToGpu() {
//        if (posBuffer.length == 0) {
//            mesh.empty = true;
//        } else {
//            mesh.empty = false;
//            mesh.sendBuffersToGPU(posBuffer, uvBuffer);
//        }
//        posBuffer = null;
//        uvBuffer = null;
//    }
//
//}
