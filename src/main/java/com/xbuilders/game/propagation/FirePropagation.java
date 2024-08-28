package com.xbuilders.game.propagation;

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

public class FirePropagation extends LivePropagationTask {

    Block FIRE_BLOCK = ItemList.getBlock(MyGame.BLOCK_FIRE);
    HashSet<Vector3i> nodes = new HashSet<>();

    @Override
    public boolean addNode(Vector3i worldPos, BlockHistory history) {
        if (history.newBlock.id == FIRE_BLOCK.id) {
            nodes.add(worldPos);
            return true;
        }
        return false;
    }

    public FirePropagation() {
        super();
        updateIntervalMS = 5000;
    }


    @Override
    public void update() {
        if(nodes.isEmpty())return;
        Main.printlnDev("fire propagation nodes: " + nodes.size());
        Iterator<Vector3i> iterator = nodes.iterator();
        while (iterator.hasNext()) {
            Vector3i node = iterator.next();

            lightBlock(node.x-1, node.y-1, node.z);
            lightBlock(node.x+1, node.y-1, node.z);
            lightBlock(node.x, node.y-1, node.z-1);
            lightBlock(node.x, node.y-1, node.z+1);

            lightBlock(node.x-1, node.y+1, node.z);
            lightBlock(node.x+1, node.y+1, node.z);
            lightBlock(node.x, node.y+1, node.z-1);
            lightBlock(node.x, node.y+1, node.z+1);

            iterator.remove();
        }
    }

    private boolean isFlammable(Block block) {
        if (block.properties.containsKey("flammable") && block.properties.get("flammable").equals("true"))
            return true;

        //else if the block is a sprite, or has a name that contains wood, leaves, grass, or planks
        if (block.renderType == RenderType.SPRITE
                || block.name.toLowerCase().contains("wood")
                || block.name.toLowerCase().contains("leaves")
                || block.name.toLowerCase().contains("grass")
                || block.name.toLowerCase().contains("planks")) {
            return true;
        }

        return false;
    }

    private void lightBlock(int x, int y, int z) {
        if (isFlammable(GameScene.world.getBlock(x, y, z))
                && GameScene.world.getBlock(x, y - 1, z).isAir()) {
            GameScene.player.setBlock(MyGame.BLOCK_FIRE, x, y - 1, z);
        }
    }
}
