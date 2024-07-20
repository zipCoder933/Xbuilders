package com.xbuilders.game;

import com.xbuilders.engine.gameScene.LivePropagationTask;

public class WaterPropagation extends LivePropagationTask {

    public WaterPropagation() {
        updateIntervalMS = 300;
    }

    @Override
    public void update() {
        System.out.println("updating water "+System.currentTimeMillis());
    }
}
