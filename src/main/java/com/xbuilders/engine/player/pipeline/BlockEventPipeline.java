package com.xbuilders.engine.player.pipeline;

import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.engine.world.wcc.WCCi;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockEventPipeline {

    public BlockEventPipeline(World world){
        this.world = world;
    }

    public void addEvent(Vector3i position, BlockEvent event){
        events.put(position, event);
    }

    public void addEvent(WCCi wcc, BlockEvent event){
        events.put(WCCi.chunkSpaceToWorldSpace(wcc), event);
    }
    Map<Vector3i,BlockEvent> events = new ConcurrentHashMap<Vector3i,BlockEvent>();
    WCCi wcc = new WCCi();
    World world;

    public void resolve(){
        events.forEach((k,v) -> {
            System.out.println(k + " -> " + v);
            wcc.set(k);
            Chunk chunk = world.chunks.get(wcc.chunk);
            chunk.updateMesh(
                    wcc.chunkVoxel.x,
                    wcc.chunkVoxel.y,
                    wcc.chunkVoxel.z);
        });
        events.clear();
    }
}
