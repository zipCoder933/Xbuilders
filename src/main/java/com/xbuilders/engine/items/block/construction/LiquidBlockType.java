package com.xbuilders.engine.items.block.construction;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.rendering.VertexSet;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.Main;

public class LiquidBlockType extends DefaultBlockType {

    @Override
    public boolean useInGreedyMesher() {
        return false;
    }

    public BlockData getInitialBlockData(BlockData existingData, Block block, UserControlledPlayer player) {
        BlockData bd = new BlockData(1);
        bd.set(0, (byte) block.liquidMaxFlow);
        return bd;
    }

    public LiquidBlockType() {
        super();
        replaceOnSet = true;
        initializationCallback = (b) -> {
            b.opaque = false;
            b.solid = false;
            b.liquidMaxFlow = 7;
//            b.localChangeEvent((hist,changedPos,thisPos) ->{
//                //Check the block above thisPos
//                if (GameScene.world.getBlock(thisPos.x, thisPos.y - 1, thisPos.z).id == b.id) {
//                    BlockData bd = new BlockData(new byte[]{(byte) b.liquidMaxFlow});
//                    GameScene.world.setBlockData(bd, changedPos.x, changedPos.y, changedPos.z);
//                }
//            });
//            b.removeBlockEvent(((x, y, z, history) -> {
//                GameScene.world.setBlockData(history.previousBlockData, x, y, z);
////                boolean xpNeighbor = GameScene.world.getBlock(x - 1, y, z).id == b.id;
////                boolean xnNeighbor = GameScene.world.getBlock(x + 1, y, z).id == b.id;
////                boolean zpNeighbor = GameScene.world.getBlock(x, y, z - 1).id == b.id;
////                boolean znNeighbor = GameScene.world.getBlock(x, y, z + 1).id == b.id;
////
////                int touchingNeighbors = (xpNeighbor ? 1 : 0) + (xnNeighbor ? 1 : 0) + (zpNeighbor ? 1 : 0) + (znNeighbor ? 1 : 0);
////
////                if (touchingNeighbors > 2 //If we are on the surface of the liquid
////                        && GameScene.world.getBlock(x, y - 1, z).id != b.id) {
////                    //Put the liquid back into the world
////                    GameScene.world.setBlock(b.id, x, y, z);
////                }
//            }));
        };
    }


    private float getHeightOfFlow(int flow, float liquidMaxFlow, int y) {
        return 1.0f + y - (flow / (liquidMaxFlow + 1)) - (1 / liquidMaxFlow);
    }

    private int getFlow(BlockData data, int liquidMaxFlow) {
        return data != null && data.size() == 1 ? data.get(0) : 7;
    }

    private float getHeightOfFlow(BlockData data, int liquidMaxFlow, int y) {
        return getHeightOfFlow(getFlow(data, liquidMaxFlow), liquidMaxFlow, y);
    }

    static final private int TEX_FLOW_STATIC = 0;
    static final private int TEX_FLOW_NEG_X = 1;
    static final private int TEX_FLOW_POS_X = 2;
    static final private int TEX_FLOW_NEG_Z = 3;
    static final private int TEX_FLOW_POS_Z = 4;
    static final private int TEX_FLOW_NEG_X_NEG_Z = 5;
    static final private int TEX_FLOW_POS_X_NEG_Z = 6;
    static final private int TEX_FLOW_NEG_X_POS_Z = 7;
    static final private int TEX_FLOW_POS_X_POS_Z = 8;

