package com.xbuilders.engine.game.model.player;


@FunctionalInterface
public interface SkinSupplier {
    public Skin get(Player player);
}


