/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders;

import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.mesh.BufferSet;
import com.xbuilders.engine.mesh.GreedyMesher;
import com.xbuilders.engine.mesh.old.GreedyMesherNublada;
import com.xbuilders.engine.utils.MiscUtils;
import com.xbuilders.engine.utils.math.PerlinNoise;
import com.xbuilders.engine.utils.preformance.MemoryProfiler;
import com.xbuilders.window.utils.preformance.Stopwatch;
import com.xbuilders.engine.world.chunk.ChunkVoxels;
import com.xbuilders.game.MyGame;

import java.util.HashMap;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author zipCoder933
 */
public class GreedyMeshTester {

    final static Mode mode = Mode.TEST_XBUILDERS_MEMORY;
    final static int trials = 100;

    public final static PerlinNoise noise = new PerlinNoise(0.0, 1000);
    static Stopwatch watch_Nublada = new Stopwatch();
    static Stopwatch watch_Xbuilders = new Stopwatch();
    static long times_xbuilders = 0;
    static long times_nublada = 0;
    static ChunkVoxels voxels = new ChunkVoxels(32, 32, 32);

    enum Mode {
        TEST_SPEED,
        TEST_NUBLADA_MEMORY,
        TEST_XBUILDERS_MEMORY
    }

    private static void generateVoxels() {
        voxels.clear();
        for (int i = 0; i < voxels.size.x; i++) {
            for (int j = 0; j < voxels.size.y; j++) {
                for (int k = 0; k < voxels.size.z; k++) {
                    if (noise.smoothNoise(i * 0.1f, j * 0.1f, k * 0.1f) > 0) {
                        voxels.setBlock(i, j, k, MyGame.BLOCK_GRASS.id);
                    } else {
                        voxels.setBlock(i, j, k, BlockList.BLOCK_AIR.id);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {

        System.out.println("Chunk Size: " + MiscUtils.printVector(voxels.size));
        HashMap<Short, Block> blockList = new HashMap<>();
        blockList.put(MyGame.BLOCK_GRASS.id, MyGame.BLOCK_GRASS);
        blockList.put(BlockList.BLOCK_AIR.id, BlockList.BLOCK_AIR);
        generateVoxels();

        GreedyMesherNublada mesher_Nublada = new GreedyMesherNublada(voxels, blockList);
        GreedyMesher mesher_Xbuilders = new GreedyMesher(voxels, blockList);

        long startMem = MemoryProfiler.getMemoryUsed();
        long totalMemory = 0;

        if (mode == Mode.TEST_SPEED) {
            for (int b = 0; b < trials; b++) {
                generateVoxels();

                watch_Nublada.start();
                mesher_Nublada.compute(new Vector3i(0, 0, 0));
                watch_Nublada.calculateElapsedTime();

                try (MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
                    watch_Xbuilders.start();
                    mesher_Xbuilders.compute(new BufferSet(), new BufferSet(),
                            new Vector3i(0, 0, 0),  stack,1);
                    watch_Xbuilders.calculateElapsedTime();
                }

                times_xbuilders += (watch_Xbuilders.getElapsedMilliseconds());
                times_nublada += (watch_Nublada.getElapsedMilliseconds());
                System.out.print("NUBLADA:\t" + watch_Nublada.toString());
                System.out.print("\tXBUILDERS:\t" + watch_Xbuilders.toString());
                System.out.println("\t\t (trial " + b + "/" + trials + ") (" + MemoryProfiler.getMemoryUsageAsString() + ") seed:" + noise.getSeed());
            }
        } else {
            for (int b = 0; b < trials; b++) {
                if (mode == Mode.TEST_NUBLADA_MEMORY) {
                    watch_Nublada.start();
                    mesher_Nublada.compute(new Vector3i(0, 0, 0));
                    watch_Nublada.calculateElapsedTime();
                } else if (mode == Mode.TEST_XBUILDERS_MEMORY) {
                    try (MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
                        watch_Xbuilders.start();
                        BufferSet buff1 = new BufferSet();
                        BufferSet buff2 = new BufferSet();
                        GreedyMesher mesher_Xbuilders2 = new GreedyMesher(voxels, blockList);
//                        mesher_Xbuilders2.compute(buff1, buff2, new Vector3i(0, 0, 0), false, stack);
                        watch_Xbuilders.calculateElapsedTime();

                        MemoryUtil.memFree(buff1.makeVertexSet());
                        MemoryUtil.memFree(buff2.makeVertexSet());
                    }
                }

                times_xbuilders += (watch_Xbuilders.getElapsedMilliseconds());
                times_nublada += (watch_Nublada.getElapsedMilliseconds());
                if (mode == Mode.TEST_NUBLADA_MEMORY) {
                    System.out.print("NUBLADA:\t" + watch_Nublada.toString());
                } else {
                    System.out.print("XBUILDERS:\t" + watch_Xbuilders.toString());
                }
                System.out.println("\t\t (trial " + b + "/" + trials + ") (" + MemoryProfiler.getMemoryUsageAsString() + ") seed:" + noise.getSeed());
                long memDiff = MemoryProfiler.getMemoryUsed() - startMem;
                startMem = MemoryProfiler.getMemoryUsed();
                if (memDiff >= 0) {
                    totalMemory += memDiff;
                    System.out.println("Memory increase: " + MemoryProfiler.formatBytes(memDiff));
                }

            }
        }

        System.out.println("\nNUBLADA TIME AVERAGE:\t" + (times_nublada / trials) + " ms"
                + "\nXBUILDERS TIME AVERAGE:\t" + (times_xbuilders / trials) + " ms"
                + "\n\nTotal Memory used:\t" + MemoryProfiler.formatBytes(totalMemory)
                + "\nMemory used/compute (avg):\t" + MemoryProfiler.formatBytes(totalMemory / trials));

    }

}