    final static float[] topFaceUV_posZ = { //Original
            0.0f, 0.0f,//0,1
            1.0f, 0.0f,//2.3
            1.0f, 1.0f,//4,5
            0.0f, 1.0f//6,7
    };
    final static float[] topFaceUV_negZ = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f
    };
    final static float[] topFaceUV_posX = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f
    };
    final static float[] topFaceUV_negX = {
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
    };

    final static float[] topFaceUV_negX_posZ = { //Original
            0.0f, 0.5f,
            0.5f, 0.0f,
            1.0f, 0.5f,
            0.5f, 1.0f
    };


    final static float[] topFaceUV_negX_negZ = {
            0.5f, 1.0f,
            0.0f, 0.5f,
            0.5f, 0.0f,
            1.0f, 0.5f
    };

    final static float[] topFaceUV_posX_negZ = {
            1.0f, 0.5f,
            0.5f, 1.0f,
            0.0f, 0.5f,
            0.5f, 0.0f
    };

    final static float[] topFaceUV_posX_posZ = {
            0.5f, 0.0f,
            1.0f, 0.5f,
            0.5f, 1.0f,
            0.0f, 0.5f
    };


    @Override
    public void constructBlock(VertexSet buffer,
                               Block block, BlockData data,
                               Block[] neighbors,
                               BlockData[] neighborData,
                               byte[] light,
                               Chunk chunk, int chunkX, int chunkY, int chunkZ) {

        if (neighborData == null) return;//for icon generation safety

        BlockTexture.FaceTexture texLayer;

        final float yFloor = 1.0f + chunkY;
        float y00 = chunkY;
        float y10 = chunkY;
        float y01 = chunkY;
        float y11 = chunkY;
        int topTextureFlowMode = TEX_FLOW_STATIC;
        float[] topFaceUV = topFaceUV_posZ;

        boolean topLiquid = neighbors[POS_Y] != block;


        if (topLiquid && chunk != null) {
            int maxFlow = block.liquidMaxFlow;
            float zeroFlowHeight = getHeightOfFlow(0, maxFlow, chunkY);
            float fullFlowHeight = getHeightOfFlow(maxFlow, maxFlow, chunkY);
            float centerFlowHeight = getHeightOfFlow(data, maxFlow, chunkY);

            int worldX = chunkX + chunk.position.x * Chunk.WIDTH;
            int worldY = chunkY + chunk.position.y * Chunk.HEIGHT;
            int worldZ = chunkZ + chunk.position.z * Chunk.WIDTH;


            float negXFlow = zeroFlowHeight;
            float posXFlow = zeroFlowHeight;
            float negZFlow = zeroFlowHeight;
            float posZFlow = zeroFlowHeight;
            int y00WithLiquid = 0;
            int y10WithLiquid = 0;
            int y01WithLiquid = 0;
            int y11WithLiquid = 0;

            if (neighbors[NEG_X] == block) {
                //If the block above this one is also a liquid block, than the flow should be maxFlow
                if (GameScene.world.getBlockID(worldX - 1, worldY - 1, worldZ) == block.id) {
                    negXFlow = fullFlowHeight;
                } else negXFlow = getHeightOfFlow(neighborData[NEG_X], maxFlow, chunkY);

                y00WithLiquid++;
                y01WithLiquid++;
            }
            if (neighbors[POS_X] == block) {
                if (GameScene.world.getBlockID(worldX + 1, worldY - 1, worldZ) == block.id) {
                    posXFlow = fullFlowHeight;
                } else posXFlow = getHeightOfFlow(neighborData[POS_X], maxFlow, chunkY);

                y10WithLiquid++;
                y11WithLiquid++;
            }
            if (neighbors[NEG_Z] == block) {
                if (GameScene.world.getBlockID(worldX, worldY - 1, worldZ - 1) == block.id) {
                    negZFlow = fullFlowHeight;
                } else negZFlow = getHeightOfFlow(neighborData[NEG_Z], maxFlow, chunkY);

                y00WithLiquid++;
                y10WithLiquid++;
            }
            if (neighbors[POS_Z] == block) {
                if (GameScene.world.getBlockID(worldX, worldY - 1, worldZ + 1) == block.id) {
                    posZFlow = fullFlowHeight;
                } else posZFlow = getHeightOfFlow(neighborData[POS_Z], maxFlow, chunkY);

                y01WithLiquid++;
                y11WithLiquid++;
            }

            float negXnegZFlow = zeroFlowHeight;
            float negXposZFlow = zeroFlowHeight;
            float posXnegZFlow = zeroFlowHeight;
            float posXposZFlow = zeroFlowHeight;


            if (GameScene.world.getBlockID(worldX - 1, worldY, worldZ - 1) == block.id) {
                if (GameScene.world.getBlockID(worldX - 1, worldY - 1, worldZ - 1) == block.id) {
                    negXnegZFlow = fullFlowHeight;
                } else
                    negXnegZFlow = getHeightOfFlow(GameScene.world.getBlockData(worldX - 1, worldY, worldZ - 1), maxFlow, chunkY);

                y00WithLiquid++;
            }
            if (GameScene.world.getBlockID(worldX - 1, worldY, worldZ + 1) == block.id) {
                if (GameScene.world.getBlockID(worldX - 1, worldY - 1, worldZ + 1) == block.id) {
                    negXposZFlow = fullFlowHeight;
                } else
                    negXposZFlow = getHeightOfFlow(GameScene.world.getBlockData(worldX - 1, worldY, worldZ + 1), maxFlow, chunkY);

                y01WithLiquid++;
            }
            if (GameScene.world.getBlockID(worldX + 1, worldY, worldZ - 1) == block.id) {
                if (GameScene.world.getBlockID(worldX + 1, worldY - 1, worldZ - 1) == block.id) {
                    posXnegZFlow = fullFlowHeight;
                } else
                    posXnegZFlow = getHeightOfFlow(GameScene.world.getBlockData(worldX + 1, worldY, worldZ - 1), maxFlow, chunkY);

                y10WithLiquid++;
            }
            if (GameScene.world.getBlockID(worldX + 1, worldY, worldZ + 1) == block.id) {
                if (GameScene.world.getBlockID(worldX + 1, worldY - 1, worldZ + 1) == block.id) {
                    posXposZFlow = fullFlowHeight;
                } else
                    posXposZFlow = getHeightOfFlow(GameScene.world.getBlockData(worldX + 1, worldY, worldZ + 1), maxFlow, chunkY);

                y11WithLiquid++;
            }

            //Each corner is the average of its four neighbors
            if (y00WithLiquid > 0)
                y00 = Math.min(Math.min(negXnegZFlow, negXFlow), Math.min(negZFlow, centerFlowHeight));
            else y00 = zeroFlowHeight;

            if (y10WithLiquid > 0)
                y10 = Math.min(Math.min(posXnegZFlow, posXFlow), Math.min(negZFlow, centerFlowHeight));
            else y10 = zeroFlowHeight;

            if (y01WithLiquid > 0)
                y01 = Math.min(Math.min(negXposZFlow, negXFlow), Math.min(posZFlow, centerFlowHeight));
            else y01 = zeroFlowHeight;

            if (y11WithLiquid > 0)
                y11 = Math.min(Math.min(posXposZFlow, posXFlow), Math.min(posZFlow, centerFlowHeight));
            else y11 = zeroFlowHeight;

            //Determine top texture flow
            if (y00 > y10
                    && y00 > y01
                    && y00 > y11) {
                topTextureFlowMode = TEX_FLOW_NEG_X_NEG_Z;
            } else if (y01 > y00
                    && y01 > y10
                    && y01 > y11) {
                topTextureFlowMode = TEX_FLOW_NEG_X_POS_Z;
            } else if (y10 > y00
                    && y10 > y01
                    && y10 > y11) {
                topTextureFlowMode = TEX_FLOW_POS_X_NEG_Z;
            } else if (y11 > y00
                    && y11 > y01
                    && y11 > y10) {
                topTextureFlowMode = TEX_FLOW_POS_X_POS_Z;
            } else if (y00 + y01 < y10 + y11) {
                topTextureFlowMode = TEX_FLOW_POS_X;
            } else if (y00 + y01 > y10 + y11) {
                topTextureFlowMode = TEX_FLOW_NEG_X;
            } else if (y00 + y10 < y01 + y11) {
                topTextureFlowMode = TEX_FLOW_POS_Z;
            } else if (y00 + y10 > y01 + y11) {
                topTextureFlowMode = TEX_FLOW_NEG_Z;
            }

            switch (topTextureFlowMode) {
                case TEX_FLOW_NEG_Z -> topFaceUV = topFaceUV_negZ;
                case TEX_FLOW_POS_X -> topFaceUV = topFaceUV_posX;
                case TEX_FLOW_NEG_X -> topFaceUV = topFaceUV_negX;
                case TEX_FLOW_NEG_X_POS_Z -> topFaceUV = topFaceUV_negX_posZ;
                case TEX_FLOW_POS_X_POS_Z -> topFaceUV = topFaceUV_posX_posZ;
                case TEX_FLOW_NEG_X_NEG_Z -> topFaceUV = topFaceUV_negX_negZ;
                case TEX_FLOW_POS_X_NEG_Z -> topFaceUV = topFaceUV_posX_negZ;
            }
        }

        if (sideIsVisibleXZ(block, data, neighborData[NEG_X], neighbors[NEG_X], topLiquid)) {
            texLayer = (block.texture.getNEG_X());
            //NEG_X FACE:
            buffer.vertex(chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 1.0f, 1.0f, NEG_X, texLayer, light[NEG_X]);
            buffer.vertex(chunkX, y00, chunkZ, /* uvs */ 0.0f, 0.0f, NEG_X, texLayer, light[NEG_X]);
            buffer.vertex(chunkX, y01, 1.0f + chunkZ, /* uvs */ 1.0f, 0.0f, NEG_X, texLayer, light[NEG_X]);

            buffer.vertex(chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 1.0f, 1.0f, NEG_X, texLayer, light[NEG_X]);
            buffer.vertex(chunkX, yFloor, chunkZ, /* uvs */ 0.0f, 1.0f, NEG_X, texLayer, light[NEG_X]);
            buffer.vertex(chunkX, y00, chunkZ, /* uvs */ 0.0f, 0.0f, NEG_X, texLayer, light[NEG_X]);
        }

        if (sideIsVisibleXZ(block, data, neighborData[POS_X], neighbors[POS_X], topLiquid)) {
            texLayer = (block.texture.getPOS_X());
            //POS_X FACE:
            buffer.vertex(1.0f + chunkX, yFloor, chunkZ, /* uvs */ 1.0f, 1.0f, POS_X, texLayer, light[POS_X]);
            buffer.vertex(1.0f + chunkX, y11, 1.0f + chunkZ, /* uvs */ 0.0f, 0.0f, POS_X, texLayer, light[POS_X]);
            buffer.vertex(1.0f + chunkX, y10, chunkZ, /* uvs */ 1.0f, 0.0f, POS_X, texLayer, light[POS_X]);

            buffer.vertex(1.0f + chunkX, yFloor, chunkZ, /* uvs */ 1.0f, 1.0f, POS_X, texLayer, light[POS_X]);
            buffer.vertex(1.0f + chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 0.0f, 1.0f, POS_X, texLayer, light[POS_X]);
            buffer.vertex(1.0f + chunkX, y11, 1.0f + chunkZ, /* uvs */ 0.0f, 0.0f, POS_X, texLayer, light[POS_X]);
        }


        if (sideIsVisibleY(block, neighbors[POS_Y])) {
            if (topTextureFlowMode == TEX_FLOW_STATIC) texLayer = (block.texture.getPOS_Y());
            else texLayer = (block.texture.getPOS_X());

            //POS_Y FACE:
            buffer.vertex(1.0f + chunkX, y10, chunkZ,           /* uvs */ topFaceUV[2], topFaceUV[3], POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(chunkX, y01, 1.0f + chunkZ,           /* uvs */ topFaceUV[6], topFaceUV[7], POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(chunkX, y00, chunkZ,                     /* uvs */ topFaceUV[0], topFaceUV[1], POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(1.0f + chunkX, y10, chunkZ,           /* uvs */ topFaceUV[2], topFaceUV[3], POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(1.0f + chunkX, y11, 1.0f + chunkZ, /* uvs */ topFaceUV[4], topFaceUV[5], POS_Y, texLayer, light[POS_Y]);
            buffer.vertex(chunkX, y01, 1.0f + chunkZ,           /* uvs */ topFaceUV[6], topFaceUV[7], POS_Y, texLayer, light[POS_Y]);
        }


        if (sideIsVisibleXZ(block, data, neighborData[NEG_Z], neighbors[NEG_Z], topLiquid)) {
            texLayer = (block.texture.getNEG_Z());
            //NEG_Z FACE:
            buffer.vertex(chunkX, yFloor, chunkZ, /* uvs */ 1.0f, 1.0f, NEG_Z, texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + chunkX, y10, chunkZ, /* uvs */ 0.0f, 0.0f, NEG_Z, texLayer, light[NEG_Z]);
            buffer.vertex(chunkX, y00, chunkZ, /* uvs */ 1.0f, 0.0f, NEG_Z, texLayer, light[NEG_Z]);

            buffer.vertex(chunkX, yFloor, chunkZ, /* uvs */ 1.0f, 1.0f, NEG_Z, texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + chunkX, yFloor, chunkZ, /* uvs */ 0.0f, 1.0f, NEG_Z, texLayer, light[NEG_Z]);
            buffer.vertex(1.0f + chunkX, y10, chunkZ, /* uvs */ 0.0f, 0.0f, NEG_Z, texLayer, light[NEG_Z]);
        }


        if (sideIsVisibleY(block, neighbors[NEG_Y])) {
            texLayer = (block.texture.getNEG_Y());
            //NEG_Y FACE:
            buffer.vertex(chunkX, yFloor, chunkZ, /* uvs */ 1.0f, 0.0f, NEG_Y, texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 0.0f, 1.0f, NEG_Y, texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + chunkX, yFloor, chunkZ, /* uvs */ 0.0f, 0.0f, NEG_Y, texLayer, light[NEG_Y]);

            buffer.vertex(chunkX, yFloor, chunkZ, /* uvs */ 1.0f, 0.0f, NEG_Y, texLayer, light[NEG_Y]);
            buffer.vertex(chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 1.0f, 1.0f, NEG_Y, texLayer, light[NEG_Y]);
            buffer.vertex(1.0f + chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 0.0f, 1.0f, NEG_Y, texLayer, light[NEG_Y]);
        }

        if (sideIsVisibleXZ(block, data, neighborData[POS_Z], neighbors[POS_Z], topLiquid)) {
            texLayer = (block.texture.getPOS_Z());
            //POS_Z FACE:
            buffer.vertex(1.0f + chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 1.0f, 1.0f, POS_Z, texLayer, light[POS_Z]);
            buffer.vertex(chunkX, y01, 1.0f + chunkZ, /* uvs */ 0.0f, 0.0f, POS_Z, texLayer, light[POS_Z]);
            buffer.vertex(1.0f + chunkX, y11, 1.0f + chunkZ, /* uvs */ 1.0f, 0.0f, POS_Z, texLayer, light[POS_Z]);

            buffer.vertex(1.0f + chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 1.0f, 1.0f, POS_Z, texLayer, light[POS_Z]);
            buffer.vertex(chunkX, yFloor, 1.0f + chunkZ, /* uvs */ 0.0f, 1.0f, POS_Z, texLayer, light[POS_Z]);
            buffer.vertex(chunkX, y01, 1.0f + chunkZ, /* uvs */ 0.0f, 0.0f, POS_Z, texLayer, light[POS_Z]);
        }
    }


    public boolean sideIsVisibleXZ(Block block, BlockData data, BlockData neighborData, Block neighbor, boolean isTopLiquid) {
        if (neighbor.isAir()) return true;
        return false;
    }

    public boolean sideIsVisibleY(Block block, Block neighbor) {
        if (neighbor.isAir()) return true;
        return false;
    }
}
