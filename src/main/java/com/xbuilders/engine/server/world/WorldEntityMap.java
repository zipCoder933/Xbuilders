package com.xbuilders.engine.server.world;

import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.world.chunk.Chunk;

import java.util.HashMap;

public class WorldEntityMap extends HashMap<Long, Entity> {
    public void removeAllEntitiesFromChunk(Chunk chunk) {
        try {
            for (Entity e : new HashMap<>(this).values()) {
                if (e.chunkPosition.chunk.equals(chunk.position)) {
                    remove(e.getUniqueIdentifier());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addAllEntitiesFromChunk(Chunk chunk) {
        try {
            for (Entity e : chunk.entities.list) {
                put(e.getUniqueIdentifier(), e);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
