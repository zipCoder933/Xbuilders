package com.xbuilders.game.vanilla.items.entities.animal.quadPedal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntitySupplier;

import java.io.IOException;


public class Mule extends QuadPedalLandAnimal {
    public Mule(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window, true);
    }

    static QuadPedalLandAnimal_StaticData staticData;

    @Override
    public QuadPedalLandAnimal_StaticData getStaticData() throws IOException {
        if (staticData == null) {
            staticData = new QuadPedalLandAnimal_StaticData(
                    "items\\entity\\animal\\horse\\mule\\body.obj",
                    null,
                    "items\\entity\\animal\\horse\\mule\\leg.obj",
                    "items\\entity\\animal\\horse\\mule\\saddle.obj",
                    "items\\entity\\animal\\horse\\mule\\textures");
        }
        return staticData;
    }


    @Override
    public void initializeOnDraw(byte[] state) {
        super.initializeOnDraw(state);
        legXSpacing = 0.35f * SCALE;
        legZSpacing = 0.8f * SCALE;
        legYSpacing = -1f * SCALE;
        lock.setOffset(-1);
    }
}
