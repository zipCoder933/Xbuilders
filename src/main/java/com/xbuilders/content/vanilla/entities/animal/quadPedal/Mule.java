package com.xbuilders.content.vanilla.entities.animal.quadPedal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.client.ClientWindow;

import java.io.IOException;


public class Mule extends QuadPedalLandAnimal {
    public Mule( long uniqueIdentifier, ClientWindow window) {
        super( uniqueIdentifier, window, true);
    }

    static QuadPedalLandAnimal_StaticData staticData;

    @Override
    public QuadPedalLandAnimal_StaticData getStaticData() throws IOException {
        if (staticData == null) {
            staticData = new QuadPedalLandAnimal_StaticData(
                    "/assets/xbuilders/entities/animal\\horse\\mule\\body.obj",
                    null,
                    "/assets/xbuilders/entities/animal\\horse\\mule\\leg.obj",
                    "/assets/xbuilders/entities/animal\\horse\\mule\\saddle.obj",
                    "/assets/xbuilders/entities/animal\\horse\\mule\\textures");
        }
        return staticData;
    }


    @Override
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);
        legXSpacing = 0.35f * SCALE;
        legZSpacing = 0.8f * SCALE;
        legYSpacing = -1f * SCALE;
        lock.setOffset(-1);
    }
}
