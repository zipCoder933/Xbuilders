/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.blocks.type;

import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.math.AABB;
import com.xbuilders.engine.world.chunk.BlockData;

import java.util.function.Consumer;

/**
 *
 * @author zipCoder933
 */
public class OrientableBlockRenderer extends BlockType {//TODO: Make the texture depend on the orientation of the block, store the orientation in the BlockData

    @Override
    public void constructBlock(VertexSet buffer,
                               Block block, BlockData data, Block[] neighbors, byte[] light,
                               int x, int y, int z) {
        BlockTexture.FaceTexture texLayer;

        if (sideIsVisible(block, neighbors[NEG_X])) {
            texLayer = (block.texture.getNEG_X());
            //NEG_X FACE:
            buffer.vertex(x, 1.0f + y, 1.0f + z, /* uvs */ 1.0f, 1.0f,NEG_X,  texLayer, light[NEG_X]);
            buffer.vertex(x, y, z, /* uvs */ 0.0f, 0.0f,NEG_X,  texLayer, light[NEG_X]);
            buffer.vertex(x, y, 1.0f + z, /* uvs */ 1.0f, 0.0f,NEG_X,  texLayer, light[NEG_X]);

            buffer.vertex(x, 1.0f + y, 1.0f + z, /* uvs */ 1.0f, 1.0f,NEG_X,  texLayer, light[NEG_X]);
            buffer.vertex(x, 1.0f + y, z, /* uvs */ 0.0f, 1.0f,NEG_X,  texLayer, light[NEG_X]);
            buffer.vertex(x, y, z, /* uvs */ 0.0f, 0.0f,NEG_X,  texLayer, light[NEG_X]);
        }

        if (sideIsVisible(block, neighbors[POS_X])) {
            texLayer = (block.texture.getPOS_X());
            //POS_X FACE:
            buffer.vertex(1.0f + x, 1.0f + y, z, /* uvs */ 1.0f, 1.0f,POS_X,  texLayer, light[POS_X]);
            buffer.vertex(1.0f + x, y, 1.0f + z, /* uvs */ 0.0f, 0.0f,POS_X,  texLayer, light[POS_X]);
            buffer.vertex(1.0f + x, y, z, /* uvs */ 1.0f, 0.0f,POS_X,  texLayer, light[POS_X]);

            buffer.vertex(1.0f + x, 1.0f + y, z, /* uvs */ 1.0f, 1.0f,POS_X,  texLayer, light[POS_X]);
            buffer.vertex(1.0f + x, 1.0f + y, 1.0f + z, /* uvs */ 0.0f, 1.0f,POS_X,  texLayer, light[POS_X]);
            buffer.vertex(1.0f + x, y, 1.0f + z, /* uvs */ 0.0f, 0.0f,POS_X,  texLayer, light[POS_X]);
        }

        if (sideIsVisible(block, neighbors[POS_Y])) {
            texLayer = (block.texture.getPOS_Y());
            //POS_Y FACE:
            buffer.vertex(1.0f + x, y, z, /* uvs */ 1.0f, 0.0f,POS_Y,  texLayer, light[POS_Y]);
            buffer.vertex(x, y, 1.0f + z, /* uvs */ -0.0f, 1.0f,POS_Y,  texLayer, light[POS_Y]);
            buffer.vertex(x, y, z, /* uvs */ 0.0f, 0.0f,POS_Y,  texLayer, light[POS_Y]);

            buffer.vertex(1.0f + x, y, z, /* uvs */ 1.0f, 0.0f,POS_Y,  texLayer, light[POS_Y]);
            buffer.vertex(1.0f + x, y, 1.0f + z, /* uvs */ 1.0f, 1.0f,POS_Y,  texLayer, light[POS_Y]);
            buffer.vertex(x, y, 1.0f + z, /* uvs */ -0.0f, 1.0f,POS_Y,  texLayer, light[POS_Y]);
        }

        if (sideIsVisible(block, neighbors[NEG_Z])) {
            texLayer = (block.texture.getNEG_Z());
            //NEG_Z FACE:
            buffer.vertex(x, 1.0f + y, z, /* uvs */ 1.0f, 1.0f, NEG_Z,  texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + x, y, z, /* uvs */ 0.0f, 0.0f, NEG_Z,  texLayer, light[NEG_Z]);
            buffer.vertex(x, y, z, /* uvs */ 1.0f, 0.0f, NEG_Z,  texLayer, light[NEG_Z]);

            buffer.vertex(x, 1.0f + y, z, /* uvs */ 1.0f, 1.0f, NEG_Z,  texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + x, 1.0f + y, z, /* uvs */ 0.0f, 1.0f, NEG_Z,  texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + x, y, z, /* uvs */ 0.0f, 0.0f, NEG_Z,  texLayer, light[NEG_Z]);
        }

        if (sideIsVisible(block, neighbors[NEG_Y])) {
            texLayer = (block.texture.getNEG_Y());
            //NEG_Y FACE:
            buffer.vertex(x, 1.0f + y, z, /* uvs */ 1.0f, 0.0f, NEG_Y,  texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + x, 1.0f + y, 1.0f + z, /* uvs */ 0.0f, 1.0f, NEG_Y,  texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + x, 1.0f + y, z, /* uvs */ 0.0f, 0.0f, NEG_Y,  texLayer, light[NEG_Y]);

            buffer.vertex(x, 1.0f + y, z, /* uvs */ 1.0f, 0.0f, NEG_Y,  texLayer, light[NEG_Y]);
            buffer.vertex(x, 1.0f + y, 1.0f + z, /* uvs */ 1.0f, 1.0f, NEG_Y,  texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + x, 1.0f + y, 1.0f + z, /* uvs */ 0.0f, 1.0f, NEG_Y,  texLayer, light[NEG_Y]);
        }

        if (sideIsVisible(block, neighbors[POS_Z])) {
            texLayer = (block.texture.getPOS_Z());
            //POS_Z FACE:
            buffer.vertex(1.0f + x, 1.0f + y, 1.0f + z, /* uvs */ 1.0f, 1.0f, POS_Z,  texLayer, light[POS_Z]);
            buffer.vertex(x, y, 1.0f + z, /* uvs */ 0.0f, 0.0f, POS_Z,  texLayer, light[POS_Z]);
            buffer.vertex(1.0f + x, y, 1.0f + z, /* uvs */ 1.0f, 0.0f, POS_Z,  texLayer, light[POS_Z]);

            buffer.vertex(1.0f + x, 1.0f + y, 1.0f + z, /* uvs */ 1.0f, 1.0f, POS_Z,  texLayer, light[POS_Z]);
            buffer.vertex(x, 1.0f + y, 1.0f + z, /* uvs */ 0.0f, 1.0f, POS_Z,  texLayer, light[POS_Z]);
            buffer.vertex(x, y, 1.0f + z, /* uvs */ 0.0f, 0.0f, POS_Z,  texLayer, light[POS_Z]);
        }
    }

    private boolean sideIsVisible(Block block, Block NEG_X) {
        return !NEG_X.opaque;
    }

    @Override
    public boolean isCubeShape() {
        return true;
    }

    @Override
    public void getCursorBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        consumer.accept(box.setPosAndSize(x, y, z, 1, 1, 1));
    }

    @Override
    public void getCollisionBoxes(Consumer<AABB> consumer, AABB box, Block block, BlockData data, int x, int y, int z) {
        consumer.accept(box.setPosAndSize(x, y, z, 1, 1, 1));
    }
}
