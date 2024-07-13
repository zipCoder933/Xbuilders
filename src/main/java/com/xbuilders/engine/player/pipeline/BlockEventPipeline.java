package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor.PriorityThreadPoolExecutor;
import com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor.comparator.HighValueComparator;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.light.SunlightUtils;
import com.xbuilders.engine.world.light.TorchUtils;
import com.xbuilders.engine.utils.BFS.ChunkNode;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.*;

public class BlockEventPipeline {

    public BlockEventPipeline(World world) {
        this.world = world;
    }

    public void addEvent(Vector3i position, BlockHistory event) {
        if (event != null) {
            if (event.previousBlock.opaque != event.currentBlock.opaque) {
                lightChangesThisFrame++;
            }
            if (event.previousBlock != event.currentBlock || event.updateBlockData) {
                blockChangesThisFrame++;
                synchronized (eventClearLock) {
                    events.put(position, event);
                }
            }
        }
    }

    public void addEvent(WCCi wcc, BlockHistory event) {
        addEvent(WCCi.chunkSpaceToWorldSpace(wcc), event);
    }

    Map<Vector3i, BlockHistory> events = new HashMap<>();
    WCCi wcc = new WCCi();
    World world;
    final Object eventClearLock = new Object();

    /**
     * corePoolSize: The number of threads to keep in the pool, even if they are idle.
     * maximumPoolSize: The maximum number of threads to allow in the pool.
     * keepAliveTime: The amount of time that idle threads will wait before terminating.
     * unit: The time unit for the keepAliveTime parameter.
     * workQueue: The BlockingQueue to use for holding tasks before they are executed.
     * threadFactory: The factory to use when creating new threads.
     * handler: The handler to use when tasks cannot be executed.
     */
    PriorityThreadPoolExecutor bulkBlockThread;
    PriorityThreadPoolExecutor eventThread;

    public void startGame() {
        eventThread = new PriorityThreadPoolExecutor(
                100, 1000,
                0L, TimeUnit.MILLISECONDS,
                new HighValueComparator());

        bulkBlockThread = new PriorityThreadPoolExecutor(
                5, 5,
                100L, TimeUnit.MILLISECONDS,
                new HighValueComparator());
    }

    public void endGame() {
        events.clear();
        eventThread.shutdown();
        bulkBlockThread.shutdown();
    }


    int blockChangesThisFrame, lightChangesThisFrame;
    long lastChunkUpdate = System.currentTimeMillis();
    int framesThatHadEvents = 0;

    //By limiting the number of frames in a row we can prevent block events that are called in an infinite loop
    //Note that this does not prevent the pipeline from handling existing events, it just
    // prevents block.setBlockEvent() and block.onLocalChange() from being called
    final int MAX_FRAMES_WITH_EVENTS_IN_A_ROW = 10;

    public void resolve(UserControlledPlayer player) {
        if (events.isEmpty()) {
            framesThatHadEvents = 0;
            return;
        }
        framesThatHadEvents++;
        boolean multiThreadedMode = blockChangesThisFrame > 100 || lightChangesThisFrame > 20;
        boolean allowBlockEvents = framesThatHadEvents < MAX_FRAMES_WITH_EVENTS_IN_A_ROW;
        System.out.println("\nUPDATING " + events.size() + " EVENTS (" + framesThatHadEvents + " frames in row): \tMultiThread: " + multiThreadedMode + " allowBlockEvents: " + allowBlockEvents);

        lightChangesThisFrame = 0;
        blockChangesThisFrame = 0;

        if (multiThreadedMode) {
            bulkBlockThread.submit(System.currentTimeMillis(),
                    () -> resolveQueue(allowBlockEvents, framesThatHadEvents, player));
        } else resolveQueue(allowBlockEvents, framesThatHadEvents, player);
    }

