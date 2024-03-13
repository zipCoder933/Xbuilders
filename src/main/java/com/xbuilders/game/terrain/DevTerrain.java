/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.terrain;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.world.chunk.Chunk;
import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.world.Terrain;
import com.xbuilders.game.MyGame;
import java.util.Random;

/**
 *
 * @author zipCoder933
 */
public class DevTerrain extends Terrain {

    public DevTerrain() {
        super("Dev Terrain");
    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {
        if (chunk.position.y == 0) {
            if (MiscUtils.isBlackCube(chunk.position.x, chunk.position.y, chunk.position.z)) {
                for (int i = 0; i < WIDTH; i++) {
                    for (int j = 0; j < WIDTH; j++) {
                        for (int k = 0; k < WIDTH; k++) {
                            chunk.data.setBlock(i, j, k, MyGame.BLOCK_SAND.id);
                        }
                    }
                }
            } else {
                for (int i = 0; i < WIDTH; i++) {
                    for (int j = 0; j < WIDTH; j++) {
                        for (int k = 0; k < WIDTH; k++) {
                            chunk.data.setBlock(i, j, k, MyGame.BLOCK_GRASS.id);
                        }
                    }
                }
            }
        } else if (chunk.position.y < 0) {
            if (MiscUtils.isBlackCube(chunk.position.x, chunk.position.y, chunk.position.z)) {
                for (int i = 0; i < WIDTH; i++) {
                    for (int j = 0; j < WIDTH; j++) {
                        for (int k = 0; k < WIDTH; k++) {
                            chunk.data.setBlock(i, j, k, MyGame.BLOCK_SAND.id);
                        }
                    }
                }
            }
        }
    }

}
