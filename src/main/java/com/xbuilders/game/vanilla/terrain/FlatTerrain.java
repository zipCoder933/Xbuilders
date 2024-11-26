/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.terrain;

import com.xbuilders.engine.world.Terrain;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.vanilla.items.Blocks;
import com.xbuilders.game.vanilla.terrain.complexTerrain.ComplexTerrain;

import java.util.HashMap;

import static com.xbuilders.engine.world.chunk.Chunk.WIDTH;

/**
 * @author zipCoder933
 */
public class FlatTerrain extends Terrain {

    public FlatTerrain() {
        super("Flat Terrain");
        MIN_SURFACE_HEIGHT = 220;
        MAX_SURFACE_HEIGHT = 220;
    }

    @Override
    public void loadWorld(HashMap<String, Boolean> options, int version) {

    }

    private int getBiomeOfVoxel(float heat) {
        if (heat > 0.55f) {// 0.6 - 1
            // We lower down the minimum temperature of desert to compensate for it only
            // being at the bottom of the terrain
            return ComplexTerrain.BIOME_DESERT;
        } else if (heat > 0.2f) {// 0.2 - 0.6
            return ComplexTerrain.BIOME_SAVANNAH;
        } else if (heat > -0.2f) {// -0.2 - 0.2
            return ComplexTerrain.BIOME_DEFAULT;
        } else if (heat > -0.6f) {// -0.6 - -0.2
            return ComplexTerrain.BIOME_JUNGLE;
        } else {// -1 - -0.6
            return ComplexTerrain.BIOME_SNOWY;
        }
    }

    @Override
    protected void generateChunkInner(Chunk chunk, GenSession session) {

        for (int cx = 0; cx < WIDTH; cx++) {

            for (int cz = 0; cz < WIDTH; cz++) {
                for (int cy = 0; cy < WIDTH; cy++) {


//                    switch (biome) {
//                        case DEFAULT -> {
//                            chunk.data.setBlock(x, y, z, MyGame.BLOCK_GRASS);
//                        }
//                        case SNOWY -> {
//                            chunk.data.setBlock(x, y, z, MyGame.BLOCK_SNOW);
//                        }
//                        case DESERT -> {
//                            if (alpha > 0) {
//                                chunk.data.setBlock(x, y, z, MyGame.BLOCK_SAND);
//                                session.setBlockWorld(wx, wy + 1, wz, MyGame.BLOCK_SAND);
//                            } else {
//                                chunk.data.setBlock(x, y, z, MyGame.BLOCK_RED_SAND);
//                                session.setBlockWorld(wx, wy + 1, wz, MyGame.BLOCK_RED_SAND);
//                            }
//                        }
//                        case SAVANNAH -> {
//                            chunk.data.setBlock(x, y, z, MyGame.BLOCK_DRY_GRASS);
//                        }
//                        case JUNGLE -> {
//                            chunk.data.setBlock(x, y, z, MyGame.BLOCK_JUNGLE_GRASS);
//                        }


                        int wy = cy + (chunk.position.y * Chunk.WIDTH);
                        if (wy == MAX_SURFACE_HEIGHT) {
                            chunk.data.setBlock(cx, cy, cz, Blocks.BLOCK_GRASS);
                        } else if (wy > MAX_SURFACE_HEIGHT + 100) {
                            chunk.data.setBlock(cx, cy, cz, Blocks.BLOCK_BEDROCK);
                        } else if (wy > MAX_SURFACE_HEIGHT + 10) {
                            chunk.data.setBlock(cx, cy, cz, Blocks.BLOCK_STONE);
                        } else if (wy > MAX_SURFACE_HEIGHT) {
                            chunk.data.setBlock(cx, cy, cz, Blocks.BLOCK_DIRT);
                        }
                    }
                }
            }
        }

    }
