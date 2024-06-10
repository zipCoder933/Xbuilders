package com.xbuilders.engine.player;

import com.xbuilders.engine.rendering.wireframeBox.Box;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class DefaultSkin extends Skin {
    Box box;
    public DefaultSkin(EntityAABB position) {
        super(position);
        box = new Box();
        box.setColor(new Vector4f(1,1,1,1));
        box.setLineWidth(3);
    }

    @Override
    public void render(Matrix4f projection, Matrix4f view) {
//        box.set(position.box);
//        box.draw(projection, view);
    }
}
