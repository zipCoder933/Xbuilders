///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.blocks.type;
//
//import com.xbuilders.engine.game.player.UserControlledPlayer;
//import com.xbuilders.engine.game.player.raycasting.Ray;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.items.block.construction.blockTypes.BlockType;
//import com.xbuilders.engine.utils.math.AABB;
//import com.xbuilders.engine.world.chunk.blockData.BlockData;
//import com.xbuilders.engine.world.chunk.blockData.BlockDataUtils;
//import com.xbuilders.engine.world.chunk.blockData.BlockOrientation;
//import processing.core.PShape;
//import processing.core.PVector;
//
//import java.util.function.Consumer;
//
///**
// * @author zipCoder933
// */
//public class WallItemRenderer extends BlockType {
//
//    @Override
//    public BlockData getInitialBlockData(UserControlledPlayer player, Block block, Ray ray) {
//        return player.getCameraBlockOrientation();
//    }
//
//    @Override
//    public void constructBlock(PShape buffers, Block block, BlockData data, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
////<editor-fold defaultstate="collapsed" desc="Rotate the object">
//        BlockOrientation orientation = BlockDataUtils.getBlockOrientation(data);
//        PVector[] verts2 = verts_ladder;
//        if (orientation != null) {
//            verts2 = rotateVerticiesYAxis(verts_ladder, orientation.getXZ());
//
//            Block positiveX2 = positiveX;
//            Block negativeX2 = negativeX;
//            Block positiveZ2 = positiveZ;
//            Block negativeZ2 = negativeZ;
//
//            switch (orientation.getXZ()) {
//                case 1:
//                    positiveX = positiveZ2;
//                    negativeX = negativeZ2;
//                    positiveZ = negativeX2;
//                    negativeZ = positiveX2;
//                    break;
//                case 2:
//                    positiveX = negativeX2;
//                    negativeX = positiveX2;
//                    positiveZ = negativeZ2;
//                    negativeZ = positiveZ2;
//                    break;
//                case 3:
//                    positiveX = negativeZ2;
//                    negativeX = positiveZ2;
//                    positiveZ = positiveX2;
//                    negativeZ = negativeX2;
//                    break;
//            }
//        }
////</editor-fold>
//
//        if (shouldRenderSide(block, positiveY)) {
//            make_ladder_positiveY_faces(verts2, uv_ladder, block, buffers, x, y, z);
//        }
//        if (shouldRenderSide(block, negativeZ)) {
//            make_ladder_negativeZ_faces(verts2, uv_ladder, block, buffers, x, y, z);
//        }
//        if (shouldRenderSide(block, positiveX)) {
//            make_ladder_positiveX_faces(verts2, uv_ladder, block, buffers, x, y, z);
//        }
//        if (shouldRenderSide(block, negativeX)) {
//            make_ladder_negativeX_faces(verts2, uv_ladder, block, buffers, x, y, z);
//        }
//        if (shouldRenderSide(block, negativeY)) {
//            make_ladder_negativeY_faces(verts2, uv_ladder, block, buffers, x, y, z);
//        }
//        make_ladder_center_faces(verts2, uv_ladder, block, buffers, x, y, z);
//
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Draw ladder">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static PVector[] verts_ladder = {
//            new PVector(0.0f, 0.0f, 0.0f), //0
//            new PVector(0.0f, 1.0f, 0.0f), //1
//            new PVector(0.0f, 0.0f, 0.0625f), //2
//            new PVector(0.0f, 1.0f, 0.0625f), //3
//            new PVector(1.0f, 0.0f, 0.0f), //4
//            new PVector(1.0f, 1.0f, 0.0f), //5
//            new PVector(1.0f, 0.0f, 0.0625f), //6
//            new PVector(1.0f, 1.0f, 0.0625f), //7
//    };
//    static PVector[] uv_ladder = {
//            new PVector(-0.0f, 1.0f), //0
//            new PVector(1.0f, 0.93750006f), //1
//            new PVector(1.0f, 1.0f), //2
//            new PVector(1.0f, 0.0f), //3
//            new PVector(0.0f, 0.0625f), //4
//            new PVector(0.0f, 0.0f), //5
//            new PVector(0.0f, 0.0f), //6
//            new PVector(1.0f, 1.0f), //7
//            new PVector(0.0f, 1.0f), //8
//            new PVector(0.93750006f, 0.0f), //9
//            new PVector(1.0f, 1.0f), //10
//            new PVector(0.93750006f, 1.0f), //11
//            new PVector(1.0E-6f, 0.0f), //12
//            new PVector(0.062501f, 1.0f), //13
//            new PVector(0.0f, 1.0f), //14
//            new PVector(1.0f, 1.0f), //15
//            new PVector(0.0f, 0.0f), //16
//            new PVector(0.0f, 1.0f), //17
//            new PVector(-0.0f, 0.93750006f), //18
//            new PVector(1.0f, 0.0625f), //19
//            new PVector(1.0f, 0.0f), //20
//            new PVector(1.0f, 0.0f), //21
//            new PVector(0.062501f, 0.0f), //22
//            new PVector(1.0f, 0.0f), //23
//    };
//
//    //</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_ladder_positiveX_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[14].x), getUVTextureCoord_Y(block, uv2[14].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[13].x), getUVTextureCoord_Y(block, uv2[13].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[13].x), getUVTextureCoord_Y(block, uv2[13].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[22].x), getUVTextureCoord_Y(block, uv2[22].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[12].x), getUVTextureCoord_Y(block, uv2[12].y));//FACE
//    }
//
//    private static void make_ladder_center_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[8].x), getUVTextureCoord_Y(block, uv2[8].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[7].x), getUVTextureCoord_Y(block, uv2[7].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[7].x), getUVTextureCoord_Y(block, uv2[7].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[20].x), getUVTextureCoord_Y(block, uv2[20].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[6].x), getUVTextureCoord_Y(block, uv2[6].y));//FACE
//    }
//
//    private static void make_ladder_negativeZ_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[17].x), getUVTextureCoord_Y(block, uv2[17].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[16].x), getUVTextureCoord_Y(block, uv2[16].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[16].x), getUVTextureCoord_Y(block, uv2[16].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[23].x), getUVTextureCoord_Y(block, uv2[23].y));
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[15].x), getUVTextureCoord_Y(block, uv2[15].y));//FACE
//    }
//
//    private static void make_ladder_negativeY_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[2].x), getUVTextureCoord_Y(block, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[1].x), getUVTextureCoord_Y(block, uv2[1].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[1].x), getUVTextureCoord_Y(block, uv2[1].y));
//        shape.vertex(verts2[6].x + x, verts2[6].y + y, verts2[6].z + z, getUVTextureCoord_X(block, uv2[18].x), getUVTextureCoord_Y(block, uv2[18].y));
//        shape.vertex(verts2[4].x + x, verts2[4].y + y, verts2[4].z + z, getUVTextureCoord_X(block, uv2[0].x), getUVTextureCoord_Y(block, uv2[0].y));//FACE
//    }
//
//    private static void make_ladder_positiveY_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//
//        shape.vertex(verts2[5].x + x, verts2[5].y + y, verts2[5].z + z, getUVTextureCoord_X(block, uv2[5].x), getUVTextureCoord_Y(block, uv2[5].y));
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[4].x), getUVTextureCoord_Y(block, uv2[4].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//
//        shape.vertex(verts2[7].x + x, verts2[7].y + y, verts2[7].z + z, getUVTextureCoord_X(block, uv2[4].x), getUVTextureCoord_Y(block, uv2[4].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[19].x), getUVTextureCoord_Y(block, uv2[19].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[3].x), getUVTextureCoord_Y(block, uv2[3].y));//FACE
//    }
//
//    private static void make_ladder_negativeX_faces(PVector[] verts2, PVector[] uv2, Block block, PShape shape, int x, int y, int z) {
//
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(block, uv2[11].x), getUVTextureCoord_Y(block, uv2[11].y));
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[10].x), getUVTextureCoord_Y(block, uv2[10].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(block, uv2[10].x), getUVTextureCoord_Y(block, uv2[10].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(block, uv2[21].x), getUVTextureCoord_Y(block, uv2[21].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(block, uv2[9].x), getUVTextureCoord_Y(block, uv2[9].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
//    @Override
//    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
//    }
//
//    @Override
//    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
//        BlockOrientation orientation = BlockDataUtils.getBlockOrientation(data);
//        if (orientation != null) {
//            switch (orientation.getXZ()) {
//                case 1 -> box.setPosAndSize(x + ONE_SIXTEENTH * 14, y, z, ONE_SIXTEENTH * 2, 1, 1);
//                case 2 -> box.setPosAndSize(x, y, z + ONE_SIXTEENTH * 14, 1, 1, ONE_SIXTEENTH * 2);
//                case 3 -> box.setPosAndSize(x, y, z, ONE_SIXTEENTH * 2, 1, 1);
//                default -> box.setPosAndSize(x, y, z, 1, 1, ONE_SIXTEENTH * 2);
//            }
//            consumer.accept(box);
//        }
//    }
//}
