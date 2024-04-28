package com.xbuilders.game.items.blocks;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockTexture;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.game.MyGame;

public class Plant extends Block {

    public Plant(int id, String name, BlockTexture texture, Block... stages) {
        super(id, name, texture);
        solid = false;
        opaque = false;
        this.type = RenderType.SPRITE;

        if (stages.length > 0) {
            setBlockEvent(true, (x, y, z, data) -> {
                boolean wasSet = false;
                if (cropPlantable(x, y, z)) {
                    growPlant(2200, x, y, z, this, stages);
                }
            });
        }
    }

    public static void growPlant(long growSpeed, final int x, final int y, final int z,
                                 final Block initialSeed, final Block... stages) {

        Block lastStage = initialSeed;

        int i = 0;
        try {
            for (Block stage : stages) {
                Thread.sleep(growSpeed);
                if (GameScene.world.getBlock(x, y, z) == lastStage
                        && cropPlantable(x, y, z)) {
                    GameScene.player.setBlock(stage, x, y, z);
                    lastStage = stage;
                } else {
                    if (i == 0) {
                        GameScene.player.setBlock(BlockList.BLOCK_AIR, x, y, z);
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
        return GameScene.world.getBlock(x, y + 1, z) == MyGame.BLOCK_FARMLAND;
    }

    public static boolean plantable(final int x, final int y, final int z) {
        return blockIsGrassSnowOrDirt(GameScene.world.getBlock(x, y + 1, z));
    }

    public static boolean blockIsGrassSnowOrDirt(Block block) {
        return block == MyGame.BLOCK_FARMLAND
                || block == MyGame.BLOCK_DIRT
                || block == MyGame.BLOCK_GRASS
                || block == MyGame.BLOCK_SNOW
                || block == MyGame.BLOCK_JUNGLE_GRASS
                || block == MyGame.BLOCK_DRY_GRASS;
    }


}