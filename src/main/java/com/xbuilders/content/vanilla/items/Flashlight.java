package com.xbuilders.content.vanilla.items;

import com.xbuilders.engine.client.LocalClient;
import com.xbuilders.engine.server.item.Item;

public class Flashlight extends Item {

    final float distance = 20f;
    boolean on = false;

    public Flashlight() {
        super("xbuilders:flashlight", "Flashlight");
        setIcon("flashlight.png");
        tags.add("tool");
        maxStackSize = 1;
        this.createClickEvent = (ray, stack) -> {
            LocalClient.userPlayer.setFlashlight(on ? 0 : distance);
            on = !on;
            return true;
        };
    }


}