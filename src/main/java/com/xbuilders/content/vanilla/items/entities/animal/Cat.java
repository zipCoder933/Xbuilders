package com.xbuilders.content.vanilla.items.entities.animal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.xbuilders.engine.client.ClientWindow;

import java.io.IOException;


public class Cat extends StaticLandAnimal {
    public Cat(int id, long uniqueIdentifier, ClientWindow window) {
        super(id, uniqueIdentifier, window);
    }

    @Override
    public void loadDefinitionData(boolean hasData, JsonParser parser, JsonNode node) throws IOException {
        super.loadDefinitionData(hasData, parser, node);//Always call super!
        setActivity(0.9f);
    }



    static StaticLandAnimal_StaticData ead;

    @Override
    public StaticLandAnimal_StaticData getStaticData() throws IOException {
        if (ead == null) {
            ead = new StaticLandAnimal_StaticData(
                    "items\\entity\\animal\\cat\\body.obj",
                    "items\\entity\\animal\\cat\\textures");
            body = ead.body;
            textures = ead.textures;
        }
        return ead;
    }

}

