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
    SkyBoxMesh skyBoxMesh;
    SkyBoxShader skyBoxShader;
    BufferedImage skyImage;

    public SkyBackground() throws IOException {
        skyBoxMesh = new SkyBoxMesh();
        skyBoxMesh.loadFromOBJ(ResourceUtils.resource("weather\\skybox.obj"));

        File texture = ResourceUtils.resource("weather\\skybox.png");
        skyBoxMesh.setTexture(texture);
        skyImage = ImageIO.read(texture);
        skyBoxShader = new SkyBoxShader();
    }


    float textureXPan;
    Vector3f defaultTint = new Vector3f(1, 1, 1);
    Vector3f defaultSkyColor = new Vector3f(0.5f, 0.5f, 0.5f);

    public static double calculateLightness(double x) {
        double lightness = (4 * (x - 0.5) * (x - 0.5));
        lightness = lightness * 2 + 0.18f;
        if (lightness > 1) lightness = 1;
        return lightness;
    }

    private void applyTint() {
        // Get the pixel color at a specific location
        int pixelColor = skyImage.getRGB((int) (skyImage.getWidth() * textureXPan), skyImage.getHeight() - 1);

        // Extract the RGB values from the pixel color
        int red = (pixelColor >> 16) & 0xFF;
        int green = (pixelColor >> 8) & 0xFF;
        int blue = pixelColor & 0xFF;
        defaultSkyColor.set(red / 255f, green / 255f, blue / 255f);

        float lightness = (float) calculateLightness(textureXPan);
        if (defaultSkyColor.x > defaultSkyColor.z) { //If red is more dominant than blue
            float redDifference = (defaultSkyColor.x - defaultSkyColor.z) * 0.5f; //Choose how much % should be tinted red
            defaultTint.set(lightness + redDifference, lightness, lightness);
        } else defaultTint.set(lightness, lightness, lightness);
        GameScene.world.chunkShader.setTintAndFogColor(defaultSkyColor, defaultTint);
        Entity.shader.setTint(defaultTint);
    }

    public void setTimeOfDay(float timeOfDay) {
        textureXPan = MathUtils.clamp(timeOfDay, 0, 1);
    }

    public void draw(Matrix4f projection, Matrix4f view) {
        GL30.glDisable(GL30.GL_DEPTH_TEST);
        skyBoxShader.bind();
        skyBoxShader.updateMatrix(projection, view);

        textureXPan += 0.0002f;
        if (textureXPan > 1) {
            textureXPan = 0;
        }

        skyBoxShader.loadFloat(skyBoxShader.uniform_cycle_value, textureXPan);
        skyBoxMesh.draw();

        if (MainWindow.frameCount % 20 == 0) {
            applyTint();
        }

        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }
}
