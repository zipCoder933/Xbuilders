/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.terrain;

import com.xbuilders.engine.world.chunk.Chunk;

import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;

import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.game.vanilla.items.Blocks;

import java.util.HashMap;

/**
 * @author zipCoder933
 */
public class DevTerrain extends Terrain {

    public DevTerrain() {
        super("Dev Terrain");
        MIN_SURFACE_HEIGHT = 195;
        MAX_SURFACE_HEIGHT = 201;
    }

    @Override
    public void loadWorld(HashMap<String, Boolean> options, int version) {

    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {
        int wy;
        int heightmap = 200;

        for (int cy = 0; cy < WIDTH; cy++) {
            wy = cy + (chunk.position.y * Chunk.WIDTH);

            for (int cx = 0; cx < WIDTH; cx++) {
                for (int cz = 0; cz < WIDTH; cz++) {
                    if (wy >= heightmap) {
                        if (MiscUtils.isBlackCube(chunk.position.x, chunk.position.y, chunk.position.z)) {
                            chunk.data.setBlock(cx, cy, cz, Blocks.BLOCK_SANDSTONE);
                        } else chunk.data.setBlock(cx, cy, cz, Blocks.BLOCK_STONE);
                    }
                }
            }
        }
    }

}
