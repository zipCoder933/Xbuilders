package com.xbuilders.engine.server.model.builtinMechanics.fire;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.model.Server;
import com.xbuilders.engine.server.model.LivePropagationTask;
import com.xbuilders.engine.server.model.items.block.BlockRegistry;
import com.xbuilders.engine.server.model.items.Registrys;
import com.xbuilders.engine.server.model.items.block.Block;
import com.xbuilders.engine.server.model.players.pipeline.BlockHistory;
import com.xbuilders.content.vanilla.items.Blocks;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Iterator;

class DisintegrationPropagation extends LivePropagationTask {

    Block FIRE_BLOCK = Registrys.getBlock(Blocks.BLOCK_FIRE);
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
        ClientWindow.printlnDev("fire dis nodes: " + disintegrationNodes.size());
        Iterator<Vector3i> iterator = disintegrationNodes.iterator();
        while (iterator.hasNext()) {
            Vector3i node = iterator.next();
            if (Math.random() > 0.5) {
                if (Server.world.getBlock(node.x, node.y - 1, node.z).id == FIRE_BLOCK.id) {
                    Server.setBlock(BlockRegistry.BLOCK_AIR.id, node.x, node.y, node.z);
                    Server.setBlock(BlockRegistry.BLOCK_AIR.id, node.x, node.y - 1, node.z);
                    iterator.remove();
                }
            }
        }
    }
}
