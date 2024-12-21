package com.xbuilders.engine.game.model.builtinMechanics.liquid;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.LivePropagationTask;
import com.xbuilders.engine.game.model.items.block.BlockRegistry;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.game.model.players.pipeline.BlockHistory;
import com.xbuilders.engine.game.model.world.chunk.BlockData;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.content.vanilla.items.Blocks;
import com.xbuilders.content.vanilla.items.blocks.RenderType;
import org.joml.Vector3i;

import java.util.HashSet;


public class LiquidPropagationTask extends LivePropagationTask {

    public final Block liquidBlock;
    public HashSet<Vector3i> nodes = new HashSet<>();

    public LiquidPropagationTask(Block liquidBlock, int updateIntervalMS) {
        this.updateIntervalMS = updateIntervalMS;
        this.liquidBlock = liquidBlock;
    }

    public boolean addNode(Vector3i worldPos, BlockHistory hist) {
        if (hist.newBlock.id == liquidBlock.id) {
            nodes.add(worldPos);
            return true;
        }
        return false;
    }

    private boolean addNode(HashSet<Vector3i> nodes, Vector3i worldPos) {
        if (GameScene.world.getBlockID(worldPos.x, worldPos.y, worldPos.z) == liquidBlock.id) {
            nodes.add(worldPos);
            return true;
        }
        return false;
    }

    public boolean isPenetrable(Block block) {
        return block.isAir() || (!block.solid && block.renderType == RenderType.SPRITE && block.toughness < 0.5f);
    }

    public static int getFlow(BlockData thisBD, int nullFlow) {
        return (thisBD != null && thisBD.size() > 0) ? thisBD.get(0) : nullFlow;
    }

    /**
     * maxFlow+1 = source water
     * maxFlow = down-flowing water
     * > maxFlow = sideways-flowing water
     */
    @Override
    public void update() {
        if (nodes.isEmpty()) {
            return;
        }
        HashSet<Vector3i> newNodes = new HashSet<>();
        MainWindow.printlnDev(liquidBlock.alias + " prop nodes: " + nodes.size());
        final int SOURCE_FLOW = liquidBlock.liquidMaxFlow + 1; //The source block is block max flow + 1

        for (Vector3i v : nodes) {
            //Get the flow from this node


            int thisFlow = getFlow(GameScene.world.getBlockData(v.x, v.y, v.z), 0);


            /**
             * FLOW DEPROPAGATION
             */
            if (thisFlow == liquidBlock.liquidMaxFlow && //If we are 100% flowing
                    GameScene.world.getBlockID(v.x, v.y - 1, v.z) != liquidBlock.id && //and there is nothing above
                    getFlow(GameScene.world.getBlockData(v.x - 1, v.y, v.z), 0) != SOURCE_FLOW &&//and there is no neighboring source
                    getFlow(GameScene.world.getBlockData(v.x + 1, v.y, v.z), 0) != SOURCE_FLOW &&
                    getFlow(GameScene.world.getBlockData(v.x, v.y, v.z - 1), 0) != SOURCE_FLOW &&
                    getFlow(GameScene.world.getBlockData(v.x, v.y, v.z + 1), 0) != SOURCE_FLOW
            ) {
                Block below = GameScene.world.getBlock(v.x, v.y + 1, v.z);
                if (below.solid) reduceFlow(newNodes, v.x, v.y, v.z);
                else GameScene.setBlock(BlockRegistry.BLOCK_AIR.id, v.x, v.y, v.z);
                continue;
            } else if (thisFlow < liquidBlock.liquidMaxFlow && //If we are flowing sideways
                    !(getFlow(GameScene.world.getBlockData(v.x - 1, v.y, v.z), 0) > thisFlow || //and there is no neighboring value higher than us
                            getFlow(GameScene.world.getBlockData(v.x + 1, v.y, v.z), 0) > thisFlow ||
                            getFlow(GameScene.world.getBlockData(v.x, v.y, v.z - 1), 0) > thisFlow ||
                            getFlow(GameScene.world.getBlockData(v.x, v.y, v.z + 1), 0) > thisFlow)
            ) {
                reduceFlow(newNodes, v.x, v.y, v.z);
            }


            /**
             * FLOW PROPAGATION
             */
            if (setWater(v.x, v.y + 1, v.z, liquidBlock.liquidMaxFlow)) {
            } else if (thisFlow > 0) {
                setWater(v.x - 1, v.y, v.z, thisFlow - 1);
                setWater(v.x + 1, v.y, v.z, thisFlow - 1);
                setWater(v.x, v.y, v.z - 1, thisFlow - 1);
                setWater(v.x, v.y, v.z + 1, thisFlow - 1);
            }

        }
        nodes.clear();
        nodes.addAll(newNodes);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param flow
     * @return if we were able to set the water
     */
    public boolean setWater(int x, int y, int z, int flow) {
        Block existingBlock = GameScene.world.getBlock(x, y, z);

        if (existingBlock.id == Blocks.BLOCK_LAVA && liquidBlock.id == Blocks.BLOCK_WATER) { //If that is lava and we are water
            GameScene.setBlock(Blocks.BLOCK_COBBLESTONE, x, y, z);
        } else if (existingBlock.id == liquidBlock.id || isPenetrable(existingBlock)) {
            int existingFlow = getFlow(GameScene.world.getBlockData(x, y, z), 0);
            flow = Math.max(flow, existingFlow); //We dont want to set something lower than the existing flow
            GameScene.setBlock(liquidBlock.id, new BlockData(new byte[]{(byte) flow}), x, y, z);
            return true;
        }
        return !existingBlock.solid;
    }

    private boolean reduceFlow(HashSet<Vector3i> nodes, int x, int y, int z) {
        short block = GameScene.world.getBlockID(x, y, z);
        if (block == liquidBlock.id) {
            BlockData bd = GameScene.world.getBlockData(x, y, z);
            int flow = getFlow(bd, 0);

            if (flow > 0) {
                if (bd != null && bd.size() > 0) {   //set the flow down 1
                    bd.set(0, (byte) (flow - 1));
                } else bd = new BlockData(new byte[]{(byte) (flow - 1)});
                GameScene.setBlock(liquidBlock.id, bd, x, y, z);

/**
 *     the block event pipeline doesnt key local events for block data changes,so we need to add the nodes from
 *     these events ourselves
 */
                addNode(nodes, new Vector3i(x, y, z));
                addNode(nodes, new Vector3i(x + 1, y, z));
                addNode(nodes, new Vector3i(x - 1, y, z));
                addNode(nodes, new Vector3i(x, y, z + 1));
                addNode(nodes, new Vector3i(x, y, z - 1));

                return true;
            } else {
                GameScene.setBlock(BlockRegistry.BLOCK_AIR.id, x, y, z);
            }
        }
        return false;
    }
}