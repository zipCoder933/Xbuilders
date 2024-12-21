package com.xbuilders.engine.game.model.players;


import com.xbuilders.engine.client.player.Skin;

@FunctionalInterface
public interface SkinSupplier {
    public Skin get(Player player);
}


