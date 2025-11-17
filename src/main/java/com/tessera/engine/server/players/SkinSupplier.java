package com.tessera.engine.server.players;


import com.tessera.engine.client.player.Skin;

@FunctionalInterface
public interface SkinSupplier {
    public Skin get(Player player);
}


