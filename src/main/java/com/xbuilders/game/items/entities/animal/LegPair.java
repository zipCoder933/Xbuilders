package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.rendering.entity.EntityShader;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.utils.obj.OBJ;
import com.xbuilders.window.utils.obj.OBJLoader;
import org.joml.Matrix4f;

import java.io.IOException;

public class LegPair {

    EntityMesh leg;
    final Matrix4f legMatrix = new Matrix4f();
    MVP mvp = new MVP();

    public LegPair(EntityMesh leg) throws IOException {
        this.leg = leg;
    }

    public void draw(Matrix4f projection, Matrix4f view, Matrix4f bodyMatrix,
                     EntityShader bodyShader,
                     float x, float y, float z,
                     float movement) {
        legMatrix.identity().translate(x, y, z).rotateX((float) (Math.sin(movement*2) * 0.3f));
        mvp.update(projection, view, bodyMatrix, legMatrix);
        mvp.sendToShader(bodyShader.getID(), bodyShader.mvpUniform);
        leg.draw(false);


        legMatrix.identity().translate(-x, y, z).rotateX((float) (-Math.sin(movement*2) * 0.3f));
        mvp.update(projection, view, bodyMatrix, legMatrix);
        mvp.sendToShader(bodyShader.getID(), bodyShader.mvpUniform);
        leg.draw(false);
    }
}
