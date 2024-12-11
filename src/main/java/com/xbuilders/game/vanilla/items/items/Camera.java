package com.xbuilders.game.vanilla.items.items;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.item.Item;

public class Camera extends Item {

    public Camera() {
        super("xbuilders:camera", "Camera");
        setIcon("camera.png");
        this.createClickEvent = (ray,stack) -> {
            MainWindow.takeScreenshot();
            return true;
        };
    }

}