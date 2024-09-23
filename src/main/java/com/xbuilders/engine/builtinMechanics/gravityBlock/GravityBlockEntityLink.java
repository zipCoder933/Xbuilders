package com.xbuilders.engine.builtinMechanics.gravityBlock;

import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.entity.EntityLink;

import java.util.function.Supplier;

public class GravityBlockEntityLink extends EntityLink {
    public GravityBlockEntityLink() {
        super(-1, "gravity block", GravityBlockEntity::new);
    }
}
