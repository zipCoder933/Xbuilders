package com.xbuilders.content.vanilla.items.entities.animal;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.client.visuals.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.RandomUtils;
import com.xbuilders.content.vanilla.items.entities.animal.mobile.LandAnimal;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class StaticLandAnimal extends LandAnimal {
    EntityMesh body;
    int[] textures;
    int textureIndex;

    public StaticLandAnimal(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
        aabb.setOffsetAndSize(0.8f, 0.9f, 0.8f, true);
        jumpOverBlocks = true;
        frustumSphereRadius = 2;
    }


    public static class StaticLandAnimal_StaticData {
        public final EntityMesh body;
        public final int[] textures;

        public StaticLandAnimal_StaticData(String bodyMesh, String texturesDir) throws IOException {
            body = new EntityMesh();
            body.loadFromOBJ(ResourceUtils.resource(bodyMesh));

            //Generate textures
            File[] textureFiles = ResourceUtils.resource(texturesDir).listFiles();
            textures = new int[textureFiles.length];
            for (int i = 0; i < textureFiles.length; i++) {
                textures[i] = Objects.requireNonNull(
                        TextureUtils.loadTexture(textureFiles[i].getAbsolutePath(), false)).id;
            }
        }
    }

    @Override
    public void serializeDefinitionData(Output output, Kryo kyro) throws IOException {
        super.serializeDefinitionData(output, kyro);
        kyro.writeObject(output, textureIndex);
    }


    public void loadDefinitionData(Input input, Kryo kyro) throws IOException {
        super.loadDefinitionData(input, kyro);

        try {
            StaticLandAnimal_StaticData ead = getStaticData();
            this.body = ead.body;
            this.textures = ead.textures;
        } catch (IOException e) {
            ErrorHandler.report(e);
        }

        if (input.available() > 0) {
            textureIndex = kyro.readObject(input, int.class);
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