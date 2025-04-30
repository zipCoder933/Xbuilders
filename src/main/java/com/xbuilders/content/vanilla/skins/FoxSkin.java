package com.xbuilders.content.vanilla.skins;

import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.common.players.Player;
import com.xbuilders.engine.common.players.Skin;
import com.xbuilders.engine.client.visuals.gameScene.rendering.entity.EntityMesh;
import com.xbuilders.engine.common.utils.LoggingUtils;
import com.xbuilders.engine.common.resource.ResourceUtils;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;
import java.util.logging.Level;

import static com.xbuilders.Main.LOGGER;

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
            LOGGER.log(Level.INFO, "Error", e);
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
