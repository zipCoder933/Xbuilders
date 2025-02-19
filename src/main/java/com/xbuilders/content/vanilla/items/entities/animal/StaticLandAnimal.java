package com.xbuilders.content.vanilla.items.entities.animal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.visuals.gameScene.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.RandomUtils;
import com.xbuilders.content.vanilla.items.entities.animal.mobile.LandAnimal;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class StaticLandAnimal extends LandAnimal {


    public StaticLandAnimal( long uniqueIdentifier, ClientWindow window) {
        super( uniqueIdentifier, window);
        aabb.setOffsetAndSize(0.8f, 0.9f, 0.8f, true);
        jumpOverBlocks = true;
        frustumSphereRadius = 2;
    }


    public static class StaticLandAnimal_StaticData {
        public final EntityMesh body;
        public final int[] textures;

        public StaticLandAnimal_StaticData(String bodyMesh, String texturesDir) throws IOException {
            body = new EntityMesh();
            body.loadFromOBJ(ResourceUtils.file(bodyMesh));

            //Generate textures
            File[] textureFiles = ResourceUtils.file(texturesDir).listFiles();
            textures = new int[textureFiles.length];
            for (int i = 0; i < textureFiles.length; i++) {
                textures[i] = Objects.requireNonNull(
                        TextureUtils.loadTextureFromFile(textureFiles[i], false)).id;
            }
        }
    }

    @Override
    public void serializeDefinitionData(JsonGenerator generator) throws IOException {
        super.serializeDefinitionData(generator);
        generator.writeNumberField(JSON_SPECIES, textureIndex);
    }

    /**
     * These are just references to the static data, so they arent taking up space in the instance.
     */
    EntityMesh body;
    int[] textures;
    int textureIndex;

    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);
        StaticLandAnimal_StaticData ead = getStaticData();
        this.body = ead.body;
        this.textures = ead.textures;

        if (hasData) {
            textureIndex = node.get(JSON_SPECIES).asInt();
            textureIndex = MathUtils.clamp(textureIndex, 0, textures.length - 1);
        } else textureIndex = RandomUtils.random.nextInt(textures.length);
    }

    public abstract StaticLandAnimal_StaticData getStaticData() throws IOException;

    @Override
    public void animal_drawBody() {
        shader.bind();
        modelMatrix.update();
        modelMatrix.sendToShader(shader.getID(), shader.uniform_modelMatrix);
        body.draw(false, textures[textureIndex]);
    }
}