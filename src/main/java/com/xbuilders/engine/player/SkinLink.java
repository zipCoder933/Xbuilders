package com.xbuilders.engine.player;

public class SkinLink {
    private final SkinSupplier skin;

    public SkinLink(SkinSupplier skin) {
        this.skin = skin;
    }

    public Skin getSkin(Player player) {
        return skin.get(player);
    }

    @FunctionalInterface
    public interface SkinSupplier {
        public Skin get(Player player);
    }
}

