package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.engine.items.EntityLink;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.utils.obj.OBJLoader;
import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class QuadPedalLandAnimalLink extends EntityLink {

    EntityMesh body, sitting;
    String textureName;
    LegPair legs;
    protected String bodyPath = "items\\entity\\animal\\horse\\horse\\body.obj";
    protected String legPath = "items\\entity\\animal\\horse\\horse\\leg.obj";
    protected String texturePrePath = "items\\entity\\animal\\horse\\";
    protected String sittingModel = null;
    boolean rideable = true;
    public Consumer<Integer> modelInit = (texture) -> {

    };

    public QuadPedalLandAnimalLink(BaseWindow window, int id, String name, String textureName) {
        super(id, name, () -> new QuadPedalEntity<QuadPedalLandAnimalLink>(window));

        this.textureName = textureName;
        setIcon("horse egg.png");
        tags.add("animal");
    }

    public QuadPedalLandAnimalLink(int id, String name, String textureName, Supplier<Entity> e) {
        super(id, name, e);
        this.textureName = textureName;
        setIcon("horse egg.png");
        tags.add("animal");
    }

    private void initMesh(File bodyOBJ, File legOBJ) {
        /**
         * We only need 1 model. We can reuse the same model for each entity to
         * save vram!
         */
        if (body == null) {
            try {
                int texture = Objects.requireNonNull(TextureUtils.loadTexture(
                        ResourceUtils.resource(texturePrePath + textureName).getAbsolutePath(),
                        false)).id;

                body = new EntityMesh();
                body.loadFromOBJ(OBJLoader.loadModel(bodyOBJ));
                body.setTextureID(texture);

                if (sittingModel != null) {
                    sitting = new EntityMesh();
                    sitting.loadFromOBJ(OBJLoader.loadModel(ResourceUtils.resource(sittingModel)));
                    sitting.setTextureID(texture);
                }

                EntityMesh legsModel = new EntityMesh();
                legsModel.loadFromOBJ(OBJLoader.loadModel(legOBJ));
                legsModel.setTextureID(texture);

                legs = new LegPair(legsModel);

                modelInit.accept(texture);

            } catch (IOException ex) {
                ErrorHandler.handleFatalError(ex);
            }
        }
    }

    @Override
    public void initializeEntity(Entity e, ArrayList<Byte> loadBytes) {
        initMesh(ResourceUtils.resource(bodyPath)
                , ResourceUtils.resource(legPath));
        e.initializeOnDraw(loadBytes); //Initialize the animal
        QuadPedalEntity a = (QuadPedalEntity) e; //Cast the entity to a fox
        a.animalInit(this, loadBytes); //Initialize the fox by passing the link so that the entity has access to the link variables
    }


}