package com.xbuilders.engine.server.world.skybox;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.server.entity.Entity;
import com.xbuilders.engine.utils.ResourceUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SkyBackground {

    SkyBoxMesh skyBoxMesh;
    SkyBoxShader skyBoxShader;
    BufferedImage skyImage;
    ClientWindow mainWindow;

    public SkyBackground(ClientWindow mainWindow) throws IOException {
        skyBoxMesh = new SkyBoxMesh();
        this.mainWindow = mainWindow;
        skyBoxMesh.loadFromOBJ(ResourceUtils.file("weather\\skybox.obj"));

        File texture = ResourceUtils.file("weather\\skybox.png");
        skyBoxMesh.setTexture(texture);
        skyImage = ImageIO.read(texture);
        skyBoxShader = new SkyBoxShader();
    }

    private static final double UPDATE_SPEED = 0.0000005f;

    double offset;
    double textureXPan;
    double lightness;
    Vector3f defaultTint = new Vector3f(1, 1, 1);
    Vector3f defaultSkyColor = new Vector3f(0.5f, 0.5f, 0.5f);

    public double getLightness() {
        return lightness;
    }

    private double calculateLightLevel(double x) {
        lightness = (double) (skyImage.getRGB((int) (skyImage.getWidth() * textureXPan), skyImage.getHeight() - 1) & 0xFF) / 255;
        if (lightness < 0.18) lightness = 0.18;
        return lightness;
    }

    public void update(boolean progressDay) {
        if (ClientWindow.frameCount % 10 == 0) {

            //Move the sky texture
            if (progressDay) {
                updateTexturePan();
            }

            //Calculate the light level
            calculateLightLevel(textureXPan);

            //Calculate the sky color
            int skyColor = skyImage.getRGB((int) (skyImage.getWidth() * textureXPan), skyImage.getHeight() - 2);
            int red = (skyColor >> 16) & 0xFF;
            int green = (skyColor >> 8) & 0xFF;
            int blue = skyColor & 0xFF;
            defaultSkyColor.set(red / 255f, green / 255f, blue / 255f);

            if (defaultSkyColor.x > defaultSkyColor.z) { //If red is more dominant than blue
                float redDifference = (defaultSkyColor.x - defaultSkyColor.z) * 0.3f; //Choose how much % should be tinted red
                defaultTint.set(lightness + redDifference, lightness, lightness);
            } else defaultTint.set(lightness, lightness, lightness);
            Server.world.chunkShader.setTintAndFogColor(defaultSkyColor, defaultTint);
            if (Entity.shader != null) {
                Entity.shader.setTint(defaultTint);
            }
            if (Entity.arrayTextureShader != null) {
                Entity.arrayTextureShader.setTint(defaultTint);
            }
        }
    }

    private void updateTexturePan() {
        double time = System.currentTimeMillis() * UPDATE_SPEED;
        textureXPan = (time + offset) % 1.0;
    }


    public void setTimeOfDay(double start) {
        double time = System.currentTimeMillis() * UPDATE_SPEED;
        //Take the normalized time plus start minus current time
        offset = Math.floor(time) + start - time;
        updateTexturePan();
    }

    public double getTimeOfDay() {
        return textureXPan;
    }

    public void draw(Matrix4f projection, Matrix4f view) {
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        skyBoxShader.bind();
        skyBoxShader.updateMatrix(projection, view);
        skyBoxShader.loadFloat(skyBoxShader.uniform_cycle_value, (float) textureXPan);
        skyBoxMesh.draw();
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }

}
