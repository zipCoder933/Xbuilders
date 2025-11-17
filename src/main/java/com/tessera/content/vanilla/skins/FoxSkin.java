package com.tessera.content.vanilla.skins;

import com.tessera.engine.server.players.Player;
import com.tessera.engine.client.player.Skin;
import com.tessera.engine.client.visuals.gameScene.rendering.entity.EntityMesh;
import com.tessera.engine.utils.ErrorHandler;
import com.tessera.engine.utils.resource.ResourceUtils;
import com.tessera.window.utils.texture.TextureUtils;

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
