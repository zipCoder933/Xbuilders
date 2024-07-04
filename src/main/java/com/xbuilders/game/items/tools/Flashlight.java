package com.xbuilders.game.items.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Tool;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;

public class Flashlight extends Tool {

    final float distance = 20f;
    boolean on = false;

    public Flashlight() {
        super(6, "Flashlight");
        setIcon("flashlight");
        setClickEvent((ray, creationMode) -> {
            GameScene.player.setFlashlight(on ? 0 : distance);
            on = !on;
        });
    }


}