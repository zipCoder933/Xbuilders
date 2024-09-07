package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.Entity;

public class CatLink extends StaticLandAnimalLink {

    public CatLink(MainWindow window, int id, String name, String texture) {
        super(window, id, name,
                "items\\entity\\animal\\cat\\body.obj",
                "items\\entity\\animal\\cat\\" + texture);
        setIcon("cat egg.png");
    }

    public void initializeEntity(Entity e, byte[] loadBytes) {
        super.initializeEntity(e, loadBytes);
        StaticLandAnimal a = (StaticLandAnimal) e;
        a.setActivity(0.9f);
    }
}
