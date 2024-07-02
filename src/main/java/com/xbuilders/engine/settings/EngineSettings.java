package com.xbuilders.engine.settings;

import java.io.File;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.utils.ResourceUtils;

public class EngineSettings {



    public EngineSettings() { // First time setup
        viewDistance = GameScene.world.DEFAULT_VIEW_DISTANCE;
    }

    public int viewDistance;
    public boolean fullscreen = false;
    public float fullscreenSizeMultiplier = 1.0f;
}
