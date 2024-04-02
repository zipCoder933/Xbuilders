/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.entity;

import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.render.ShaderBase;

import java.io.IOException;

/**
 *
 * @author zipCoder933
 */
public class EntityShader extends ShaderBase {

    public final int mvpUniform;

    public EntityShader() throws IOException {
        init(
                ResourceUtils.localResource("/res/shaders/entityShader/shader.vs"),
                ResourceUtils.localResource("/res/shaders/entityShader/shader.fs"));
        mvpUniform = getUniformLocation("MVP");
//        textureUniform = getUniformLocation("texture");
    }

    @Override
    public void bindAttributes() {
    }

}
