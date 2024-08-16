package com.xbuilders.engine.rendering.chunk.meshers;

import com.xbuilders.engine.utils.BooleanBuffer;
import com.xbuilders.engine.world.chunk.Chunk;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;

class UseGreedyMesherBuffer {
    public BooleanBuffer buffer;
    public final static Vector3i size = new Vector3i(Chunk.WIDTH, Chunk.HEIGHT, Chunk.WIDTH);

    public UseGreedyMesherBuffer(MemoryStack stack) {
        buffer = new BooleanBuffer(size.x * size.y * size.z, stack);
    }

    public static int getIndexOfCoords(final int x, final int y, final int z) {
        return x + size.x * (y + size.y * z);
    }

    public boolean get(int x, int y, int z) {
        return buffer.get(getIndexOfCoords(x, y, z));
    }

    public void put(int x, int y, int z, boolean value) {
        buffer.put(getIndexOfCoords(x, y, z), value);
    }
}
