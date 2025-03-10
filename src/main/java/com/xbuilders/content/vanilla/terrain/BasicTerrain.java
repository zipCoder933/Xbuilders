/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.terrain;

import com.xbuilders.engine.server.world.chunk.Chunk;

import static com.xbuilders.engine.server.world.chunk.Chunk.WIDTH;

import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.server.world.Terrain;
import com.xbuilders.content.vanilla.Blocks;

import java.util.HashMap;

/**
 * @author zipCoder933
 */
public class BasicTerrain extends Terrain {

    public BasicTerrain() {
        super("Basic Terrain");
        MIN_SURFACE_HEIGHT = 70;
        MAX_SURFACE_HEIGHT = 140;
    }

    @Override
    public void loadWorld(HashMap<String, Boolean> options, int version) {

    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {
        boolean genOutsideBoundary = false;
        if ((chunk.position.y * Chunk.WIDTH) + Chunk.WIDTH > MIN_SURFACE_HEIGHT - 2) {
            for (int cx = 0; cx < WIDTH; cx++) {
                for (int cy = 0; cy < WIDTH; cy++) {
                    for (int cz = 0; cz < WIDTH; cz++) {

                        int wx = cx + (chunk.position.x * Chunk.WIDTH);
                        int wy = cy + (chunk.position.y * Chunk.WIDTH);
                        int wz = cz + (chunk.position.z * Chunk.WIDTH);

                        int heightmap = (int) MathUtils.map(
                                (float) perlinNoise.noise(wx * 0.3f, wz * 0.3f),
                                -1, 1, MAX_SURFACE_HEIGHT, MIN_SURFACE_HEIGHT);

                        if (wy == heightmap) {
                            chunk.data.setBlock(cx, cy, cz, Blocks.BLOCK_GRASS);
                            if (session.random.nextFloat() > 0.995) {
                                TreeUtils.makeTree(session.random, session, wx, wy + 1, wz);
                                genOutsideBoundary = true;
                            }
                        } else if (heightmap < wy) {
                            chunk.data.setBlock(cx, cy, cz, Blocks.BLOCK_DIRT);
                        }
//                        else if (heightmap > wy) {
//                            if (perlinNoise.fastNoise(wx, wy , wz ) > 0.1f) {
//                                chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_STONE);
//                            }
//                        }
                    }
                }
            }

        }
        session.generatedOutsideOfChunk = genOutsideBoundary;
    }

}
