package com.xbuilders.engine.server.players;


import com.xbuilders.engine.client.player.Skin;

@FunctionalInterface
public interface SkinSupplier {
    public Skin get(Player player);
}


