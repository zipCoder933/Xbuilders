package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.window.BaseWindow;

import java.util.ArrayList;

public class FoxLink extends StaticLandAnimalLink {

    public FoxLink(BaseWindow window, int id, String name, String texture) {
        super(window, id, name,
                "items\\entity\\animal\\fox\\body.obj",
                "items\\entity\\animal\\fox\\" + texture);
    }

    public void initializeEntity(Entity e, ArrayList<Byte> loadBytes) {
        super.initializeEntity(e, loadBytes);
        StaticLandAnimal a = (StaticLandAnimal) e;
        a.setSize(0.6f, 0.8f, 0.6f,true);
    }
}
