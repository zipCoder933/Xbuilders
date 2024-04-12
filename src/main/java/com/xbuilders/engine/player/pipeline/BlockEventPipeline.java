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
import java.util.concurrent.ConcurrentHashMap;

public class BlockEventPipeline {

    public BlockEventPipeline(World world) {
        this.world = world;
    }

    public void addEvent(Vector3i position, BlockHistory event) {
        events.put(position, event);
    }

    public void addEvent(WCCi wcc, BlockHistory event) {
        events.put(WCCi.chunkSpaceToWorldSpace(wcc), event);
    }

    Map<Vector3i, BlockHistory> events = new ConcurrentHashMap<Vector3i, BlockHistory>();
    WCCi wcc = new WCCi();
    World world;
    List<ChunkNode> sunQueue = new ArrayList<>();
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

        HashMap<Vector3i, BlockHistory> eventsCopy = new HashMap<>(events);
        events.clear(); //We want to clear the old events before iterating over and picking up new ones

        framesThatHadEvents++;
        System.out.println(eventsCopy.size() + " Block Events ("+framesThatHadEvents+" frames in row)");
        boolean allowBlockEvents = framesThatHadEvents < MAX_FRAMES_WITH_EVENTS_IN_A_ROW;

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
                    resolveQueue_setBlock(chunk, blockHist.currentBlock, data, type, wcc, player);

                    if (blockHist.previousBlock.opaque && !blockHist.currentBlock.opaque) {
                        System.out.println("Propagating");
                        SunlightUtils.addInitialNodesForSunlightPropagation(sunQueue, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        SunlightUtils.propagateSunlight(sunQueue, affectedChunks, true);
                        TorchUtils.opaqueToTransparent(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        sunQueue.clear();
                    } else if (!blockHist.previousBlock.opaque && blockHist.currentBlock.opaque) {
                        System.out.println("Erasing");
                        SunlightUtils.addInitialNodesForSunlightErasure(sunQueue, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        HashSet<ChunkNode> repropagationNodes = new HashSet<>();//I think total nodes is suppsed to be for repropagating light
                        SunlightUtils.eraseSunlight(sunQueue, affectedChunks, repropagationNodes);
                        System.out.println("Re-propagating (size of repropagationNodes: " + repropagationNodes.size() + ")");
                        sunQueue.clear();
                        sunQueue.addAll(repropagationNodes);
                        SunlightUtils.propagateSunlight(sunQueue, affectedChunks, false);
                        TorchUtils.transparentToOpaque(affectedChunks, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        sunQueue.clear();
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
