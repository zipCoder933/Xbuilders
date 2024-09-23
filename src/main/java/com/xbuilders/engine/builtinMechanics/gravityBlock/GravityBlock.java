package com.xbuilders.engine.builtinMechanics.gravityBlock;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;

public class GravityBlock {

    final static GravityBlockEntityLink link = new GravityBlockEntityLink();

    public void convert(Block block) {
        block.properties.put("gravity", "true");
        block.localChangeEvent(((history, changedPosition, thisPosition) -> {
            //Get the block below this block
            Block blockBelow = GameScene.world.getBlock(thisPosition.x, thisPosition.y - 1, thisPosition.z);
            if (!blockBelow.solid) {
                System.out.println("Block below is not solid, not falling");
                GameScene.player.setEntity(link, thisPosition);
                return;
            }
        }));
    }
}
