package com.xbuilders.engine.builtinMechanics.fire;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Iterator;

class DisintegrationPropagation extends LivePropagationTask {

    Block FIRE_BLOCK = Registrys.getBlock(MyGame.BLOCK_FIRE);
    HashSet<Vector3i> disintegrationNodes = new HashSet<>();

    @Override
    public boolean addNode(Vector3i worldPos, BlockHistory history) {
//        if (history.newBlock.id == FIRE_BLOCK.id) {
//            disintegrationNodes.add(worldPos);
//            return true;
//        }
        return false;
    }

    public DisintegrationPropagation(int updateMS) {
        super();
        updateIntervalMS = updateMS;
    }


    @Override
    public void update() {
        if (disintegrationNodes.isEmpty()) return;
        MainWindow.printlnDev("fire dis nodes: " + disintegrationNodes.size());
        Iterator<Vector3i> iterator = disintegrationNodes.iterator();
        while (iterator.hasNext()) {
            Vector3i node = iterator.next();
            if (Math.random() > 0.5) {
                if (GameScene.world.getBlock(node.x, node.y - 1, node.z).id == FIRE_BLOCK.id) {
                    GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, node.x, node.y, node.z);
                    GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, node.x, node.y - 1, node.z);
                    iterator.remove();
                }
            }
        }
    }
}
