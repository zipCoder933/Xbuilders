package com.xbuilders.content.vanilla;

import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.item.Item;
import com.xbuilders.engine.server.world.chunk.Chunk;
import com.xbuilders.engine.server.world.wcc.WCCi;
import com.xbuilders.engine.utils.ErrorHandler;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

import static com.xbuilders.engine.utils.math.MathUtils.positiveMod;

public class EntityRemovalTool extends Item {
    Predicate<Entity> predicate;

    public EntityRemovalTool(Predicate<Entity> p, String id, String name) {
        super(id, name);
        setIcon("entity_removal_tool.png");
        maxStackSize = 1;
        tags.add("tool");
        predicate = p;
        destroyClickEvent = (ray, itemStack) -> {
            Vector3i pos = ray.getHitPos();
            deleteEntitiesAtPos(pos, 20);
            return true;
        };
        createClickEvent = (ray, itemStack) -> {
            Vector3i pos = ray.getHitPos();
            deleteEntitiesAtPos(pos, 50);
            return true;
        };
    }


    private void deleteEntitiesAtPos(Vector3i pos, int radius) {
        System.out.println("Removing all entities at " + pos);
        try {
            // Create a snapshot of the entities to avoid concurrency issues during iteration
            Collection<Entity> entitiesSnapshot = new ArrayList<>(LocalServer.world.entities.values());
            entitiesSnapshot.forEach(entity -> {
                try {
                    if (entity.worldPosition.distance(pos.x, pos.y, pos.z) < radius) {
                        System.out.println("Checking entity " + entity);
                        // Check if the entity meets the criteria and is within the radius
                        if (entity != null && predicate.test(entity)) {
                            System.out.println("\tRemoving entity " + entity);
                            entity.destroy();
                        }
                    }
                } catch (Exception e) {
                    // Handle any issues that arise while processing a specific entity
                    GameScene.alert("Error processing entity " + entity + ": " + e.getMessage());
                }
            });

            //Remove all entities in ALL chunks
            System.out.println("Removing all entities in the current chunk");
            for (Chunk chunk : LocalServer.world.chunks.values()) {
                //Iterate over the list backwards
                for (int i = chunk.entities.list.size() - 1; i >= 0; i--) {
                    Entity entity = chunk.entities.list.get(i);
                    if (entity.worldPosition.distance(pos.x, pos.y, pos.z) < radius) {
                        // Check if the entity meets the criteria
                        System.out.println("Checking entity " + entity);
                        if (entity != null
                                && predicate.test(entity)) {
                            System.out.println("\tRemoving entity " + entity);
                            entity.destroy();
                            chunk.entities.list.remove(i); //Remove from the list
                        }
                    }
                }
            }

        } catch (Exception e) {
            GameScene.alert("Error removing entities: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
