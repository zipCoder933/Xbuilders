package com.xbuilders.engine.player;

import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import org.joml.Matrix4f;

public abstract class Skin {
    public EntityAABB position;

    public Skin(EntityAABB position) {
        this.position = position;
    }

    public abstract void render(Matrix4f projection, Matrix4f view);
}
