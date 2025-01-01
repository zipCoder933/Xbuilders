/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.entities.animal.fish;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.RandomUtils;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author zipCoder933
 */
public class FishA extends FishAnimal {
    public FishA(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
    }

    static EntityMesh body;
    static int[] textures;

    int textureIndex;

    @Override
    public byte[] save() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes(super.save());
        baos.write((byte) textureIndex);
        return baos.toByteArray();
    }

    public void load(byte[] loadBytes, AtomicInteger start) {
        super.load(loadBytes, start);//Always call super!
        if (body == null) {
            body = new EntityMesh();

            try {
                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\fish\\fish_A.obj"));
                File[] textureFiles = ResourceUtils.resource("items\\entity\\animal\\fish\\textures\\fish_A").listFiles();
                textures = new int[textureFiles.length];
                for (int i = 0; i < textureFiles.length; i++) {
                    textures[i] = Objects.requireNonNull(
                            TextureUtils.loadTexture(textureFiles[i].getAbsolutePath(), false)).id;
                }

            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }

        if (loadBytes.length > 0) {
            textureIndex = MathUtils.clamp(loadBytes[0], 0, textures.length - 1);
        } else textureIndex = RandomUtils.random.nextInt(textures.length);
    }


    @Override
    public final void renderFish() {
        body.draw(false, textures[textureIndex]);
    }
}
