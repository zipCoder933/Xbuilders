package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.Item;
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
import com.xbuilders.game.Main;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockEventPipeline {

    public BlockEventPipeline(World world) {
        this.world = world;
    }

    public void addEvent(Vector3i worldPos, BlockHistory blockHist) {
        if (blockHist != null) {
            //If the previous block is null, set it to the block at the position
            if (blockHist.previousBlock == null) {
                blockHist.previousBlock = GameScene.world.getBlock(worldPos.x, worldPos.y, worldPos.z);
            }

            if (blockHist.previousBlock.opaque != blockHist.currentBlock.opaque) {
                lightChangesThisFrame++;
            }
            if (blockHist.previousBlock != blockHist.currentBlock || blockHist.updateBlockData) {
                blockChangesThisFrame++;
                synchronized (eventClearLock) {
                    events.put(worldPos, blockHist);
                }
            }
        }
    }

    public void addEvent(WCCi wcc, BlockHistory event) {
        addEvent(WCCi.chunkSpaceToWorldSpace(wcc), event);
    }

    private Map<Vector3i, BlockHistory> events = new HashMap<>(); //ALL events must be submitted to this

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

        boolean allowBlockEvents = framesThatHadEvents < MAX_FRAMES_WITH_EVENTS_IN_A_ROW;

        HashSet<Chunk> affectedChunks = new HashSet<>();
        ArrayList<ChunkNode> sunNode_OpaqueToTrans = new ArrayList<>();
        ArrayList<ChunkNode> sunNode_transToOpaque = new ArrayList<>();

        resolveQueue(allowBlockEvents, framesThatHadEvents, player,
                affectedChunks, sunNode_OpaqueToTrans, sunNode_transToOpaque);

        boolean multiThreadedMode = blockChangesThisFrame > 100 || lightChangesThisFrame > 20 ||
                sunNode_transToOpaque.size() > 10 || sunNode_OpaqueToTrans.size() > 50;

        Main.printlnDev("\nUPDATING " + events.size() + " EVENTS: [  "
                + "  frames in row=" + framesThatHadEvents
                + "  block changes=" + blockChangesThisFrame
                + "  light changes=" + lightChangesThisFrame
                + "  opaque>trans=" + sunNode_OpaqueToTrans.size()
                + "  trans>opaque=" + sunNode_transToOpaque.size()
                + "  ]\tMultiThread: " + multiThreadedMode + " allowBlockEvents: " + allowBlockEvents);

        lightChangesThisFrame = 0;
        blockChangesThisFrame = 0;

        if (multiThreadedMode) {
            bulkBlockThread.submit(System.currentTimeMillis(),
                    () -> {
                        updateSunlightAndMeshes(affectedChunks, sunNode_OpaqueToTrans, sunNode_transToOpaque);
                    });
        } else {
            updateSunlightAndMeshes(affectedChunks, sunNode_OpaqueToTrans, sunNode_transToOpaque);
        }
    }


    private void resolveQueue(boolean allowBlockEvents, int framesInARow, UserControlledPlayer player,
                              final HashSet<Chunk> affectedChunks,
                              final List<ChunkNode> sunNode_OpaqueToTrans,
                              final List<ChunkNode> sunNode_transToOpaque
    ) {

        final AtomicBoolean firstChunkUpdate = new AtomicBoolean(true);
        HashMap<Vector3i, BlockHistory> eventsCopy;
        synchronized (eventClearLock) {
            eventsCopy = new HashMap<>(events);
            events.clear(); //We want to clear the old events before iterating over and picking up new ones
        }


        Main.printlnDev("EVENTS: " + eventsCopy.size());

        eventsCopy.forEach((worldPos, blockHist) -> {

            if (!World.worldYIsWithinBounds(worldPos.y)) return;

            wcc.set(worldPos);
            Chunk chunk = world.chunks.get(wcc.chunk);
            if (chunk == null) return;


            if (!blockHist.previousBlock.equals(blockHist.currentBlock)) { //If the 2 blocks are different
                //Send the block to the client

                BlockType type = ItemList.blocks.getBlockType(blockHist.currentBlock.type);
                if (type == null) return;

                BlockData blockData = null;
                if (blockHist.updateBlockData) {
                    blockData = blockHist.data;
                } else {
                    blockData = type.getInitialBlockData(chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z), player);
                }

                if (blockHist.currentBlock.allowExistence(worldPos.x, worldPos.y, worldPos.z)
                        && type.allowExistence(blockHist.currentBlock, worldPos.x, worldPos.y, worldPos.z)) {  //Should we set the block?

                    //<editor-fold defaultstate="collapsed" desc="set the block">
                    GameScene.server.sendBlockChange(worldPos, blockHist.currentBlock, blockData);
                    chunk.markAsModifiedByUser();
                    chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockHist.currentBlock.id);
                    chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockData);
//                    if (firstChunkUpdate.get() && eventsCopy.size() < 6) { //We can update the chunk right after the first block is set
//                        chunk.updateMesh(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
//                        firstChunkUpdate.set(false);
//                    }
                    //</editor-fold>

                    // <editor-fold defaultstate="collapsed" desc="sunlight and torchlight">
                    if (blockHist.previousBlock.opaque && !blockHist.currentBlock.opaque) {
                        SunlightUtils.addInitialNodesForSunlightPropagation(sunNode_OpaqueToTrans, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        TorchUtils.opaqueToTransparent(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);//TODO: We might need to optimize this by creating the nodes first and then propagating once
                    } else if (!blockHist.previousBlock.opaque && blockHist.currentBlock.opaque) {
                        SunlightUtils.addInitialNodesForSunlightErasure(sunNode_transToOpaque, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        TorchUtils.transparentToOpaque(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
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
                    if (allowBlockEvents && !blockHist.isFromMultiplayer) {//Dont do block events if the block was set by the server
                        startLocalChange(worldPos, blockHist, allowBlockEvents);
                        blockHist.previousBlock.run_RemoveBlockEvent(worldPos);
                        blockHist.currentBlock.run_SetBlockEvent(eventThread, worldPos, blockData); //Run the block event
                    }
                }
            } else if (blockHist.updateBlockData) {
                chunk.markAsModifiedByUser();
                chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockHist.data);
                affectedChunks.add(chunk);
            }
        });
    }

    public void updateSunlightAndMeshes(HashSet<Chunk> affectedChunks,
                                        ArrayList<ChunkNode> sunNode_OpaqueToTrans,
                                        ArrayList<ChunkNode> sunNode_transToOpaque) {

        //Simply resolveing a queue of sunlight adds MAJOR IMPROVEMENTS
        Main.printlnDev("\tOpaque to trans: " + sunNode_OpaqueToTrans.size() + "; Trans to opaque: " + sunNode_transToOpaque.size());

        long start = System.currentTimeMillis();
        boolean longSunlight = sunNode_OpaqueToTrans.size() > 10000 || sunNode_transToOpaque.size() > 10000;
        if (longSunlight) {
            GameScene.alert("The lighting is being calculated. This may take a while.");
            Main.printlnDev("Pre-Updating Meshes");
            updateAffectedChunks(affectedChunks);
        }

        SunlightUtils.updateFromQueue(sunNode_OpaqueToTrans, sunNode_transToOpaque, affectedChunks);
        sunNode_OpaqueToTrans.clear();
        sunNode_transToOpaque.clear();
        Main.printlnDev("Done. Chunks affected: " + affectedChunks.size());

        if (longSunlight) {
            GameScene.alert("Sunlight calculation finished " +
                    ((System.currentTimeMillis() - start) / 1000) + "s");
        }

        //Resolve affected chunks
        updateAffectedChunks(affectedChunks);
        affectedChunks.clear();
    }

    private void updateAffectedChunks(HashSet<Chunk> affectedChunks) {
        for (Chunk chunk : affectedChunks) {
            chunk.updateMesh(
                    wcc.chunkVoxel.x,
                    wcc.chunkVoxel.y,
                    wcc.chunkVoxel.z);
        }
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
