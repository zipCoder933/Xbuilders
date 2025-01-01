package com.xbuilders.content.vanilla.items.entities.animal.quadPedal;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.xbuilders.engine.MainWindow;

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
    public void loadDefinitionData(Input input, Kryo kyro) throws IOException {
        super.loadDefinitionData(input, kyro);
        legXSpacing = 0.35f * SCALE;
        legZSpacing = 0.8f * SCALE;
        legYSpacing = -1f * SCALE;
        lock.setOffset(-1);
    }
}
