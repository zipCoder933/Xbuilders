///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.blocks.type;
//
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.items.block.construction.blockTypes.BlockType;
//import com.xbuilders.engine.utils.math.AABB;
//import com.xbuilders.engine.world.chunk.blockData.BlockData;
//import com.xbuilders.engine.world.chunk.blockData.BlockDataUtils;
//import com.xbuilders.engine.world.chunk.blockData.BlockOrientation;
//import org.joml.Vector2f;
//import org.joml.Vector3f;
//import processing.core.PShape;
//
//import java.util.function.Consumer;
//
///**
// *
// * @author zipCoder933
// */
//public class LampRenderer extends BlockType {
//
//    private boolean sideIsSolid(Block block) {
//        return block != null && block.isSolid();
//    }
//
//    @Override
//    public void constructBlock(PShape buffers, Block block, BlockData data, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        BlockOrientation orientation = BlockDataUtils.getBlockOrientation(data);
//
//        if (orientation != null && orientation.getXZ() == 2 && sideIsSolid(positiveZ)) {
//            if (positiveZ.getRenderType() == BlockRenderType.FENCE) {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side_fence, 2);
//                make_lamp_side_fence_center_faces(verts2, uv_lamp_side_fence, block, buffers, x, y, z);
//            } else {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side, 2);
//                make_lamp_side_center_faces(verts2, uv_lamp_side, block, buffers, x, y, z);
//            }
//        } else if (orientation != null && orientation.getXZ() == 3 && sideIsSolid(negativeX)) {
//            if (negativeX.getRenderType() == BlockRenderType.FENCE) {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side_fence, 3);
//                make_lamp_side_fence_center_faces(verts2, uv_lamp_side_fence, block, buffers, x, y, z);
//            } else {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side, 3);
//                make_lamp_side_center_faces(verts2, uv_lamp_side, block, buffers, x, y, z);
//            }
//        } else if (orientation != null && orientation.getXZ() == 0 && sideIsSolid(negativeZ)) {
//            if (negativeZ.getRenderType() == BlockRenderType.FENCE) {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side_fence, 0);
//                make_lamp_side_fence_center_faces(verts2, uv_lamp_side_fence, block, buffers, x, y, z);
//            } else {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side, 0);
//                make_lamp_side_center_faces(verts2, uv_lamp_side, block, buffers, x, y, z);
//            }
//        } else if (orientation != null && orientation.getXZ() == 1 && sideIsSolid(positiveX)) {
//            if (positiveX.getRenderType() == BlockRenderType.FENCE) {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side_fence, 1);
//                make_lamp_side_fence_center_faces(verts2, uv_lamp_side_fence, block, buffers, x, y, z);
//            } else {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side, 1);
//                make_lamp_side_center_faces(verts2, uv_lamp_side, block, buffers, x, y, z);
//            }
//        } else if (sideIsSolid(positiveY)) {
//            make_lamp_center_faces(verts_lamp, uv_lamp, block, buffers, x, y, z);
//        } else if (sideIsSolid(positiveZ)) {
//            if (positiveZ.getRenderType() == BlockRenderType.FENCE) {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side_fence, 2);
//                make_lamp_side_fence_center_faces(verts2, uv_lamp_side_fence, block, buffers, x, y, z);
//            } else {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side, 2);
//                make_lamp_side_center_faces(verts2, uv_lamp_side, block, buffers, x, y, z);
//            }
//        } else if (sideIsSolid(negativeX)) {
//            if (negativeX.getRenderType() == BlockRenderType.FENCE) {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side_fence, 3);
//                make_lamp_side_fence_center_faces(verts2, uv_lamp_side_fence, block, buffers, x, y, z);
//            } else {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side, 3);
//                make_lamp_side_center_faces(verts2, uv_lamp_side, block, buffers, x, y, z);
//            }
//        } else if (sideIsSolid(negativeZ)) {
//            if (negativeZ.getRenderType() == BlockRenderType.FENCE) {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side_fence, 0);
//                make_lamp_side_fence_center_faces(verts2, uv_lamp_side_fence, block, buffers, x, y, z);
//            } else {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side, 0);
//                make_lamp_side_center_faces(verts2, uv_lamp_side, block, buffers, x, y, z);
//            }
//        } else if (sideIsSolid(positiveX)) {
//            if (positiveX.getRenderType() == BlockRenderType.FENCE) {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side_fence, 1);
//                make_lamp_side_fence_center_faces(verts2, uv_lamp_side_fence, block, buffers, x, y, z);
//            } else {
//                Vector3f[] verts2 = verts2 = rotateVerticiesYAxis(verts_lamp_side, 1);
//                make_lamp_side_center_faces(verts2, uv_lamp_side, block, buffers, x, y, z);
//            }
//        } else {
//            make_lamp_center_faces(verts_lamp, uv_lamp, block, buffers, x, y, z);
//        }
//    }
//
////<editor-fold defaultstate="collapsed" desc="Draw lamp">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_lamp = {
//        new Vector3f(0.687499f, 0.8125f, 0.687501f), //0
//        new Vector3f(0.312499f, 0.8125f, 0.687499f), //1
//        new Vector3f(0.687501f, 0.8125f, 0.312501f), //2
//        new Vector3f(0.312501f, 0.8125f, 0.312499f), //3
//        new Vector3f(0.312501f, 0.375f, 0.312499f), //4
//        new Vector3f(0.312499f, 0.375f, 0.687499f), //5
//        new Vector3f(0.687501f, 0.375f, 0.312501f), //6
//        new Vector3f(0.687499f, 0.375f, 0.687501f), //7
//        new Vector3f(0.312501f, 0.8125f, 0.312499f), //8
//        new Vector3f(0.312499f, 0.8125f, 0.687499f), //9
//        new Vector3f(0.687501f, 0.8125f, 0.312501f), //10
//        new Vector3f(0.687499f, 0.8125f, 0.687501f), //11
//        new Vector3f(0.44375002f, 0.8125f, 0.44375002f), //12
//        new Vector3f(0.44375002f, 0.8125f, 0.55625f), //13
//        new Vector3f(0.55625f, 0.8125f, 0.44375002f), //14
//        new Vector3f(0.55625f, 0.8125f, 0.55625f), //15
//        new Vector3f(0.44375002f, 1.0f, 0.44375002f), //16
//        new Vector3f(0.44375002f, 1.0f, 0.55625f), //17
//        new Vector3f(0.55625f, 1.0f, 0.44375002f), //18
//        new Vector3f(0.55625f, 1.0f, 0.55625f), //19
//        new Vector3f(0.25f, 0.375f, 0.25f), //20
//        new Vector3f(0.25f, 0.375f, 0.75f), //21
//        new Vector3f(0.75f, 0.375f, 0.25f), //22
//        new Vector3f(0.75f, 0.375f, 0.75f), //23
//        new Vector3f(0.25f, 0.4375f, 0.25f), //24
//        new Vector3f(0.25f, 0.4375f, 0.75f), //25
//        new Vector3f(0.75f, 0.4375f, 0.25f), //26
//        new Vector3f(0.75f, 0.4375f, 0.75f), //27
//    };
//    static Vector2f[] uv_lamp = {
//        new Vector2f(0.3125f, 1.0f), //0
//        new Vector2f(0.6875f, 1.0f), //1
//        new Vector2f(0.6875f, 1.0f), //2
//        new Vector2f(0.3125f, 0.5625f), //3
//        new Vector2f(0.6875f, 1.0f), //4
//        new Vector2f(0.3125f, 1.0f), //5
//        new Vector2f(0.6875f, 1.0f), //6
//        new Vector2f(0.3125f, 0.5625f), //7
//        new Vector2f(0.3125f, 1.0f), //8
//        new Vector2f(0.6875f, 1.0f), //9
//        new Vector2f(0.3125f, 0.5625f), //10
//        new Vector2f(0.3125f, 1.0f), //11
//        new Vector2f(0.3125f, 0.5625f), //12
//        new Vector2f(0.3125f, 0.625f), //13
//        new Vector2f(0.6875f, 0.25f), //14
//        new Vector2f(0.3125f, 0.25f), //15
//        new Vector2f(0.3125f, 0.5625f), //16
//        new Vector2f(0.6875f, 1.0f), //17
//        new Vector2f(0.6875f, 1.0f), //18
//        new Vector2f(0.6875f, 0.625f), //19
//        new Vector2f(0.3125f, 0.25f), //20
//        new Vector2f(0.3125f, 0.625f), //21
//        new Vector2f(0.4375f, 0.4375f), //22
//        new Vector2f(0.5625f, 0.625f), //23
//        new Vector2f(0.5625f, 0.4375f), //24
//        new Vector2f(0.3125f, 0.4375f), //25
//        new Vector2f(0.4375f, 0.625f), //26
//        new Vector2f(0.4375f, 0.4375f), //27
//        new Vector2f(0.3125f, 0.25f), //28
//        new Vector2f(0.4375f, 0.4375f), //29
//        new Vector2f(0.4375f, 0.25f), //30
//        new Vector2f(0.6875f, 0.625f), //31
//        new Vector2f(0.6875f, 0.4375f), //32
//        new Vector2f(0.4375f, 0.375f), //33
//        new Vector2f(0.5625f, 0.5f), //34
//        new Vector2f(0.5625f, 0.375f), //35
//        new Vector2f(0.3125f, 0.375f), //36
//        new Vector2f(0.4375f, 0.5f), //37
//        new Vector2f(0.4375f, 0.375f), //38
//        new Vector2f(0.4375f, 0.25f), //39
//        new Vector2f(0.5625f, 0.375f), //40
//        new Vector2f(0.5625f, 0.25f), //41
//        new Vector2f(0.3125f, 0.25f), //42
//        new Vector2f(0.4375f, 0.375f), //43
//        new Vector2f(0.4375f, 0.25f), //44
//        new Vector2f(0.3125f, 1.0f), //45
//        new Vector2f(0.6875f, 0.5625f), //46
//        new Vector2f(0.6875f, 0.5625f), //47
//        new Vector2f(0.6875f, 0.5625f), //48
//        new Vector2f(0.6875f, 0.5625f), //49
//        new Vector2f(0.6875f, 0.625f), //50
//        new Vector2f(0.3125f, 1.0f), //51
//        new Vector2f(0.3125f, 1.0f), //52
//        new Vector2f(0.3125f, 1.0f), //53
//        new Vector2f(0.6875f, 0.25f), //54
//        new Vector2f(0.4375f, 0.625f), //55
//        new Vector2f(0.3125f, 0.625f), //56
//        new Vector2f(0.3125f, 0.4375f), //57
//        new Vector2f(0.4375f, 0.5f), //58
//        new Vector2f(0.3125f, 0.5f), //59
//        new Vector2f(0.4375f, 0.375f), //60
//        new Vector2f(0.3125f, 0.375f), //61
//    };
//
////</editor-fold>
//    public boolean constructBlock_lamp(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_lamp_center_faces(verts_lamp, uv_lamp, block, shape, x, y, z);
//
//        return false;
//    }
//
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_lamp_center_faces(Vector3f[] verts2, Vector2f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));//FACE
//
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));//FACE
//
//        shape.vertex(verts2[21].x + x, verts2[21].y + y, verts2[21].z + z, getUVTextureCoord_X(pos, uv2[21].x), getUVTextureCoord_Y(pos, uv2[21].y));
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[20].x), getUVTextureCoord_Y(pos, uv2[20].y));
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[23].x), getUVTextureCoord_Y(pos, uv2[23].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[22].x), getUVTextureCoord_Y(pos, uv2[22].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[26].x), getUVTextureCoord_Y(pos, uv2[26].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[25].x), getUVTextureCoord_Y(pos, uv2[25].y));//FACE
//
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[30].x), getUVTextureCoord_Y(pos, uv2[30].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[29].x), getUVTextureCoord_Y(pos, uv2[29].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[28].x), getUVTextureCoord_Y(pos, uv2[28].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[31].x), getUVTextureCoord_Y(pos, uv2[31].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));//FACE
//
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));
//        shape.vertex(verts2[26].x + x, verts2[26].y + y, verts2[26].z + z, getUVTextureCoord_X(pos, uv2[34].x), getUVTextureCoord_Y(pos, uv2[34].y));
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[33].x), getUVTextureCoord_Y(pos, uv2[33].y));//FACE
//
//        shape.vertex(verts2[21].x + x, verts2[21].y + y, verts2[21].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));
//        shape.vertex(verts2[25].x + x, verts2[25].y + y, verts2[25].z + z, getUVTextureCoord_X(pos, uv2[37].x), getUVTextureCoord_Y(pos, uv2[37].y));
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[36].x), getUVTextureCoord_Y(pos, uv2[36].y));//FACE
//
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[41].x), getUVTextureCoord_Y(pos, uv2[41].y));
//        shape.vertex(verts2[24].x + x, verts2[24].y + y, verts2[24].z + z, getUVTextureCoord_X(pos, uv2[40].x), getUVTextureCoord_Y(pos, uv2[40].y));
//        shape.vertex(verts2[21].x + x, verts2[21].y + y, verts2[21].z + z, getUVTextureCoord_X(pos, uv2[39].x), getUVTextureCoord_Y(pos, uv2[39].y));//FACE
//
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[44].x), getUVTextureCoord_Y(pos, uv2[44].y));
//        shape.vertex(verts2[27].x + x, verts2[27].y + y, verts2[27].z + z, getUVTextureCoord_X(pos, uv2[43].x), getUVTextureCoord_Y(pos, uv2[43].y));
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[42].x), getUVTextureCoord_Y(pos, uv2[42].y));//FACE
//
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[45].x), getUVTextureCoord_Y(pos, uv2[45].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[46].x), getUVTextureCoord_Y(pos, uv2[46].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[47].x), getUVTextureCoord_Y(pos, uv2[47].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[48].x), getUVTextureCoord_Y(pos, uv2[48].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[49].x), getUVTextureCoord_Y(pos, uv2[49].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));//FACE
//
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[50].x), getUVTextureCoord_Y(pos, uv2[50].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));//FACE
//
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[51].x), getUVTextureCoord_Y(pos, uv2[51].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));//FACE
//
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[52].x), getUVTextureCoord_Y(pos, uv2[52].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));//FACE
//
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[53].x), getUVTextureCoord_Y(pos, uv2[53].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));//FACE
//
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[20].x), getUVTextureCoord_Y(pos, uv2[20].y));
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[54].x), getUVTextureCoord_Y(pos, uv2[54].y));
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));//FACE
//
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[23].x), getUVTextureCoord_Y(pos, uv2[23].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[55].x), getUVTextureCoord_Y(pos, uv2[55].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[22].x), getUVTextureCoord_Y(pos, uv2[22].y));//FACE
//
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[26].x), getUVTextureCoord_Y(pos, uv2[26].y));
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[56].x), getUVTextureCoord_Y(pos, uv2[56].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[25].x), getUVTextureCoord_Y(pos, uv2[25].y));//FACE
//
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[29].x), getUVTextureCoord_Y(pos, uv2[29].y));
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[57].x), getUVTextureCoord_Y(pos, uv2[57].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[28].x), getUVTextureCoord_Y(pos, uv2[28].y));//FACE
//
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[31].x), getUVTextureCoord_Y(pos, uv2[31].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[23].x), getUVTextureCoord_Y(pos, uv2[23].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));//FACE
//
//        shape.vertex(verts2[26].x + x, verts2[26].y + y, verts2[26].z + z, getUVTextureCoord_X(pos, uv2[34].x), getUVTextureCoord_Y(pos, uv2[34].y));
//        shape.vertex(verts2[24].x + x, verts2[24].y + y, verts2[24].z + z, getUVTextureCoord_X(pos, uv2[58].x), getUVTextureCoord_Y(pos, uv2[58].y));
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[33].x), getUVTextureCoord_Y(pos, uv2[33].y));//FACE
//
//        shape.vertex(verts2[25].x + x, verts2[25].y + y, verts2[25].z + z, getUVTextureCoord_X(pos, uv2[37].x), getUVTextureCoord_Y(pos, uv2[37].y));
//        shape.vertex(verts2[27].x + x, verts2[27].y + y, verts2[27].z + z, getUVTextureCoord_X(pos, uv2[59].x), getUVTextureCoord_Y(pos, uv2[59].y));
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[36].x), getUVTextureCoord_Y(pos, uv2[36].y));//FACE
//
//        shape.vertex(verts2[24].x + x, verts2[24].y + y, verts2[24].z + z, getUVTextureCoord_X(pos, uv2[40].x), getUVTextureCoord_Y(pos, uv2[40].y));
//        shape.vertex(verts2[25].x + x, verts2[25].y + y, verts2[25].z + z, getUVTextureCoord_X(pos, uv2[60].x), getUVTextureCoord_Y(pos, uv2[60].y));
//        shape.vertex(verts2[21].x + x, verts2[21].y + y, verts2[21].z + z, getUVTextureCoord_X(pos, uv2[39].x), getUVTextureCoord_Y(pos, uv2[39].y));//FACE
//
//        shape.vertex(verts2[27].x + x, verts2[27].y + y, verts2[27].z + z, getUVTextureCoord_X(pos, uv2[43].x), getUVTextureCoord_Y(pos, uv2[43].y));
//        shape.vertex(verts2[26].x + x, verts2[26].y + y, verts2[26].z + z, getUVTextureCoord_X(pos, uv2[61].x), getUVTextureCoord_Y(pos, uv2[61].y));
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[42].x), getUVTextureCoord_Y(pos, uv2[42].y));//FACE
//    }
//
////</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw lamp_side">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_lamp_side = {
//        new Vector3f(0.687499f, 0.8125f, 0.500001f), //0
//        new Vector3f(0.312499f, 0.8125f, 0.49999896f), //1
//        new Vector3f(0.687501f, 0.8125f, 0.125001f), //2
//        new Vector3f(0.312501f, 0.8125f, 0.124998994f), //3
//        new Vector3f(0.312501f, 0.375f, 0.124998994f), //4
//        new Vector3f(0.312499f, 0.375f, 0.49999896f), //5
//        new Vector3f(0.687501f, 0.375f, 0.125001f), //6
//        new Vector3f(0.687499f, 0.375f, 0.500001f), //7
//        new Vector3f(0.43797904f, 0.375f, 0.250478f), //8
//        new Vector3f(0.437978f, 0.375f, 0.374521f), //9
//        new Vector3f(0.562021f, 0.375f, 0.250479f), //10
//        new Vector3f(0.562021f, 0.375f, 0.374521f), //11
//        new Vector3f(0.43797904f, 0.25f, 0.250478f), //12
//        new Vector3f(0.437978f, 0.25f, 0.374521f), //13
//        new Vector3f(0.562021f, 0.25f, 0.250479f), //14
//        new Vector3f(0.562021f, 0.25f, 0.374521f), //15
//        new Vector3f(0.43797904f, 0.125f, 0.250478f), //16
//        new Vector3f(0.437978f, 0.125f, 0.374521f), //17
//        new Vector3f(0.562021f, 0.125f, 0.250479f), //18
//        new Vector3f(0.562021f, 0.125f, 0.374521f), //19
//        new Vector3f(0.562023f, 0.25f, 0.0f), //20
//        new Vector3f(0.43798f, 0.25f, -0.0f), //21
//        new Vector3f(0.562022f, 0.125f, 0.0f), //22
//        new Vector3f(0.43798f, 0.125f, -0.0f), //23
//    };
//    static Vector2f[] uv_lamp_side = {
//        new Vector2f(0.6875f, 0.625f), //0
//        new Vector2f(0.3125f, 0.25f), //1
//        new Vector2f(0.6875f, 0.25f), //2
//        new Vector2f(0.6875f, 0.25f), //3
//        new Vector2f(0.3125f, 0.625f), //4
//        new Vector2f(0.3125f, 0.25f), //5
//        new Vector2f(0.6875f, 0.5625f), //6
//        new Vector2f(0.3125f, 1.0f), //7
//        new Vector2f(0.6875f, 1.0f), //8
//        new Vector2f(0.3125f, 1.0f), //9
//        new Vector2f(0.6875f, 0.5625f), //10
//        new Vector2f(0.6875f, 1.0f), //11
//        new Vector2f(0.3125f, 1.0f), //12
//        new Vector2f(0.6875f, 0.5625f), //13
//        new Vector2f(0.6875f, 1.0f), //14
//        new Vector2f(0.3125f, 1.0f), //15
//        new Vector2f(0.6875f, 0.5625f), //16
//        new Vector2f(0.6875f, 1.0f), //17
//        new Vector2f(0.5625f, 0.3125f), //18
//        new Vector2f(0.625f, 0.25f), //19
//        new Vector2f(0.625f, 0.3125f), //20
//        new Vector2f(0.5625f, 0.375f), //21
//        new Vector2f(0.625f, 0.375f), //22
//        new Vector2f(0.5625f, 0.5625f), //23
//        new Vector2f(0.625f, 0.625f), //24
//        new Vector2f(0.5625f, 0.625f), //25
//        new Vector2f(0.375f, 0.375f), //26
//        new Vector2f(0.4375f, 0.5f), //27
//        new Vector2f(0.375f, 0.5f), //28
//        new Vector2f(0.375f, 0.375f), //29
//        new Vector2f(0.4375f, 0.3125f), //30
//        new Vector2f(0.4375f, 0.375f), //31
//        new Vector2f(0.3125f, 0.5625f), //32
//        new Vector2f(0.375f, 0.625f), //33
//        new Vector2f(0.3125f, 0.625f), //34
//        new Vector2f(0.625f, 0.5625f), //35
//        new Vector2f(0.6875f, 0.625f), //36
//        new Vector2f(0.4375f, 0.375f), //37
//        new Vector2f(0.5625f, 0.5f), //38
//        new Vector2f(0.4375f, 0.625f), //39
//        new Vector2f(0.375f, 0.375f), //40
//        new Vector2f(0.375f, 0.5625f), //41
//        new Vector2f(0.6875f, 0.375f), //42
//        new Vector2f(0.6875f, 0.5625f), //43
//        new Vector2f(0.6875f, 0.375f), //44
//        new Vector2f(0.625f, 0.25f), //45
//        new Vector2f(0.6875f, 0.25f), //46
//        new Vector2f(0.3125f, 0.625f), //47
//        new Vector2f(0.6875f, 0.625f), //48
//        new Vector2f(0.3125f, 0.5625f), //49
//        new Vector2f(0.3125f, 0.5625f), //50
//        new Vector2f(0.3125f, 0.5625f), //51
//        new Vector2f(0.3125f, 0.5625f), //52
//        new Vector2f(0.5625f, 0.25f), //53
//        new Vector2f(0.375f, 0.3125f), //54
//        new Vector2f(0.5625f, 0.375f), //55
//        new Vector2f(0.5625f, 0.625f), //56
//        new Vector2f(0.3125f, 0.375f), //57
//        new Vector2f(0.625f, 0.375f), //58
//        new Vector2f(0.625f, 0.375f), //59
//    };
//
////</editor-fold>
//    public boolean constructBlock_lamp_side(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_lamp_side_center_faces(verts_lamp_side, uv_lamp_side, block, shape, x, y, z);
//
//        return false;
//    }
//
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_lamp_side_center_faces(Vector3f[] verts2, Vector2f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[20].x), getUVTextureCoord_Y(pos, uv2[20].y));
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));//FACE
//
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[22].x), getUVTextureCoord_Y(pos, uv2[22].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[20].x), getUVTextureCoord_Y(pos, uv2[20].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[21].x), getUVTextureCoord_Y(pos, uv2[21].y));//FACE
//
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[25].x), getUVTextureCoord_Y(pos, uv2[25].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[23].x), getUVTextureCoord_Y(pos, uv2[23].y));//FACE
//
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[28].x), getUVTextureCoord_Y(pos, uv2[28].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[26].x), getUVTextureCoord_Y(pos, uv2[26].y));//FACE
//
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[31].x), getUVTextureCoord_Y(pos, uv2[31].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[30].x), getUVTextureCoord_Y(pos, uv2[30].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[29].x), getUVTextureCoord_Y(pos, uv2[29].y));//FACE
//
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[34].x), getUVTextureCoord_Y(pos, uv2[34].y));
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[33].x), getUVTextureCoord_Y(pos, uv2[33].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[36].x), getUVTextureCoord_Y(pos, uv2[36].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[37].x), getUVTextureCoord_Y(pos, uv2[37].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[39].x), getUVTextureCoord_Y(pos, uv2[39].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));//FACE
//
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[41].x), getUVTextureCoord_Y(pos, uv2[41].y));
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[40].x), getUVTextureCoord_Y(pos, uv2[40].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));//FACE
//
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[43].x), getUVTextureCoord_Y(pos, uv2[43].y));
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[42].x), getUVTextureCoord_Y(pos, uv2[42].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));//FACE
//
//        shape.vertex(verts2[21].x + x, verts2[21].y + y, verts2[21].z + z, getUVTextureCoord_X(pos, uv2[46].x), getUVTextureCoord_Y(pos, uv2[46].y));
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[45].x), getUVTextureCoord_Y(pos, uv2[45].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[44].x), getUVTextureCoord_Y(pos, uv2[44].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[47].x), getUVTextureCoord_Y(pos, uv2[47].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[48].x), getUVTextureCoord_Y(pos, uv2[48].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[49].x), getUVTextureCoord_Y(pos, uv2[49].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[50].x), getUVTextureCoord_Y(pos, uv2[50].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[51].x), getUVTextureCoord_Y(pos, uv2[51].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[52].x), getUVTextureCoord_Y(pos, uv2[52].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));//FACE
//
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[53].x), getUVTextureCoord_Y(pos, uv2[53].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[20].x), getUVTextureCoord_Y(pos, uv2[20].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[21].x), getUVTextureCoord_Y(pos, uv2[21].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[23].x), getUVTextureCoord_Y(pos, uv2[23].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[37].x), getUVTextureCoord_Y(pos, uv2[37].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[26].x), getUVTextureCoord_Y(pos, uv2[26].y));//FACE
//
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[30].x), getUVTextureCoord_Y(pos, uv2[30].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[54].x), getUVTextureCoord_Y(pos, uv2[54].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[29].x), getUVTextureCoord_Y(pos, uv2[29].y));//FACE
//
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[33].x), getUVTextureCoord_Y(pos, uv2[33].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[41].x), getUVTextureCoord_Y(pos, uv2[41].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));//FACE
//
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[36].x), getUVTextureCoord_Y(pos, uv2[36].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[43].x), getUVTextureCoord_Y(pos, uv2[43].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));//FACE
//
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[55].x), getUVTextureCoord_Y(pos, uv2[55].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[37].x), getUVTextureCoord_Y(pos, uv2[37].y));//FACE
//
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[39].x), getUVTextureCoord_Y(pos, uv2[39].y));
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[56].x), getUVTextureCoord_Y(pos, uv2[56].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));//FACE
//
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[40].x), getUVTextureCoord_Y(pos, uv2[40].y));
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[57].x), getUVTextureCoord_Y(pos, uv2[57].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));//FACE
//
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[42].x), getUVTextureCoord_Y(pos, uv2[42].y));
//        shape.vertex(verts2[21].x + x, verts2[21].y + y, verts2[21].z + z, getUVTextureCoord_X(pos, uv2[58].x), getUVTextureCoord_Y(pos, uv2[58].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));//FACE
//
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[45].x), getUVTextureCoord_Y(pos, uv2[45].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[59].x), getUVTextureCoord_Y(pos, uv2[59].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[44].x), getUVTextureCoord_Y(pos, uv2[44].y));//FACE
//    }
//
////</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw lamp_side_fence">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_lamp_side_fence = {
//        new Vector3f(0.687499f, 0.8125f, 0.125001f), //0
//        new Vector3f(0.312499f, 0.8125f, 0.124998994f), //1
//        new Vector3f(0.687501f, 0.8125f, -0.24999899f), //2
//        new Vector3f(0.312501f, 0.8125f, -0.250001f), //3
//        new Vector3f(0.312501f, 0.375f, -0.250001f), //4
//        new Vector3f(0.312499f, 0.375f, 0.124998994f), //5
//        new Vector3f(0.687501f, 0.375f, -0.24999899f), //6
//        new Vector3f(0.687499f, 0.375f, 0.125001f), //7
//        new Vector3f(0.43797904f, 0.375f, -0.12452199f), //8
//        new Vector3f(0.437978f, 0.375f, -4.7900004E-4f), //9
//        new Vector3f(0.562021f, 0.375f, -0.12452101f), //10
//        new Vector3f(0.562021f, 0.375f, -4.78E-4f), //11
//        new Vector3f(0.43797904f, 0.25f, -0.12452199f), //12
//        new Vector3f(0.437978f, 0.25f, -4.7900004E-4f), //13
//        new Vector3f(0.562021f, 0.25f, -0.12452101f), //14
//        new Vector3f(0.562021f, 0.25f, -4.78E-4f), //15
//        new Vector3f(0.43797904f, 0.125f, -0.12452199f), //16
//        new Vector3f(0.437978f, 0.125f, -4.7900004E-4f), //17
//        new Vector3f(0.562021f, 0.125f, -0.12452101f), //18
//        new Vector3f(0.562021f, 0.125f, -4.78E-4f), //19
//        new Vector3f(0.562023f, 0.25f, -0.375f), //20
//        new Vector3f(0.43798f, 0.25f, -0.375f), //21
//        new Vector3f(0.562022f, 0.125f, -0.375f), //22
//        new Vector3f(0.43798f, 0.125f, -0.375f), //23
//    };
//    static Vector2f[] uv_lamp_side_fence = {
//        new Vector2f(0.6875f, 0.625f), //0
//        new Vector2f(0.3125f, 0.25f), //1
//        new Vector2f(0.6875f, 0.25f), //2
//        new Vector2f(0.6875f, 0.25f), //3
//        new Vector2f(0.3125f, 0.625f), //4
//        new Vector2f(0.3125f, 0.25f), //5
//        new Vector2f(0.6875f, 0.5625f), //6
//        new Vector2f(0.3125f, 1.0f), //7
//        new Vector2f(0.6875f, 1.0f), //8
//        new Vector2f(0.3125f, 1.0f), //9
//        new Vector2f(0.6875f, 0.5625f), //10
//        new Vector2f(0.6875f, 1.0f), //11
//        new Vector2f(0.3125f, 1.0f), //12
//        new Vector2f(0.6875f, 0.5625f), //13
//        new Vector2f(0.6875f, 1.0f), //14
//        new Vector2f(0.3125f, 1.0f), //15
//        new Vector2f(0.6875f, 0.5625f), //16
//        new Vector2f(0.6875f, 1.0f), //17
//        new Vector2f(0.5625f, 0.3125f), //18
//        new Vector2f(0.625f, 0.25f), //19
//        new Vector2f(0.625f, 0.3125f), //20
//        new Vector2f(0.5625f, 0.375f), //21
//        new Vector2f(0.625f, 0.375f), //22
//        new Vector2f(0.5625f, 0.5625f), //23
//        new Vector2f(0.625f, 0.625f), //24
//        new Vector2f(0.5625f, 0.625f), //25
//        new Vector2f(0.375f, 0.375f), //26
//        new Vector2f(0.4375f, 0.5f), //27
//        new Vector2f(0.375f, 0.5f), //28
//        new Vector2f(0.375f, 0.375f), //29
//        new Vector2f(0.4375f, 0.3125f), //30
//        new Vector2f(0.4375f, 0.375f), //31
//        new Vector2f(0.3125f, 0.5625f), //32
//        new Vector2f(0.375f, 0.625f), //33
//        new Vector2f(0.3125f, 0.625f), //34
//        new Vector2f(0.625f, 0.5625f), //35
//        new Vector2f(0.6875f, 0.625f), //36
//        new Vector2f(0.4375f, 0.375f), //37
//        new Vector2f(0.5625f, 0.5f), //38
//        new Vector2f(0.4375f, 0.625f), //39
//        new Vector2f(0.375f, 0.375f), //40
//        new Vector2f(0.375f, 0.5625f), //41
//        new Vector2f(0.6875f, 0.375f), //42
//        new Vector2f(0.6875f, 0.5625f), //43
//        new Vector2f(0.6875f, 0.375f), //44
//        new Vector2f(0.625f, 0.25f), //45
//        new Vector2f(0.6875f, 0.25f), //46
//        new Vector2f(0.3125f, 0.625f), //47
//        new Vector2f(0.6875f, 0.625f), //48
//        new Vector2f(0.3125f, 0.5625f), //49
//        new Vector2f(0.3125f, 0.5625f), //50
//        new Vector2f(0.3125f, 0.5625f), //51
//        new Vector2f(0.3125f, 0.5625f), //52
//        new Vector2f(0.5625f, 0.25f), //53
//        new Vector2f(0.375f, 0.3125f), //54
//        new Vector2f(0.5625f, 0.375f), //55
//        new Vector2f(0.5625f, 0.625f), //56
//        new Vector2f(0.3125f, 0.375f), //57
//        new Vector2f(0.625f, 0.375f), //58
//        new Vector2f(0.625f, 0.375f), //59
//    };
//
////</editor-fold>
//    public boolean constructBlock_lamp_side_fence(Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_lamp_side_fence_center_faces(verts_lamp_side_fence, uv_lamp_side_fence, block, shape, x, y, z);
//
//        return false;
//    }
//
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_lamp_side_fence_center_faces(Vector3f[] verts2, Vector2f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[5].x), getUVTextureCoord_Y(pos, uv2[5].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[8].x), getUVTextureCoord_Y(pos, uv2[8].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[11].x), getUVTextureCoord_Y(pos, uv2[11].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[14].x), getUVTextureCoord_Y(pos, uv2[14].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[17].x), getUVTextureCoord_Y(pos, uv2[17].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[20].x), getUVTextureCoord_Y(pos, uv2[20].y));
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));//FACE
//
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[22].x), getUVTextureCoord_Y(pos, uv2[22].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[20].x), getUVTextureCoord_Y(pos, uv2[20].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[21].x), getUVTextureCoord_Y(pos, uv2[21].y));//FACE
//
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[25].x), getUVTextureCoord_Y(pos, uv2[25].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[23].x), getUVTextureCoord_Y(pos, uv2[23].y));//FACE
//
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[28].x), getUVTextureCoord_Y(pos, uv2[28].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[26].x), getUVTextureCoord_Y(pos, uv2[26].y));//FACE
//
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[31].x), getUVTextureCoord_Y(pos, uv2[31].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[30].x), getUVTextureCoord_Y(pos, uv2[30].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[29].x), getUVTextureCoord_Y(pos, uv2[29].y));//FACE
//
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[34].x), getUVTextureCoord_Y(pos, uv2[34].y));
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[33].x), getUVTextureCoord_Y(pos, uv2[33].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[36].x), getUVTextureCoord_Y(pos, uv2[36].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[37].x), getUVTextureCoord_Y(pos, uv2[37].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[39].x), getUVTextureCoord_Y(pos, uv2[39].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));//FACE
//
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[41].x), getUVTextureCoord_Y(pos, uv2[41].y));
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[40].x), getUVTextureCoord_Y(pos, uv2[40].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));//FACE
//
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[43].x), getUVTextureCoord_Y(pos, uv2[43].y));
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[42].x), getUVTextureCoord_Y(pos, uv2[42].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));//FACE
//
//        shape.vertex(verts2[21].x + x, verts2[21].y + y, verts2[21].z + z, getUVTextureCoord_X(pos, uv2[46].x), getUVTextureCoord_Y(pos, uv2[46].y));
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[45].x), getUVTextureCoord_Y(pos, uv2[45].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[44].x), getUVTextureCoord_Y(pos, uv2[44].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[47].x), getUVTextureCoord_Y(pos, uv2[47].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[4].x), getUVTextureCoord_Y(pos, uv2[4].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[48].x), getUVTextureCoord_Y(pos, uv2[48].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[7].x), getUVTextureCoord_Y(pos, uv2[7].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[49].x), getUVTextureCoord_Y(pos, uv2[49].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[6].x), getUVTextureCoord_Y(pos, uv2[6].y));//FACE
//
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[10].x), getUVTextureCoord_Y(pos, uv2[10].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[50].x), getUVTextureCoord_Y(pos, uv2[50].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[9].x), getUVTextureCoord_Y(pos, uv2[9].y));//FACE
//
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(pos, uv2[13].x), getUVTextureCoord_Y(pos, uv2[13].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(pos, uv2[51].x), getUVTextureCoord_Y(pos, uv2[51].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[12].x), getUVTextureCoord_Y(pos, uv2[12].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(pos, uv2[16].x), getUVTextureCoord_Y(pos, uv2[16].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(pos, uv2[52].x), getUVTextureCoord_Y(pos, uv2[52].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[15].x), getUVTextureCoord_Y(pos, uv2[15].y));//FACE
//
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[19].x), getUVTextureCoord_Y(pos, uv2[19].y));
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[53].x), getUVTextureCoord_Y(pos, uv2[53].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));//FACE
//
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[20].x), getUVTextureCoord_Y(pos, uv2[20].y));
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[18].x), getUVTextureCoord_Y(pos, uv2[18].y));
//        shape.vertex(verts2[9].x + x, verts2[9].y + y, verts2[9].z + z, getUVTextureCoord_X(pos, uv2[21].x), getUVTextureCoord_Y(pos, uv2[21].y));//FACE
//
//        shape.vertex(verts2[13].x + x, verts2[13].y + y, verts2[13].z + z, getUVTextureCoord_X(pos, uv2[24].x), getUVTextureCoord_Y(pos, uv2[24].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));
//        shape.vertex(verts2[8].x + x, verts2[8].y + y, verts2[8].z + z, getUVTextureCoord_X(pos, uv2[23].x), getUVTextureCoord_Y(pos, uv2[23].y));//FACE
//
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[27].x), getUVTextureCoord_Y(pos, uv2[27].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[37].x), getUVTextureCoord_Y(pos, uv2[37].y));
//        shape.vertex(verts2[11].x + x, verts2[11].y + y, verts2[11].z + z, getUVTextureCoord_X(pos, uv2[26].x), getUVTextureCoord_Y(pos, uv2[26].y));//FACE
//
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[30].x), getUVTextureCoord_Y(pos, uv2[30].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[54].x), getUVTextureCoord_Y(pos, uv2[54].y));
//        shape.vertex(verts2[10].x + x, verts2[10].y + y, verts2[10].z + z, getUVTextureCoord_X(pos, uv2[29].x), getUVTextureCoord_Y(pos, uv2[29].y));//FACE
//
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[33].x), getUVTextureCoord_Y(pos, uv2[33].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[41].x), getUVTextureCoord_Y(pos, uv2[41].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));//FACE
//
//        shape.vertex(verts2[17].x + x, verts2[17].y + y, verts2[17].z + z, getUVTextureCoord_X(pos, uv2[36].x), getUVTextureCoord_Y(pos, uv2[36].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[43].x), getUVTextureCoord_Y(pos, uv2[43].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));//FACE
//
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));
//        shape.vertex(verts2[19].x + x, verts2[19].y + y, verts2[19].z + z, getUVTextureCoord_X(pos, uv2[55].x), getUVTextureCoord_Y(pos, uv2[55].y));
//        shape.vertex(verts2[15].x + x, verts2[15].y + y, verts2[15].z + z, getUVTextureCoord_X(pos, uv2[37].x), getUVTextureCoord_Y(pos, uv2[37].y));//FACE
//
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[39].x), getUVTextureCoord_Y(pos, uv2[39].y));
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[56].x), getUVTextureCoord_Y(pos, uv2[56].y));
//        shape.vertex(verts2[18].x + x, verts2[18].y + y, verts2[18].z + z, getUVTextureCoord_X(pos, uv2[38].x), getUVTextureCoord_Y(pos, uv2[38].y));//FACE
//
//        shape.vertex(verts2[22].x + x, verts2[22].y + y, verts2[22].z + z, getUVTextureCoord_X(pos, uv2[40].x), getUVTextureCoord_Y(pos, uv2[40].y));
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[57].x), getUVTextureCoord_Y(pos, uv2[57].y));
//        shape.vertex(verts2[16].x + x, verts2[16].y + y, verts2[16].z + z, getUVTextureCoord_X(pos, uv2[32].x), getUVTextureCoord_Y(pos, uv2[32].y));//FACE
//
//        shape.vertex(verts2[23].x + x, verts2[23].y + y, verts2[23].z + z, getUVTextureCoord_X(pos, uv2[42].x), getUVTextureCoord_Y(pos, uv2[42].y));
//        shape.vertex(verts2[21].x + x, verts2[21].y + y, verts2[21].z + z, getUVTextureCoord_X(pos, uv2[58].x), getUVTextureCoord_Y(pos, uv2[58].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[35].x), getUVTextureCoord_Y(pos, uv2[35].y));//FACE
//
//        shape.vertex(verts2[20].x + x, verts2[20].y + y, verts2[20].z + z, getUVTextureCoord_X(pos, uv2[45].x), getUVTextureCoord_Y(pos, uv2[45].y));
//        shape.vertex(verts2[14].x + x, verts2[14].y + y, verts2[14].z + z, getUVTextureCoord_X(pos, uv2[59].x), getUVTextureCoord_Y(pos, uv2[59].y));
//        shape.vertex(verts2[12].x + x, verts2[12].y + y, verts2[12].z + z, getUVTextureCoord_X(pos, uv2[44].x), getUVTextureCoord_Y(pos, uv2[44].y));//FACE
//    }
//
////</editor-fold>
////</editor-fold>
//
//    @Override
//    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
//        float a = ONE_SIXTEENTH * 3;
//        float b = ONE_SIXTEENTH * 6;
//        box.setPosAndSize(x + a, y + (ONE_SIXTEENTH * 2), z + a, 1 - b, 1 - (ONE_SIXTEENTH * 3), 1 - b);
//        consumer.accept(box);
//    }
//
//}
