/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.terrain;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.chunk.Chunk;

import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.game.MyGame;

import java.util.Random;

/**
 * @author zipCoder933
 */
public class TestTerrain extends Terrain {

    public TestTerrain() {
        super("Test Terrain");
        MIN_HEIGHT = 90;
        MAX_HEIGHT = 140;
    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {
        boolean genOutsideBoundary = false;
        if ((chunk.position.y * Chunk.WIDTH) + Chunk.WIDTH > MIN_HEIGHT - 2) {
            for (int cx = 0; cx < WIDTH; cx++) {
                for (int cy = 0; cy < WIDTH; cy++) {
                    for (int cz = 0; cz < WIDTH; cz++) {

                        int wx = cx + (chunk.position.x * Chunk.WIDTH);
                        int wy = cy + (chunk.position.y * Chunk.WIDTH);
                        int wz = cz + (chunk.position.z * Chunk.WIDTH);


                        if (wy >= MAX_HEIGHT) {
                            if (MiscUtils.isBlackCube(chunk.position.x, chunk.position.y, chunk.position.z)) {
                                chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_GRASS);
                            } else {
                                chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_STONE);
                            }
                            if (wy == MAX_HEIGHT && session.random.nextFloat() > 0.995) {
                                TreeUtils.makeTree(session.random, session, wx, wy + 1, wz);
                                genOutsideBoundary = true;
                            }
                        } else if (wy == MIN_HEIGHT && perlinNoise.noise(wx * 0.3f, wz * 0.3f) > -0.1f) {
                            if (perlinNoise.noise(wx, wy, wz) > -0.1f) {
                                chunk.data.setBlock(cx, cy, cz, MyGame.BLOCK_GRANITE);
                            }
                        }
                    }
                }
            }

        }
        session.generatedOutsideOfChunk = genOutsideBoundary;
    }

}
