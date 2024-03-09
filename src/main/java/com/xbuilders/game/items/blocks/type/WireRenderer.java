///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.blocks.type;
//
//import com.xbuilders.engine.game.player.UserControlledPlayer;
//import com.xbuilders.engine.game.player.raycasting.Ray;
//import com.xbuilders.engine.items.ItemTag;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.items.block.construction.blockTypes.BlockType;
//import com.xbuilders.engine.world.chunk.blockData.BlockData;
//import processing.core.PShape;
//import processing.core.PVector;
//
///**
// * @author zipCoder933
// */
//public class WireRenderer extends BlockType {
//
//    @Override
//    public BlockData getInitialBlockData(UserControlledPlayer player, Block block, Ray ray) {
//        return new BlockData(2);
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Draw on_center">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_on_center = {
//            new PVector(0.56f, 0.56f, 0.56f), //0
//            new PVector(0.56f, 0.44f, 0.56f), //1
//            new PVector(0.56f, 0.56f, 0.44f), //2
//            new PVector(0.56f, 0.44f, 0.44f), //3
//            new PVector(0.44f, 0.56f, 0.56f), //4
//            new PVector(0.44f, 0.44f, 0.56f), //5
//            new PVector(0.44f, 0.56f, 0.44f), //6
//            new PVector(0.44f, 0.44f, 0.44f), //7
//    };
//    static PVector[] uv_on_center = {
//            new PVector(0.0f, 1.0f), //0
//            new PVector(0.18f, 0.82f), //1
//            new PVector(0.18f, 1.0f), //2
//            new PVector(0.18f, 1.0f), //3
//            new PVector(0.0f, 0.82f), //4
//            new PVector(0.0f, 1.0f), //5
//            new PVector(0.18f, 0.82f), //6
//            new PVector(0.18f, 1.0f), //7
//            new PVector(0.18f, 0.82f), //8
//            new PVector(0.0f, 1.0f), //9
//            new PVector(0.0f, 0.82f), //10
//            new PVector(0.18f, 0.82f), //11
//            new PVector(0.0f, 1.0f), //12
//            new PVector(0.0f, 0.82f), //13
//            new PVector(0.0f, 0.82f), //14
//            new PVector(0.0f, 0.82f), //15
//            new PVector(0.0f, 0.82f), //16
//            new PVector(0.18f, 1.0f), //17
//            new PVector(0.18f, 1.0f), //18
//            new PVector(0.18f, 1.0f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_on_center(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_on_center_center_faces(verts_on_center, uv_on_center, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_on_center_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw on_top">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_on_top = {
//            new PVector(0.56f, 0.44f, 0.56f), //0
//            new PVector(0.44f, 0.44f, 0.56f), //1
//            new PVector(0.56f, 0.0f, 0.56f), //2
//            new PVector(0.44f, 0.0f, 0.56f), //3
//            new PVector(0.56f, 0.44f, 0.44f), //4
//            new PVector(0.44f, 0.44f, 0.44f), //5
//            new PVector(0.56f, 0.0f, 0.44f), //6
//            new PVector(0.44f, 0.0f, 0.44f), //7
//    };
//    static PVector[] uv_on_top = {
//            new PVector(0.44f, 0.68f), //0
//            new PVector(0.88f, 0.82f), //1
//            new PVector(0.44f, 0.82f), //2
//            new PVector(0.18f, 0.82f), //3
//            new PVector(0.0f, 1.0f), //4
//            new PVector(0.0f, 0.82f), //5
//            new PVector(0.44f, 0.68f), //6
//            new PVector(0.88f, 0.82f), //7
//            new PVector(0.44f, 0.82f), //8
//            new PVector(0.44f, 0.68f), //9
//            new PVector(0.88f, 0.82f), //10
//            new PVector(0.44f, 0.82f), //11
//            new PVector(0.44f, 0.68f), //12
//            new PVector(0.88f, 0.82f), //13
//            new PVector(0.44f, 0.82f), //14
//            new PVector(0.88f, 0.68f), //15
//            new PVector(0.18f, 1.0f), //16
//            new PVector(0.88f, 0.68f), //17
//            new PVector(0.88f, 0.68f), //18
//            new PVector(0.88f, 0.68f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_on_top(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (shouldRenderSide(block, negativeY)) {
//            make_on_top_negativeY_faces(verts_on_top, uv_on_top, block, shape, x, y, z);
//        }
//        make_on_top_center_faces(verts_on_top, uv_on_top, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_on_top_negativeY_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    private static void make_on_top_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw on_bottom">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_on_bottom = {
//            new PVector(0.56f, 0.56f, 0.44f), //0
//            new PVector(0.44f, 0.56f, 0.44f), //1
//            new PVector(0.56f, 1.0f, 0.44f), //2
//            new PVector(0.44f, 1.0f, 0.44f), //3
//            new PVector(0.56f, 0.56f, 0.56f), //4
//            new PVector(0.44f, 0.56f, 0.56f), //5
//            new PVector(0.56f, 1.0f, 0.56f), //6
//            new PVector(0.44f, 1.0f, 0.56f), //7
//    };
//    static PVector[] uv_on_bottom = {
//            new PVector(0.44f, 0.68f), //0
//            new PVector(0.88f, 0.82f), //1
//            new PVector(0.44f, 0.82f), //2
//            new PVector(0.18f, 0.82f), //3
//            new PVector(0.0f, 1.0f), //4
//            new PVector(0.0f, 0.82f), //5
//            new PVector(0.44f, 0.68f), //6
//            new PVector(0.88f, 0.82f), //7
//            new PVector(0.44f, 0.82f), //8
//            new PVector(0.44f, 0.68f), //9
//            new PVector(0.88f, 0.82f), //10
//            new PVector(0.44f, 0.82f), //11
//            new PVector(0.44f, 0.68f), //12
//            new PVector(0.88f, 0.82f), //13
//            new PVector(0.44f, 0.82f), //14
//            new PVector(0.88f, 0.68f), //15
//            new PVector(0.18f, 1.0f), //16
//            new PVector(0.88f, 0.68f), //17
//            new PVector(0.88f, 0.68f), //18
//            new PVector(0.88f, 0.68f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_on_bottom(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (shouldRenderSide(block, positiveY)) {
//            make_on_bottom_positiveY_faces(verts_on_bottom, uv_on_bottom, block, shape, x, y, z);
//        }
//        make_on_bottom_center_faces(verts_on_bottom, uv_on_bottom, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_on_bottom_positiveY_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    private static void make_on_bottom_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw on_x1">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_on_x1 = {
//            new PVector(0.56f, 0.56f, 0.56f), //0
//            new PVector(0.56f, 0.44f, 0.56f), //1
//            new PVector(1.0f, 0.56f, 0.56f), //2
//            new PVector(1.0f, 0.44f, 0.56f), //3
//            new PVector(0.56f, 0.56f, 0.44f), //4
//            new PVector(0.56f, 0.44f, 0.44f), //5
//            new PVector(1.0f, 0.56f, 0.44f), //6
//            new PVector(1.0f, 0.44f, 0.44f), //7
//    };
//    static PVector[] uv_on_x1 = {
//            new PVector(0.44f, 0.68f), //0
//            new PVector(0.88f, 0.82f), //1
//            new PVector(0.44f, 0.82f), //2
//            new PVector(0.18f, 0.82f), //3
//            new PVector(0.0f, 1.0f), //4
//            new PVector(0.0f, 0.82f), //5
//            new PVector(0.44f, 0.68f), //6
//            new PVector(0.88f, 0.82f), //7
//            new PVector(0.44f, 0.82f), //8
//            new PVector(0.44f, 0.68f), //9
//            new PVector(0.88f, 0.82f), //10
//            new PVector(0.44f, 0.82f), //11
//            new PVector(0.44f, 0.68f), //12
//            new PVector(0.88f, 0.82f), //13
//            new PVector(0.44f, 0.82f), //14
//            new PVector(0.88f, 0.68f), //15
//            new PVector(0.18f, 1.0f), //16
//            new PVector(0.88f, 0.68f), //17
//            new PVector(0.88f, 0.68f), //18
//            new PVector(0.88f, 0.68f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_on_x1(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (shouldRenderSide(block, positiveX)) {
//            make_on_x1_positiveX_faces(verts_on_x1, uv_on_x1, block, shape, x, y, z);
//        }
//        make_on_x1_center_faces(verts_on_x1, uv_on_x1, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_on_x1_positiveX_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    private static void make_on_x1_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw on_x2">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_on_x2 = {
//            new PVector(0.44f, 0.56f, 0.44f), //0
//            new PVector(0.44f, 0.44f, 0.44f), //1
//            new PVector(0.0f, 0.56f, 0.44f), //2
//            new PVector(0.0f, 0.44f, 0.44f), //3
//            new PVector(0.44f, 0.56f, 0.56f), //4
//            new PVector(0.44f, 0.44f, 0.56f), //5
//            new PVector(0.0f, 0.56f, 0.56f), //6
//            new PVector(0.0f, 0.44f, 0.56f), //7
//    };
//    static PVector[] uv_on_x2 = {
//            new PVector(0.44f, 0.68f), //0
//            new PVector(0.88f, 0.82f), //1
//            new PVector(0.44f, 0.82f), //2
//            new PVector(0.18f, 0.82f), //3
//            new PVector(0.0f, 1.0f), //4
//            new PVector(0.0f, 0.82f), //5
//            new PVector(0.44f, 0.68f), //6
//            new PVector(0.88f, 0.82f), //7
//            new PVector(0.44f, 0.82f), //8
//            new PVector(0.44f, 0.68f), //9
//            new PVector(0.88f, 0.82f), //10
//            new PVector(0.44f, 0.82f), //11
//            new PVector(0.44f, 0.68f), //12
//            new PVector(0.88f, 0.82f), //13
//            new PVector(0.44f, 0.82f), //14
//            new PVector(0.88f, 0.68f), //15
//            new PVector(0.18f, 1.0f), //16
//            new PVector(0.88f, 0.68f), //17
//            new PVector(0.88f, 0.68f), //18
//            new PVector(0.88f, 0.68f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_on_x2(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_on_x2_center_faces(verts_on_x2, uv_on_x2, block, shape, x, y, z);
//        if (shouldRenderSide(block, negativeX)) {
//            make_on_x2_negativeX_faces(verts_on_x2, uv_on_x2, block, shape, x, y, z);
//        }
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_on_x2_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    private static void make_on_x2_negativeX_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw on_y1">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_on_y1 = {
//            new PVector(0.56f, 0.56f, 0.44f), //0
//            new PVector(0.56f, 0.44f, 0.44f), //1
//            new PVector(0.56f, 0.56f, 0.0f), //2
//            new PVector(0.56f, 0.44f, 0.0f), //3
//            new PVector(0.44f, 0.56f, 0.44f), //4
//            new PVector(0.44f, 0.44f, 0.44f), //5
//            new PVector(0.44f, 0.56f, 0.0f), //6
//            new PVector(0.44f, 0.44f, 0.0f), //7
//    };
//    static PVector[] uv_on_y1 = {
//            new PVector(0.44f, 0.68f), //0
//            new PVector(0.88f, 0.82f), //1
//            new PVector(0.44f, 0.82f), //2
//            new PVector(0.18f, 0.82f), //3
//            new PVector(0.0f, 1.0f), //4
//            new PVector(0.0f, 0.82f), //5
//            new PVector(0.44f, 0.68f), //6
//            new PVector(0.88f, 0.82f), //7
//            new PVector(0.44f, 0.82f), //8
//            new PVector(0.44f, 0.68f), //9
//            new PVector(0.88f, 0.82f), //10
//            new PVector(0.44f, 0.82f), //11
//            new PVector(0.44f, 0.68f), //12
//            new PVector(0.88f, 0.82f), //13
//            new PVector(0.44f, 0.82f), //14
//            new PVector(0.88f, 0.68f), //15
//            new PVector(0.18f, 1.0f), //16
//            new PVector(0.88f, 0.68f), //17
//            new PVector(0.88f, 0.68f), //18
//            new PVector(0.88f, 0.68f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_on_y1(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (shouldRenderSide(block, negativeZ)) {
//            make_on_y1_negativeZ_faces(verts_on_y1, uv_on_y1, block, shape, x, y, z);
//        }
//        make_on_y1_center_faces(verts_on_y1, uv_on_y1, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_on_y1_negativeZ_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    private static void make_on_y1_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw on_y2">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_on_y2 = {
//            new PVector(0.44f, 0.56f, 0.56f), //0
//            new PVector(0.44f, 0.44f, 0.56f), //1
//            new PVector(0.44f, 0.56f, 1.0f), //2
//            new PVector(0.44f, 0.44f, 1.0f), //3
//            new PVector(0.56f, 0.56f, 0.56f), //4
//            new PVector(0.56f, 0.44f, 0.56f), //5
//            new PVector(0.56f, 0.56f, 1.0f), //6
//            new PVector(0.56f, 0.44f, 1.0f), //7
//    };
//    static PVector[] uv_on_y2 = {
//            new PVector(0.44f, 0.68f), //0
//            new PVector(0.88f, 0.82f), //1
//            new PVector(0.44f, 0.82f), //2
//            new PVector(0.18f, 0.82f), //3
//            new PVector(0.0f, 1.0f), //4
//            new PVector(0.0f, 0.82f), //5
//            new PVector(0.44f, 0.68f), //6
//            new PVector(0.88f, 0.82f), //7
//            new PVector(0.44f, 0.82f), //8
//            new PVector(0.44f, 0.68f), //9
//            new PVector(0.88f, 0.82f), //10
//            new PVector(0.44f, 0.82f), //11
//            new PVector(0.44f, 0.68f), //12
//            new PVector(0.88f, 0.82f), //13
//            new PVector(0.44f, 0.82f), //14
//            new PVector(0.88f, 0.68f), //15
//            new PVector(0.18f, 1.0f), //16
//            new PVector(0.88f, 0.68f), //17
//            new PVector(0.88f, 0.68f), //18
//            new PVector(0.88f, 0.68f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_on_y2(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_on_y2_center_faces(verts_on_y2, uv_on_y2, block, shape, x, y, z);
//        if (shouldRenderSide(block, positiveZ)) {
//            make_on_y2_positiveZ_faces(verts_on_y2, uv_on_y2, block, shape, x, y, z);
//        }
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_on_y2_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    private static void make_on_y2_positiveZ_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw off_center">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_off_center = {
//            new PVector(0.56f, 0.56f, 0.56f), //0
//            new PVector(0.56f, 0.44f, 0.56f), //1
//            new PVector(0.56f, 0.56f, 0.44f), //2
//            new PVector(0.56f, 0.44f, 0.44f), //3
//            new PVector(0.44f, 0.56f, 0.56f), //4
//            new PVector(0.44f, 0.44f, 0.56f), //5
//            new PVector(0.44f, 0.56f, 0.44f), //6
//            new PVector(0.44f, 0.44f, 0.44f), //7
//    };
//    static PVector[] uv_off_center = {
//            new PVector(0.0f, 0.62f), //0
//            new PVector(0.18f, 0.44f), //1
//            new PVector(0.18f, 0.62f), //2
//            new PVector(0.18f, 0.62f), //3
//            new PVector(0.0f, 0.44f), //4
//            new PVector(0.0f, 0.62f), //5
//            new PVector(0.18f, 0.44f), //6
//            new PVector(0.18f, 0.62f), //7
//            new PVector(0.18f, 0.44f), //8
//            new PVector(0.0f, 0.62f), //9
//            new PVector(0.0f, 0.44f), //10
//            new PVector(0.18f, 0.44f), //11
//            new PVector(0.0f, 0.62f), //12
//            new PVector(0.0f, 0.44f), //13
//            new PVector(0.0f, 0.44f), //14
//            new PVector(0.0f, 0.44f), //15
//            new PVector(0.0f, 0.44f), //16
//            new PVector(0.18f, 0.62f), //17
//            new PVector(0.18f, 0.62f), //18
//            new PVector(0.18f, 0.62f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_off_center(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_off_center_center_faces(verts_off_center, uv_off_center, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_off_center_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw off_top">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_off_top = {
//            new PVector(0.56f, 0.44f, 0.56f), //0
//            new PVector(0.44f, 0.44f, 0.56f), //1
//            new PVector(0.56f, 0.0f, 0.56f), //2
//            new PVector(0.44f, 0.0f, 0.56f), //3
//            new PVector(0.56f, 0.44f, 0.44f), //4
//            new PVector(0.44f, 0.44f, 0.44f), //5
//            new PVector(0.56f, 0.0f, 0.44f), //6
//            new PVector(0.44f, 0.0f, 0.44f), //7
//    };
//    static PVector[] uv_off_top = {
//            new PVector(0.44f, 0.32f), //0
//            new PVector(0.88f, 0.44f), //1
//            new PVector(0.44f, 0.44f), //2
//            new PVector(0.18f, 0.44f), //3
//            new PVector(0.0f, 0.62f), //4
//            new PVector(0.0f, 0.44f), //5
//            new PVector(0.44f, 0.32f), //6
//            new PVector(0.88f, 0.44f), //7
//            new PVector(0.44f, 0.44f), //8
//            new PVector(0.44f, 0.32f), //9
//            new PVector(0.88f, 0.44f), //10
//            new PVector(0.44f, 0.44f), //11
//            new PVector(0.44f, 0.32f), //12
//            new PVector(0.88f, 0.44f), //13
//            new PVector(0.44f, 0.44f), //14
//            new PVector(0.88f, 0.32f), //15
//            new PVector(0.18f, 0.62f), //16
//            new PVector(0.88f, 0.32f), //17
//            new PVector(0.88f, 0.32f), //18
//            new PVector(0.88f, 0.32f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_off_top(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (shouldRenderSide(block, negativeY)) {
//            make_off_top_negativeY_faces(verts_off_top, uv_off_top, block, shape, x, y, z);
//        }
//        make_off_top_center_faces(verts_off_top, uv_off_top, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_off_top_negativeY_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    private static void make_off_top_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw off_bottom">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_off_bottom = {
//            new PVector(0.56f, 0.56f, 0.44f), //0
//            new PVector(0.44f, 0.56f, 0.44f), //1
//            new PVector(0.56f, 1.0f, 0.44f), //2
//            new PVector(0.44f, 1.0f, 0.44f), //3
//            new PVector(0.56f, 0.56f, 0.56f), //4
//            new PVector(0.44f, 0.56f, 0.56f), //5
//            new PVector(0.56f, 1.0f, 0.56f), //6
//            new PVector(0.44f, 1.0f, 0.56f), //7
//    };
//    static PVector[] uv_off_bottom = {
//            new PVector(0.44f, 0.32f), //0
//            new PVector(0.88f, 0.44f), //1
//            new PVector(0.44f, 0.44f), //2
//            new PVector(0.18f, 0.44f), //3
//            new PVector(0.0f, 0.62f), //4
//            new PVector(0.0f, 0.44f), //5
//            new PVector(0.44f, 0.32f), //6
//            new PVector(0.88f, 0.44f), //7
//            new PVector(0.44f, 0.44f), //8
//            new PVector(0.44f, 0.32f), //9
//            new PVector(0.88f, 0.44f), //10
//            new PVector(0.44f, 0.44f), //11
//            new PVector(0.44f, 0.32f), //12
//            new PVector(0.88f, 0.44f), //13
//            new PVector(0.44f, 0.44f), //14
//            new PVector(0.88f, 0.32f), //15
//            new PVector(0.18f, 0.62f), //16
//            new PVector(0.88f, 0.32f), //17
//            new PVector(0.88f, 0.32f), //18
//            new PVector(0.88f, 0.32f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_off_bottom(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (shouldRenderSide(block, positiveY)) {
//            make_off_bottom_positiveY_faces(verts_off_bottom, uv_off_bottom, block, shape, x, y, z);
//        }
//        make_off_bottom_center_faces(verts_off_bottom, uv_off_bottom, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_off_bottom_positiveY_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    private static void make_off_bottom_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw off_x1">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_off_x1 = {
//            new PVector(0.44f, 0.56f, 0.44f), //0
//            new PVector(0.44f, 0.44f, 0.44f), //1
//            new PVector(0.0f, 0.56f, 0.44f), //2
//            new PVector(0.0f, 0.44f, 0.44f), //3
//            new PVector(0.44f, 0.56f, 0.56f), //4
//            new PVector(0.44f, 0.44f, 0.56f), //5
//            new PVector(0.0f, 0.56f, 0.56f), //6
//            new PVector(0.0f, 0.44f, 0.56f), //7
//    };
//    static PVector[] uv_off_x1 = {
//            new PVector(0.44f, 0.32f), //0
//            new PVector(0.88f, 0.44f), //1
//            new PVector(0.44f, 0.44f), //2
//            new PVector(0.18f, 0.44f), //3
//            new PVector(0.0f, 0.62f), //4
//            new PVector(0.0f, 0.44f), //5
//            new PVector(0.44f, 0.32f), //6
//            new PVector(0.88f, 0.44f), //7
//            new PVector(0.44f, 0.44f), //8
//            new PVector(0.44f, 0.32f), //9
//            new PVector(0.88f, 0.44f), //10
//            new PVector(0.44f, 0.44f), //11
//            new PVector(0.44f, 0.32f), //12
//            new PVector(0.88f, 0.44f), //13
//            new PVector(0.44f, 0.44f), //14
//            new PVector(0.88f, 0.32f), //15
//            new PVector(0.18f, 0.62f), //16
//            new PVector(0.88f, 0.32f), //17
//            new PVector(0.88f, 0.32f), //18
//            new PVector(0.88f, 0.32f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_off_x1(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_off_x1_center_faces(verts_off_x1, uv_off_x1, block, shape, x, y, z);
//        if (shouldRenderSide(block, negativeX)) {
//            make_off_x1_negativeX_faces(verts_off_x1, uv_off_x1, block, shape, x, y, z);
//        }
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_off_x1_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    private static void make_off_x1_negativeX_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw off_x2">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_off_x2 = {
//            new PVector(0.56f, 0.56f, 0.56f), //0
//            new PVector(0.56f, 0.44f, 0.56f), //1
//            new PVector(1.0f, 0.56f, 0.56f), //2
//            new PVector(1.0f, 0.44f, 0.56f), //3
//            new PVector(0.56f, 0.56f, 0.44f), //4
//            new PVector(0.56f, 0.44f, 0.44f), //5
//            new PVector(1.0f, 0.56f, 0.44f), //6
//            new PVector(1.0f, 0.44f, 0.44f), //7
//    };
//    static PVector[] uv_off_x2 = {
//            new PVector(0.44f, 0.32f), //0
//            new PVector(0.88f, 0.44f), //1
//            new PVector(0.44f, 0.44f), //2
//            new PVector(0.18f, 0.44f), //3
//            new PVector(0.0f, 0.62f), //4
//            new PVector(0.0f, 0.44f), //5
//            new PVector(0.44f, 0.32f), //6
//            new PVector(0.88f, 0.44f), //7
//            new PVector(0.44f, 0.44f), //8
//            new PVector(0.44f, 0.32f), //9
//            new PVector(0.88f, 0.44f), //10
//            new PVector(0.44f, 0.44f), //11
//            new PVector(0.44f, 0.32f), //12
//            new PVector(0.88f, 0.44f), //13
//            new PVector(0.44f, 0.44f), //14
//            new PVector(0.88f, 0.32f), //15
//            new PVector(0.18f, 0.62f), //16
//            new PVector(0.88f, 0.32f), //17
//            new PVector(0.88f, 0.32f), //18
//            new PVector(0.88f, 0.32f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_off_x2(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (shouldRenderSide(block, positiveX)) {
//            make_off_x2_positiveX_faces(verts_off_x2, uv_off_x2, block, shape, x, y, z);
//        }
//        make_off_x2_center_faces(verts_off_x2, uv_off_x2, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_off_x2_positiveX_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    private static void make_off_x2_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw off_y1">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_off_y1 = {
//            new PVector(0.44f, 0.56f, 0.56f), //0
//            new PVector(0.44f, 0.44f, 0.56f), //1
//            new PVector(0.44f, 0.56f, 1.0f), //2
//            new PVector(0.44f, 0.44f, 1.0f), //3
//            new PVector(0.56f, 0.56f, 0.56f), //4
//            new PVector(0.56f, 0.44f, 0.56f), //5
//            new PVector(0.56f, 0.56f, 1.0f), //6
//            new PVector(0.56f, 0.44f, 1.0f), //7
//    };
//    static PVector[] uv_off_y1 = {
//            new PVector(0.44f, 0.32f), //0
//            new PVector(0.88f, 0.44f), //1
//            new PVector(0.44f, 0.44f), //2
//            new PVector(0.18f, 0.44f), //3
//            new PVector(0.0f, 0.62f), //4
//            new PVector(0.0f, 0.44f), //5
//            new PVector(0.44f, 0.32f), //6
//            new PVector(0.88f, 0.44f), //7
//            new PVector(0.44f, 0.44f), //8
//            new PVector(0.44f, 0.32f), //9
//            new PVector(0.88f, 0.44f), //10
//            new PVector(0.44f, 0.44f), //11
//            new PVector(0.44f, 0.32f), //12
//            new PVector(0.88f, 0.44f), //13
//            new PVector(0.44f, 0.44f), //14
//            new PVector(0.88f, 0.32f), //15
//            new PVector(0.18f, 0.62f), //16
//            new PVector(0.88f, 0.32f), //17
//            new PVector(0.88f, 0.32f), //18
//            new PVector(0.88f, 0.32f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_off_y1(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_off_y1_center_faces(verts_off_y1, uv_off_y1, block, shape, x, y, z);
//        if (shouldRenderSide(block, positiveZ)) {
//            make_off_y1_positiveZ_faces(verts_off_y1, uv_off_y1, block, shape, x, y, z);
//        }
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_off_y1_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    private static void make_off_y1_positiveZ_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw off_y2">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_off_y2 = {
//            new PVector(0.56f, 0.56f, 0.44f), //0
//            new PVector(0.56f, 0.44f, 0.44f), //1
//            new PVector(0.56f, 0.56f, 0.0f), //2
//            new PVector(0.56f, 0.44f, 0.0f), //3
//            new PVector(0.44f, 0.56f, 0.44f), //4
//            new PVector(0.44f, 0.44f, 0.44f), //5
//            new PVector(0.44f, 0.56f, 0.0f), //6
//            new PVector(0.44f, 0.44f, 0.0f), //7
//    };
//    static PVector[] uv_off_y2 = {
//            new PVector(0.44f, 0.32f), //0
//            new PVector(0.88f, 0.44f), //1
//            new PVector(0.44f, 0.44f), //2
//            new PVector(0.18f, 0.44f), //3
//            new PVector(0.0f, 0.62f), //4
//            new PVector(0.0f, 0.44f), //5
//            new PVector(0.44f, 0.32f), //6
//            new PVector(0.88f, 0.44f), //7
//            new PVector(0.44f, 0.44f), //8
//            new PVector(0.44f, 0.32f), //9
//            new PVector(0.88f, 0.44f), //10
//            new PVector(0.44f, 0.44f), //11
//            new PVector(0.44f, 0.32f), //12
//            new PVector(0.88f, 0.44f), //13
//            new PVector(0.44f, 0.44f), //14
//            new PVector(0.88f, 0.32f), //15
//            new PVector(0.18f, 0.62f), //16
//            new PVector(0.88f, 0.32f), //17
//            new PVector(0.88f, 0.32f), //18
//            new PVector(0.88f, 0.32f), //19
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_off_y2(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (shouldRenderSide(block, negativeZ)) {
//            make_off_y2_negativeZ_faces(verts_off_y2, uv_off_y2, block, shape, x, y, z);
//        }
//        make_off_y2_center_faces(verts_off_y2, uv_off_y2, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_off_y2_negativeZ_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//    }
//
//    private static void make_off_y2_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
//    @Override
//    public void constructBlock(PShape buffers, Block block, BlockData data, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        if (data != null && data.bytes.length > 0 && data.bytes[0] > 0) {
//            constructBlock_off_center(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            if (makeConnection(negativeX, block)) {
//                constructBlock_off_x1(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(positiveX, block)) {
//                constructBlock_off_x2(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(negativeY, block)) {
//                constructBlock_off_top(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(positiveY, block)) {
//                constructBlock_off_bottom(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(positiveZ, block)) {
//                constructBlock_off_y1(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(negativeZ, block)) {
//                constructBlock_off_y2(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//        } else {
//            constructBlock_on_center(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            if (makeConnection(negativeX, block)) {
//                constructBlock_on_x2(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(positiveX, block)) {
//                constructBlock_on_x1(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(negativeY, block)) {
//                constructBlock_on_top(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(positiveY, block)) {
//                constructBlock_on_bottom(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(positiveZ, block)) {
//                constructBlock_on_y2(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//            if (makeConnection(negativeZ, block)) {
//                constructBlock_on_y1(block, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//            }
//        }
//    }
//
//
//    private boolean makeConnection(Block negativeX, Block block) {
//        return negativeX != null && (negativeX.getId() == block.getId()
//                || negativeX.getTags().contains(ItemTag.ELECTRONIC));
//    }
//
//}
