///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.xbuilders.game.items.blocks.type;
//
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.items.block.construction.blockTypes.BlockType;
//import com.xbuilders.engine.utils.math.AABB;
//import com.xbuilders.engine.world.chunk.Chunk;
//import com.xbuilders.engine.world.chunk.blockData.BlockData;
//import org.joml.Vector3f;
//import processing.core.PShape;
//
//import java.util.function.Consumer;
//
///**
// * @author zipCoder933
// */
//public class SunflowerRenderer extends BlockType {
//
//    //<editor-fold defaultstate="collapsed" desc="Draw sunflower">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_sunflower = {
//            new Vector3f(0.92f, -0.06f, 0.2f), //0
//            new Vector3f(0.2f, -0.04f, 0.92f), //1
//            new Vector3f(1.1f, 0.92f, 0.38f), //2
//            new Vector3f(0.4f, 0.92f, 1.08f), //3
//    };
//    static Vector3f[] uv_sunflower = {
//            new Vector3f(1.0f, 0.0f, 0), //0
//            new Vector3f(0.0f, 1.0f, 0), //1
//            new Vector3f(1.0f, 1.0f, 0), //2
//            new Vector3f(0.0f, 0.0f, 0), //3
//    };
//
//    //</editor-fold>
//    public boolean constructBlock_sunflower(Chunk chunk, Block block, PShape shape, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_sunflower_center_faces(verts_sunflower, uv_sunflower, block, shape, x, y, z);
//
//        return false;
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_sunflower_center_faces(Vector3f[] verts2, Vector3f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.TOP;
//
//        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, getUVTextureCoord_X(pos, uv2[2].x), getUVTextureCoord_Y(pos, uv2[2].y));
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//
//        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, getUVTextureCoord_X(pos, uv2[1].x), getUVTextureCoord_Y(pos, uv2[1].y));
//        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, getUVTextureCoord_X(pos, uv2[3].x), getUVTextureCoord_Y(pos, uv2[3].y));
//        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, getUVTextureCoord_X(pos, uv2[0].x), getUVTextureCoord_Y(pos, uv2[0].y));//FACE
//    }
//
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw sunflower_2">
////NOTES:
////The UV map for this block only exists on the top face.
////<editor-fold defaultstate="collapsed" desc="Verticies">
//    static Vector3f[] verts_sunflower_2 = {
//            new Vector3f(0.92f, -0.06f, 0.2f), //0
//            new Vector3f(0.2f, -0.04f, 0.92f), //1
//            new Vector3f(1.1f, 0.92f, 0.38f), //2
//            new Vector3f(0.4f, 0.92f, 1.08f), //3
//    };
//    static Vector3f[] uv_sunflower_2 = {
//            new Vector3f(0.0f, 1.0f, 0), //0
//            new Vector3f(1.0f, 0.0f, 0), //1
//            new Vector3f(1.0f, 1.0f, 0), //2
//            new Vector3f(0.0f, 0.0f, 0), //3
//    };
//
//    //</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Face methods">
//    private static void make_sunflower_2_center_faces(Vector3f[] verts2, Vector3f[] uv2, Block block, PShape shape, int x, int y, int z) {
//        int[] pos = block.texture.BOTTOM;
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
//    //</editor-fold>
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="Draw sprite">
//    static Vector3f[] vertices = {
//            new Vector3f(0, 0, 0),//0
//            new Vector3f(1, 0, 0),//1
//            new Vector3f(1, 0, 1),//2
//            new Vector3f(0, 0, 1),//3
//            new Vector3f(0, 1, 0),//4
//            new Vector3f(1, 1, 0),//5
//            new Vector3f(1, 1, 1),//6
//            new Vector3f(0, 1, 1)};//7
//    static int[] spriteIndices = {
//            0, 2, 6,
//            0, 6, 4,
//            3, 1, 5,
//            3, 5, 7,
//            2, 0, 4,
//            2, 4, 6,
//            1, 3, 7,
//            1, 7, 5,};
//
//    public void drawSprite(Block block, BlockData data, PShape shape,
//                           Block negativeX, Block positiveX, Block negativeY,
//                           Block positiveY, Block negativeZ, Block positiveZ,
//                           int x, int y, int z) {
//        //shading.applySpriteShading(shape);
//        shape.vertex(vertices[spriteIndices[0]].x + x, vertices[spriteIndices[0]].y + y, vertices[spriteIndices[0]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[1]].x + x, vertices[spriteIndices[1]].y + y, vertices[spriteIndices[1]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[2]].x + x, vertices[spriteIndices[2]].y + y, vertices[spriteIndices[2]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//        shape.vertex(vertices[spriteIndices[3]].x + x, vertices[spriteIndices[3]].y + y, vertices[spriteIndices[3]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[4]].x + x, vertices[spriteIndices[4]].y + y, vertices[spriteIndices[4]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//        shape.vertex(vertices[spriteIndices[5]].x + x, vertices[spriteIndices[5]].y + y, vertices[spriteIndices[5]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//
//        shape.vertex(vertices[spriteIndices[6]].x + x, vertices[spriteIndices[6]].y + y, vertices[spriteIndices[6]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[7]].x + x, vertices[spriteIndices[7]].y + y, vertices[spriteIndices[7]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[8]].x + x, vertices[spriteIndices[8]].y + y, vertices[spriteIndices[8]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//        shape.vertex(vertices[spriteIndices[9]].x + x, vertices[spriteIndices[9]].y + y, vertices[spriteIndices[9]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[10]].x + x, vertices[spriteIndices[10]].y + y, vertices[spriteIndices[10]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//        shape.vertex(vertices[spriteIndices[11]].x + x, vertices[spriteIndices[11]].y + y, vertices[spriteIndices[11]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//
//        shape.vertex(vertices[spriteIndices[12]].x + x, vertices[spriteIndices[12]].y + y, vertices[spriteIndices[12]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[13]].x + x, vertices[spriteIndices[13]].y + y, vertices[spriteIndices[13]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[14]].x + x, vertices[spriteIndices[14]].y + y, vertices[spriteIndices[14]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//        shape.vertex(vertices[spriteIndices[15]].x + x, vertices[spriteIndices[15]].y + y, vertices[spriteIndices[15]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[16]].x + x, vertices[spriteIndices[16]].y + y, vertices[spriteIndices[16]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//        shape.vertex(vertices[spriteIndices[17]].x + x, vertices[spriteIndices[17]].y + y, vertices[spriteIndices[17]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//
//        shape.vertex(vertices[spriteIndices[18]].x + x, vertices[spriteIndices[18]].y + y, vertices[spriteIndices[18]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[19]].x + x, vertices[spriteIndices[19]].y + y, vertices[spriteIndices[19]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[20]].x + x, vertices[spriteIndices[20]].y + y, vertices[spriteIndices[20]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//        shape.vertex(vertices[spriteIndices[21]].x + x, vertices[spriteIndices[21]].y + y, vertices[spriteIndices[21]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).minY);
//        shape.vertex(vertices[spriteIndices[22]].x + x, vertices[spriteIndices[22]].y + y, vertices[spriteIndices[22]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).minX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//        shape.vertex(vertices[spriteIndices[23]].x + x, vertices[spriteIndices[23]].y + y, vertices[spriteIndices[23]].z + z, getTextureManager().getTextureIndex(block.texture.SIDES).maxX, getTextureManager().getTextureIndex(block.texture.SIDES).maxY);
//    }
////</editor-fold>
//
//    @Override
//    public void constructBlock(PShape buffers, Block block, BlockData data, Block negativeX, Block positiveX, Block negativeY, Block positiveY, Block negativeZ, Block positiveZ, int x, int y, int z) {
//        make_sunflower_center_faces(verts_sunflower, uv_sunflower, block, buffers, x, y, z);
//        make_sunflower_2_center_faces(verts_sunflower_2, uv_sunflower_2, block, buffers, x, y, z);
//        drawSprite(block, data, buffers, negativeX, positiveX, negativeY, positiveY, negativeZ, positiveZ, x, y, z);
//
//    }
//
//    private final float sixteenthConstant = 0.0625f;
//
//    @Override
//    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
//    }
//
//    @Override
//    public boolean isCubeShape() {
//        return true;
//    }
//}
