/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.mesh.chunkMesh;

import com.xbuilders.engine.mesh.chunkMesh.BufferSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.render.ShaderBase;
import org.joml.Vector3f;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public class ChunkShader extends ShaderBase {

    public final int mvpUniform,
            maxMult12bitsUniform,
            maxMult10bitsUniform,
            textureLayerCountUniform,
            viewDistanceUniform,
            skyColorUniform,
            animationTimeUniform;

    int animationTime = 0;
    long lastTick = 0;
    final int ANIMATION_SPEED = 100;

    public ChunkShader(int textureLayers) throws IOException {
        init(
                ResourceUtils.localResource("/res/shaders/chunkShader/shader.vs"),
                ResourceUtils.localResource("/res/shaders/chunkShader/shader.fs"));
        mvpUniform = getUniformLocation("MVP");
        maxMult12bitsUniform = getUniformLocation("maxMult12bits");
        maxMult10bitsUniform = getUniformLocation("maxMult10bits");
        textureLayerCountUniform = getUniformLocation("textureLayerCount");
        viewDistanceUniform = getUniformLocation("viewDistance");
        skyColorUniform = getUniformLocation("skyColor");
        animationTimeUniform = getUniformLocation("animationTime");


        loadFloat(maxMult10bitsUniform, BufferSet.maxMult10bits);
        loadFloat(maxMult12bitsUniform, BufferSet.maxMult12bits);
        loadInt(textureLayerCountUniform, textureLayers - 1);
    }

    public void setViewDistance(int viewDistance) {
        loadInt(viewDistanceUniform, viewDistance);
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
