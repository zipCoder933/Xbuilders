/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.type;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;

import java.util.function.Consumer;

import org.joml.Vector3f;

/**
 * @author zipCoder933
 */
public class SpriteRenderer extends BlockType {

    public SpriteRenderer() {
        super();
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = false;
        };
    }

    static Vector3f[] vertices = {
            new Vector3f(0, 0, 0),//0
            new Vector3f(1, 0, 0),//1
            new Vector3f(1, 0, 1),//2
            new Vector3f(0, 0, 1),//3
            new Vector3f(0, 1, 0),//4
            new Vector3f(1, 1, 0),//5
            new Vector3f(1, 1, 1),//6
            new Vector3f(0, 1, 1)};//7
    static int[] indx = {
            0, 2, 6,
            0, 6, 4,
            3, 1, 5,
            3, 5, 7,
            2, 0, 4,
            2, 4, 6,
            1, 3, 7,
            1, 7, 5,};


    public boolean allowExistence(Block block, int worldX, int worldY, int worldZ) {
        return !GameScene.world.getBlock(worldX, worldY + 1, worldZ).liquid
                && !GameScene.world.getBlock(worldX, worldY + 1, worldZ).isAir();
    }

    @Override
    public void constructBlock(VertexSet buffers,
                               Block block, BlockData data, Block[] neighbors, byte[] light,
                               int x, int y, int z) {

        BlockTexture.FaceTexture texLayer = block.texture.getNEG_X();

        buffers.vertex(vertices[indx[0]].x + x, vertices[indx[0]].y + y, vertices[indx[0]].z + z, 1.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[1]].x + x, vertices[indx[1]].y + y, vertices[indx[1]].z + z, 0.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[2]].x + x, vertices[indx[2]].y + y, vertices[indx[2]].z + z, 0.0f, 1.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[3]].x + x, vertices[indx[3]].y + y, vertices[indx[3]].z + z, 1.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[4]].x + x, vertices[indx[4]].y + y, vertices[indx[4]].z + z, 0.0f, 1.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[5]].x + x, vertices[indx[5]].y + y, vertices[indx[5]].z + z, 1.0f, 1.0f, texLayer, light[0]);

        buffers.vertex(vertices[indx[6]].x + x, vertices[indx[6]].y + y, vertices[indx[6]].z + z, 1.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[7]].x + x, vertices[indx[7]].y + y, vertices[indx[7]].z + z, 0.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[8]].x + x, vertices[indx[8]].y + y, vertices[indx[8]].z + z, 0.0f, 1.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[9]].x + x, vertices[indx[9]].y + y, vertices[indx[9]].z + z, 1.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[10]].x + x, vertices[indx[10]].y + y, vertices[indx[10]].z + z, 0.0f, 1.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[11]].x + x, vertices[indx[11]].y + y, vertices[indx[11]].z + z, 1.0f, 1.0f, texLayer, light[0]);

        buffers.vertex(vertices[indx[12]].x + x, vertices[indx[12]].y + y, vertices[indx[12]].z + z, 1.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[13]].x + x, vertices[indx[13]].y + y, vertices[indx[13]].z + z, 0.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[14]].x + x, vertices[indx[14]].y + y, vertices[indx[14]].z + z, 0.0f, 1.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[15]].x + x, vertices[indx[15]].y + y, vertices[indx[15]].z + z, 1.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[16]].x + x, vertices[indx[16]].y + y, vertices[indx[16]].z + z, 0.0f, 1.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[17]].x + x, vertices[indx[17]].y + y, vertices[indx[17]].z + z, 1.0f, 1.0f, texLayer, light[0]);

        buffers.vertex(vertices[indx[18]].x + x, vertices[indx[18]].y + y, vertices[indx[18]].z + z, 1.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[19]].x + x, vertices[indx[19]].y + y, vertices[indx[19]].z + z, 0.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[20]].x + x, vertices[indx[20]].y + y, vertices[indx[20]].z + z, 0.0f, 1.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[21]].x + x, vertices[indx[21]].y + y, vertices[indx[21]].z + z, 1.0f, 0.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[22]].x + x, vertices[indx[22]].y + y, vertices[indx[22]].z + z, 0.0f, 1.0f, texLayer, light[0]);
        buffers.vertex(vertices[indx[23]].x + x, vertices[indx[23]].y + y, vertices[indx[23]].z + z, 1.0f, 1.0f, texLayer, light[0]);
    }
//

    public final float ONE_SIXTEENTH = (float) 1 / 16;

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        float width = 1 - (ONE_SIXTEENTH * 2);
        box.setPosAndSize(x + (ONE_SIXTEENTH), y, z + (ONE_SIXTEENTH), width, 1, width);
        consumer.accept(box);
    }

    @Override
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
    }

}
