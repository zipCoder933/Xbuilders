/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.game.model.world.skybox;

import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.render.Shader;
import org.joml.Matrix4f;

import java.io.IOException;

/**
 * @author zipCoder933
 */
 class SkyBoxShader extends Shader {



    public final int uniform_cycle_value,
            uniform_projViewMatrix;

    static MVP mvp = new MVP();

    public SkyBoxShader() {
        try {
            init(
                    ResourceUtils.localResource("/res/shaders/skybox/sky_shader.vs"),
                    ResourceUtils.localResource("/res/shaders/skybox/sky_shader.fs"));
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
        uniform_cycle_value = getUniformLocation("cycle_value");
        uniform_projViewMatrix = getUniformLocation("projViewMatrix");
    }

    public void updateMatrix(Matrix4f projection, Matrix4f view) {
        mvp.update(projection, view);
        mvp.sendToShader(getID(), uniform_projViewMatrix);
    }

    @Override
    public void bindAttributes() {
    }

}
