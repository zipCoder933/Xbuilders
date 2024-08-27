package com.xbuilders.game.propagation;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.items.blocks.RenderType;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashSet;


public class WaterPropagation extends LivePropagationTask {

    public Block liquidBlock;
    ArrayList<Vector3i> reduceNodes = new ArrayList<>();
    public HashSet<Vector3i> nodes = new HashSet<>();

    public WaterPropagation() {
        updateIntervalMS = 300;
        liquidBlock = ItemList.getBlock(MyGame.BLOCK_WATER);
    }

    public boolean addNode(Vector3i worldPos, BlockHistory hist) {
        if (hist.newBlock.id == liquidBlock.id) {
            if (reduceNodes.contains(worldPos)) {
                return false;
            }
            nodes.add(worldPos);
            return true;
        }
        return false;
    }

    public boolean isPenetrable(Block block) {
        return block.isAir() || (!block.solid && block.type == RenderType.SPRITE);
    }

    private int getFlow(BlockData thisBD) {
        return thisBD != null && thisBD.size() > 0 ? thisBD.get(0) : 0;
    }

    /**
     * maxFlow+1 = source water
     * maxFlow = down-flowing water
     * > maxFlow = sidewards-flowing water
     */
    @Override
    public void update() {
        if (nodes.isEmpty() && reduceNodes.isEmpty()) {
            return;
        }
        Main.printlnDev(liquidBlock.name + " prop nodes: " + nodes.size());
        int sourceFlow = liquidBlock.liquidMaxFlow + 1;
        for (Vector3i v : nodes) {
            //Get the flow from this node

            int flow = getFlow(GameScene.world.getBlockData(v.x, v.y, v.z));
            Block below = GameScene.world.getBlock(v.x, v.y + 1, v.z);

            if (
                    flow == liquidBlock.liquidMaxFlow && //If we are 100% flowing
                            GameScene.world.getBlockID(v.x, v.y - 1, v.z) != liquidBlock.id && //and there is nothing above
                            getFlow(GameScene.world.getBlockData(v.x - 1, v.y, v.z)) != sourceFlow &&//and there is no neighboring source
                            getFlow(GameScene.world.getBlockData(v.x + 1, v.y, v.z)) != sourceFlow &&
                            getFlow(GameScene.world.getBlockData(v.x, v.y, v.z - 1)) != sourceFlow &&
                            getFlow(GameScene.world.getBlockData(v.x, v.y, v.z + 1)) != sourceFlow
            ) {
                GameScene.player.setBlock(BlockList.BLOCK_AIR.id, v.x, v.y, v.z);
                continue;
            } else if (
                    flow < liquidBlock.liquidMaxFlow &&
                    GameScene.world.getBlockID(v.x, v.y - 1, v.z) != liquidBlock.id && //and there is nothing above
                    !below.solid  //and there is nothing below
            ) {
                GameScene.player.setBlock(BlockList.BLOCK_AIR.id, v.x, v.y, v.z);
                continue;
            }

            if (setWater(v.x, v.y + 1, v.z, liquidBlock.liquidMaxFlow)) {
            } else if (flow > 0) {
                setWater(v.x - 1, v.y, v.z, flow - 1);
                setWater(v.x + 1, v.y, v.z, flow - 1);
                setWater(v.x, v.y, v.z - 1, flow - 1);
                setWater(v.x, v.y, v.z + 1, flow - 1);
            }

        }
        nodes.clear();


//        //Iterate through the reduce nodes
//        Main.printlnDev("Reduce nodes: " + reduceNodes.size());
//        ArrayList<Vector3i> new_reduceNodes = new ArrayList<>();
//        for (Vector3i v : reduceNodes) {
//            int flow = getFlow(GameScene.world.getBlockData(v.x, v.y, v.z));
//            reduceFlow(flow, v.x, v.y, v.z);
//            if (reduceFlow(flow, v.x + 1, v.y, v.z)) {
//                new_reduceNodes.add(new Vector3i(v.x + 1, v.y, v.z));
//            }
//            if (reduceFlow(flow, v.x - 1, v.y, v.z)) {
//                new_reduceNodes.add(new Vector3i(v.x - 1, v.y, v.z));
//            }
//            if (reduceFlow(flow, v.x, v.y, v.z + 1)) {
//                new_reduceNodes.add(new Vector3i(v.x, v.y, v.z + 1));
//            }
//            if (reduceFlow(flow, v.x, v.y, v.z - 1)) {
//                new_reduceNodes.add(new Vector3i(v.x, v.y, v.z - 1));
//            }
//            //Diagonals
//            if (reduceFlow(flow, v.x - 1, v.y, v.z - 1)) {
//                new_reduceNodes.add(new Vector3i(v.x - 1, v.y, v.z - 1));
//            }
//            if (reduceFlow(flow, v.x + 1, v.y, v.z - 1)) {
//                new_reduceNodes.add(new Vector3i(v.x + 1, v.y, v.z - 1));
//            }
//            if (reduceFlow(flow, v.x - 1, v.y, v.z + 1)) {
//                new_reduceNodes.add(new Vector3i(v.x - 1, v.y, v.z + 1));
//            }
//            if (reduceFlow(flow, v.x + 1, v.y, v.z + 1)) {
//                new_reduceNodes.add(new Vector3i(v.x + 1, v.y, v.z + 1));
//            }
//        }
//        reduceNodes.clear();
//        reduceNodes.addAll(new_reduceNodes);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param flow
     * @return if we were able to set the water
     */
    public boolean setWater(int x, int y, int z, int flow) {
        Block b = GameScene.world.getBlock(x, y, z);
        if (b.id == MyGame.BLOCK_LAVA && liquidBlock.id == MyGame.BLOCK_WATER) { //If that is lava and we are water
            GameScene.player.setBlock(MyGame.BLOCK_COBBLESTONE, x, y, z);
        } else if (b.id == liquidBlock.id || isPenetrable(b)) {
            //We dont want to set something lower than the existing flow
            int existingFlow = getFlow(GameScene.world.getBlockData(x, y, z));
            flow = Math.max(flow, existingFlow);
            GameScene.player.setBlock(liquidBlock.id, new BlockData(new byte[]{(byte) flow}), x, y, z);
            return true;
        }
        return !b.solid;
    }

    private boolean reduceFlow(int sourceFlow,
                               int x, int y, int z) {
        short block = GameScene.world.getBlockID(x, y, z);
        if (block == liquidBlock.id) {
            BlockData bd = GameScene.world.getBlockData(x, y, z);
            int flow = getFlow(bd);
            //set the flow down 1
            if (flow > 0) {
                if (bd != null && bd.size() > 0) {
                    bd.set(0, (byte) (flow - 1));
                } else bd = new BlockData(new byte[]{(byte) (flow - 1)});
                GameScene.player.setBlock(liquidBlock.id, bd, x, y, z);
                return true;
            } else {
                GameScene.player.setBlock(BlockList.BLOCK_AIR.id, x, y, z);
            }
        }
        return false;
    }
}