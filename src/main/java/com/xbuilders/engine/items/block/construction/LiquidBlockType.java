package com.xbuilders.engine.items.block.construction;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.world.chunk.BlockData;

public class LiquidBlockType extends DefaultBlockType {

    public boolean useInGreedyMesher() {
        return true;
    }

    public LiquidBlockType() {
        super();
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = false;
            b.liquidMaxFlow = 7;
            b.removeBlockEvent(((x, y, z, history) -> {
                GameScene.world.setBlockData(history.previousBlockData, x, y, z);
//                boolean xpNeighbor = GameScene.world.getBlock(x - 1, y, z).id == b.id;
//                boolean xnNeighbor = GameScene.world.getBlock(x + 1, y, z).id == b.id;
//                boolean zpNeighbor = GameScene.world.getBlock(x, y, z - 1).id == b.id;
//                boolean znNeighbor = GameScene.world.getBlock(x, y, z + 1).id == b.id;
//
//                int touchingNeighbors = (xpNeighbor ? 1 : 0) + (xnNeighbor ? 1 : 0) + (zpNeighbor ? 1 : 0) + (znNeighbor ? 1 : 0);
//
//                if (touchingNeighbors > 2 //If we are on the surface of the liquid
//                        && GameScene.world.getBlock(x, y - 1, z).id != b.id) {
//                    //Put the liquid back into the world
//                    GameScene.world.setBlock(b.id, x, y, z);
//                }
            }));
        };
    }

    public static final int getFlow(BlockData data) {
        return data != null && data.size() == 1 ? data.get(0) : 0;
    }

    @Override
    public void constructBlock(VertexSet buffer,
                               Block block, BlockData data, Block[] neighbors, byte[] light,
                               int x, int y, int z) {
        BlockTexture.FaceTexture texLayer;

        final float yFloor = 1.0f + y;
        float y00 = y;
        float y10 = y;
        float y01 = y;
        float y11 = y;

//        if (neighbors[NEG_Y] != block) {
//            int flow = getFlow(data);
//
//            float subFlow = (flow / (float) block.liquidMaxFlow) - (1 / (float) block.liquidMaxFlow);
//            y00 = 1.0f + y - subFlow;
//            y10 = 1.0f + y - subFlow;
//            y01 = 1.0f + y - subFlow;
//            y11 = 1.0f + y - subFlow;
//        }

        if (sideIsVisible(block, neighbors[NEG_X])) {
            texLayer = (block.texture.getNEG_X());
            //NEG_X FACE:
            buffer.vertex(x, yFloor, 1.0f + z, /* uvs */ 1.0f, 1.0f, NEG_X, texLayer, light[NEG_X]);
            buffer.vertex(x, y00, z, /* uvs */ 0.0f, 0.0f, NEG_X, texLayer, light[NEG_X]);
            buffer.vertex(x, y01, 1.0f + z, /* uvs */ 1.0f, 0.0f, NEG_X, texLayer, light[NEG_X]);

            buffer.vertex(x, yFloor, 1.0f + z, /* uvs */ 1.0f, 1.0f, NEG_X, texLayer, light[NEG_X]);
            buffer.vertex(x, yFloor, z, /* uvs */ 0.0f, 1.0f, NEG_X, texLayer, light[NEG_X]);
            buffer.vertex(x, y00, z, /* uvs */ 0.0f, 0.0f, NEG_X, texLayer, light[NEG_X]);
        }

        if (sideIsVisible(block, neighbors[POS_X])) {
            texLayer = (block.texture.getPOS_X());
            //POS_X FACE:
            buffer.vertex(1.0f + x, yFloor, z, /* uvs */ 1.0f, 1.0f, POS_X, texLayer, light[POS_X]);
            buffer.vertex(1.0f + x, y11, 1.0f + z, /* uvs */ 0.0f, 0.0f, POS_X, texLayer, light[POS_X]);
            buffer.vertex(1.0f + x, y10, z, /* uvs */ 1.0f, 0.0f, POS_X, texLayer, light[POS_X]);

            buffer.vertex(1.0f + x, yFloor, z, /* uvs */ 1.0f, 1.0f, POS_X, texLayer, light[POS_X]);
            buffer.vertex(1.0f + x, yFloor, 1.0f + z, /* uvs */ 0.0f, 1.0f, POS_X, texLayer, light[POS_X]);
            buffer.vertex(1.0f + x, y11, 1.0f + z, /* uvs */ 0.0f, 0.0f, POS_X, texLayer, light[POS_X]);
        }

        if (sideIsVisible(block, neighbors[POS_Y])) {
            texLayer = (block.texture.getPOS_Y());
            //POS_Y FACE:
            buffer.vertex(1.0f + x, y10, z, /* uvs */ 1.0f, 0.0f, POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(x, y01, 1.0f + z, /* uvs */ -0.0f, 1.0f, POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(x, y00, z, /* uvs */ 0.0f, 0.0f, POS_Y, texLayer, light[POS_Y]);

            buffer.vertex(1.0f + x, y10, z, /* uvs */ 1.0f, 0.0f, POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(1.0f + x, y11, 1.0f + z, /* uvs */ 1.0f, 1.0f, POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(x, y01, 1.0f + z, /* uvs */ -0.0f, 1.0f, POS_Y, texLayer, light[POS_Y]);
        }

        if (sideIsVisible(block, neighbors[NEG_Z])) {
            texLayer = (block.texture.getNEG_Z());
            //NEG_Z FACE:
            buffer.vertex(x, yFloor, z, /* uvs */ 1.0f, 1.0f, NEG_Z, texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + x, y10, z, /* uvs */ 0.0f, 0.0f, NEG_Z, texLayer, light[NEG_Z]);
            buffer.vertex(x, y00, z, /* uvs */ 1.0f, 0.0f, NEG_Z, texLayer, light[NEG_Z]);

            buffer.vertex(x, yFloor, z, /* uvs */ 1.0f, 1.0f, NEG_Z, texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + x, yFloor, z, /* uvs */ 0.0f, 1.0f, NEG_Z, texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + x, y10, z, /* uvs */ 0.0f, 0.0f, NEG_Z, texLayer, light[NEG_Z]);
        }

        if (sideIsVisible(block, neighbors[NEG_Y])) {
            texLayer = (block.texture.getNEG_Y());
            //NEG_Y FACE:
            buffer.vertex(x, yFloor, z, /* uvs */ 1.0f, 0.0f, NEG_Y, texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + x, yFloor, 1.0f + z, /* uvs */ 0.0f, 1.0f, NEG_Y, texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + x, yFloor, z, /* uvs */ 0.0f, 0.0f, NEG_Y, texLayer, light[NEG_Y]);

            buffer.vertex(x, yFloor, z, /* uvs */ 1.0f, 0.0f, NEG_Y, texLayer, light[NEG_Y]);
            buffer.vertex(x, yFloor, 1.0f + z, /* uvs */ 1.0f, 1.0f, NEG_Y, texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + x, yFloor, 1.0f + z, /* uvs */ 0.0f, 1.0f, NEG_Y, texLayer, light[NEG_Y]);
        }

        if (sideIsVisible(block, neighbors[POS_Z])) {
            texLayer = (block.texture.getPOS_Z());
            //POS_Z FACE:
            buffer.vertex(1.0f + x, yFloor, 1.0f + z, /* uvs */ 1.0f, 1.0f, POS_Z, texLayer, light[POS_Z]);
            buffer.vertex(x, y01, 1.0f + z, /* uvs */ 0.0f, 0.0f, POS_Z, texLayer, light[POS_Z]);
            buffer.vertex(1.0f + x, y11, 1.0f + z, /* uvs */ 1.0f, 0.0f, POS_Z, texLayer, light[POS_Z]);

            buffer.vertex(1.0f + x, yFloor, 1.0f + z, /* uvs */ 1.0f, 1.0f, POS_Z, texLayer, light[POS_Z]);
            buffer.vertex(x, yFloor, 1.0f + z, /* uvs */ 0.0f, 1.0f, POS_Z, texLayer, light[POS_Z]);
            buffer.vertex(x, y01, 1.0f + z, /* uvs */ 0.0f, 0.0f, POS_Z, texLayer, light[POS_Z]);
        }
    }


    @Override
    public boolean sideIsVisible(Block block, Block NEG_X) {
        return NEG_X.isAir();
    }
}
