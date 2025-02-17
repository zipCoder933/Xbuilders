package com.xbuilders.content.vanilla.items.items;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.items.item.Item;

public class Camera extends Item {

    public Camera() {
        super("xbuilders:camera", "Camera");
        setIcon("camera.png");
        this.createClickEvent = (ray,stack) -> {
            ClientWindow.takeScreenshot();
            return true;
        };
    }

}