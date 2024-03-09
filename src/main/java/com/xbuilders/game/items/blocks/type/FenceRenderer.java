///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.blocks.type;
//
//import com.xbuilders.engine.VoxelGame;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.items.block.construction.blockTypes.BlockType;
//import com.xbuilders.engine.utils.math.AABB;
//import com.xbuilders.engine.world.chunk.blockData.BlockData;
//import processing.core.BufferSet;
//import processing.core.PVector;
//
//import java.util.function.Consumer;
//
//import static com.xbuilders.engine.items.BlockList.DEFAULT_BLOCK_TYPE_ID;
//
///**
// * @author zipCoder933
// */
//public class FenceRenderer extends BlockType {
//
//    private boolean isSolid(Block block) {
//        return (block.isSolid())
//                && block.getRenderType() != BlockRenderType.FLOOR
//                && block.getRenderType() != BlockRenderType.WALL_ITEM
//                && block.getRenderType() != BlockRenderType.SPRITE;
//    }
//
//    @Override
//    public void constructBlock(BufferSet buffers, Block block, BlockData data, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (negativeY == null || !negativeY.isOpaque()
//                || negativeY.getRenderType() != DEFAULT_BLOCK_TYPE_ID) {
//            make_negativeY_faces(verts, block, buffers, x, y, z);
//        }
//        if (positiveY == null || !positiveY.isOpaque()
//                || positiveY.getRenderType() != DEFAULT_BLOCK_TYPE_ID) {
//            make_positiveY_faces(verts, block, buffers, x, y, z);
//        }
//        make_center_faces(verts, block, buffers, x, y, z);
//
//        if (positiveZ != null && isSolid(positiveZ)) {
//            make_fence_rail0_center_faces(verts_fence_rail0, uv_fence_rail0, block, buffers, x, y, z);
//        }
//        if (negativeX != null && isSolid(negativeX)) {
//            make_fence_rail1_center_faces(verts_fence_rail1, uv_fence_rail1, block, buffers, x, y, z);
//        }
//        if (negativeZ != null && isSolid(negativeZ)) {
//            make_fence_rail2_center_faces(verts_fence_rail2, uv_fence_rail2, block, buffers, x, y, z);
//        }
//        if (positiveX != null && isSolid(positiveX)) {
//            make_fence_rail3_center_faces(verts_fence_rail3, uv_fence_rail3, block, buffers, x, y, z);
//        }
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Draw fence_rail0">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_fence_rail0 = {
//            new PVector(0.437501f, 0.0625f, 1.0f), //0
//            new PVector(0.437501f, 0.25f, 1.0f), //1
//            new PVector(0.562501f, 0.0625f, 1.0f), //2
//            new PVector(0.562501f, 0.25f, 1.0f), //3
//            new PVector(0.4375f, 0.0625f, 0.625f), //4
//            new PVector(0.4375f, 0.25f, 0.625f), //5
//            new PVector(0.5625f, 0.0625f, 0.625f), //6
//            new PVector(0.5625f, 0.25f, 0.625f), //7
//            new PVector(0.437501f, 0.4375f, 1.0f), //8
//            new PVector(0.437501f, 0.625f, 1.0f), //9
//            new PVector(0.562501f, 0.4375f, 1.0f), //10
//            new PVector(0.562501f, 0.625f, 1.0f), //11
//            new PVector(0.4375f, 0.4375f, 0.625f), //12
//            new PVector(0.4375f, 0.625f, 0.625f), //13
//            new PVector(0.5625f, 0.4375f, 0.625f), //14
//            new PVector(0.5625f, 0.625f, 0.625f), //15
//    };
//    static PVector[] uv_fence_rail0 = {
//            new PVector(0.375f, 0.5625f), //0
//            new PVector(-0.0f, 0.4375f), //1
//            new PVector(0.375f, 0.4375f), //2
//            new PVector(0.0f, 0.5f), //3
//            new PVector(0.375f, 0.375f), //4
//            new PVector(0.375f, 0.5f), //5
//            new PVector(-0.0f, 0.625f), //6
//            new PVector(0.375f, 0.8125f), //7
//            new PVector(-0.0f, 0.8125f), //8
//            new PVector(-0.0f, 0.625f), //9
//            new PVector(0.375f, 0.8125f), //10
//            new PVector(-0.0f, 0.8125f), //11
//            new PVector(0.375f, 0.5625f), //12
//            new PVector(0.0f, 0.4375f), //13
//            new PVector(0.375f, 0.4375f), //14
//            new PVector(0.0f, 0.25f), //15
//            new PVector(0.375f, 0.125f), //16
//            new PVector(0.375f, 0.25f), //17
//            new PVector(0.0f, 0.25f), //18
//            new PVector(0.375f, 0.4375f), //19
//            new PVector(0.0f, 0.4375f), //20
//            new PVector(0.0f, 0.25f), //21
//            new PVector(0.375f, 0.4375f), //22
//            new PVector(0.0f, 0.4375f), //23
//            new PVector(-0.0f, 0.5625f), //24
//            new PVector(0.0f, 0.375f), //25
//            new PVector(0.375f, 0.625f), //26
//            new PVector(0.375f, 0.625f), //27
//            new PVector(0.0f, 0.5625f), //28
//            new PVector(0.0f, 0.125f), //29
//            new PVector(0.375f, 0.25f), //30
//            new PVector(0.375f, 0.25f), //31
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_fence_rail0(Block block, BufferSet shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_fence_rail0_center_faces(verts_fence_rail0, uv_fence_rail0, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_fence_rail0_center_faces(PVector[] verts2, PVector[] uv2, Block block, BufferSet shape, int x, int y, int z) {
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[2].x), getUVTextureCoord_Y(block, uv2[2].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[1].x), getUVTextureCoord_Y(block, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[5].x), getUVTextureCoord_Y(block, uv2[5].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[4].x), getUVTextureCoord_Y(block, uv2[4].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[8].x), getUVTextureCoord_Y(block, uv2[8].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[7].x), getUVTextureCoord_Y(block, uv2[7].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[11].x), getUVTextureCoord_Y(block, uv2[11].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[10].x), getUVTextureCoord_Y(block, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[14].x), getUVTextureCoord_Y(block, uv2[14].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[13].x), getUVTextureCoord_Y(block, uv2[13].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(block, uv2[17].x), getUVTextureCoord_Y(block, uv2[17].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[16].x), getUVTextureCoord_Y(block, uv2[16].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(block, uv2[20].x), getUVTextureCoord_Y(block, uv2[20].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[19].x), getUVTextureCoord_Y(block, uv2[19].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(block, uv2[23].x), getUVTextureCoord_Y(block, uv2[23].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[22].x), getUVTextureCoord_Y(block, uv2[22].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[1].x), getUVTextureCoord_Y(block, uv2[1].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[24].x), getUVTextureCoord_Y(block, uv2[24].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[4].x), getUVTextureCoord_Y(block, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[25].x), getUVTextureCoord_Y(block, uv2[25].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[7].x), getUVTextureCoord_Y(block, uv2[7].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[26].x), getUVTextureCoord_Y(block, uv2[26].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[10].x), getUVTextureCoord_Y(block, uv2[10].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[27].x), getUVTextureCoord_Y(block, uv2[27].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[13].x), getUVTextureCoord_Y(block, uv2[13].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[28].x), getUVTextureCoord_Y(block, uv2[28].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[16].x), getUVTextureCoord_Y(block, uv2[16].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(block, uv2[29].x), getUVTextureCoord_Y(block, uv2[29].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[19].x), getUVTextureCoord_Y(block, uv2[19].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[30].x), getUVTextureCoord_Y(block, uv2[30].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));//FACE
//
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[22].x), getUVTextureCoord_Y(block, uv2[22].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[31].x), getUVTextureCoord_Y(block, uv2[31].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw fence_rail1">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_fence_rail1 = {
//            new PVector(0.0f, 0.0625f, 0.562501f), //0
//            new PVector(0.0f, 0.25f, 0.562501f), //1
//            new PVector(-0.0f, 0.0625f, 0.437501f), //2
//            new PVector(-0.0f, 0.25f, 0.437501f), //3
//            new PVector(0.375f, 0.0625f, 0.5625f), //4
//            new PVector(0.375f, 0.25f, 0.5625f), //5
//            new PVector(0.375f, 0.0625f, 0.4375f), //6
//            new PVector(0.375f, 0.25f, 0.4375f), //7
//            new PVector(0.0f, 0.4375f, 0.562501f), //8
//            new PVector(0.0f, 0.625f, 0.562501f), //9
//            new PVector(-0.0f, 0.4375f, 0.437501f), //10
//            new PVector(-0.0f, 0.625f, 0.437501f), //11
//            new PVector(0.375f, 0.4375f, 0.5625f), //12
//            new PVector(0.375f, 0.625f, 0.5625f), //13
//            new PVector(0.375f, 0.4375f, 0.4375f), //14
//            new PVector(0.375f, 0.625f, 0.4375f), //15
//    };
//    static PVector[] uv_fence_rail1 = {
//            new PVector(0.375f, 0.5625f), //0
//            new PVector(0.375f, 0.4375f), //1
//            new PVector(-0.0f, 0.4375f), //2
//            new PVector(0.0f, 0.5f), //3
//            new PVector(0.375f, 0.5f), //4
//            new PVector(0.375f, 0.375f), //5
//            new PVector(-0.0f, 0.625f), //6
//            new PVector(-0.0f, 0.8125f), //7
//            new PVector(0.375f, 0.8125f), //8
//            new PVector(-0.0f, 0.625f), //9
//            new PVector(-0.0f, 0.8125f), //10
//            new PVector(0.375f, 0.8125f), //11
//            new PVector(0.375f, 0.5625f), //12
//            new PVector(0.375f, 0.4375f), //13
//            new PVector(0.0f, 0.4375f), //14
//            new PVector(0.0f, 0.25f), //15
//            new PVector(0.375f, 0.25f), //16
//            new PVector(0.375f, 0.125f), //17
//            new PVector(0.0f, 0.25f), //18
//            new PVector(0.0f, 0.4375f), //19
//            new PVector(0.375f, 0.4375f), //20
//            new PVector(0.0f, 0.25f), //21
//            new PVector(0.0f, 0.4375f), //22
//            new PVector(0.375f, 0.4375f), //23
//            new PVector(-0.0f, 0.5625f), //24
//            new PVector(0.0f, 0.375f), //25
//            new PVector(0.375f, 0.625f), //26
//            new PVector(0.375f, 0.625f), //27
//            new PVector(0.0f, 0.5625f), //28
//            new PVector(0.0f, 0.125f), //29
//            new PVector(0.375f, 0.25f), //30
//            new PVector(0.375f, 0.25f), //31
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_fence_rail1(Block block, BufferSet shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_fence_rail1_center_faces(verts_fence_rail1, uv_fence_rail1, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_fence_rail1_center_faces(PVector[] verts2, PVector[] uv2, Block block, BufferSet shape, int x, int y, int z) {
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[2].x), getUVTextureCoord_Y(block, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[1].x), getUVTextureCoord_Y(block, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[5].x), getUVTextureCoord_Y(block, uv2[5].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[4].x), getUVTextureCoord_Y(block, uv2[4].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[8].x), getUVTextureCoord_Y(block, uv2[8].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[7].x), getUVTextureCoord_Y(block, uv2[7].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[11].x), getUVTextureCoord_Y(block, uv2[11].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[10].x), getUVTextureCoord_Y(block, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[14].x), getUVTextureCoord_Y(block, uv2[14].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[13].x), getUVTextureCoord_Y(block, uv2[13].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[17].x), getUVTextureCoord_Y(block, uv2[17].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(block, uv2[16].x), getUVTextureCoord_Y(block, uv2[16].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[20].x), getUVTextureCoord_Y(block, uv2[20].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(block, uv2[19].x), getUVTextureCoord_Y(block, uv2[19].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));//FACE
//
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[23].x), getUVTextureCoord_Y(block, uv2[23].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(block, uv2[22].x), getUVTextureCoord_Y(block, uv2[22].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[24].x), getUVTextureCoord_Y(block, uv2[24].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[2].x), getUVTextureCoord_Y(block, uv2[2].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[25].x), getUVTextureCoord_Y(block, uv2[25].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[5].x), getUVTextureCoord_Y(block, uv2[5].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[26].x), getUVTextureCoord_Y(block, uv2[26].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[8].x), getUVTextureCoord_Y(block, uv2[8].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[27].x), getUVTextureCoord_Y(block, uv2[27].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[11].x), getUVTextureCoord_Y(block, uv2[11].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[28].x), getUVTextureCoord_Y(block, uv2[28].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[14].x), getUVTextureCoord_Y(block, uv2[14].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(block, uv2[29].x), getUVTextureCoord_Y(block, uv2[29].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[17].x), getUVTextureCoord_Y(block, uv2[17].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[30].x), getUVTextureCoord_Y(block, uv2[30].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[20].x), getUVTextureCoord_Y(block, uv2[20].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));//FACE
//
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[31].x), getUVTextureCoord_Y(block, uv2[31].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[23].x), getUVTextureCoord_Y(block, uv2[23].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw fence_rail2">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_fence_rail2 = {
//            new PVector(0.437499f, 0.0625f, 0.0f), //0
//            new PVector(0.437499f, 0.25f, 0.0f), //1
//            new PVector(0.5625f, 0.0625f, -0.0f), //2
//            new PVector(0.5625f, 0.25f, -0.0f), //3
//            new PVector(0.4375f, 0.0625f, 0.375f), //4
//            new PVector(0.4375f, 0.25f, 0.375f), //5
//            new PVector(0.5625f, 0.0625f, 0.375f), //6
//            new PVector(0.5625f, 0.25f, 0.375f), //7
//            new PVector(0.437499f, 0.4375f, 0.0f), //8
//            new PVector(0.437499f, 0.625f, 0.0f), //9
//            new PVector(0.562499f, 0.4375f, -0.0f), //10
//            new PVector(0.562499f, 0.625f, -0.0f), //11
//            new PVector(0.4375f, 0.4375f, 0.375f), //12
//            new PVector(0.4375f, 0.625f, 0.375f), //13
//            new PVector(0.5625f, 0.4375f, 0.375f), //14
//            new PVector(0.5625f, 0.625f, 0.375f), //15
//    };
//    static PVector[] uv_fence_rail2 = {
//            new PVector(0.75f, 0.5625f), //0
//            new PVector(0.75f, 0.4375f), //1
//            new PVector(0.375f, 0.4375f), //2
//            new PVector(0.375f, 0.5f), //3
//            new PVector(0.75f, 0.5f), //4
//            new PVector(0.75f, 0.375f), //5
//            new PVector(0.375f, 0.625f), //6
//            new PVector(0.375f, 0.8125f), //7
//            new PVector(0.75f, 0.8125f), //8
//            new PVector(0.375f, 0.625f), //9
//            new PVector(0.375f, 0.8125f), //10
//            new PVector(0.75f, 0.8125f), //11
//            new PVector(0.75f, 0.5625f), //12
//            new PVector(0.75f, 0.4375f), //13
//            new PVector(0.375f, 0.4375f), //14
//            new PVector(0.375f, 0.25f), //15
//            new PVector(0.75f, 0.25f), //16
//            new PVector(0.75f, 0.125f), //17
//            new PVector(0.375f, 0.25f), //18
//            new PVector(0.375f, 0.4375f), //19
//            new PVector(0.75f, 0.4375f), //20
//            new PVector(0.375f, 0.25f), //21
//            new PVector(0.375f, 0.4375f), //22
//            new PVector(0.75f, 0.4375f), //23
//            new PVector(0.375f, 0.5625f), //24
//            new PVector(0.375f, 0.375f), //25
//            new PVector(0.75f, 0.625f), //26
//            new PVector(0.75f, 0.625f), //27
//            new PVector(0.375f, 0.5625f), //28
//            new PVector(0.375f, 0.125f), //29
//            new PVector(0.75f, 0.25f), //30
//            new PVector(0.75f, 0.25f), //31
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_fence_rail2(Block block, BufferSet shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_fence_rail2_center_faces(verts_fence_rail2, uv_fence_rail2, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_fence_rail2_center_faces(PVector[] verts2, PVector[] uv2, Block block, BufferSet shape, int x, int y, int z) {
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[2].x), getUVTextureCoord_Y(block, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[1].x), getUVTextureCoord_Y(block, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[5].x), getUVTextureCoord_Y(block, uv2[5].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[4].x), getUVTextureCoord_Y(block, uv2[4].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[8].x), getUVTextureCoord_Y(block, uv2[8].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[7].x), getUVTextureCoord_Y(block, uv2[7].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[11].x), getUVTextureCoord_Y(block, uv2[11].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[10].x), getUVTextureCoord_Y(block, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[14].x), getUVTextureCoord_Y(block, uv2[14].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[13].x), getUVTextureCoord_Y(block, uv2[13].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[17].x), getUVTextureCoord_Y(block, uv2[17].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(block, uv2[16].x), getUVTextureCoord_Y(block, uv2[16].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[20].x), getUVTextureCoord_Y(block, uv2[20].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(block, uv2[19].x), getUVTextureCoord_Y(block, uv2[19].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));//FACE
//
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[23].x), getUVTextureCoord_Y(block, uv2[23].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(block, uv2[22].x), getUVTextureCoord_Y(block, uv2[22].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[24].x), getUVTextureCoord_Y(block, uv2[24].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[2].x), getUVTextureCoord_Y(block, uv2[2].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[25].x), getUVTextureCoord_Y(block, uv2[25].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[5].x), getUVTextureCoord_Y(block, uv2[5].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[26].x), getUVTextureCoord_Y(block, uv2[26].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[8].x), getUVTextureCoord_Y(block, uv2[8].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[27].x), getUVTextureCoord_Y(block, uv2[27].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[11].x), getUVTextureCoord_Y(block, uv2[11].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[28].x), getUVTextureCoord_Y(block, uv2[28].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[14].x), getUVTextureCoord_Y(block, uv2[14].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(block, uv2[29].x), getUVTextureCoord_Y(block, uv2[29].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[17].x), getUVTextureCoord_Y(block, uv2[17].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[30].x), getUVTextureCoord_Y(block, uv2[30].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[20].x), getUVTextureCoord_Y(block, uv2[20].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));//FACE
//
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[31].x), getUVTextureCoord_Y(block, uv2[31].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[23].x), getUVTextureCoord_Y(block, uv2[23].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw fence_rail3">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_fence_rail3 = {
//            new PVector(1.0f, 0.0625f, 0.5625f), //0
//            new PVector(1.0f, 0.25f, 0.5625f), //1
//            new PVector(1.0f, 0.0625f, 0.4375f), //2
//            new PVector(1.0f, 0.25f, 0.4375f), //3
//            new PVector(0.625f, 0.0625f, 0.5625f), //4
//            new PVector(0.625f, 0.25f, 0.5625f), //5
//            new PVector(0.625f, 0.0625f, 0.4375f), //6
//            new PVector(0.625f, 0.25f, 0.4375f), //7
//            new PVector(1.0f, 0.4375f, 0.5625f), //8
//            new PVector(1.0f, 0.625f, 0.5625f), //9
//            new PVector(1.0f, 0.4375f, 0.4375f), //10
//            new PVector(1.0f, 0.625f, 0.4375f), //11
//            new PVector(0.625f, 0.4375f, 0.5625f), //12
//            new PVector(0.625f, 0.625f, 0.5625f), //13
//            new PVector(0.625f, 0.4375f, 0.4375f), //14
//            new PVector(0.625f, 0.625f, 0.4375f), //15
//    };
//    static PVector[] uv_fence_rail3 = {
//            new PVector(0.75f, 0.5625f), //0
//            new PVector(0.375f, 0.4375f), //1
//            new PVector(0.75f, 0.4375f), //2
//            new PVector(0.375f, 0.5f), //3
//            new PVector(0.75f, 0.375f), //4
//            new PVector(0.75f, 0.5f), //5
//            new PVector(0.375f, 0.625f), //6
//            new PVector(0.75f, 0.8125f), //7
//            new PVector(0.375f, 0.8125f), //8
//            new PVector(0.375f, 0.625f), //9
//            new PVector(0.75f, 0.8125f), //10
//            new PVector(0.375f, 0.8125f), //11
//            new PVector(0.75f, 0.5625f), //12
//            new PVector(0.375f, 0.4375f), //13
//            new PVector(0.75f, 0.4375f), //14
//            new PVector(0.375f, 0.25f), //15
//            new PVector(0.75f, 0.125f), //16
//            new PVector(0.75f, 0.25f), //17
//            new PVector(0.375f, 0.25f), //18
//            new PVector(0.75f, 0.4375f), //19
//            new PVector(0.375f, 0.4375f), //20
//            new PVector(0.375f, 0.25f), //21
//            new PVector(0.75f, 0.4375f), //22
//            new PVector(0.375f, 0.4375f), //23
//            new PVector(0.375f, 0.5625f), //24
//            new PVector(0.375f, 0.375f), //25
//            new PVector(0.75f, 0.625f), //26
//            new PVector(0.75f, 0.625f), //27
//            new PVector(0.375f, 0.5625f), //28
//            new PVector(0.375f, 0.125f), //29
//            new PVector(0.75f, 0.25f), //30
//            new PVector(0.75f, 0.25f), //31
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_fence_rail3(Block block, BufferSet shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_fence_rail3_center_faces(verts_fence_rail3, uv_fence_rail3, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_fence_rail3_center_faces(PVector[] verts2, PVector[] uv2, Block block, BufferSet shape, int x, int y, int z) {
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[2].x), getUVTextureCoord_Y(block, uv2[2].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[1].x), getUVTextureCoord_Y(block, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[5].x), getUVTextureCoord_Y(block, uv2[5].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[4].x), getUVTextureCoord_Y(block, uv2[4].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[8].x), getUVTextureCoord_Y(block, uv2[8].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[7].x), getUVTextureCoord_Y(block, uv2[7].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[11].x), getUVTextureCoord_Y(block, uv2[11].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[10].x), getUVTextureCoord_Y(block, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[14].x), getUVTextureCoord_Y(block, uv2[14].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[13].x), getUVTextureCoord_Y(block, uv2[13].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(block, uv2[17].x), getUVTextureCoord_Y(block, uv2[17].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[16].x), getUVTextureCoord_Y(block, uv2[16].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(block, uv2[20].x), getUVTextureCoord_Y(block, uv2[20].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[19].x), getUVTextureCoord_Y(block, uv2[19].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(block, uv2[23].x), getUVTextureCoord_Y(block, uv2[23].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[22].x), getUVTextureCoord_Y(block, uv2[22].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[1].x), getUVTextureCoord_Y(block, uv2[1].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[24].x), getUVTextureCoord_Y(block, uv2[24].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[4].x), getUVTextureCoord_Y(block, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[25].x), getUVTextureCoord_Y(block, uv2[25].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[7].x), getUVTextureCoord_Y(block, uv2[7].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[26].x), getUVTextureCoord_Y(block, uv2[26].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[10].x), getUVTextureCoord_Y(block, uv2[10].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[27].x), getUVTextureCoord_Y(block, uv2[27].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[13].x), getUVTextureCoord_Y(block, uv2[13].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[28].x), getUVTextureCoord_Y(block, uv2[28].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[16].x), getUVTextureCoord_Y(block, uv2[16].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(block, uv2[29].x), getUVTextureCoord_Y(block, uv2[29].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(block, uv2[19].x), getUVTextureCoord_Y(block, uv2[19].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(block, uv2[30].x), getUVTextureCoord_Y(block, uv2[30].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));//FACE
//
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(block, uv2[22].x), getUVTextureCoord_Y(block, uv2[22].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(block, uv2[31].x), getUVTextureCoord_Y(block, uv2[31].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw Mesh">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts = {
//            new PVector(0.375f, 0.0f, 0.375f), //0
//            new PVector(0.375f, 1.0f, 0.375f), //1
//            new PVector(0.375f, 0.0f, 0.625f), //2
//            new PVector(0.375f, 1.0f, 0.625f), //3
//            new PVector(0.625f, 0.0f, 0.375f), //4
//            new PVector(0.625f, 1.0f, 0.375f), //5
//            new PVector(0.625f, 0.0f, 0.625f), //6
//            new PVector(0.625f, 1.0f, 0.625f), //7
//    };
//    static PVector[] uvVerts = {
//            new PVector(0.6875f, 0.93750006f), //0
//            new PVector(0.93750006f, 0.6875f), //1
//            new PVector(0.93750006f, 0.93750006f), //2
//            new PVector(0.93750006f, 0.125f), //3
//            new PVector(0.6875f, 0.375f), //4
//            new PVector(0.6875f, 0.125f), //5
//            new PVector(0.125f, 0.0f), //6
//            new PVector(0.375f, 1.0f), //7
//            new PVector(0.125f, 1.0f), //8
//            new PVector(0.125f, 0.0f), //9
//            new PVector(0.375f, 1.0f), //10
//            new PVector(0.125f, 1.0f), //11
//            new PVector(0.125f, 0.0f), //12
//            new PVector(0.375f, 1.0f), //13
//            new PVector(0.125f, 1.0f), //14
//            new PVector(0.125f, 0.0f), //15
//            new PVector(0.375f, 1.0f), //16
//            new PVector(0.125f, 1.0f), //17
//            new PVector(0.6875f, 0.6875f), //18
//            new PVector(0.93750006f, 0.375f), //19
//            new PVector(0.375f, 0.0f), //20
//            new PVector(0.375f, 0.0f), //21
//            new PVector(0.375f, 0.0f), //22
//            new PVector(0.375f, 0.0f), //23
//    };
////</editor-fold>
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_negativeY_faces(PVector[] verts2, Block block, BufferSet shape, int x, int y, int z) {
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uvVerts[2].x), getUVTextureCoord_Y(block, uvVerts[2].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uvVerts[1].x), getUVTextureCoord_Y(block, uvVerts[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uvVerts[0].x), getUVTextureCoord_Y(block, uvVerts[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uvVerts[1].x), getUVTextureCoord_Y(block, uvVerts[1].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uvVerts[18].x), getUVTextureCoord_Y(block, uvVerts[18].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uvVerts[0].x), getUVTextureCoord_Y(block, uvVerts[0].y));//FACE
//    }
//
//    private static void make_positiveY_faces(PVector[] verts2, Block block, BufferSet shape, int x, int y, int z) {
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uvVerts[5].x), getUVTextureCoord_Y(block, uvVerts[5].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uvVerts[4].x), getUVTextureCoord_Y(block, uvVerts[4].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uvVerts[3].x), getUVTextureCoord_Y(block, uvVerts[3].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uvVerts[4].x), getUVTextureCoord_Y(block, uvVerts[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uvVerts[19].x), getUVTextureCoord_Y(block, uvVerts[19].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uvVerts[3].x), getUVTextureCoord_Y(block, uvVerts[3].y));//FACE
//    }
//
//    private static void make_center_faces(PVector[] verts2, Block block, BufferSet shape, int x, int y, int z) {
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uvVerts[8].x), getUVTextureCoord_Y(block, uvVerts[8].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uvVerts[7].x), getUVTextureCoord_Y(block, uvVerts[7].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uvVerts[6].x), getUVTextureCoord_Y(block, uvVerts[6].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uvVerts[11].x), getUVTextureCoord_Y(block, uvVerts[11].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uvVerts[10].x), getUVTextureCoord_Y(block, uvVerts[10].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uvVerts[9].x), getUVTextureCoord_Y(block, uvVerts[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uvVerts[14].x), getUVTextureCoord_Y(block, uvVerts[14].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uvVerts[13].x), getUVTextureCoord_Y(block, uvVerts[13].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uvVerts[12].x), getUVTextureCoord_Y(block, uvVerts[12].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uvVerts[17].x), getUVTextureCoord_Y(block, uvVerts[17].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uvVerts[16].x), getUVTextureCoord_Y(block, uvVerts[16].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uvVerts[15].x), getUVTextureCoord_Y(block, uvVerts[15].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uvVerts[7].x), getUVTextureCoord_Y(block, uvVerts[7].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uvVerts[20].x), getUVTextureCoord_Y(block, uvVerts[20].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uvVerts[6].x), getUVTextureCoord_Y(block, uvVerts[6].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uvVerts[10].x), getUVTextureCoord_Y(block, uvVerts[10].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uvVerts[21].x), getUVTextureCoord_Y(block, uvVerts[21].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uvVerts[9].x), getUVTextureCoord_Y(block, uvVerts[9].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uvVerts[13].x), getUVTextureCoord_Y(block, uvVerts[13].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uvVerts[22].x), getUVTextureCoord_Y(block, uvVerts[22].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uvVerts[12].x), getUVTextureCoord_Y(block, uvVerts[12].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uvVerts[16].x), getUVTextureCoord_Y(block, uvVerts[16].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uvVerts[23].x), getUVTextureCoord_Y(block, uvVerts[23].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uvVerts[15].x), getUVTextureCoord_Y(block, uvVerts[15].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
//    float sixtheenth = 0.0625f;
//
//    @Override
//    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
//        box.setPosAndSize(x + (sixtheenth * 6), y - 0.5f, z + (sixtheenth * 6), (sixtheenth * 4), 1.5f, (sixtheenth * 4));
//        consumer.accept(box);
//
//        if (isSolid(VoxelGame.getWorld().getBlock(x + 1, y, z))) {
//            box.setPosAndSize(x + (sixtheenth * 10), y - 0.5f, z + (sixtheenth * 6), (sixtheenth * 6), 1.5f, (sixtheenth * 4));
//            consumer.accept(box);
//        }
//        if (isSolid(VoxelGame.getWorld().getBlock(x - 1, y, z))) {
//            box.setPosAndSize(x, y - 0.5f, z + (sixtheenth * 6), (sixtheenth * 6), 1.5f, (sixtheenth * 4));
//            consumer.accept(box);
//        }
//        if (isSolid(VoxelGame.getWorld().getBlock(x, y, z + 1))) {
//            box.setPosAndSize(x + (sixtheenth * 6), y - 0.5f, z + (sixtheenth * 10), (sixtheenth * 4), 1.5f, (sixtheenth * 6));
//            consumer.accept(box);
//        }
//        if (isSolid(VoxelGame.getWorld().getBlock(x, y, z - 1))) {
//            box.setPosAndSize(x + (sixtheenth * 6), y - 0.5f, z, (sixtheenth * 4), 1.5f, (sixtheenth * 6));
//            consumer.accept(box);
//        }
//    }
//
//    @Override
//    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
//        box.setPosAndSize(x + (sixtheenth * 6), y, z + (sixtheenth * 6), (sixtheenth * 4), 1f, (sixtheenth * 4));
//        consumer.accept(box);
//
//        if (isSolid(VoxelGame.getWorld().getBlock(x + 1, y, z))) {
//            box.setPosAndSize(x + (sixtheenth * 10), y, z + (sixtheenth * 6), (sixtheenth * 6), 1f, (sixtheenth * 4));
//            consumer.accept(box);
//        }
//        if (isSolid(VoxelGame.getWorld().getBlock(x - 1, y, z))) {
//            box.setPosAndSize(x, y, z + (sixtheenth * 6), (sixtheenth * 6), 1f, (sixtheenth * 4));
//            consumer.accept(box);
//        }
//        if (isSolid(VoxelGame.getWorld().getBlock(x, y, z + 1))) {
//            box.setPosAndSize(x + (sixtheenth * 6), y, z + (sixtheenth * 10), (sixtheenth * 4), 1f, (sixtheenth * 6));
//            consumer.accept(box);
//        }
//        if (isSolid(VoxelGame.getWorld().getBlock(x, y, z - 1))) {
//            box.setPosAndSize(x + (sixtheenth * 6), y, z, (sixtheenth * 4), 1f, (sixtheenth * 6));
//            consumer.accept(box);
//        }
//    }
//
//}
