package com.xbuilders.engine.rendering.block;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.render.Shader;
import org.joml.Matrix4f;

import java.io.IOException;

public class BlockShader extends Shader {

    public final int
            uniform_projViewMatrix,
            uniform_modelMatrix,
            textureLayerCountUniform,
            uniform_sun,
            uniform_torch;

   private static MVP projViewMatrix = new MVP();

    public BlockShader() {
        try {
            init(
                    ResourceUtils.localResource("/res/shaders/blockShader/block.vs"),
                    ResourceUtils.localResource("/res/shaders/blockShader/block.fs"));
        } catch (IOException e) {
            ErrorHandler.handleFatalError(e);
        }
        uniform_projViewMatrix = getUniformLocation("projViewMatrix");
        uniform_modelMatrix = getUniformLocation("modelMatrix");
        uniform_sun = getUniformLocation("sun");
        uniform_torch = getUniformLocation("torch");

        //Texture layers (array texture)
        textureLayerCountUniform = getUniformLocation("textureLayerCount");
        int textureLayers = ItemList.blocks.textures.layerCount;
        loadInt(textureLayerCountUniform, textureLayers - 1);
    }

    public void updateProjectionViewMatrix(Matrix4f projection, Matrix4f view) {
        projViewMatrix.update(projection, view);
        projViewMatrix.sendToShader(getID(), uniform_projViewMatrix);
    }

    @Override
    public void bindAttributes() {
    }

}

