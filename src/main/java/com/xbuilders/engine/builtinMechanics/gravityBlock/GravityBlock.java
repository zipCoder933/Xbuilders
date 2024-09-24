package com.xbuilders.engine.builtinMechanics.gravityBlock;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import org.joml.Vector3i;

public class GravityBlock {

    final GravityBlockEntityLink link;

    public GravityBlock(MainWindow window) {
        link = new GravityBlockEntityLink(window);
    }

    public void convert(Block block) {
        block.properties.put("gravity", "true");
        block.localChangeEvent(true, ((history, changedPosition, thisPosition) -> {
            checkFall(block, thisPosition);
        }));
        block.setBlockEvent(true, ((x, y, z) -> {
            checkFall(block, new Vector3i(x, y, z));
        }));
    }

    private void checkFall(Block block, Vector3i thisPosition) {

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //Get the block below this block
        Block blockBelow = GameScene.world.getBlock(thisPosition.x, thisPosition.y + 1, thisPosition.z);
        if (!blockBelow.solid
                && GameScene.world.getBlockID(thisPosition.x, thisPosition.y, thisPosition.z) == block.id) {
            GameScene.player.setBlock(BlockList.BLOCK_AIR.id, thisPosition.x, thisPosition.y, thisPosition.z);

            Entity e = GameScene.player.setEntity(link, thisPosition);
            GravityBlockEntity gravityBlockEntity = (GravityBlockEntity) e;
            gravityBlockEntity.block = block;

        }
    }
}
