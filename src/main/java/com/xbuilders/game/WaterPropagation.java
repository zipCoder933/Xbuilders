package com.xbuilders.game;

import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.world.chunk.BlockData;
import org.joml.Vector3i;

import java.util.ArrayList;

public class WaterPropagation extends LivePropagationTask {

    public WaterPropagation() {
        updateIntervalMS = 300;
        interestedBlock = MyGame.BLOCK_WATER;
    }


    public int maxFlow = 7;

    @Override
    public void update() {
        if (nodes.size() == 0) {
            return;
        }

//        System.out.println("Propagation nodes: " + nodes.size());
        ArrayList<Vector3i> newNodes = new ArrayList<>();
        for (Vector3i v : nodes) {

//            if(GameScene.world.getBlockID(v.x, v.y, v.z) != interestedBlock) { //Sometimes block change before we call this
//                continue;
//            }

            //Get the flow from this node
            BlockData thisBD = GameScene.world.getBlockData(v.x, v.y, v.z);
            int flow = thisBD != null && thisBD.size() > 0 ? thisBD.get(0) : 0;

            if (setWater(v.x, v.y + 1, v.z, maxFlow)) {
            } else if (flow > 0) {
                setWater(v.x - 1, v.y, v.z, flow - 1);
                setWater(v.x + 1, v.y, v.z, flow - 1);
                setWater(v.x, v.y, v.z - 1, flow - 1);
                setWater(v.x, v.y, v.z + 1, flow - 1);
            }

        }
        nodes.clear();
        nodes.addAll(newNodes);
    }

    public boolean setWater(int x, int y, int z, int flow) {
        Block b = GameScene.world.getBlock(x, y, z);
        if (b.isAir()) {
            GameScene.player.setBlock(interestedBlock, x, y, z, new BlockData(new byte[]{(byte) flow}));
        }
        return !b.solid;
    }
}