package com.xbuilders.content.vanilla.items.entities.animal;

import com.xbuilders.engine.MainWindow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class Fox extends StaticLandAnimal {
    public Fox(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
    }

    @Override
    public void load(byte[] serializedBytes, AtomicInteger start) {
        super.load(serializedBytes, start);
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
