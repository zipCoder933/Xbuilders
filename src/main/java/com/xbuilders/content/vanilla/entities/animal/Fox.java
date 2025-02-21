package com.xbuilders.content.vanilla.entities.animal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.client.ClientWindow;

import java.io.IOException;


public class Fox extends StaticLandAnimal {
    public Fox( long uniqueIdentifier, ClientWindow window) {
        super( uniqueIdentifier, window);
    }

    @Override
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);//Always call super!
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
