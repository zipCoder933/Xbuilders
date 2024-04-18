package com.xbuilders.game.items.blocks;


import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import com.xbuilders.game.MyGame;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author zipCoder933
 */
public class TNTUtils {

    public static void startTNT(Block thisBlock, final int EXPLOSTION_RADIUS, long fuseDelay, int setX, int setY, int setZ) {
        try {
            GameScene.player.setBlock(MyGame.BLOCK_TNT_ACTIVE, setX, setY, setZ);
            Thread.sleep(fuseDelay);
            if (GameScene.world.getBlock(setX, setY, setZ) == MyGame.BLOCK_TNT_ACTIVE) {
                TNTUtils.removeBlock(setX, setY, setZ);
                HashSet<Vector3i> explosionList = new HashSet<>();
                explosionList.addAll(explosion(thisBlock, EXPLOSTION_RADIUS, new Vector3i(setX, setY, setZ)));
                System.out.println("Finished with explosion of size " + EXPLOSTION_RADIUS + ". New explosions: " + explosionList.size());
                while (!explosionList.isEmpty()) {
                    ArrayList<Vector3i> explosionList2 = new ArrayList<>();
                    for (Vector3i pos : explosionList) {
                        if (GameScene.world.getBlock(pos.x, pos.y, pos.z) == thisBlock) {
                            GameScene.player.setBlock(MyGame.BLOCK_TNT_ACTIVE, pos.x, pos.y, pos.z);
                        }
                    }
                    Thread.sleep((long) (fuseDelay * 0.6f));
                    for (Vector3i pos : explosionList) {
                        if (GameScene.world.getBlock(pos.x, pos.y, pos.z) == MyGame.BLOCK_TNT_ACTIVE) {
                            Thread.sleep(300);
                            explosionList2.addAll(TNTUtils.explosion(thisBlock, EXPLOSTION_RADIUS, pos));
                        }
                    }
                    explosionList.clear();
                    System.out.println("Adding " + explosionList2.size() + " new explosions to list.");
                    explosionList.addAll(explosionList2);
                }
            }
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public static void removeBlock(int x, int y, int z) {
        GameScene.player.setBlock(BlockList.BLOCK_AIR, x, y, z);
    }

    public static ArrayList<Vector3i> explosion(Block thisBlock, final int size, Vector3i position) throws InterruptedException {
        removeBlock(position.x, position.y, position.z);
        boolean large = size > 5;
        if (!GameScene.world.getBlock(position.x, position.y - 1, position.z).liquid) {

            if (large) {
                removeBlocksWithinRadius(size - 2, position);
                Thread.sleep(200);
            }

//            ph.getWorld().hologramList.add(new ExplosionHologram( new Vector3f(position), large));
            return removeEverythingWithinRadius(thisBlock, size, position);
        }
        return new ArrayList<>();
    }

    public static void removeBlocksWithinRadius(final int radius, Vector3i position) {
        int setX = position.x;
        int setY = position.y;
        int setZ = position.z;
        for (int x = 0 - radius; x < radius; x++) {
            for (int y = 0 - radius; y < radius; y++) {
                for (int z = 0 - radius; z < radius; z++) {
                    if (MathUtils.dist(setX, setY, setZ, setX + x, setY + y, setZ + z) < radius) {
                        Block highlightedBlock = GameScene.world.getBlock(setX + x, setY + z, setZ + y);
                        if (isPenetrable(highlightedBlock)) {
                            removeBlock(setX + x, setY + z, setZ + y);
                        }
                    }
                }
            }
        }
    }

    /**
     * @param ph        pointer handler
     * @param thisBlock this TNT block type
     * @param position  the center-point of the explosion
     * @param size      the radius of the explosion
     * @return the TNT blocks found within the radius (as checked by thisBlock)
     */
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
                        Block highlightedBlock = GameScene.world.getBlock(setX + x, setY + z, setZ + y);
                        if (isPenetrable(highlightedBlock)) {
                            chunks.add(new WCCi().set(setX + x, setY + z, setZ + y).chunk);
                            removeBlock(setX + x, setY + z, setZ + y);
                        } else if (highlightedBlock == thisBlock) {
                            explosionList.add(new Vector3i(setX + x, setY + z, setZ + y));
                        }
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

    private static boolean isPenetrable(Block block) {
        return !block.isAir()
                && block != MyGame.BLOCK_TNT
                && block != MyGame.BLOCK_MEGA_TNT
                && block != MyGame.BLOCK_TNT_ACTIVE
                && block != MyGame.BLOCK_BEDROCK;
    }

}