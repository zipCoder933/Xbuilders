package com.xbuilders.content.vanilla.items.entities.animal;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.xbuilders.engine.MainWindow;

import java.io.IOException;


public class Fox extends StaticLandAnimal {
    public Fox(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
    }

    @Override
    public void loadDefinitionData(Input input, Kryo kyro) throws IOException {
        super.loadDefinitionData(input, kyro);//Always call super!
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
