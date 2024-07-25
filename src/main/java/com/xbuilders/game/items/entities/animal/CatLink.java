package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.window.BaseWindow;

import java.util.ArrayList;

public class CatLink extends StaticLandAnimalLink {

    public CatLink(BaseWindow window, int id, String name, String texture) {
        super(window, id, name,
                "items\\entity\\animal\\cat\\body.obj",
                "items\\entity\\animal\\cat\\" + texture);
    }

    public void initializeEntity(Entity e, ArrayList<Byte> loadBytes) {
        super.initializeEntity(e, loadBytes);
        StaticLandAnimal a = (StaticLandAnimal) e;
        a.setActivity(0.9f);
    }
}
