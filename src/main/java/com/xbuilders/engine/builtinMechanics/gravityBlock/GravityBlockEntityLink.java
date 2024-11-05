package com.xbuilders.engine.builtinMechanics.gravityBlock;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;

import java.util.function.Supplier;

public class GravityBlockEntityLink extends EntityLink {
    public GravityBlockEntityLink(MainWindow window) {
        super(-1, "gravity block", () -> new GravityBlockEntity(-1, window));
    }
}
