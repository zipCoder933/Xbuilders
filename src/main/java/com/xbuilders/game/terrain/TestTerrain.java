/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.terrain;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.world.chunk.Chunk;
import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.game.MyGame;
import java.util.Random;

/**
 *
 * @author zipCoder933
 */
public class TestTerrain extends Terrain {

    public TestTerrain() {
        super("Test Terrain");
    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {
        if (chunk.position.y >= 0) {
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    for (int k = 0; k < WIDTH; k++) {
                        chunk.data.setBlock(i, j, k, BlockList.BLOCK_AIR.id);
                    }
                }
            }
        } else {
            int wx, wy, wz;
            for (int i = 0; i < WIDTH; i++) {
                for (int j = 0; j < WIDTH; j++) {
                    for (int k = 0; k < WIDTH; k++) {

                        wx = i + (chunk.position.x * Chunk.WIDTH);
                        wy = j + (chunk.position.y * Chunk.WIDTH);
                        wz = k + (chunk.position.z * Chunk.WIDTH);

                        if (noise.GetValueFractal(wx * 0.1f, wy * 0.1f, wz * 0.1f) > 0) {
                            if (noise.GetValueFractal(wy * 0.02f, wz * 0.02f, wx * 0.02f) < 0) {
                                chunk.data.setBlock(i, j, k, MyGame.BLOCK_GLASS.id);
                            } else {
                                chunk.data.setBlock(i, j, k, MyGame.BLOCK_GRASS.id);
                            }
                        } else {
                            chunk.data.setBlock(i, j, k, BlockList.BLOCK_AIR.id);
                        }
                    }
                }
            }
        }
    }

}
