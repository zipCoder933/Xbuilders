package com.xbuilders.content.vanilla.items.entities.animal.quadPedal;

import com.xbuilders.engine.MainWindow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class Horse extends QuadPedalLandAnimal {
    public Horse(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window, true);
    }

    static QuadPedalLandAnimal_StaticData staticData;

    @Override
    public QuadPedalLandAnimal_StaticData getStaticData() throws IOException {
        if (staticData == null) {
            staticData = new QuadPedalLandAnimal_StaticData(
                    "items\\entity\\animal\\horse\\horse\\body.obj",
                    null,
                    "items\\entity\\animal\\horse\\horse\\leg.obj",
                    "items\\entity\\animal\\horse\\horse\\saddle.obj",
                    "items\\entity\\animal\\horse\\horse\\textures");
        }
        return staticData;
    }


    @Override
    public void load(byte[] serializedBytes, AtomicInteger start) {
        super.load(serializedBytes, start);
        lock.setOffset(-1);
    }
}
