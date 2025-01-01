package com.xbuilders.content.vanilla.items.entities.animal;

import com.xbuilders.engine.MainWindow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Rabbit extends StaticLandAnimal {
    long lastJumpTime = 0;


    public Rabbit(int id, MainWindow window, long uniqueIdentifier) {
        super(id, uniqueIdentifier, window);
    }

    @Override
    public void load(byte[] state, AtomicInteger start) {
        super.load(state, start);
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
