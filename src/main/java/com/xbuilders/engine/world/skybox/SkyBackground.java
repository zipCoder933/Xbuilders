package com.xbuilders.engine.world.skybox;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SkyBackground {

    private static final float UPDATE_SPEED = 0.00001f;
    SkyBoxMesh skyBoxMesh;
    SkyBoxShader skyBoxShader;
    BufferedImage skyImage;
    MainWindow mainWindow;

    public SkyBackground(MainWindow mainWindow) throws IOException {
        skyBoxMesh = new SkyBoxMesh();
        this.mainWindow = mainWindow;
        skyBoxMesh.loadFromOBJ(ResourceUtils.resource("weather\\skybox.obj"));

        File texture = ResourceUtils.resource("weather\\skybox.png");
        skyBoxMesh.setTexture(texture);
        skyImage = ImageIO.read(texture);
        skyBoxShader = new SkyBoxShader();
    }


    double textureXPan;
    Vector3f defaultTint = new Vector3f(1, 1, 1);
    Vector3f defaultSkyColor = new Vector3f(0.5f, 0.5f, 0.5f);

    private double calculateLightness(double x) {
        double light = (double) (skyImage.getRGB((int) (skyImage.getWidth() * textureXPan), skyImage.getHeight() - 1) & 0xFF) / 255;
        if (light < 0.18) light = 0.18;
        return light;
    }

    private void applyTint() {
        double lightness = calculateLightness(textureXPan);
        int skyColor = skyImage.getRGB((int) (skyImage.getWidth() * textureXPan), skyImage.getHeight() - 2);
        int red = (skyColor >> 16) & 0xFF;
        int green = (skyColor >> 8) & 0xFF;
        int blue = skyColor & 0xFF;
        defaultSkyColor.set(red / 255f, green / 255f, blue / 255f);

        if (defaultSkyColor.x > defaultSkyColor.z) { //If red is more dominant than blue
            float redDifference = (defaultSkyColor.x - defaultSkyColor.z) * 0.3f; //Choose how much % should be tinted red
            defaultTint.set(lightness + redDifference, lightness, lightness);
        } else defaultTint.set(lightness, lightness, lightness);
        GameScene.world.chunkShader.setTintAndFogColor(defaultSkyColor, defaultTint);
        if (Entity.shader != null) {
            Entity.shader.setTint(defaultTint);
        }
        if (Entity.arrayTextureShader != null) {
            Entity.arrayTextureShader.setTint(defaultTint);
        }
    }

    public void setTimeOfDay(float timeOfDay) {
        textureXPan = MathUtils.clamp(timeOfDay, 0, 1);
    }

    public void draw(Matrix4f projection, Matrix4f view) {
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        skyBoxShader.bind();
        skyBoxShader.updateMatrix(projection, view);

        textureXPan += UPDATE_SPEED * ((double) mainWindow.frameDeltaSec);
        if (textureXPan > 1) {
            textureXPan = 0;
        }

        skyBoxShader.loadFloat(skyBoxShader.uniform_cycle_value, (float) textureXPan);
        skyBoxMesh.draw();

        if (MainWindow.frameCount % 20 == 0) {
            applyTint();  //System.out.println(textureXPan);
        }
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }
}
