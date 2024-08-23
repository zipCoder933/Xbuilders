package com.xbuilders.game.items.entities.animal.mobile;

import com.xbuilders.engine.rendering.entity.EntityShader;
import com.xbuilders.window.render.MVP;
import org.joml.Matrix4f;

public class Limb {
    private final MVP mvp;
    public final Matrix4f limbMatrix = new Matrix4f();
    public Limb[] limbs;
    public final EntityShader shader;

    public Limb(EntityShader shader, MVP mvpObject) {
        this.shader = shader;
        mvp = mvpObject;
        limbs = new Limb[0];
    }

    public void draw(Matrix4f parentMatrix) {
        mvp.update(parentMatrix, limbMatrix);
        mvp.sendToShader(shader.getID(), shader.uniform_modelMatrix);
        if (limbs != null) for (int i = 0; i < limbs.length; i++) {
            limbs[i].draw(mvp);
        }
    }
}
