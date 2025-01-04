package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.items.item.Item;

public class Flashlight extends Item {

    final float distance = 20f;
    boolean on = false;

    public Flashlight() {
        super("xbuilders:flashlight", "Flashlight");
        setIcon("flashlight");
        this.createClickEvent = (ray, stack) -> {
            GameScene.userPlayer.setFlashlight(on ? 0 : distance);
            on = !on;
            return true;
        };
    }


}