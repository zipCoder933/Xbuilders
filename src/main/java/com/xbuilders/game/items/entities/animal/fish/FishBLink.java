/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.items.entities.animal.fish;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.RandomUtils;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


/**
 * @author zipCoder933
 */
public class FishBLink extends EntitySupplier {

    public FishBLink(MainWindow window, int id, String name) {
        super(id, name, () -> new FishObject(id, window));
        setIcon("fish egg.png");
        tags.add("animal");
        tags.add("fish");
    }


    public static class FishObject extends FishAnimal {

        public FishObject(int id, MainWindow window) {
            super(id, window);
        }

        static EntityMesh body;
        static int[] textures;

        int textureIndex;

        public void initializeOnDraw(byte[] loadBytes) {
            super.initializeOnDraw(loadBytes);//Always call super!
            if (body == null) {
                body = new EntityMesh();

                try {
                    body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\fish\\fish_B.obj"));
                    File[] textureFiles = ResourceUtils.resource("items\\entity\\animal\\fish\\textures\\fish_B").listFiles();
                    textures = new int[textureFiles.length];
                    for (int i = 0; i < textureFiles.length; i++) {
                        textures[i] = Objects.requireNonNull(
                                TextureUtils.loadTexture(textureFiles[i].getAbsolutePath(), false)).id;
                    }

                } catch (IOException ex) {
                    ErrorHandler.report(ex);
                }
            }

            if (loadBytes != null) {
                textureIndex = MathUtils.clamp(loadBytes[0], 0, textures.length - 1);
            } else textureIndex = RandomUtils.random.nextInt(textures.length);
        }

        @Override
        public final void renderFish() {
            body.draw(false, textures[textureIndex]);
        }
    }
}