package com.xbuilders.engine.server.model.builtinMechanics.gravityBlock;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.server.model.GameScene;
import com.xbuilders.engine.server.model.items.block.BlockRegistry;
import com.xbuilders.engine.server.model.items.block.Block;
import com.xbuilders.engine.server.model.items.entity.Entity;
import com.xbuilders.engine.server.model.items.entity.EntitySupplier;
import com.xbuilders.engine.server.model.world.World;
import org.joml.Vector3i;


public class GravityBlock {

    EntitySupplier entitySupplier;

    public GravityBlock(MainWindow window) {
        entitySupplier = new EntitySupplier(0, (uniqueIdentifier) -> new GravityBlockEntity(uniqueIdentifier, window));
    }

    public void convert(Block block) {
        block.properties.put("gravity", "true");
        block.localChangeEvent(false, ((history, changedPosition, thisPosition) -> {
            checkFall(block, thisPosition);
        }));
        block.setBlockEvent(false, ((x, y, z) -> {
            checkFall(block, new Vector3i(x, y, z));
        }));
    }

    private void checkFall(Block block, Vector3i thisPosition) {
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        /**
         * There are 2 ways we can make the blocks fall
         * 1. Wait Xms in another thread tham create an entity
         *  - This one has the advantage of producing a much longer chain reaction
         *  - The blocks dont always fall reliably and sometimes the result looks different than expected
         * 2. Create the entity but wait Xms to start moving it
         *  - This one allows us to have a more predictable result
         *  - But the block pipeline places a restriction on how many frames can sustain a conntinuous chain reaction of block events
         *      - The threaded version gets around this by dispatching block events over frames at times when there are no block events
         */

        //Get the block below this block
        Block blockBelow = GameScene.world.getBlock(thisPosition.x, thisPosition.y + 1, thisPosition.z);
        if (!blockBelow.solid
                && GameScene.world.getBlockID(thisPosition.x, thisPosition.y, thisPosition.z) == block.id) {
            GameScene.setBlock(BlockRegistry.BLOCK_AIR.id, thisPosition.x, thisPosition.y, thisPosition.z);

            //Under certain conditions, we immediately move the block to the bottom
            if (thisPosition.distance(
                    (int) GameScene.player.worldPosition.x,
                    (int) GameScene.player.worldPosition.y,
                    (int) GameScene.player.worldPosition.z) >= Math.min(50, MainWindow.settings.video_entityDistance.value)) {

                //Set the block at the bottom
                for (int y = thisPosition.y + 1; y < World.WORLD_BOTTOM_Y; y++) {
                    blockBelow = GameScene.world.getBlock(thisPosition.x, y, thisPosition.z);
                    if (blockBelow.solid) {
                        GameScene.setBlock(block.id, thisPosition.x, y - 1, thisPosition.z);
                        break;
                    }
                }
                return;
            }
            Entity e = GameScene.world.placeEntity(entitySupplier, thisPosition, null);
            GravityBlockEntity gravityBlockEntity = (GravityBlockEntity) e;
            gravityBlockEntity.block = block;
        }
    }
}