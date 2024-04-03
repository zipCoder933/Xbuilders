package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.world.World;
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

    public void addEvent(Vector3i position, BlockEvent event) {
        events.put(position, event);
    }

    public void addEvent(WCCi wcc, BlockEvent event) {
        events.put(WCCi.chunkSpaceToWorldSpace(wcc), event);
    }

    Map<Vector3i, BlockEvent> events = new ConcurrentHashMap<Vector3i, BlockEvent>();
    WCCi wcc = new WCCi();
    World world;
    List<ChunkNode> sunQueue = new ArrayList<>();
    HashSet<Chunk> affectedChunks = new HashSet<>();

    public void resolve() {
        events.forEach((k, v) -> {

            wcc.set(k);
            Chunk chunk = world.chunks.get(wcc.chunk);
            System.out.println("Block Event: " + k + " -> " + v);

            if (v.previousBlock.opaque && !v.currentBlock.opaque) {
                System.out.println("Propagating");
                SunlightUtils.addBrightestNeighboringNode(sunQueue, chunk, wcc.chunkVoxel.x, wcc.chunkVoxel.y, wcc.chunkVoxel.z);
                SunlightUtils.propagateSunlight(sunQueue, affectedChunks);
            }
            affectedChunks.add(chunk);
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
}
