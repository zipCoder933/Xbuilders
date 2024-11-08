package com.xbuilders.game.items.entities.animal.quadPedal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntitySupplier;
import java.io.IOException;

public class HorseLink extends EntitySupplier {
    public HorseLink(MainWindow window, int id, String name) {
        super(id, name, () -> new HorseLink.Horse(id,window));
        setIcon("horse egg.png");
    }

    public static class Horse extends QuadPedalLandAnimal {
        public Horse(int id, MainWindow window) {
            super(id, window, true);
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
}