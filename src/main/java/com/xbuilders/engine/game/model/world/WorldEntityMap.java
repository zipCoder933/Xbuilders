package com.xbuilders.engine.game.model.world;

import com.xbuilders.engine.game.model.items.entity.Entity;
import com.xbuilders.engine.game.model.world.chunk.Chunk;

import java.util.HashMap;

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
            put(e.getUniqueIdentifier(), e);
        }
    }
}
