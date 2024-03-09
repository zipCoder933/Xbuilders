/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.items.block.blockIconRendering;

import com.xbuilders.engine.mesh.BufferSet;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.render.ShaderBase;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public class BlockShader extends ShaderBase {

    public final int mvpUniform, maxMult12bitsUniform, maxMult10bitsUniform, textureLayerCountUniform;

    public BlockShader(int textureLayers) throws IOException {
        init(
                ResourceUtils.localResource("/res/Shaders/iconRTT/iconRTT.vs"),
                ResourceUtils.localResource("/res/Shaders/iconRTT/iconRTT.fs"));
        mvpUniform = getUniformLocation("MVP");
        maxMult12bitsUniform = getUniformLocation("maxMult12bits");
        maxMult10bitsUniform = getUniformLocation("maxMult10bits");
        textureLayerCountUniform = getUniformLocation("textureLayerCount");
        loadFloat(maxMult10bitsUniform, BufferSet.maxMult10bits);
        loadFloat(maxMult12bitsUniform, BufferSet.maxMult12bits);
        loadInt(textureLayerCountUniform, textureLayers - 1);
    }

    @Override
    public void bindAttributes() {
    }

}
