///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.blocks.type;
//
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.items.block.construction.blockTypes.BlockType;
//import com.xbuilders.engine.world.chunk.blockData.BlockData;
//import com.xbuilders.engine.world.chunk.blockData.BlockDataUtils;
//import com.xbuilders.engine.world.chunk.blockData.BlockOrientation;
//import org.joml.Vector3f;
//import processing.core.PShape;
//
///**
// *
// * @author zipCoder933
// */
//public class RaisedTrackRenderer extends BlockType {
//
//
//
////<editor-fold defaultstate="collapsed" desc="Draw up_track">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_up_track = {
//        new Vector3f(0.0f, -0.0625f, -0.0f), //0
//        new Vector3f(1.0f, -0.0625f, 0.0f), //1
//        new Vector3f(0.0f, 0.93750006f, 1.0f), //2
//        new Vector3f(1.0f, 0.93750006f, 1.0f), //3
//    };
//    static Vector3f[] uv_up_track = {
//        new Vector3f(0.0f, 1.0f,0), //0
//        new Vector3f(1.0f, 0.0f,0), //1
//        new Vector3f(1.0f, 1.0f,0), //2
//        new Vector3f(0.0f, 0.0f,0), //3
//    };
//
////</editor-fold>
//    public boolean constructBlock_up_track(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_up_track_center_faces(verts_up_track, uv_up_track, block, shape, x, y, z);
//
//        return false;
//    }
//
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_up_track_center_faces(Vector3f[] verts2, Vector3f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//    }
//
////</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw up_track_A">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_up_track_A = {
//        new Vector3f(1.0f, -0.0625f, 0.0f), //0
//        new Vector3f(1.0f, -0.0625f, 1.0f), //1
//        new Vector3f(0.0f, 0.93750006f, -0.0f), //2
//        new Vector3f(-0.0f, 0.93750006f, 1.0f), //3
//    };
//    static Vector3f[] uv_up_track_A = {
//        new Vector3f(0.0f, 1.0f,0), //0
//        new Vector3f(1.0f, 0.0f,0), //1
//        new Vector3f(1.0f, 1.0f,0), //2
//        new Vector3f(0.0f, 0.0f,0), //3
//    };
//
////</editor-fold>
//    public boolean constructBlock_up_track_A(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_up_track_A_center_faces(verts_up_track_A, uv_up_track_A, block, shape, x, y, z);
//
//        return false;
//    }
//
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_up_track_A_center_faces(Vector3f[] verts2, Vector3f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//    }
//
////</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw up_track_B">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_up_track_B = {
//        new Vector3f(1.0f, -0.0625f, 1.0f), //0
//        new Vector3f(-0.0f, -0.0625f, 1.0f), //1
//        new Vector3f(1.0f, 0.93750006f, 0.0f), //2
//        new Vector3f(0.0f, 0.93750006f, -0.0f), //3
//    };
//    static Vector3f[] uv_up_track_B = {
//        new Vector3f(0.0f, 1.0f,0), //0
//        new Vector3f(1.0f, 0.0f,0), //1
//        new Vector3f(1.0f, 1.0f,0), //2
//        new Vector3f(0.0f, 0.0f,0), //3
//    };
//
////</editor-fold>
//    public boolean constructBlock_up_track_B(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_up_track_B_center_faces(verts_up_track_B, uv_up_track_B, block, shape, x, y, z);
//
//        return false;
//    }
//
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_up_track_B_center_faces(Vector3f[] verts2, Vector3f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//    }
//
////</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw up_track_C">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_up_track_C = {
//        new Vector3f(-0.0f, -0.0625f, 0.9999989f), //0
//        new Vector3f(0.0f, -0.0625f, -0.0f), //1
//        new Vector3f(1.0f, 0.93750006f, 1.0f), //2
//        new Vector3f(1.0f, 0.93750006f, 0.0f), //3
//    };
//    static Vector3f[] uv_up_track_C = {
//        new Vector3f(0.0f, 1.0f,0), //0
//        new Vector3f(1.0f, 0.0f,0), //1
//        new Vector3f(1.0f, 1.0f,0), //2
//        new Vector3f(0.0f, 0.0f,0), //3
//    };
//
////</editor-fold>
//    public boolean constructBlock_up_track_C(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_up_track_C_center_faces(verts_up_track_C, uv_up_track_C, block, shape, x, y, z);
//
//        return false;
//    }
//
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_up_track_C_center_faces(Vector3f[] verts2, Vector3f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//    }
//
////</editor-fold>
////</editor-fold>
//    @Override
//    public void constructBlock(PShape buffers, Block block, BlockData data, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        BlockOrientation orientation = BlockDataUtils.getBlockOrientation(data);
//
//        if (orientation == null || orientation.getXZ() == 0) {
//            make_up_track_center_faces(verts_up_track, uv_up_track, block, buffers, x, y, z);
//        } else if (orientation.getXZ() == 1) {
//            make_up_track_A_center_faces(verts_up_track_A, uv_up_track_A, block, buffers, x, y, z);
//        } else if (orientation.getXZ() == 2) {
//            make_up_track_B_center_faces(verts_up_track_B, uv_up_track_B, block, buffers, x, y, z);
//        } else {
//            make_up_track_C_center_faces(verts_up_track_C, uv_up_track_C, block, buffers, x, y, z);
//        }
//    }
//
//
//
//}
