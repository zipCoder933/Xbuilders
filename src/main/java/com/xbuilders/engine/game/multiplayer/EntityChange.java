package com.xbuilders.engine.game.multiplayer;

import com.xbuilders.engine.game.model.items.entity.Entity;

public class EntityChange {
    public Entity entity;
    public int mode;

    public EntityChange(Entity entity, int mode) {
        this.entity = entity;
        this.mode = mode;
    }
}
