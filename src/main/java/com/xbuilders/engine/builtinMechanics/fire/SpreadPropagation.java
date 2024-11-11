package com.xbuilders.engine.builtinMechanics.fire;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.game.items.Blocks;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Iterator;

class SpreadPropagation extends LivePropagationTask {

    Block FIRE_BLOCK = Registrys.getBlock(Blocks.BLOCK_FIRE);
    HashSet<Vector3i> fireNodes = new HashSet<>();
    HashSet<Vector3i> disintegrationNodes;

    public SpreadPropagation(int updateMS, HashSet<Vector3i> disintegrationNodes) {
        super();
        updateIntervalMS = updateMS;
        this.disintegrationNodes = disintegrationNodes;
    }

    @Override
    public boolean addNode(Vector3i worldPos, BlockHistory history) {
        if (history.newBlock.id == FIRE_BLOCK.id) {
            fireNodes.add(worldPos);
            return true;
        }
        return false;
    }


    @Override
    public void update() {
        if (fireNodes.isEmpty()) return;
        MainWindow.printlnDev("fire prop nodes: " + fireNodes.size());
        Iterator<Vector3i> iterator = fireNodes.iterator();
        while (iterator.hasNext()) {
            Vector3i node = iterator.next();
            iterator.remove();

            if (Math.random() > 0.5) {
                //Above 1
                lightBlock(node.x - 1,   /**/node.y, /**/node.z);
                lightBlock(node.x + 1,   /**/node.y, /**/node.z);
                lightBlock(node.x,          /**/node.y, /**/node.z - 1);
                lightBlock(node.x,          /**/node.y, /**/node.z + 1);
            }
            if (Math.random() > 0.5) {
                //At the same height
                lightBlock(node.x - 1,   /**/node.y + 1, /**/node.z);
                lightBlock(node.x + 1,   /**/node.y + 1, /**/node.z);
                lightBlock(node.x,          /**/node.y + 1, /**/node.z - 1);
                lightBlock(node.x,          /**/node.y + 1, /**/node.z + 1);
            }
            if (Math.random() > 0.5) {
                //Down 1
                lightBlock(node.x - 1,   /**/node.y + 2, /**/node.z);
                lightBlock(node.x + 1,   /**/node.y + 2, /**/node.z);
                lightBlock(node.x,          /**/node.y + 2, /**/node.z - 1);
                lightBlock(node.x,          /**/node.y + 2, /**/node.z + 1);
            }

            //Get the block at this node
            Block block = GameScene.world.getBlock(node.x, node.y+1, node.z);
            //If it is not solid, remove it
            if (!block.solid && isFlammable(block)) {
                GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, node.x, node.y, node.z);
                GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, node.x, node.y+1, node.z);
            }else if (!isFlammable(block)) {
                GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, node.x, node.y, node.z);
            }

        }
    }

    private boolean isFlammable(Block block) {
        if (block.properties.containsKey("flammable")) {
            return true;
        }
        return false;
    }

    private boolean lightBlock(int x, int y, int z) {
        if (isFlammable(GameScene.world.getBlock(x, y, z))
                && GameScene.world.getBlock(x, y - 1, z).isAir()) {
            GameScene.player.setBlock(Blocks.BLOCK_FIRE, x, y - 1, z);
            disintegrationNodes.add(new Vector3i(x, y, z));
            return true;
        }
        return false;
    }
}
