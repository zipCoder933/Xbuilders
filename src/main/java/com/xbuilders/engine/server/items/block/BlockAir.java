// 
// Decompiled by Procyon v0.5.36
// 
package com.xbuilders.engine.server.items.block;

public class BlockAir extends Block {

    public BlockAir() {
        super(0, "Air");
        opaque = false;
        solid = false;
    }

    @Override
    public boolean isAir() {
        return true;
    }
}
