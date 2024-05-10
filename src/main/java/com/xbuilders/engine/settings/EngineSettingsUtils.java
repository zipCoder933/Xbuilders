package com.xbuilders.engine.settings;

import java.io.File;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;

public class EngineSettingsUtils {

    final File settingsFile = ResourceUtils.appDataResource("settings.json");

    public EngineSettings load() {
        if (settingsFile.exists()) {
            Gson gson = new Gson();
            try {
                String jsonString =Files.readString(settingsFile.toPath());
                return gson.fromJson(jsonString, EngineSettings.class);
            } catch (Exception e) {
                ErrorHandler.handleFatalError(e);
            }
        }
        return new EngineSettings();
    }

    public void save(EngineSettings settings) {
        // Save to JSON
        Gson gson = new Gson();
        try {
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
                settingsFile.setWritable(true);
                settingsFile.setReadable(true);
            }
            // Save to JSON
            Files.write(settingsFile.toPath(), gson.toJson(settings).getBytes());
        } catch (Exception e) {
            ErrorHandler.handleFatalError(e);
        }
    }
}
