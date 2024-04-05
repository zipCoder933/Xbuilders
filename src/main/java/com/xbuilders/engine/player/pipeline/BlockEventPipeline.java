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
import com.xbuilders.engine.world.wcc.ChunkNode;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    private void setBlock(Chunk chunk, Block block, BlockData data, BlockType type,
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


    public void resolve(UserControlledPlayer player) {
        events.forEach((worldPos, blockHist) -> {
            wcc.set(worldPos);
            Chunk chunk = world.chunks.get(wcc.chunk);
            System.out.println("Block Event: " + worldPos + " -> " + blockHist);

            //Should we set the block?
            if (blockHist.currentBlock.setBlockEvent(worldPos.x, worldPos.y, worldPos.z,
                    chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z))) {

                BlockType type = ItemList.blocks.getBlockType(blockHist.currentBlock.type);//Test if the blockType is ok with setting

                BlockData data = chunk.data.getBlockData(
                        wcc.chunkVoxel.x,
                        wcc.chunkVoxel.y,
                        wcc.chunkVoxel.z);
                data = type.getInitialBlockData(data, player);

                if (type == null || type.allowToBeSet(blockHist.currentBlock, data, worldPos.x, worldPos.y, worldPos.z)) {
                    setBlock(chunk, blockHist.currentBlock, data, type, wcc, player);

                    if (blockHist.previousBlock.opaque && !blockHist.currentBlock.opaque) {
                        System.out.println("Propagating");
                        SunlightUtils.addInitialNodesForSunlightPropagation(sunQueue, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                        SunlightUtils.propagateSunlight(sunQueue, affectedChunks, true);
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
                        sunQueue.clear();
                    }
                    affectedChunks.add(chunk);
                    startLocalChange(worldPos, blockHist);
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
        events.clear();
    }

    public static void startLocalChange(Vector3i originPos, BlockHistory hist) {
        checkAndStartBlock(originPos.x, originPos.y - 1, originPos.z, originPos, hist);
        checkAndStartBlock(originPos.x, originPos.y + 1, originPos.z, originPos, hist);

        checkAndStartBlock(originPos.x - 1, originPos.y, originPos.z, originPos, hist);
        checkAndStartBlock(originPos.x + 1, originPos.y, originPos.z, originPos, hist);
        checkAndStartBlock(originPos.x, originPos.y, originPos.z - 1, originPos, hist);
        checkAndStartBlock(originPos.x, originPos.y, originPos.z + 1, originPos, hist);

        checkAndStartBlock(originPos.x - 1, originPos.y - 1, originPos.z, originPos, hist);
        checkAndStartBlock(originPos.x + 1, originPos.y - 1, originPos.z, originPos, hist);
        checkAndStartBlock(originPos.x, originPos.y - 1, originPos.z - 1, originPos, hist);
        checkAndStartBlock(originPos.x, originPos.y - 1, originPos.z + 1, originPos, hist);

        checkAndStartBlock(originPos.x - 1, originPos.y + 1, originPos.z, originPos, hist);
        checkAndStartBlock(originPos.x + 1, originPos.y + 1, originPos.z, originPos, hist);
        checkAndStartBlock(originPos.x, originPos.y + 1, originPos.z - 1, originPos, hist);
        checkAndStartBlock(originPos.x, originPos.y + 1, originPos.z + 1, originPos, hist);
    }

    private static void checkAndStartBlock(int x, int y, int z, Vector3i originPos, BlockHistory hist) {
        WCCi wcc = new WCCi().set(x, y, z);
        Chunk chunk = wcc.getChunk(GameScene.world);
        if (chunk != null) {
            Block targetBlockID = ItemList.getBlock(chunk.data.getBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z));
            if (!targetBlockID.isAir()) {
                BlockData data = chunk.data.getBlockData(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                if (!ItemList.blocks.getBlockType(targetBlockID.type).allowToBeSet(hist.currentBlock, data, x, y, z)) {
                    chunk.data.setBlock(wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z, BlockList.BLOCK_AIR.id);
                } else targetBlockID.onLocalChange(hist, originPos, new Vector3i(x, y, z));
            }
        }
    }

}
