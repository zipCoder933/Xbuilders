/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.type;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.mesh.BufferSet;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * @author zipCoder933
 */
public class FloorItemRenderer extends BlockType {

    @Override
    public BlockData getInitialBlockData(BlockData existingData, UserControlledPlayer player) {
        return player.camera.simplifiedPanTiltAsBlockData(new BlockData(2));
    }

    //<editor-fold defaultstate="collapsed" desc="Draw floor1">
//NOTES:
//The UV map for this block only exists on the top face.
//<editor-fold defaultstate="collapsed" desc="Verticies">
    static Vector3f[] verts_floor1 = {
            new Vector3f(-0.0f, 0.93750006f, 0.9999989f), //0
            new Vector3f(0.0f, 0.93750006f, -0.0f), //1
            new Vector3f(1.0f, 0.93750006f, 1.0f), //2
            new Vector3f(1.0f, 0.93750006f, 0.0f), //3
    };
    static Vector2f[] uv_floor1 = {
            new Vector2f(0.0f, 1.0f), //0
            new Vector2f(1.0f, 0.0f), //1
            new Vector2f(1.0f, 1.0f), //2
            new Vector2f(0.0f, 0.0f), //3
    };

    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="Face methods">
    private static void make_floor1_center_faces(Vector3f[] verts2, Vector2f[] uv2, Block block, BufferSet shape, int x, int y, int z) {
        BlockTexture.FaceTexture texLayer = block.texture.getNEG_Y();
        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, uv2[2].x, uv2[2].y, texLayer);
        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, uv2[1].x, uv2[1].y, texLayer);
        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, uv2[0].x, uv2[0].y, texLayer);//FACE
        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, uv2[1].x, uv2[1].y, texLayer);
        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, uv2[3].x, uv2[3].y, texLayer);
        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, uv2[0].x, uv2[0].y, texLayer);//FACE
    }

    //</editor-fold>
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Draw floor2">
    //NOTES:
    //The UV map for this block only exists on the top face.
    //<editor-fold defaultstate="collapsed" desc="Verticies">
    static Vector3f[] verts_floor2 = {
            new Vector3f(1.0E-6f, 0.93750006f, -1.0E-6f), //0
            new Vector3f(1.0f, 0.93750006f, 0.0f), //1
            new Vector3f(-0.0f, 0.93750006f, 1.0f), //2
            new Vector3f(1.0f, 0.93750006f, 1.0f), //3
    };
    static Vector2f[] uv_floor2 = {
            new Vector2f(0.0f, 1.0f), //0
            new Vector2f(1.0f, 0.0f), //1
            new Vector2f(1.0f, 1.0f), //2
            new Vector2f(0.0f, 0.0f), //3
    };

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Face methods">
    private static void make_floor2_center_faces(Vector3f[] verts2, Vector2f[] uv2, Block block, BufferSet shape, int x, int y, int z) {
        BlockTexture.FaceTexture texLayer = block.texture.getNEG_Y();

        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, uv2[2].x, uv2[2].y, texLayer);
        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, uv2[1].x, uv2[1].y, texLayer);
        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, uv2[0].x, uv2[0].y, texLayer);//FACE
        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, uv2[1].x, uv2[1].y, texLayer);
        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, uv2[3].x, uv2[3].y, texLayer);
        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, uv2[0].x, uv2[0].y, texLayer);//FACE
    }

    //</editor-fold>
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Draw floor3">
    //NOTES:
    //The UV map for this block only exists on the top face.
    //<editor-fold defaultstate="collapsed" desc="Verticies">
    static Vector3f[] verts_floor3 = {
            new Vector3f(1.0f, 0.93750006f, 1.0E-6f), //0
            new Vector3f(0.9999989f, 0.93750006f, 1.0f), //1
            new Vector3f(0.0f, 0.93750006f, -0.0f), //2
            new Vector3f(-1.0E-6f, 0.93750006f, 0.9999989f), //3
    };
    static Vector2f[] uv_floor3 = {
            new Vector2f(0.0f, 1.0f), //0
            new Vector2f(1.0f, 0.0f), //1
            new Vector2f(1.0f, 1.0f), //2
            new Vector2f(0.0f, 0.0f), //3
    };

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Face methods">
    private static void make_floor3_center_faces(Vector3f[] verts2, Vector2f[] uv2, Block block, BufferSet shape, int x, int y, int z) {
        BlockTexture.FaceTexture texLayer = block.texture.getNEG_Y();
        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, uv2[2].x, uv2[2].y, texLayer);
        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, uv2[1].x, uv2[1].y, texLayer);
        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, uv2[0].x, uv2[0].y, texLayer);//FACE
        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, uv2[1].x, uv2[1].y, texLayer);
        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, uv2[3].x, uv2[3].y, texLayer);
        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, uv2[0].x, uv2[0].y, texLayer);//FACE
    }

    //</editor-fold>
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Draw floor4">
    //NOTES:
    //The UV map for this block only exists on the top face.
    //<editor-fold defaultstate="collapsed" desc="Verticies">
    static Vector3f[] verts_floor4 = {
            new Vector3f(0.9999989f, 0.93750006f, 1.000001f), //0
            new Vector3f(-1.0E-6f, 0.93750006f, 0.9999989f), //1
            new Vector3f(1.000001f, 0.93750006f, 1.0E-6f), //2
            new Vector3f(1.0E-6f, 0.93750006f, -1.0E-6f), //3
    };
    static Vector2f[] uv_floor4 = {
            new Vector2f(0.0f, 1.0f), //0
            new Vector2f(1.0f, 0.0f), //1
            new Vector2f(1.0f, 1.0f), //2
            new Vector2f(0.0f, 0.0f), //3
    };

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Face methods">
    private static void make_floor4_center_faces(Vector3f[] verts2, Vector2f[] uv2, Block block, BufferSet shape, int x, int y, int z) {
        BlockTexture.FaceTexture texLayer = block.texture.getNEG_Y();
        shape.vertex(verts2[3].x + x, verts2[3].y + y, verts2[3].z + z, uv2[2].x, uv2[2].y, texLayer);
        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, uv2[1].x, uv2[1].y, texLayer);
        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, uv2[0].x, uv2[0].y, texLayer);//FACE
        shape.vertex(verts2[1].x + x, verts2[1].y + y, verts2[1].z + z, uv2[1].x, uv2[1].y, texLayer);
        shape.vertex(verts2[0].x + x, verts2[0].y + y, verts2[0].z + z, uv2[3].x, uv2[3].y, texLayer);
        shape.vertex(verts2[2].x + x, verts2[2].y + y, verts2[2].z + z, uv2[0].x, uv2[0].y, texLayer);//FACE
    }

    //</editor-fold>
    //</editor-fold>
    @Override
    public void constructBlock(BufferSet buffers, Block block, BlockData data, Block[] neighbors, int x, int y, int z) {

        if (data == null || data.get(0) == 3) {
            make_floor1_center_faces(verts_floor1, uv_floor1, block, buffers, x, y, z);
        } else if (data.get(0) == 0) {
            make_floor2_center_faces(verts_floor2, uv_floor2, block, buffers, x, y, z);
        } else if (data.get(0) == 1) {
            make_floor3_center_faces(verts_floor3, uv_floor3, block, buffers, x, y, z);
        } else {
            make_floor4_center_faces(verts_floor4, uv_floor4, block, buffers, x, y, z);
        }
    }

    private final float sixteenthConstant = 0.0625f;

    @Override
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x, y + (sixteenthConstant * 15), z, 1, sixteenthConstant * 1, 1);
        consumer.accept(box);
    }

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        box.setPosAndSize(x, y + (sixteenthConstant * 15), z, 1, sixteenthConstant * 1, 1);
        consumer.accept(box);
    }

}
