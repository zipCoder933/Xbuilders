package com.xbuilders.game.items.blocks;


import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.block.BlockRegistry;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.game.items.Blocks;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.BiConsumer;

/**
 * @author zipCoder933
 */
public class BlockEventUtils {

    public static void makeVerticalPairedBlock(short id_top, short id_bottom) {
        Block topBlock = Registrys.getBlock(id_top);
        Block bottomBlock = Registrys.getBlock(id_bottom);

        topBlock.setBlockEvent(false, (x, y, z) -> {
            GameScene.player.setBlock(bottomBlock.id, x, y + 1, z);
        });
        topBlock.removeBlockEvent(false, (x, y, z, history) -> {
            if (GameScene.world.getBlock(x, y + 1, z) == bottomBlock) {
                GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, x, y + 1, z);
            }
        });

        bottomBlock.setBlockEvent(false, (x, y, z) -> {
            GameScene.player.setBlock(topBlock.id, x, y - 1, z);
        });
        bottomBlock.removeBlockEvent(false, (x, y, z, history) -> {
            if (GameScene.world.getBlock(x, y - 1, z) == topBlock) {
                GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, x, y - 1, z);
            }
        });
    }


    public static void setTNTEvents(Block thisBlock, final int EXPLOSTION_RADIUS, long fuseDelay) {
        thisBlock.clickEvent(true, (setX, setY, setZ) -> {
            GameScene.player.setBlock(Blocks.BLOCK_TNT_ACTIVE, setX, setY, setZ);
            try {
                Thread.sleep(fuseDelay);
                if (GameScene.world.getBlockID(setX, setY, setZ) == Blocks.BLOCK_TNT_ACTIVE) {
                    GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, setX, setY, setZ);
                    removeEverythingWithinRadius(thisBlock, EXPLOSTION_RADIUS, new Vector3i(setX, setY, setZ));



                    //Move the player away
                    Vector3f direction = new Vector3f(
                            GameScene.player.worldPosition.x - setX,
                            GameScene.player.worldPosition.y - setY,
                            GameScene.player.worldPosition.z - setZ).normalize();
                    direction = direction.mul(1f / MathUtils.dist(
                            GameScene.player.worldPosition.x,
                            GameScene.player.worldPosition.y,
                            GameScene.player.worldPosition.z, setX, setY, setZ));
                    direction.mul(50);
                    GameScene.player.positionHandler.addVelocity(direction.x, direction.y, direction.z);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void radius(final int radius, Vector3i position, BiConsumer<Vector3i, Block> cons) {
        int setX = position.x;
        int setY = position.y;
        int setZ = position.z;
        for (int x = 0 - radius; x < radius; x++) {
            for (int y = 0 - radius; y < radius; y++) {
                for (int z = 0 - radius; z < radius; z++) {
                    if (MathUtils.dist(setX, setY, setZ, setX + x, setY + y, setZ + z) < radius) {
                        Block highlightedBlock = GameScene.world.getBlock(setX + x, setY + z, setZ + y);
                        cons.accept(new Vector3i(setX + x, setY + z, setZ + y), highlightedBlock);
                    }
                }
            }
        }
    }

    public static ArrayList<Vector3i> removeEverythingWithinRadius(Block thisBlock, int size, Vector3i position) {
        int setX = position.x;
        int setY = position.y;
        int setZ = position.z;

        HashSet<Vector3i> chunks = new HashSet<>();
        ArrayList<Vector3i> explosionList = new ArrayList<>();
        for (int x = 0 - size; x < size; x++) {
            for (int y = 0 - size; y < size; y++) {
                for (int z = 0 - size; z < size; z++) {
                    if (MathUtils.dist(setX, setY, setZ, setX + x, setY + y, setZ + z) < size) {
                        chunks.add(new WCCi().set(setX + x, setY + z, setZ + y).chunk);
                        GameScene.player.setBlock(BlockRegistry.BLOCK_AIR.id, setX + x, setY + z, setZ + y);
                    }
                }
            }
        }

        ArrayList<Entity> entitiesToDelete = new ArrayList<>();
        for (Vector3i cc : chunks) {
            Chunk chunk = GameScene.world.chunks.get(cc);
            if (chunk != null) {
                for (Entity e : chunk.entities.list) {
                    if (MathUtils.dist(setX, setY, setZ, e.worldPosition.x, e.worldPosition.y, e.worldPosition.z) < size
                        /* && !(e instanceof Animal)*/) {
                        entitiesToDelete.add(e);
                    }
                }
            }
        }
        for (Entity e : entitiesToDelete) {
            e.destroy();
        }
        return explosionList;
    }

}