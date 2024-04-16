package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.BlockList;
import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.block.construction.BlockType;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.light.SunlightUtils;
import com.xbuilders.engine.world.light.TorchUtils;
import com.xbuilders.engine.world.wcc.ChunkNode;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockEventPipeline {

    public BlockEventPipeline(World world) {
        this.world = world;
    }

    public void addEvent(Vector3i position, BlockHistory event) {
        if (event != null) {
            if (event.previousBlock.opaque != event.currentBlock.opaque) {
                lightChangesThisFrame++;
            }
            blockChangesThisFrame++;
            events.put(position, event);
        }
    }

    public void addEvent(WCCi wcc, BlockHistory event) {
        addEvent(WCCi.chunkSpaceToWorldSpace(wcc), event);
    }

    Map<Vector3i, BlockHistory> events = new HashMap<Vector3i, BlockHistory>();
    WCCi wcc = new WCCi();
    World world;

    /**
     * corePoolSize: The number of threads to keep in the pool, even if they are idle.
     * maximumPoolSize: The maximum number of threads to allow in the pool.
     * keepAliveTime: The amount of time that idle threads will wait before terminating.
     * unit: The time unit for the keepAliveTime parameter.
     * workQueue: The BlockingQueue to use for holding tasks before they are executed.
     * threadFactory: The factory to use when creating new threads.
     * handler: The handler to use when tasks cannot be executed.
     */
    ThreadPoolExecutor eventThread = new ThreadPoolExecutor(
            0, 5,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
    HashSet<Chunk> affectedChunks = new HashSet<>();

    private void resolveQueue_setBlock(Chunk chunk, Block block, BlockData data, BlockType type,
                                       WCCi wcc, UserControlledPlayer player) {
        chunk.markAsModifiedByUser();
        chunk.data.setBlock(
                wcc.chunkVoxel.x,
                wcc.chunkVoxel.y,
                wcc.chunkVoxel.z, block.id);
        if (type != null) {
            chunk.data.setBlockData(wcc.chunkVoxel.x,
                    wcc.chunkVoxel.y,
                    wcc.chunkVoxel.z,
                    data);
        }
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
        System.out.println("\nUPDATING EVENTS: \tMultiThreaded: " + multiThreadedMode + " allowBlockEvents: " + allowBlockEvents);

//        if (multiThreadedMode) { //TODO: Successfully implemented multithreaded block setting
//            eventThread.execute(() -> resolveQueue(events, allowBlockEvents, player));
//            return;
//        } else
        resolveQueue(events, allowBlockEvents, player);
//
//
//        //Affected chunks are already updated and cleared at teh end of resolveQueue, however if we are multirhreaded we need to update them here as well
//        if(affectedChunks.size() > 0 && lastChunkUpdate + 1000 < System.currentTimeMillis()){
//            System.out.println("Updating " + affectedChunks.size() + " chunks");
//            lastChunkUpdate = System.currentTimeMillis();
//            for(Chunk c : affectedChunks){
//                c.markAsModifiedByUser();
//            }
//        }

        lightChangesThisFrame = 0;
        blockChangesThisFrame = 0;
    }

    private void resolveQueue(Map<Vector3i, BlockHistory> queue, boolean allowBlockEvents, UserControlledPlayer player) {
        HashMap<Vector3i, BlockHistory> eventsCopy = new HashMap<>(queue);
        List<ChunkNode> opaqueToTransparent = new ArrayList<>();
        List<ChunkNode> transparentToOpaque = new ArrayList<>();

        queue.clear(); //We want to clear the old events before iterating over and picking up new ones
        System.out.println(eventsCopy.size() + " Block Events (" + framesThatHadEvents + " frames in row)");

        eventsCopy.forEach((worldPos, blockHist) -> {
            wcc.set(worldPos);
            Chunk chunk = world.chunks.get(wcc.chunk);

            //Should we set the block?
            boolean shouldSet = allowBlockEvents ?
                    blockHist.currentBlock.setBlockEvent(worldPos.x, worldPos.y, worldPos.z,
                            chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z)) :
                    false;
            if (allowBlockEvents) {
                BlockType type = ItemList.blocks.getBlockType(blockHist.currentBlock.type);//Test if the blockType is ok with setting

                BlockData data = chunk.data.getBlockData(
                        wcc.chunkVoxel.x,
                        wcc.chunkVoxel.y,
                        wcc.chunkVoxel.z);
                data = type.getInitialBlockData(data, player);

                if (type == null || type.allowToBeSet(blockHist.currentBlock, data, worldPos.x, worldPos.y, worldPos.z)) {
                    //We Actually set the block here
                    resolveQueue_setBlock(chunk, blockHist.currentBlock, data, type, wcc, player);

                    if (blockHist.previousBlock.opaque && !blockHist.currentBlock.opaque) {
                        SunlightUtils.addInitialNodesForSunlightPropagation(opaqueToTransparent, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        //We dont have to propagate or erase here as we are doing it in the step
//                        SunlightUtils.propagateSunlight(opaqueToTransparent, affectedChunks, true);
//                        TorchUtils.opaqueToTransparent(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
//                        opaqueToTransparent.clear();
                    } else if (!blockHist.previousBlock.opaque && blockHist.currentBlock.opaque) {
                        SunlightUtils.addInitialNodesForSunlightErasure(transparentToOpaque, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        //We dont have to propagate or erase here as we are doing it in the step
                        //                        HashSet<ChunkNode> repropagationNodes = new HashSet<>();//I think total nodes is suppsed to be for repropagating light
//                        SunlightUtils.eraseSunlight(opaqueToTransparent, affectedChunks, repropagationNodes);
//                        opaqueToTransparent.clear();
//                        opaqueToTransparent.addAll(repropagationNodes);
//                        SunlightUtils.propagateSunlight(opaqueToTransparent, affectedChunks, false);
//                        TorchUtils.transparentToOpaque(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
//                        opaqueToTransparent.clear();
                    }

                    if (!blockHist.previousBlock.isLuminous() && blockHist.currentBlock.isLuminous()) {
                        TorchUtils.setTorch(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z,
                                blockHist.currentBlock.torchlightStartingValue);
                    } else if (blockHist.previousBlock.isLuminous() && !blockHist.currentBlock.isLuminous()) {
                        TorchUtils.removeTorch(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    }
                    affectedChunks.add(chunk);
                    startLocalChange(worldPos, blockHist, allowBlockEvents);
                }
            }
        });

        //Simply resolveing a queue of sunlight adds MAJOR IMPROVEMENTS
        System.out.println("Opaque to transparent: " + opaqueToTransparent.size());
        System.out.println("Transparent to opaque: " + transparentToOpaque.size());
        SunlightUtils.updateFromQueue(opaqueToTransparent, transparentToOpaque, affectedChunks);
        opaqueToTransparent.clear();
        transparentToOpaque.clear();

        //Resolve affected chunks
        for (Chunk chunk : affectedChunks) {
//            System.out.println(chunk+" was affected");
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
            if (!targetBlockID.isAir()) {
                BlockData data = chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                if (!ItemList.blocks.getBlockType(targetBlockID.type).allowToBeSet(hist.currentBlock, data, x, y, z)) {

                    //Set to air
                    short previousBlock = chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                    chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, BlockList.BLOCK_AIR.id);
                    addEvent(wcc, new BlockHistory(previousBlock, BlockList.BLOCK_AIR.id));

                } else if (dispatchBlockEvent) targetBlockID.onLocalChange(hist, originPos, new Vector3i(x, y, z));
            }
        }
    }

}
