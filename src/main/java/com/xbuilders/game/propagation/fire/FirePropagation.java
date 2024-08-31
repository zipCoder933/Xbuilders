package com.xbuilders.game.propagation.fire;

import com.xbuilders.engine.gameScene.LivePropagationHandler;

public class FirePropagation {
    DisintegrationPropagation disintegrationPropagation;
    SpreadPropagation spreadPropagation;

    public FirePropagation(LivePropagationHandler handler) {
        DisintegrationPropagation disintegrationPropagation = new DisintegrationPropagation(1000);
        SpreadPropagation spreadPropagation = new SpreadPropagation(100, disintegrationPropagation.disintegrationNodes);
        handler.addTask(disintegrationPropagation);
        handler.addTask(spreadPropagation);
    }
}
