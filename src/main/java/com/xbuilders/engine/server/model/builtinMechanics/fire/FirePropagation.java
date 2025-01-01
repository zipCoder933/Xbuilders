package com.xbuilders.engine.server.model.builtinMechanics.fire;

import com.xbuilders.engine.server.model.LivePropagationHandler;

public class FirePropagation {

    public FirePropagation(LivePropagationHandler handler) {
        DisintegrationPropagation disintegrationPropagation = new DisintegrationPropagation(10000);
        SpreadPropagation spreadPropagation = new SpreadPropagation(10000, disintegrationPropagation.disintegrationNodes);

//        handler.addTask(disintegrationPropagation);
        handler.addTask(spreadPropagation);
    }
}
