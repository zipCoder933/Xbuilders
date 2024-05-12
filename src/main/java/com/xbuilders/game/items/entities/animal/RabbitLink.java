package com.xbuilders.game.items.entities.animal;

import com.xbuilders.engine.items.Entity;
import com.xbuilders.window.BaseWindow;

import java.util.ArrayList;

public class RabbitLink extends StaticLandAnimalLink {

    public RabbitLink(BaseWindow window, int id, String name, String texture) {
        super(window, id, name,
                "items\\entity\\animal\\rabbit\\body.obj",
                "items\\entity\\animal\\rabbit\\" + texture);
    }

    long lastJumpTime = 0;

    public void initializeEntity(Entity e, ArrayList<Byte> loadBytes) {
        super.initializeEntity(e, loadBytes);
        StaticLandAnimal a = (StaticLandAnimal) e;
        a.goForwardCallback = (amount) -> {
            if (amount > 0.01) {
                if (System.currentTimeMillis() - lastJumpTime > 500) {
                    lastJumpTime = System.currentTimeMillis();
                    a.pos.jump();
                }
            }
        };
    }
}
