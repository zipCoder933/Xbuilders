package com.xbuilders.game.items.tools;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Tool;
import com.xbuilders.game.Main;
import com.xbuilders.game.MyGame;
import org.joml.Vector3i;

public class Camera extends Tool {

    public Camera() {
        super(3, "Camera");
        setIcon("camera.png");
        setClickEvent((ray, creationMode) -> {
            Main.takeScreenshot();
        });
    }

}