    private void resolveQueue(boolean allowBlockEvents, int framesInARow, UserControlledPlayer player) {
        HashMap<Vector3i, BlockHistory> eventsCopy;
        synchronized (eventClearLock) {
            eventsCopy = new HashMap<>(events);
            events.clear(); //We want to clear the old events before iterating over and picking up new ones
        }
        HashSet<Chunk> affectedChunks = new HashSet<>();
        List<ChunkNode> opaqueToTransparent = new ArrayList<>();
        List<ChunkNode> transparentToOpaque = new ArrayList<>();

        System.out.println("EVENTS SIZE: " + eventsCopy.size());

        eventsCopy.forEach((worldPos, blockHist) -> {

            if (!World.worldYIsWithinBounds(worldPos.y)) return;

            wcc.set(worldPos);
            Chunk chunk = world.chunks.get(wcc.chunk);
            if (chunk == null) return;
            if (!blockHist.previousBlock.equals(blockHist.currentBlock)) { //If the 2 blocks are different
                //Send the block to the client

                /**
                 * A few problems:
                 * 1. How to se know that a block has changed by us or someone else
                 * 2.
                 */
//                GameScene.server.sendBlockChange(worldPos, blockHist.currentBlock, blockHist.data);

                BlockType type = ItemList.blocks.getBlockType(blockHist.currentBlock.type);
                if (type == null) return;

                if (blockHist.currentBlock.allowExistence(worldPos.x, worldPos.y, worldPos.z)
                        && type.allowExistence(blockHist.currentBlock, worldPos.x, worldPos.y, worldPos.z)) {  //Should we set the block?

                    //<editor-fold defaultstate="collapsed" desc="set the block">
                    chunk.markAsModifiedByUser();
                    chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockHist.currentBlock.id);
                    BlockData data = null;
                    if (blockHist.updateBlockData) {
                        data = blockHist.data;
                    } else {
                        data = type.getInitialBlockData(chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z), player);
                    }
                    chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, data);
                    //</editor-fold>

                    // <editor-fold defaultstate="collapsed" desc="sunlight and torchlight">
                    if (blockHist.previousBlock.opaque && !blockHist.currentBlock.opaque) {
                        SunlightUtils.addInitialNodesForSunlightPropagation(opaqueToTransparent, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    } else if (!blockHist.previousBlock.opaque && blockHist.currentBlock.opaque) {
                        SunlightUtils.addInitialNodesForSunlightErasure(transparentToOpaque, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    }

                    if (!blockHist.previousBlock.isLuminous() && blockHist.currentBlock.isLuminous()) {
                        TorchUtils.setTorch(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z,
                                blockHist.currentBlock.torchlightStartingValue);
                    } else if (blockHist.previousBlock.isLuminous() && !blockHist.currentBlock.isLuminous()) {
                        TorchUtils.removeTorch(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    }
// </editor-fold>

                    affectedChunks.add(chunk);

                    //Block events:
                    if (allowBlockEvents) {
                        startLocalChange(worldPos, blockHist, allowBlockEvents);
                        blockHist.previousBlock.run_RemoveBlockEvent(worldPos);
                        blockHist.currentBlock.run_SetBlockEvent(eventThread, worldPos, data); //Run the block event
                    }
                }
            } else if (blockHist.updateBlockData) {
                chunk.markAsModifiedByUser();
                chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockHist.data);
                affectedChunks.add(chunk);
            }
        });


        //Simply resolveing a queue of sunlight adds MAJOR IMPROVEMENTS
        System.out.println("\tOpaque to trans: " + opaqueToTransparent.size() + "; Trans to opaque: " + transparentToOpaque.size());

        if (opaqueToTransparent.size() > 10000 || transparentToOpaque.size() > 10000) {
            System.out.println("Pre-Updating Meshes");
            for (Chunk chunk : affectedChunks) {
                chunk.updateMesh(
                        wcc.chunkVoxel.x,
                        wcc.chunkVoxel.y,
                        wcc.chunkVoxel.z);
            }
        }


        SunlightUtils.updateFromQueue(opaqueToTransparent, transparentToOpaque, affectedChunks);
        opaqueToTransparent.clear();
        transparentToOpaque.clear();
        System.out.println("Done. Chunks affected: " + affectedChunks.size());

        //Resolve affected chunks
        for (Chunk chunk : affectedChunks) {
            chunk.updateMesh(
                    wcc.chunkVoxel.x,
                    wcc.chunkVoxel.y,
                    wcc.chunkVoxel.z);
        }
        affectedChunks.clear();
    }


    private void startLocalChange(Vector3i originPos, BlockHistory hist, boolean dispatchBlockEvent) {
        checkAndStartBlock(originPos.x, originPos.y - 1, originPos.z, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x, originPos.y + 1, originPos.z, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x - 1, originPos.y, originPos.z, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x + 1, originPos.y, originPos.z, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x, originPos.y, originPos.z - 1, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x, originPos.y, originPos.z + 1, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x - 1, originPos.y - 1, originPos.z, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x + 1, originPos.y - 1, originPos.z, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x, originPos.y - 1, originPos.z - 1, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x, originPos.y - 1, originPos.z + 1, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x - 1, originPos.y + 1, originPos.z, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x + 1, originPos.y + 1, originPos.z, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x, originPos.y + 1, originPos.z - 1, originPos, hist, dispatchBlockEvent);
        checkAndStartBlock(originPos.x, originPos.y + 1, originPos.z + 1, originPos, hist, dispatchBlockEvent);
    }


    private void checkAndStartBlock(int x, int y, int z, Vector3i originPos, BlockHistory hist,
                                    boolean dispatchBlockEvent) {
        WCCi wcc = new WCCi().set(x, y, z);
        Chunk chunk = wcc.getChunk(GameScene.world);
        if (chunk != null) {
            Block targetBlockID = ItemList.getBlock(chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z));
            if (targetBlockID != null && !targetBlockID.isAir()) {
                if (!ItemList.blocks.getBlockType(targetBlockID.type).allowExistence(hist.currentBlock, x, y, z)) {

                    //Set to air
                    short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, BlockList.BLOCK_AIR.id);
                    addEvent(wcc, new BlockHistory(previousBlock, BlockList.BLOCK_AIR.id));

                } else if (dispatchBlockEvent) targetBlockID.run_OnLocalChange(hist, originPos, new Vector3i(x, y, z));
            }
        }
    }

}
