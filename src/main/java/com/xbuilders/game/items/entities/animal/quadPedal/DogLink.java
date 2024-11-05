package com.xbuilders.game.items.entities.animal.quadPedal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;

import java.io.IOException;

public class DogLink extends EntityLink {

    public DogLink(MainWindow window, int id, String name) {
        super(id, name, () -> new DogLink.Dog(id,window));
        setIcon("dog egg.png");
    }

    public static class Dog extends QuadPedalLandAnimal {
        public Dog(int id, MainWindow window) {
            super(id, window, false);
        }

        static QuadPedalLandAnimal_StaticData staticData;

        @Override
        public QuadPedalLandAnimal_StaticData getStaticData() throws IOException {
            if (staticData == null) {
                staticData = new QuadPedalLandAnimal_StaticData(
                        "items\\entity\\animal\\dog\\large\\body.obj",
                        "items\\entity\\animal\\dog\\large\\sitting.obj",
                        "items\\entity\\animal\\dog\\large\\leg.obj",
                        null,
                        "items\\entity\\animal\\dog\\textures");
            }
            return staticData;
        }


        @Override
        public void initializeOnDraw(byte[] state) {
            super.initializeOnDraw(state);
            setActivity(0.7f);
            //Z is the direciton of the animal
            legXSpacing = 0.30f * SCALE;
            legZSpacing = 0.82f * SCALE;
            legYSpacing = -1.15f * SCALE; //negative=higher, positive=lower
        }


    }
}