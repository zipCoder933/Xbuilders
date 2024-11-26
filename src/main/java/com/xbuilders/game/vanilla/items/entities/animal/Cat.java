package com.xbuilders.game.vanilla.items.entities.animal;

import com.xbuilders.engine.MainWindow;

import java.io.IOException;


public class Cat extends StaticLandAnimal {
    public Cat(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
    }

    @Override
    public void initializeOnDraw(byte[] state) {
        super.initializeOnDraw(state);
        setActivity(0.9f);
    }

    static StaticLandAnimal_StaticData ead;

    @Override
    public StaticLandAnimal_StaticData getStaticData() throws IOException {
        if (ead == null) {
            ead = new StaticLandAnimal_StaticData(
                    "items\\entity\\animal\\cat\\body.obj",
                    "items\\entity\\animal\\cat\\textures");
            body = ead.body;
            textures = ead.textures;
        }
        return ead;
    }

}

