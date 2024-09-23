package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.multiplayer.Local_PendingBlockChanges;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.multiplayer.PendingBlockChanges;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor.PriorityThreadPoolExecutor;
import com.xbuilders.engine.utils.threadPoolExecutor.PriorityExecutor.comparator.HighValueComparator;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.light.SunlightUtils;
import com.xbuilders.engine.world.light.TorchUtils;
import com.xbuilders.engine.utils.BFS.ChunkNode;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockEventPipeline {


    private final Map<Vector3i, BlockHistory> events = new HashMap<>(); //ALL events must be submitted to this
    public final Local_PendingBlockChanges outOfReachEvents;


    WCCi wcc = new WCCi();
    World world;
    UserControlledPlayer player;
    final Object eventClearLock = new Object();

    public BlockEventPipeline(World world, UserControlledPlayer player) {
        this.world = world;
        this.player = player;
        outOfReachEvents = new Local_PendingBlockChanges(player);
    }

    public void addEvent(Vector3i worldPos, BlockHistory blockHist) {
        if (blockHist != null) {
            if (!PendingBlockChanges.changeCanBeLoaded(player, worldPos)) {
                //If there is a block event that is on a empty chunk or too far away, don't add it
                outOfReachEvents.addBlockChange(worldPos, blockHist);
                return;
            }
            blockChangesThisFrame++;
            synchronized (eventClearLock) {
                if (events.containsKey(worldPos)) { //We need to get the original previous block
                    blockHist.previousBlock = events.get(worldPos).previousBlock;
                } else if (blockHist.previousBlock == null) {
                    blockHist.previousBlock = GameScene.world.getBlock(worldPos.x, worldPos.y, worldPos.z);
                }
                if (blockHist.previousBlock.opaque != blockHist.newBlock.opaque) {
                    lightChangesThisFrame++;
                }
                events.put(worldPos, blockHist);
            }
        }
    }

    public void addEvent(WCCi wcc, BlockHistory event) {
        addEvent(WCCi.chunkSpaceToWorldSpace(wcc), event);
    }


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
    WorldInfo worldInfo;

    public void startGame(WorldInfo worldInfo) {
        eventThread = new PriorityThreadPoolExecutor(
                100, 1000,
                0L, TimeUnit.MILLISECONDS,
                new HighValueComparator());

        bulkBlockThread = new PriorityThreadPoolExecutor(
                5, 5,
                100L, TimeUnit.MILLISECONDS,
                new HighValueComparator());


        this.worldInfo = worldInfo;
        outOfReachEvents.load(worldInfo);
    }

    public void save() {
        if (outOfReachEvents.needsSaving) {
            outOfReachEvents.save(worldInfo);
            outOfReachEvents.needsSaving = false;
        }
    }

    public void endGame() {
        events.clear();
        eventThread.shutdown();
        bulkBlockThread.shutdown();
        save();
    }


    int blockChangesThisFrame, lightChangesThisFrame;
    int framesThatHadEvents = 0;

    //By limiting the number of frames in a row we can prevent block events that are called in an infinite loop
    //Note that this does not prevent the pipeline from handling existing events, it just
    // prevents block.setBlockEvent() and block.onLocalChange() from being called
    final int MAX_FRAMES_WITH_EVENTS_IN_A_ROW = 10;

    public void update() {
        if (MainWindow.devkeyF3 && MainWindow.devMode)
            return;//Check to see if the block pipeline could be causing problems, It could also the the threads?

        if (outOfReachEvents.periodicRangeSendCheck(5000)) {
            int changes = outOfReachEvents.readApplicableChanges((worldPos, history) -> {
                addEvent(worldPos, history);
            });
            MainWindow.printlnDev("Loaded " + changes + " local changes");
        }

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

//        Main.printlnDev("\nUPDATING " + events.size() + " EVENTS: [  "
//                + "  frames in row=" + framesThatHadEvents
//                + "  block changes=" + blockChangesThisFrame
//                + "  light changes=" + lightChangesThisFrame
//                + "  opaque>trans=" + sunNode_OpaqueToTrans.size()
//                + "  trans>opaque=" + sunNode_transToOpaque.size()
//                + "  ]\tMultiThread: " + multiThreadedMode + " allowBlockEvents: " + allowBlockEvents);

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

        HashMap<Vector3i, BlockHistory> eventsCopy;
        //We always run through each event the frame of/after the event was added
        synchronized (eventClearLock) {
            eventsCopy = new HashMap<>(events);
            events.clear(); //We want to clear the old events before iterating over and picking up new ones
        }

        eventsCopy.forEach((worldPos, blockHist) -> {
            if (!World.inYBounds(worldPos.y)) return;
            wcc.set(worldPos);
            Chunk chunk = world.chunks.get(wcc.chunk);
            if (chunk == null) return;

            BlockType type = ItemList.blocks.getBlockType(blockHist.newBlock.renderType);
            BlockData newBlockData = null;
            if (blockHist.updateBlockData) {
                newBlockData = blockHist.newBlockData;
            } else {
                newBlockData = type.getInitialBlockData(
                        chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z),
                        blockHist.newBlock, player);
            }
            blockHist.newBlockData = newBlockData; //Store the new block data so that we can use it later

            if (!blockHist.previousBlock.equals(blockHist.newBlock)) { //If the 2 blocks are different
                if (blockHist.newBlock.allowExistence(worldPos.x, worldPos.y, worldPos.z) //Should we set the block?
                        && type.allowExistence(blockHist.newBlock, worldPos.x, worldPos.y, worldPos.z)) {

                    //set block
                    if (!blockHist.fromNetwork)  //only send change if not from network
                        GameScene.server.addBlockChange(worldPos, blockHist.newBlock, newBlockData);
                    chunk.markAsModifiedByUser();
                    chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, blockHist.newBlock.id);

                    //set block data
                    blockHist.previousBlockData = chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, newBlockData);

                    // <editor-fold defaultstate="collapsed" desc="update sunlight and torchlight">
                    if (blockHist.previousBlock.opaque && !blockHist.newBlock.opaque) {
                        SunlightUtils.addNodeForPropagation(sunNode_OpaqueToTrans, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        TorchUtils.opaqueToTransparent(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);//TODO: We might need to optimize this by creating the nodes first and then propagating once
                    } else if (!blockHist.previousBlock.opaque && blockHist.newBlock.opaque) {
                        SunlightUtils.addNodeForErasure(sunNode_transToOpaque, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        TorchUtils.transparentToOpaque(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    }

                    if (!blockHist.previousBlock.isLuminous() && blockHist.newBlock.isLuminous()) {
                        TorchUtils.setTorch(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z,
                                blockHist.newBlock.torchlightStartingValue);
                    } else if (blockHist.previousBlock.isLuminous() && !blockHist.newBlock.isLuminous()) {
                        TorchUtils.removeTorch(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    }
// </editor-fold>

                    affectedChunks.add(chunk);
                }
            } else { //If both blocks are the same, just update the block data
                if (!blockHist.fromNetwork) //only send change if not from network
                    GameScene.server.addBlockChange(worldPos, blockHist.newBlock, newBlockData);

                blockHist.previousBlockData = chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                chunk.data.setBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, newBlockData);
                chunk.markAsModifiedByUser();
                affectedChunks.add(chunk);
            }
        });


        //Block events
        if (allowBlockEvents) {
            eventsCopy.forEach((worldPos, blockHist) -> {
                if (World.inYBounds(worldPos.y)) {
                    if (!blockHist.fromNetwork) {//Dont do block events if the block was set by the server

                        if (//TODO: Try to check for block data changes without setting off infinite recursion
                                blockHist.previousBlock != blockHist.newBlock //If the blocks are different
                        ) {
                            startLocalChange(worldPos, blockHist, allowBlockEvents);
                            MainWindow.gameScene.livePropagationHandler.addNode(worldPos, blockHist);
                            blockHist.previousBlock.run_RemoveBlockEvent(eventThread, worldPos, blockHist);
                            blockHist.newBlock.run_SetBlockEvent(eventThread, worldPos);
                        }
                    }
                }
            });
        }
        GameScene.server.sendNearBlockChanges();
    }


    public void updateSunlightAndMeshes(HashSet<Chunk> affectedChunks,
                                        ArrayList<ChunkNode> sunNode_OpaqueToTrans,
                                        ArrayList<ChunkNode> sunNode_transToOpaque) {
//        Main.printlnDev("\tOpaque > trans: " + sunNode_OpaqueToTrans.size() + "; Trans > opaque: " + sunNode_transToOpaque.size());


        AtomicBoolean longSunlight = new AtomicBoolean(false);
        AtomicBoolean firstChunkUpdate = new AtomicBoolean(true);

        long elapsedMS = SunlightUtils.updateFromQueue(
                sunNode_OpaqueToTrans,
                sunNode_transToOpaque,
                affectedChunks,
                (time) -> {
                    if (time > 1000 && firstChunkUpdate.get()) {
                        updateAffectedChunks(affectedChunks);
                        firstChunkUpdate.set(false);
                    } else if (time > 3000 && !longSunlight.get()) {
                        GameScene.alert("The lighting is being calculated. This may take a while.");
                        longSunlight.set(true);
                    }
                });

        if (longSunlight.get()) {
            GameScene.alert("Sunlight calculation finished " + (elapsedMS / 1000) + "s");
        }

        //Resolve affected chunks
        updateAffectedChunks(affectedChunks);
        affectedChunks.clear();
    }

    private void updateAffectedChunks(HashSet<Chunk> affectedChunks) {
        for (Chunk chunk : affectedChunks) {
            //If a block was set next to something else, we have to acount for that in the mesh update
            //TODO: This mesh updating could be optimized by only updating the affected chunks
            chunk.updateMesh(true, 0, 0, 0);
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


    private void checkAndStartBlock(
            int nx, int ny, int nz,//Neighboring voxel that we notify of the change
            Vector3i originPos, //Where the change actually happened
            BlockHistory hist, //What changed
            boolean dispatchBlockEvent) {
        WCCi wcc = new WCCi().set(nx, ny, nz);
        Chunk chunk = wcc.getChunk(GameScene.world);
        if (chunk != null) {
            Block nBlock = ItemList.getBlock(chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z));//The block at the neighboring voxel
            if (nBlock != null && !nBlock.isAir()) {
                if (!ItemList.blocks.getBlockType(nBlock.renderType).allowExistence(hist.newBlock, nx, ny, nz)) {

                    //Set blocks that are not allowed here to air
                    short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, BlockList.BLOCK_AIR.id);
                    addEvent(wcc, new BlockHistory(previousBlock, BlockList.BLOCK_AIR.id));

                } else if (dispatchBlockEvent) {
                    BlockHistory nhist = new BlockHistory(nBlock, nBlock);
                    MainWindow.gameScene.livePropagationHandler.addNode(new Vector3i(nx, ny, nz), nhist);
                    nBlock.run_LocalChangeEvent(eventThread, hist, originPos, new Vector3i(nx, ny, nz));
                }
            }
        }
    }

}
