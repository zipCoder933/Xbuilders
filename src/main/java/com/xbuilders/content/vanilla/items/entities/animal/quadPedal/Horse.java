package com.xbuilders.content.vanilla.items.entities.animal.quadPedal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.client.ClientWindow;

import java.io.IOException;


public class Horse extends QuadPedalLandAnimal {
    public Horse( long uniqueIdentifier, ClientWindow window) {
        super(uniqueIdentifier, window, true);
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
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);
        lock.setOffset(-1);
    }
}
