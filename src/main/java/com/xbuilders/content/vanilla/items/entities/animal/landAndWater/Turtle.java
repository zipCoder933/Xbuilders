/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.entities.animal.landAndWater;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.RandomUtils;
import com.xbuilders.window.render.MVP;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zipCoder933
 */


public class Turtle extends LandAndWaterAnimal {

    static EntityMesh left_fin, right_fin, left_back_fin, right_back_fin, body;
    static int[] textures;

    int textureIndex;

    public Turtle(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
        aabb.setOffsetAndSize(1f, 1f, 1f, true);
        frustumSphereRadius = 3;
        setMaxSpeed(0.1f);
        setActivity(0.5f);
    }


    @Override
    public byte[] save() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes(super.save());
        baos.write((byte) textureIndex);
        return baos.toByteArray();
    }

    @Override
    public void load(byte[] loadBytes, AtomicInteger start) {
        if (body == null) {
            body = new EntityMesh();
            left_fin = new EntityMesh();
            right_fin = new EntityMesh();
            left_back_fin = new EntityMesh();
            right_back_fin = new EntityMesh();
            try {
                File[] textureFiles = ResourceUtils.resource("items\\entity\\animal\\turtle\\textures").listFiles();
                textures = new int[textureFiles.length];
                for (int i = 0; i < textureFiles.length; i++) {
                    textures[i] = Objects.requireNonNull(
                            TextureUtils.loadTexture(textureFiles[i].getAbsolutePath(), false)).id;
                }

                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\body.obj"));
                left_fin.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\left_fin.obj"));
                right_fin.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\right_fin.obj"));

                left_back_fin.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\left_back_fin.obj"));
                right_back_fin.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\turtle\\right_back_fin.obj"));
            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }

        if (loadBytes.length > 0) {
            textureIndex = MathUtils.clamp(loadBytes[0], 0, textures.length - 1);
        } else textureIndex = RandomUtils.random.nextInt(textures.length);
    }

    @Override
    public void animal_drawBody() {
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
        body.draw(false, textures[textureIndex]);

        float animationTarget = 0f;
        if (getWalkAmt() > 0) {
            animationTarget = MathUtils.map(getWalkAmt(), 0, getMaxSpeed(), 0, 0.4f);
        }
        drawFin(right_fin, 0, 0, ONE_SIXTEENTH * 7,
                animationTarget, 0.0f, 0.4f);

        drawFin(left_fin, 0, 0, ONE_SIXTEENTH * 7,
                animationTarget, 1.5f, 0.4f);

        drawFin(left_back_fin,
                0, 0, ONE_SIXTEENTH * -4,
                animationTarget, 0.0f, 0.05f);

        drawFin(right_back_fin,
                0, 0, ONE_SIXTEENTH * -4,
                animationTarget, 0.0f, -0.05f);
    }

    private static final float ONE_SIXTEENTH = (float) 1 / 16;
    MVP finModelMatrix = new MVP();

    private void drawFin(EntityMesh fin,
                         float x, float y, float z,
                         float animationSpeed, float animationAdd, float multiplier) {

        finModelMatrix.set(modelMatrix).translate(x, y, z);
        if (animationSpeed != 0) {
            float rot = (float) Math.sin((MainWindow.frameCount * animationSpeed) + animationAdd) * multiplier;
            finModelMatrix.rotateY(rot);
        }
        finModelMatrix.update();
        finModelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
        fin.draw(false, textures[textureIndex]);
    }

}