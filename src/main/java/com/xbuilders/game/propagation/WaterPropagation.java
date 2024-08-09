package com.xbuilders.game.propagation;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;


public class WaterPropagation extends LivePropagationTask {

    public Block liquidBlock;

    public WaterPropagation() {
        updateIntervalMS = 300;
        liquidBlock = ItemList.getBlock(MyGame.BLOCK_WATER);
    }

    public boolean isInterestedInBlock(BlockHistory hist) {
        return hist.newBlock.id == liquidBlock.id;
        //|| hist.previousBlock.id == liquidBlock;
    }


    @Override
    public void update() {
        if (nodes.isEmpty()) {
            return;
        }
//        System.out.println(liquidBlock.name + " propagation: " + nodes.size());
        for (Vector3i v : nodes) {
            //Get the flow from this node
            BlockData thisBD = GameScene.world.getBlockData(v.x, v.y, v.z);
            int flow = thisBD != null && thisBD.size() > 0 ? thisBD.get(0) : 0;


//
//            if (GameScene.world.getBlockID(v.x, v.y, v.z) != liquidBlock.id) {
//                if(GameScene.world.getBlock(v.x, v.y+1, v.z).id == liquidBlock.id) {
//                    GameScene.player.setBlock(BlockList.BLOCK_AIR.id, v.x, v.y+1, v.z);
//                }
//                if(GameScene.world.getBlock(v.x -1, v.y, v.z).id == liquidBlock.id) {
//                    GameScene.player.setBlock(BlockList.BLOCK_AIR.id, v.x-1, v.y, v.z);
//                }
//                if(GameScene.world.getBlock(v.x +1, v.y, v.z).id == liquidBlock.id) {
//                    GameScene.player.setBlock(BlockList.BLOCK_AIR.id, v.x+1, v.y, v.z);
//                }
//                if(GameScene.world.getBlock(v.x, v.y, v.z -1).id == liquidBlock.id) {
//                    GameScene.player.setBlock(BlockList.BLOCK_AIR.id, v.x, v.y, v.z-1);
//                }
//                if(GameScene.world.getBlock(v.x, v.y, v.z +1).id == liquidBlock.id) {
//                    GameScene.player.setBlock(BlockList.BLOCK_AIR.id, v.x, v.y, v.z+1);
//                }
//            } else
            if (setWater(v.x, v.y + 1, v.z, liquidBlock.liquidMaxFlow)) {
            } else if (flow > 0) {
                setWater(v.x - 1, v.y, v.z, flow - 1);
                setWater(v.x + 1, v.y, v.z, flow - 1);
                setWater(v.x, v.y, v.z - 1, flow - 1);
                setWater(v.x, v.y, v.z + 1, flow - 1);
            }

        }
        nodes.clear();
    }

    public boolean setWater(int x, int y, int z, int flow) {
        Block b = GameScene.world.getBlock(x, y, z);
        if (b.isAir()) {
            GameScene.player.setBlock(liquidBlock.id, new BlockData(new byte[]{(byte) flow}), x, y, z);
        }
        return !b.solid;
    }
}
