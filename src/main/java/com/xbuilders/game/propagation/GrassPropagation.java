package com.xbuilders.game.propagation;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.terrain.complexTerrain.ComplexTerrain;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class GrassPropagation extends LivePropagationTask {

    public GrassPropagation() {
        updateIntervalMS = 10000;
    }

    public HashSet<Vector3i> nodes = new HashSet<>();
//    List<Long> nodeSetMS = new ArrayList<>();

    public boolean addNode(Vector3i worldPos, BlockHistory hist) {
        if (isGrass(hist.newBlock.id)
                || hist.newBlock.id == MyGame.BLOCK_DIRT) {
            nodes.add(worldPos);
//            nodeSetMS.add((long) (System.currentTimeMillis() - (Math.random()*3000)));
            return true;
        }
        return false;
    }


    @Override
    public void update() {
        if (nodes.isEmpty()) {
            return;
        }
        Main.printlnDev("grass prop nodes: " + nodes.size());

        for (Vector3i node : nodes) {
            short thisBlock = GameScene.world.getBlockID(node.x, node.y, node.z);
            Block aboveBlock = GameScene.world.getBlock(node.x, node.y - 1, node.z);

            if (thisBlock == MyGame.BLOCK_DIRT && !aboveBlock.solid) {
                GameScene.player.setBlock(
                        getGrassBlockOfBiome(node.x, node.y, node.z),
                        node.x, node.y, node.z);
            } else if (isGrass(thisBlock) && aboveBlock.solid) {
                GameScene.player.setBlock(MyGame.BLOCK_DIRT, node.x, node.y, node.z);
            }
        }
        nodes.clear();
    }

    private short getGrassBlockOfBiome(int wx, int wy, int wz) {
        int biome = GameScene.world.terrain.getBiomeOfVoxel(wx, wy, wz);
        switch (biome) {
            case ComplexTerrain.BIOME_DEFAULT -> {
                return MyGame.BLOCK_GRASS;
            }
            case ComplexTerrain.BIOME_SNOWY -> {
                return MyGame.BLOCK_SNOW_GRASS;
            }
            case ComplexTerrain.BIOME_JUNGLE -> {
                return MyGame.BLOCK_JUNGLE_GRASS;
            }
            case ComplexTerrain.BIOME_SAVANNAH -> {
                return MyGame.BLOCK_DRY_GRASS;
            }
            case ComplexTerrain.BIOME_DESERT -> {
                return MyGame.BLOCK_DRY_GRASS;
            }
            default -> {
                return MyGame.BLOCK_GRASS;
            }
        }
    }

    private boolean isGrass(short thisBlock) {
        return thisBlock == MyGame.BLOCK_GRASS ||
                thisBlock == MyGame.BLOCK_SNOW_GRASS ||
                thisBlock == MyGame.BLOCK_JUNGLE_GRASS ||
                thisBlock == MyGame.BLOCK_DRY_GRASS;
    }
}
