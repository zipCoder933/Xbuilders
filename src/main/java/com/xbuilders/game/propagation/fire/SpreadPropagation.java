package com.xbuilders.game.propagation.fire;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.items.blocks.RenderType;
import org.joml.Vector3i;

import java.util.HashSet;
import java.util.Iterator;

class SpreadPropagation extends LivePropagationTask {

    Block FIRE_BLOCK = ItemList.getBlock(MyGame.BLOCK_FIRE);
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
        Main.printlnDev("fire prop nodes: " + fireNodes.size());
        Iterator<Vector3i> iterator = fireNodes.iterator();
        while (iterator.hasNext()) {
            Vector3i node = iterator.next();

            if (Math.random() > 0.5) {
                //Above 1
                lightBlock(node.x - 1,   /**/node.y, /**/node.z);
                lightBlock(node.x + 1,   /**/node.y, /**/node.z);
                lightBlock(node.x,          /**/node.y, /**/node.z - 1);
                lightBlock(node.x,          /**/node.y, /**/node.z + 1);

                //At the same height
                lightBlock(node.x - 1,   /**/node.y + 1, /**/node.z);
                lightBlock(node.x + 1,   /**/node.y + 1, /**/node.z);
                lightBlock(node.x,          /**/node.y + 1, /**/node.z - 1);
                lightBlock(node.x,          /**/node.y + 1, /**/node.z + 1);

                //Down 1
                lightBlock(node.x - 1,   /**/node.y + 2, /**/node.z);
                lightBlock(node.x + 1,   /**/node.y + 2, /**/node.z);
                lightBlock(node.x,          /**/node.y + 2, /**/node.z - 1);
                lightBlock(node.x,          /**/node.y + 2, /**/node.z + 1);
                iterator.remove();
            }
        }
    }

    private boolean isFlammable(Block block) {
        if (block.properties.containsKey("flammable")
                && block.properties.get("flammable").equals("true"))
            return true;
        return false;
    }

    private void lightBlock(int x, int y, int z) {
        if (isFlammable(GameScene.world.getBlock(x, y, z))
                && GameScene.world.getBlock(x, y - 1, z).isAir()) {
            GameScene.player.setBlock(MyGame.BLOCK_FIRE, x, y - 1, z);
            disintegrationNodes.add(new Vector3i(x, y, z));
        }
    }
}
