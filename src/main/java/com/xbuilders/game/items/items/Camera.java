package com.xbuilders.game.items.items;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.item.Item;

public class Camera extends Item {

    public Camera() {
        super("xbuilders:camera", "Camera");
        setIcon("camera.png");
        this.createClickEvent = (ray) -> {
            MainWindow.takeScreenshot();
            return true;
        };
    }

}