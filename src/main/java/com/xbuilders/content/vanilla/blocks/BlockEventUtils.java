package com.xbuilders.content.vanilla.blocks;


import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.block.BlockRegistry;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.Registrys;
import com.xbuilders.engine.server.block.Block;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.wcc.WCCi;
import com.xbuilders.content.vanilla.Blocks;
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
            Server.setBlock(bottomBlock.id, x, y + 1, z);
        });
        topBlock.removeBlockEvent(false, (x, y, z, history) -> {
            if (Server.world.getBlock(x, y + 1, z) == bottomBlock) {
                Server.setBlock(BlockRegistry.BLOCK_AIR.id, x, y + 1, z);
            }
        });

        bottomBlock.setBlockEvent(false, (x, y, z) -> {
            Server.setBlock(topBlock.id, x, y - 1, z);
        });
        bottomBlock.removeBlockEvent(false, (x, y, z, history) -> {
            if (Server.world.getBlock(x, y - 1, z) == topBlock) {
                Server.setBlock(BlockRegistry.BLOCK_AIR.id, x, y - 1, z);
            }
        });
    }


    public static void setTNTEvents(Block thisBlock, final int radius, long fuseDelay) {
        thisBlock.clickEvent(true, (setX, setY, setZ) -> {
            Server.setBlock(Blocks.BLOCK_TNT_ACTIVE, setX, setY, setZ);
            try {
                Thread.sleep(fuseDelay);
                if (Server.world.getBlockID(setX, setY, setZ) == Blocks.BLOCK_TNT_ACTIVE) {
                    Server.setBlock(BlockRegistry.BLOCK_AIR.id, setX, setY, setZ);
                    removeEverythingWithinRadius(thisBlock, radius, new Vector3i(setX, setY, setZ));
                    float dist = GameScene.userPlayer.worldPosition.distance(setX, setY, setZ);
                    if (dist < radius) {
                        GameScene.userPlayer.addHealth(
                                MathUtils.mapAndClamp(dist, radius, 0, 0, -10));
                    }


                    //Move the player away
                    Vector3f direction = new Vector3f(
                            GameScene.userPlayer.worldPosition.x - setX,
                            GameScene.userPlayer.worldPosition.y - setY,
                            GameScene.userPlayer.worldPosition.z - setZ).normalize();
                    direction = direction.mul(1f / MathUtils.dist(
                            GameScene.userPlayer.worldPosition.x,
                            GameScene.userPlayer.worldPosition.y,
                            GameScene.userPlayer.worldPosition.z, setX, setY, setZ));
                    direction.mul(50);
                    GameScene.userPlayer.positionHandler.addVelocity(direction.x, direction.y, direction.z);
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
                        Block highlightedBlock = Server.world.getBlock(setX + x, setY + z, setZ + y);
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
                        Server.setBlock(BlockRegistry.BLOCK_AIR.id, setX + x, setY + z, setZ + y);
                    }
                }
            }
        }

        ArrayList<Entity> entitiesToDelete = new ArrayList<>();
        for (Vector3i cc : chunks) {
            Chunk chunk = Server.world.chunks.get(cc);
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