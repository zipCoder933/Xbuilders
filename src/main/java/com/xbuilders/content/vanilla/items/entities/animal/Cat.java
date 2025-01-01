package com.xbuilders.content.vanilla.items.entities.animal;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.xbuilders.engine.MainWindow;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class Cat extends StaticLandAnimal {
    public Cat(int id, long uniqueIdentifier, MainWindow window) {
        super(id, uniqueIdentifier, window);
    }

    @Override
    public void load(Input input, Kryo kyro) throws IOException {
        super.load(input, kyro);//Always call super!
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

