package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.Entity;

public class FoxLink extends StaticLandAnimalLink {

    public FoxLink(MainWindow window, int id, String name, String texture) {
        super(window, id, name,
                "items\\entity\\animal\\fox\\body.obj",
                "items\\entity\\animal\\fox\\" + texture);
        setIcon("fox egg.png");
    }

    public void initializeEntity(Entity e, byte[] loadBytes) {
        super.initializeEntity(e, loadBytes);
        StaticLandAnimal a = (StaticLandAnimal) e;
        a.aabb.setOffsetAndSize(0.6f, 0.8f, 0.6f,true);
    }
}
