package com.xbuilders.game.items.tools;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemType;

public class Camera extends Item {

    public Camera() {
        super(4, "Camera", ItemType.TOOL);
        setIcon("camera.png");
        setClickEvent((ray, creationMode) -> {
            MainWindow.takeScreenshot();
        });
    }

}