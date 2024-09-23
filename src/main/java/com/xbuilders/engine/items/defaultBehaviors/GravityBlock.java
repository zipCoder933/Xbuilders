package com.xbuilders.engine.items.defaultBehaviors;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.Block;

public class GravityBlock {

    public void convert(Block block) {
        block.properties.put("gravity", "true");
        block.localChangeEvent(((history, changedPosition, thisPosition) -> {
            //Get the block below this block
            Block blockBelow = GameScene.world.getBlock(thisPosition.x, thisPosition.y - 1, thisPosition.z);
            if (!blockBelow.solid) {
                System.out.println("Block below is not solid, not falling");
                return;
            }
        }));
    }
}
