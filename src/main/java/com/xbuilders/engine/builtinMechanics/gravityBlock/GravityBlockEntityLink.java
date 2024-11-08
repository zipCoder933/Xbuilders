package com.xbuilders.engine.builtinMechanics.gravityBlock;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.entity.EntitySupplier;

public class GravityBlockEntityLink extends EntitySupplier {
    public GravityBlockEntityLink(MainWindow window) {
        super(-1, "gravity block", () -> new GravityBlockEntity(-1, window));
    }
}
