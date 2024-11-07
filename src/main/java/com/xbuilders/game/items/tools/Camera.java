package com.xbuilders.game.items.tools;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemType;

public class Camera extends Item {

    public Camera() {
        super(4, "Camera", ItemType.ITEM);
        setIcon("camera.png");
        setClickEvent((ray, creationMode) -> {
            MainWindow.takeScreenshot();
        });
    }

}