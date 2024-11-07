package com.xbuilders.game.items.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemType;

public class Flashlight extends Item {

    final float distance = 20f;
    boolean on = false;

    public Flashlight() {
        super(6, "Flashlight", ItemType.ITEM);
        setIcon("flashlight");
        setClickEvent((ray, creationMode) -> {
            GameScene.player.setFlashlight(on ? 0 : distance);
            on = !on;
        });
    }


}