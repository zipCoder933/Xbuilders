/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.entity;

import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author zipCoder933
 */
public class EntityLink extends Item {

    public Supplier<Entity> supplier = null;
    public Consumer<EntityLink> initializationCallback;

    public EntityLink(int id, String name) {
        super(id, name, ItemType.ENTITY_LINK);
    }

    public EntityLink(int id, String name, Supplier<Entity> supplier) {
        super(id, name, ItemType.ENTITY_LINK);
        this.supplier = supplier;
    }

    public EntityLink(int id, String name, Supplier<Entity> supplier,
                      Consumer<EntityLink> initializationCallback) {
        super(id, name, ItemType.ENTITY_LINK);
        this.supplier = supplier;
        this.initializationCallback = initializationCallback;
    }

    public Entity makeNew(Chunk chunk, float worldX, float worldY, float worldZ, byte[] bytes) {
        if (supplier != null) {
            Entity entity = supplier.get();
            entity.link = this;
            entity.chunk = chunk;
            entity.worldPosition.set(worldX, worldY, worldZ);
            entity.loadBytes = bytes;
            return entity;
        }
        return null;
    }

    public void initializeEntity(Entity entity, byte[] loadBytes) {
        entity.hidden_entityInitialize(loadBytes);
    }
}
