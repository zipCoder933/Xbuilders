package com.xbuilders.engine.world;

import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.world.chunk.Chunk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WorldEntityMap extends HashMap<Long, Entity> {
    public void removeAllEntitiesFromChunk(Chunk chunk) {
//        this.forEach((k, v) -> {
//            if (v.chunkPosition.chunk.equals(chunk.position)) {
//                System.out.println("Removing " + v);
//            }
//        });
        entrySet().removeIf(entry -> entry.getValue().chunkPosition.chunk.equals(chunk.position));
    }

    public void addAllEntitiesFromChunk(Chunk chunk) {
        for (Entity e : chunk.entities.list) {
            put(e.getIdentifier(), e);
        }
    }
}
