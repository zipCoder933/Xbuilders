package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntitySupplier;

import java.io.IOException;

public class CatLink extends EntitySupplier {

    public CatLink(MainWindow window, int id, String name) {
        super(id, name, () -> new CatLink.Cat(id, window));
    }

    static class Cat extends StaticLandAnimal {
        public Cat(int id, MainWindow window) {
            super(id, window);
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

}
