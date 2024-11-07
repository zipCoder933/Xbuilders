/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.chunk;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.rendering.chunk.meshers.bufferSet.vertexSet.CompactVertexSet;
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
public class ChunkShader extends Shader {

    public static final String CHUNK_SHADER_DIR = "/res/shaders/chunkShader";

    public final int mvpUniform;

    private final int
            maxMult12bitsUniform,
            maxMult10bitsUniform,
            textureLayerCountUniform,
            viewDistanceUniform,
            tintUniform,
            fogColorUniform,
            animationTimeUniform,
            flashlightDistanceUniform,
            colorUniform;

    int animationTime = 0;
    long lastTick = 0;
    final int ANIMATION_SPEED = 100;

    public final static int FRAG_MODE_CHUNK = 0;
    public final static int FRAG_MODE_DIRECT = 1;
    public final static int FRAG_MODE_TEST = 2;


    public ChunkShader(int fragmentShader) {
        int textureLayers = Registrys.blocks.textures.layerCount;
        try {
            File fragShader = null;
            switch (fragmentShader) {
                case FRAG_MODE_CHUNK:
                    fragShader = ResourceUtils.localResource(CHUNK_SHADER_DIR + "/frag.glsl");
                    break;
                case FRAG_MODE_DIRECT:
                    fragShader = ResourceUtils.localResource(CHUNK_SHADER_DIR + "/frag_direct.glsl");
                    break;
                case FRAG_MODE_TEST:
                    fragShader = ResourceUtils.localResource(CHUNK_SHADER_DIR + "/frag_test.glsl");
                    break;
            }
            init(ResourceUtils.localResource(CHUNK_SHADER_DIR + "/vertex.glsl"),
                    fragShader);
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
        mvpUniform = getUniformLocation("MVP");
        maxMult12bitsUniform = getUniformLocation("maxMult12bits");
        maxMult10bitsUniform = getUniformLocation("maxMult10bits");
        textureLayerCountUniform = getUniformLocation("textureLayerCount");
        viewDistanceUniform = getUniformLocation("viewDistance");
        animationTimeUniform = getUniformLocation("animationTime");
        flashlightDistanceUniform = getUniformLocation("flashlightDistance");
        colorUniform = getUniformLocation("solidColor");
        tintUniform = getUniformLocation("tint");
        fogColorUniform = getUniformLocation("fogColor");

        loadFloat(maxMult10bitsUniform, CompactVertexSet.maxMult10bits);
        loadFloat(maxMult12bitsUniform, CompactVertexSet.maxMult12bits);
        loadInt(textureLayerCountUniform, textureLayers - 1);
        loadVec3f(tintUniform, new Vector3f(1, 1, 1));
    }

    public void setFlashlightDistance(float distance) {
        distance = MathUtils.clamp(distance, 0, 100);
        loadFloat(flashlightDistanceUniform, distance);
    }

    //It makes sense to put both in the same place, After all, I notices some strange artifacts when rendering transparent fog
    public void setTintAndFogColor(Vector3f fogColor, Vector3f tint) {
        loadVec3f(tintUniform, tint);
        loadVec3f(fogColorUniform, fogColor);
    }

    final int MIN_VIEW_DIST = 100;

    public void setViewDistance(int viewDistance) {
        loadInt(viewDistanceUniform, Math.min(MIN_VIEW_DIST, viewDistance));
//        if (Entity.shader != null) Entity.shader.loadInt(Entity.shader.uniform_view_distance, viewDistance);
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


    @Override
    public void bindAttributes() {
    }

}
