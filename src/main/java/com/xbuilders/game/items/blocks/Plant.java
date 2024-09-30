package com.xbuilders.game.items.blocks;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.game.MyGame;

public class Plant {

    public static void makePlant(Block block, short... stages) {
        block.solid = false;
        block.opaque = false;
        block.renderType = RenderType.SPRITE;

        if (stages.length > 0) {
            block.setBlockEvent(true,(x, y, z) -> {
                if (cropPlantable(x, y, z)) {
                    growPlant(15000, x, y, z, block.id, stages);
                }
            });
        }
    }

    public static void growPlant(long growSpeed, final int x, final int y, final int z,
                                 final short initialSeed, final short... stages) {

        short lastStage = initialSeed;

        int i = 0;
        try {
            for (short stage : stages) {
                Thread.sleep(growSpeed);
                if (GameScene.world.getBlockID(x, y, z) == lastStage
                        && cropPlantable(x, y, z)) {
                    GameScene.player.setBlock(stage, x, y, z);
                    lastStage = stage;
                } else {
                    if (i == 0) {
                        GameScene.player.setBlock(BlockList.BLOCK_AIR.id, x, y, z);
                    }
                    return;
                }
                i++;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean deepPlantable(final int x, final int y, final int z) {
        boolean val = blockIsGrassSnowOrDirt(GameScene.world.getBlock(x, y + 1, z))
                && blockIsGrassSnowOrDirt(GameScene.world.getBlock(x, y + 2, z));
        return val;
    }

    public static boolean cropPlantable(final int x, final int y, final int z) {
        return GameScene.world.getBlockID(x, y + 1, z) == MyGame.BLOCK_FARMLAND;
    }

    public static boolean plantable(final int x, final int y, final int z) {
        return blockIsGrassSnowOrDirt(GameScene.world.getBlock(x, y + 1, z));
    }

    public static boolean blockIsGrassSnowOrDirt(Block block) {
        return block.id == MyGame.BLOCK_FARMLAND
                || block.id == MyGame.BLOCK_DIRT
                || block.id == MyGame.BLOCK_GRASS
                || block.id == MyGame.BLOCK_SNOW_GRASS
                || block.id == MyGame.BLOCK_JUNGLE_GRASS
                || block.id == MyGame.BLOCK_DRY_GRASS;
    }


}