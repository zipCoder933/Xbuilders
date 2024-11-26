package com.xbuilders.game.vanilla.items.items;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.Item;

public class Flashlight extends Item {

    final float distance = 20f;
    boolean on = false;

    public Flashlight() {
        super("xbuilders:flashlight", "Flashlight");
        setIcon("flashlight");
        this.createClickEvent = (ray) -> {
            GameScene.player.setFlashlight(on ? 0 : distance);
            on = !on;
            return true;
        };
    }


}