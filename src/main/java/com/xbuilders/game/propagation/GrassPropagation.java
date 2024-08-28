package com.xbuilders.game.propagation;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.gameScene.LivePropagationTask;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.player.pipeline.BlockHistory;
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import com.xbuilders.game.terrain.complexTerrain.ComplexTerrain;
import org.joml.Vector3i;

import java.util.*;


public class GrassPropagation extends LivePropagationTask {

    final int UPDATE_INTERVAL = 10 * 1000;

    public GrassPropagation() {
        updateIntervalMS = UPDATE_INTERVAL;
    }

    public HashMap<Vector3i, Long> nodes = new HashMap<>();

    public boolean addNode(Vector3i worldPos, BlockHistory hist) {
        if (isGrass(hist.newBlock.id)
                || hist.newBlock.id == MyGame.BLOCK_DIRT) {
            long setTime = (long) (System.currentTimeMillis() + (Math.random() * 5000));
            nodes.put(worldPos, setTime);
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

        //Iterator is better than entrySet. Entryset creates a copy
        Iterator<Map.Entry<Vector3i, Long>> iterator = nodes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Vector3i, Long> entry = iterator.next();
            Vector3i node = entry.getKey();
            long setTime = entry.getValue();

            short thisBlock = GameScene.world.getBlockID(node.x, node.y, node.z);
            Block aboveBlock = GameScene.world.getBlock(node.x, node.y - 1, node.z);

            if (System.currentTimeMillis() - setTime > UPDATE_INTERVAL / 2) { //If it's been 10 seconds since we last set the block
                if (thisBlock == MyGame.BLOCK_DIRT && !aboveBlock.solid) {
                    GameScene.player.setBlock(
                            getGrassBlockOfBiome(node.x, node.y, node.z),
                            node.x, node.y, node.z);
                } else if (isGrass(thisBlock) && aboveBlock.solid) {
                    GameScene.player.setBlock(MyGame.BLOCK_DIRT, node.x, node.y, node.z);
                }
                iterator.remove(); // remove the entry from the map
            }
        }
    }

    private short getGrassBlockOfBiome(int wx, int wy, int wz) {
        int biome = GameScene.world.terrain.getBiomeOfVoxel(wx, wy, wz);
        switch (biome) {
            case ComplexTerrain.BIOME_SNOWY -> {
                return MyGame.BLOCK_SNOW_GRASS;
            }
            case ComplexTerrain.BIOME_JUNGLE -> {
                return MyGame.BLOCK_JUNGLE_GRASS;
            }
            case ComplexTerrain.BIOME_SAVANNAH, ComplexTerrain.BIOME_DESERT -> {
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
