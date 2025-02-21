package com.xbuilders.content.vanilla.items;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.item.Item;

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