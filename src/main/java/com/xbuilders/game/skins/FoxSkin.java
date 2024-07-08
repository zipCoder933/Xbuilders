package com.xbuilders.game.skins;

import com.xbuilders.engine.player.Player;
import com.xbuilders.engine.player.Skin;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.worldInteraction.collision.EntityAABB;
import com.xbuilders.window.utils.obj.OBJ;
import com.xbuilders.window.utils.obj.OBJLoader;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;
import java.util.Objects;

public class FoxSkin extends Skin {

    EntityMesh mesh;

    public FoxSkin(Player position) {
        super(position);
        mesh=new EntityMesh();
        try {
            mesh.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\fox\\body.obj"));
            mesh.setTexture(ResourceUtils.resource("items\\entity\\animal\\fox\\red.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render() {
        modelMatrix.translate(0,player.aabb.size.y,0);
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(),shader.uniform_modelMatrix);
        mesh.draw(false);
    }
}
