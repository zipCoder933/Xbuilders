package com.xbuilders.engine.client.visuals.rendering.entity;

import com.xbuilders.engine.server.model.items.Registrys;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;

import java.io.IOException;

public class EntityShader_ArrayTexture extends EntityShader {

    public final int uniform_textureLayerCount;

    public EntityShader_ArrayTexture() {
        super();
        //Texture layers (array texture)
        uniform_textureLayerCount = getUniformLocation("textureLayerCount");
        int textureLayers = Registrys.blocks.textures.layerCount;
        loadInt(uniform_textureLayerCount, textureLayers - 1);
    }

    public void loadShader() {
        try {
            init(
                    ResourceUtils.localResource("/res/shaders/entityShader/array texture.vs"),
                    ResourceUtils.localResource("/res/shaders/entityShader/array texture.fs"));
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
    }
}
