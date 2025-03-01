package com.xbuilders.content.vanilla;

import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.server.item.Item;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

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
            Collection<Entity> entitiesSnapshot = new ArrayList<>(Server.world.entities.values());
            entitiesSnapshot.forEach(entity -> {
                try {
                    // Check if the entity meets the criteria and is within the radius
                    if (predicate.test(entity) && entity.worldPosition.distance(pos.x, pos.y, pos.z) < radius) {
                        entity.destroy();
                    }
                } catch (Exception e) {
                    // Handle any issues that arise while processing a specific entity
                    System.err.println("Error processing entity " + entity + ": " + e.getMessage());
                }
            });
        } catch (Exception e) {
            // Handle errors that might occur while creating the snapshot or iterating
            System.err.println("Error during entity deletion: " + e.getMessage());
        }
    }
}
