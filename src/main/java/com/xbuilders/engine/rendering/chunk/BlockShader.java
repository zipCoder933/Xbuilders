/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk;

import com.xbuilders.engine.items.ItemList;
import com.xbuilders.engine.rendering.chunk.mesh.bufferSet.vertexSet.VertexSet;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.render.Shader;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.io.File;
import java.io.IOException;

/**
 * @author zipCoder933
 */
public class BlockShader extends Shader {

    public final int mvpUniform,
            maxMult12bitsUniform,
            maxMult10bitsUniform,
            textureLayerCountUniform,
            viewDistanceUniform,
            skyColorUniform,
            animationTimeUniform,
            flashlightDistanceUniform,
            colorUniform;

    int animationTime = 0;
    long lastTick = 0;
    final int ANIMATION_SPEED = 100;

    public final static int FRAG_MODE_CHUNK = 0;
    public final static int FRAG_MODE_DIRECT = 1;
    public final static int FRAG_MODE_TEST = 2;


    public BlockShader(int fragmentShader) {
        int textureLayers = ItemList.blocks.textures.layerCount;
        try {
            File fragShader = null;
            switch (fragmentShader) {
                case FRAG_MODE_CHUNK:
                    fragShader = ResourceUtils.localResource("/res/shaders/blockShader/frag.glsl");
                    break;
                case FRAG_MODE_DIRECT:
                    fragShader = ResourceUtils.localResource("/res/shaders/blockShader/frag_direct.glsl");
                    break;
                case FRAG_MODE_TEST:
                    fragShader = ResourceUtils.localResource("/res/shaders/blockShader/frag_test.glsl");
                    break;
            }
            init(ResourceUtils.localResource("/res/shaders/blockShader/vertex.glsl"),
                    fragShader);
        } catch (IOException e) {
            ErrorHandler.handleFatalError(e);
        }
        mvpUniform = getUniformLocation("MVP");
        maxMult12bitsUniform = getUniformLocation("maxMult12bits");
        maxMult10bitsUniform = getUniformLocation("maxMult10bits");
        textureLayerCountUniform = getUniformLocation("textureLayerCount");
        viewDistanceUniform = getUniformLocation("viewDistance");
        skyColorUniform = getUniformLocation("skyColor");
        animationTimeUniform = getUniformLocation("animationTime");
        flashlightDistanceUniform = getUniformLocation("flashlightDistance");
        colorUniform = getUniformLocation("solidColor");

        loadFloat(maxMult10bitsUniform, VertexSet.maxMult10bits);
        loadFloat(maxMult12bitsUniform, VertexSet.maxMult12bits);
        loadInt(textureLayerCountUniform, textureLayers - 1);
    }

    public void setFlashlightDistance(float distance) {
        distance = MathUtils.clamp(distance, 0, 100);
        loadFloat(flashlightDistanceUniform, distance);
    }

    public void setViewDistance(int viewDistance) {
        loadInt(viewDistanceUniform, viewDistance);
    }

    public void setColorMode(float r, float g, float b) {
        loadVec4f(colorUniform, new Vector4f(r, g, b, 1));
    }

    public void setTextureMode() {
        loadVec4f(colorUniform, new Vector4f(0));
    }

    public void tickAnimation() {
        long time = System.currentTimeMillis();
        if (time - lastTick > ANIMATION_SPEED) {
            lastTick = time;
            animationTime++;
            loadInt(animationTimeUniform, animationTime);
        }
    }

    public void setSkyColor(Vector3f color) {
        loadVec3f(skyColorUniform, color);
    }

    @Override
    public void bindAttributes() {
    }

}
