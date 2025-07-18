package com.xbuilders.engine.common.world;

import com.xbuilders.Main;
import com.xbuilders.engine.common.packets.ChunkDataPacket;
import com.xbuilders.engine.common.threadPoolExecutor.PriorityExecutor.ExecutorServiceUtils;
import com.xbuilders.engine.common.threadPoolExecutor.PriorityExecutor.PriorityThreadPoolExecutor;
import com.xbuilders.engine.common.threadPoolExecutor.PriorityExecutor.comparator.LowValueComparator;
import com.xbuilders.engine.common.world.chunk.Chunk;
import com.xbuilders.engine.common.world.chunk.FutureChunk;
import com.xbuilders.engine.common.world.chunk.ServerChunk;
import org.joml.Vector3i;

public class ServerWorld extends World<ServerChunk> {

    /**
     * For a local server, we just want to share unused chunks for memory manegment
     */
    public ServerWorld(ClientWorld otherWorld) {
        this.setData(new WorldData(otherWorld.getData())); //Everything except for the chunks is its own instance
    }

    /**
     * = new ScheduledThreadPoolExecutor(1, r -> { ... });: This line creates an
     * instance of ScheduledThreadPoolExecutor. It's a typeReference of
     * ScheduledExecutorService that uses a pool of threads to execute
     * tasks.<br>
     * <br>
     * <p>
     * - 1 specifies that the pool will have one thread. This means it will be
     * capable of executing one task at a time.<br>
     * <br>
     * <p>
     * - r -> { ... } is a lambda expression that provides a ThreadFactory to
     * the executor. It defines how threads are created. In this case, it
     * creates a new thread, sets its name to "Generation Thread", and marks it
     * as a daemon thread (meaning it won't prevent the JVM from exiting).
     */
    public static final PriorityThreadPoolExecutor generationService = new PriorityThreadPoolExecutor(
            CHUNK_LOAD_THREADS, r -> {
        Thread thread = new Thread(r, "Generation Thread");
        thread.setDaemon(true);
        return thread;
    }, new LowValueComparator());

    public static final PriorityThreadPoolExecutor lightService = new PriorityThreadPoolExecutor(CHUNK_LIGHT_THREADS,
            r -> {
                Thread thread = new Thread(r, "Light Thread");
                thread.setDaemon(true);
                return thread;
            }, new LowValueComparator());


    @Override
    protected ServerChunk internal_createChunkObject(Chunk recycleChunk, final Vector3i coords, FutureChunk futureChunk) {
        if (recycleChunk != null) return new ServerChunk(recycleChunk, coords, futureChunk, this);
        else return new ServerChunk(coords, futureChunk, this);
    }

    public ServerChunk addChunk(final Vector3i coords) {
        ServerChunk chunk = super.addChunk(coords);
        if (chunk != null) {

            chunk.loadFuture = generationService.submit(chunk.distToPlayer, () -> {
                try {
                    System.out.println("Chunk Blocks...");
                    chunk.loadBlocksAndLight(futureChunks.remove(coords));

                    System.out.println("Sending chunk...");
                    Main.getServer().writeAndFlushToAllPlayers(new ChunkDataPacket(chunk));
                    return false;
                } finally {
                    newGameTasks.incrementAndGet();
                }
            });
        }
        return chunk;
    }

    public void close() {
        super.close();
        ExecutorServiceUtils.cancelAllTasks(generationService);
        ExecutorServiceUtils.cancelAllTasks(lightService);
    }

}
