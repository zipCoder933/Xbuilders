package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntitySupplier;

import java.io.IOException;

public class RabbitLink extends EntitySupplier {

    public RabbitLink(MainWindow window, int id, String name) {
        super(id, name, () -> new Rabbit(id,window));
        setIcon("rabbit egg.png");
        tags.add("animal");
    }


    public static class Rabbit extends StaticLandAnimal {
        long lastJumpTime = 0;


        public Rabbit(int id, MainWindow window) {
            super(id, window);
        }

        @Override
        public void initializeOnDraw(byte[] state) {
            super.initializeOnDraw(state);
            goForwardCallback = (amount) -> {
                if (amount > 0.01) {
                    if (System.currentTimeMillis() - lastJumpTime > 500) {
                        lastJumpTime = System.currentTimeMillis();
                        pos.jump();
                    }
                }
            };
        }

        static StaticLandAnimal_StaticData ead;
        @Override
        public StaticLandAnimal_StaticData getStaticData() throws IOException {
            if (ead == null) {
                ead = new StaticLandAnimal_StaticData(
                        "items\\entity\\animal\\rabbit\\body.obj",
                        "items\\entity\\animal\\rabbit\\textures");
                body = ead.body;
                textures = ead.textures;
            }
            return ead;
        }


    }
}
