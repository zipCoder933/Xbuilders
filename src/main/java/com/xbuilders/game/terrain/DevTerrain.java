/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.terrain;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.Chunk;

import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.game.MyGame;

import java.util.Random;

/**
 * @author zipCoder933
 */
public class DevTerrain extends Terrain {

    public DevTerrain() {
        super("Dev Terrain");
        MIN_HEIGHT = 70;
        MAX_HEIGHT = 140;
    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {
//        System.out.println("Generating Dev Terrain");
        for (int cx = 0; cx < WIDTH; cx++) {
            for (int cy = 0; cy < WIDTH; cy++) {
                for (int cz = 0; cz < WIDTH; cz++) {

                    int wy = cy + (chunk.position.y * Chunk.WIDTH);
                    int heightmap = 200;

                    if (wy >= heightmap) {
                        if (MiscUtils.isBlackCube(chunk.position.x, chunk.position.y, chunk.position.z)) {
                            chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_SAND.id);
                        } else chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_STONE.id);
                    }
                }
            }
        }
    }

}
