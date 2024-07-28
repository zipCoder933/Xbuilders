//package com.xbuilders.game.propagation;
//
//import com.xbuilders.engine.gameScene.GameScene;
//import com.xbuilders.engine.gameScene.LivePropagationTask;
//import com.xbuilders.engine.items.BlockList;
//import com.xbuilders.engine.items.ItemList;
//import com.xbuilders.engine.items.block.Block;
//import com.xbuilders.engine.world.chunk.BlockData;
//import com.xbuilders.game.MyGame;
//import org.joml.Vector3i;
//
//import java.util.ArrayList;
//
//public class GravityPropagation extends LivePropagationTask {
//
//    public GravityPropagation() {
//        updateIntervalMS = 200;
//    }
//
//    public boolean isInterestedInBlock(short block) {
//        return block == MyGame.BLOCK_SAND;
//    }
//
//    @Override
//    public void update() {
//        if (nodes.isEmpty()) {
//            return;
//        }
//        for (Vector3i v : nodes) {
//            if (!GameScene.world.getBlock(v.x, v.y + 1, v.z).solid) {
//                short center = GameScene.world.getBlockID(v.x, v.y, v.z);
//                GameScene.player.setBlock(BlockList.BLOCK_AIR.id, v.x, v.y, v.z);
//                GameScene.player.setBlock(center, v.x, v.y + 1, v.z);
//            }
//        }
//        nodes.clear();
//    }
//}
