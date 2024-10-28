package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntityLink;

import java.io.IOException;

public class FoxLink extends EntityLink {

    public FoxLink(MainWindow window, int id, String name) {
        super(id, name, () -> new Fox(window));
        setIcon("fox egg.png");
    }

    static class Fox extends StaticLandAnimal {
        public Fox(MainWindow window) {
            super(window);
        }

        @Override
        public void initializeOnDraw(byte[] state) {
            super.initializeOnDraw(state);
            aabb.setOffsetAndSize(0.6f, 0.8f, 0.6f, true);
        }

        static StaticLandAnimal_StaticData ead;

        @Override
        public StaticLandAnimal_StaticData getStaticData() throws IOException {
            if (ead == null) {
                ead = new StaticLandAnimal_StaticData(
                        "items\\entity\\animal\\fox\\body.obj",
                        "items\\entity\\animal\\fox\\textures");
                body = ead.body;
                textures = ead.textures;
            }
            return ead;
        }

    }
}
