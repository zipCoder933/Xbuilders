package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.item.Item;

public class Flashlight extends Item {

    final float distance = 20f;
    boolean on = false;

    public Flashlight() {
        super("xbuilders:flashlight", "Flashlight");
        setIcon("flashlight");
        this.createClickEvent = (ray, stack) -> {
            GameScene.player.setFlashlight(on ? 0 : distance);
            on = !on;
            return true;
        };
    }


}