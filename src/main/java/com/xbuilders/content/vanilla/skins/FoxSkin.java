package com.xbuilders.content.vanilla.skins;

import com.xbuilders.engine.server.players.Player;
import com.xbuilders.engine.client.player.Skin;
import com.xbuilders.engine.client.visuals.gameScene.rendering.entity.EntityMesh;
import com.xbuilders.engine.common.utils.ErrorHandler;
import com.xbuilders.engine.common.resource.ResourceUtils;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;

public class FoxSkin extends Skin {

    EntityMesh mesh;
    String texture;
    int textureID;

    public FoxSkin(Player position, String texture) {
        super("fox (" + texture + ")", position);
        this.texture = texture;
    }

    public void init() {
        mesh = new EntityMesh();
        try {
            mesh.loadFromOBJ(ResourceUtils.file("skins\\fox\\body.obj"));
            textureID =
                    TextureUtils.loadTextureFromFile(
                            ResourceUtils.file("skins\\fox\\" + texture + ".png"),
                    false).id;
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }

    @Override
    public void render() {
        modelMatrix.translate(0, player.aabb.size.y, 0);
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
        mesh.draw(false, textureID);
    }
}
