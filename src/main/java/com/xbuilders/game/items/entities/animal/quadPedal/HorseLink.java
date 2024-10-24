package com.xbuilders.game.items.entities.animal.quadPedal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.rendering.entity.EntityMesh;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.utils.obj.OBJLoader;

import java.io.FileNotFoundException;

public class HorseLink extends QuadPedalLandAnimalLink {

    String saddledPath = "items\\entity\\animal\\horse\\horse\\saddle.obj";
    EntityMesh saddleModel;

    public HorseLink(MainWindow window, int id, String name, String textureName) {
        super(id, name, textureName, () -> new HorseEntity(window));

        bodyPath = "items\\entity\\animal\\horse\\horse\\body.obj";
        legPath = "items\\entity\\animal\\horse\\horse\\leg.obj";
        texturePrePath = "items\\entity\\animal\\horse\\";
        sittingModel = null;
        rideable = true;
        setIcon("horse egg.png");
        tags.add("animal");


        modelInit = (texture) -> {
            try {
                saddleModel = new EntityMesh();
                saddleModel.loadFromOBJ(OBJLoader.loadModel(ResourceUtils.resource(saddledPath)));
                saddleModel.setTextureID(texture);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public void initializeEntity(Entity e, byte[] loadBytes) {
        super.initializeEntity(e, loadBytes);

        QuadPedalLandAnimal a = (QuadPedalLandAnimal) e; //Cast the entity to a fox
        a.animalInit(this, loadBytes); //Initialize the fox by passing the link so that the entity has access to the link variables

    }

    static class HorseEntity extends QuadPedalLandAnimal<HorseLink> {

        public HorseEntity(MainWindow window) {
            super(window);
            lock.setOffset(-1);
        }

        public void drawBody() {
            link.saddleModel.draw(false);
            link.body.draw(false);
        }
    }
}