package com.xbuilders.game.vanilla.items.entities.animal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.utils.math.RandomUtils;
import com.xbuilders.game.vanilla.items.entities.animal.mobile.LandAnimal;
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

    @Override
    public byte[] toBytes() throws IOException {
        return new byte[]{(byte) textureIndex};
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

    public void initializeOnDraw(byte[] state) {
        super.initializeOnDraw(state);

        try {
            StaticLandAnimal_StaticData ead = getStaticData();
            this.body = ead.body;
            this.textures = ead.textures;
        } catch (IOException e) {
            ErrorHandler.report(e);
        }

        if (state != null) {
            textureIndex = MathUtils.clamp(state[0], 0, textures.length - 1);
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