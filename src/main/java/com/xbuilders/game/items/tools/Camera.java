package com.xbuilders.game.items.tools;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.item.Item;

public class Camera extends Item {

    public Camera() {
        super(4, "Camera");
        setIcon("camera.png");
        this.createClickEvent = (ray) -> {
            MainWindow.takeScreenshot();
            return true;
        };
    }

}