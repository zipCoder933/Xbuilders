package com.xbuilders.engine.settings;

import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.Main;

public class EngineSettings {


    public EngineSettings(boolean devMode) { // First time setup
        viewDistance = (Chunk.WIDTH * 5);

        if (devMode) {
            fullscreen = false;
            largerUI = false;
            smallWindow = true;
        }
    }

    public static boolean shouldReset() {
        return Main.devMode;
    }

    public int viewDistance;
    public boolean fullscreen = true;
    public float fullscreenSizeMultiplier = 1.0f;
    public int maxCursorRaycastDist = 50;
    public boolean largerUI = true;
    public boolean smallWindow = false;
}
