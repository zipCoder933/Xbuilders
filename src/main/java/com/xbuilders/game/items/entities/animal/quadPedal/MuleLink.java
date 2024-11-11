package com.xbuilders.game.items.entities.animal.quadPedal;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntitySupplier;
import java.io.IOException;

public class MuleLink extends EntitySupplier {
    public MuleLink(MainWindow window, int id, String name) {
        super(id, name, () -> new MuleLink.Mule(id,window));
    }

    public static class Mule extends QuadPedalLandAnimal {
        public Mule(int id, MainWindow window) {
            super(id, window, true);
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
}