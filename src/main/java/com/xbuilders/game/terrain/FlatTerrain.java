/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.terrain;

import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.MyGame;

import java.util.HashMap;

import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;

/**
 * @author zipCoder933
 */
public class FlatTerrain extends Terrain {

    public FlatTerrain() {
        super("Flat Terrain");
        MIN_SURFACE_HEIGHT = 200;
        MAX_SURFACE_HEIGHT = 200;
    }
    public FlatTerrain(int surfaceHeight) {
        super("Custom Flat Terrain");
        MIN_SURFACE_HEIGHT = surfaceHeight;
        MAX_SURFACE_HEIGHT = surfaceHeight;
    }

    @Override
    public void loadWorld(HashMap<String, Boolean> options, int version) {

    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {
        for (int cx = 0; cx < WIDTH; cx++) {
            for (int cy = 0; cy < WIDTH; cy++) {
                for (int cz = 0; cz < WIDTH; cz++) {

                    int wy = cy + (chunk.position.y * Chunk.WIDTH);

                    if (wy == MAX_SURFACE_HEIGHT) {
                        chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_GRASS);
                    } else if (wy > MAX_SURFACE_HEIGHT + 10) {
                        chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_STONE);
                    } else if (wy > MAX_SURFACE_HEIGHT) {
                        chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_DIRT);
                    }
                }
            }
        }
    }

}
