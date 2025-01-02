/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.content.vanilla.items.entities.animal.fish;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh;
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

public class FishB extends FishAnimal {

    public FishB(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
    }

    static EntityMesh body;
    static int[] textures;

    int textureIndex;

    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);//Always call super!

        if (body == null) {
            body = new EntityMesh();

            try {
                body.loadFromOBJ(ResourceUtils.resource("items\\entity\\animal\\fish\\fish_B.obj"));
                File[] textureFiles = ResourceUtils.resource("items\\entity\\animal\\fish\\textures\\fish_B").listFiles();
                textures = new int[textureFiles.length];
                for (int i = 0; i < textureFiles.length; i++) {
                    textures[i] = Objects.requireNonNull(TextureUtils.loadTexture(textureFiles[i].getAbsolutePath(), false)).id;
                }

            } catch (IOException ex) {
                ErrorHandler.report(ex);
            }
        }

        if (hasData) {
            textureIndex = node.get(JSON_SPECIES).asInt();
            textureIndex = MathUtils.clamp(textureIndex, 0, textures.length - 1);
        } else textureIndex = RandomUtils.random.nextInt(textures.length);
    }

    @Override
    public final void renderFish() {
        body.draw(false, textures[textureIndex]);
    }
}
