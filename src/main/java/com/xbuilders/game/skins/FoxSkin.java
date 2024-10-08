package com.xbuilders.game.skins;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.Skin;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;

import java.io.IOException;

public class FoxSkin extends Skin {

    EntityMesh mesh;
    String texture;

    public FoxSkin(Player position, String texture) {
        super("fox (" + texture + ")", position);
        this.texture = texture;
    }

    public void init() {
        mesh = new EntityMesh();
        try {
            mesh.loadFromOBJ(ResourceUtils.resource("skins\\fox\\body.obj"));
            mesh.setTexture(ResourceUtils.resource("skins\\fox\\" + texture + ".png"));
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }

    @Override
    public void render() {
        modelMatrix.translate(0, player.aabb.size.y, 0);
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
        mesh.draw(false);
    }
}
