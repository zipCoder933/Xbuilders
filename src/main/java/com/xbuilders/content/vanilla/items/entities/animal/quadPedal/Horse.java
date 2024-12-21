package com.xbuilders.content.vanilla.items.entities.animal.quadPedal;

import com.xbuilders.engine.MainWindow;

import java.io.IOException;


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
    public void initializeOnDraw(byte[] state) {
        super.initializeOnDraw(state);
        lock.setOffset(-1);
    }
}
