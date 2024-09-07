package com.xbuilders.game.items.tools;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.Tool;

public class Camera extends Tool {

    public Camera() {
        super(4, "Camera");
        setIcon("camera.png");
        setClickEvent((ray, creationMode) -> {
            MainWindow.takeScreenshot();
        });
    }

}