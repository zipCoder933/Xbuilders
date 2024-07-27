/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.rendering.entity;

import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.render.Shader;
import org.joml.Matrix4f;

import java.io.IOException;

/**
 * @author zipCoder933
 */
public class EntityShader extends Shader {

    public final int
            uniform_projViewMatrix,
            uniform_modelMatrix,
            uniform_sun,
            uniform_torch;

    static MVP mvp = new MVP();

    public EntityShader() {
        try {
            init(
                    ResourceUtils.localResource("/res/shaders/entityShader/shader.vs"),
                    ResourceUtils.localResource("/res/shaders/entityShader/shader.fs"));
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
        uniform_projViewMatrix = getUniformLocation("projViewMatrix");
        uniform_modelMatrix = getUniformLocation("modelMatrix");
        uniform_sun = getUniformLocation("sun");
        uniform_torch = getUniformLocation("torch");
//        textureUniform = getUniformLocation("texture");
    }

    public void updateProjectionViewMatrix(Matrix4f projection, Matrix4f view) {
        mvp.update(projection, view);
        mvp.sendToShader(getID(), uniform_projViewMatrix);
    }

    @Override
    public void bindAttributes() {
    }

}
