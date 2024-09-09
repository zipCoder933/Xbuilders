package com.xbuilders.engine.world.skybox;

import com.xbuilders.engine.utils.ResourceUtils;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30;

import java.io.IOException;

public class SkyBackground {
    SkyBoxMesh skyBoxMesh;
    SkyBoxShader skyBoxShader;

    public SkyBackground() throws IOException {
        skyBoxMesh = new SkyBoxMesh();
        skyBoxMesh.loadFromOBJ(ResourceUtils.resource("weather\\skybox.obj"));
        skyBoxMesh.setTexture(ResourceUtils.resource("weather\\skybox.png"));

        skyBoxShader = new SkyBoxShader();
    }

    float time;

    public void draw(Matrix4f projection, Matrix4f view) {
//        GL30.glDisable(GL30.GL_DEPTH_TEST);
//        skyBoxShader.bind();
//        skyBoxShader.updateMatrix(projection, view);
//        time += 0.001f;
//        skyBoxShader.loadFloat(skyBoxShader.uniform_cycle_value, time);
//        if (time > 1) {
//            time = 0;
//        }
//        skyBoxMesh.draw(true);
//        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }
}
