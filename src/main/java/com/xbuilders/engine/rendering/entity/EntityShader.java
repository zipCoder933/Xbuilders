/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.entity;

import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.render.Shader;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public class EntityShader extends Shader {

    public final int mvpUniform, sunUniform, torchUniform;

    public EntityShader() {
        try {
            init(
                    ResourceUtils.localResource("/res/shaders/entityShader/shader.vs"),
                    ResourceUtils.localResource("/res/shaders/entityShader/shader.fs"));
        } catch (IOException e) {
            ErrorHandler.handleFatalError(e);
        }
        mvpUniform = getUniformLocation("MVP");
        sunUniform = getUniformLocation("sun");
        torchUniform = getUniformLocation("torch");
//        textureUniform = getUniformLocation("texture");
    }

    @Override
    public void bindAttributes() {
    }

}
