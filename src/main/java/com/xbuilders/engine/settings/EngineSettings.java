package com.xbuilders.engine.settings;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.game.Main;

public class EngineSettings {


    public EngineSettings() { // First time setup
        viewDistance = GameScene.world.DEFAULT_VIEW_DISTANCE;
    }

    public static boolean shouldReset(){
        return Main.devMode;
    }

    public int viewDistance;
    public boolean fullscreen = false;
    public float fullscreenSizeMultiplier = 1.0f;
    public int maxCursorRaycastDist = 45;
    public boolean largerUI = true;
}
