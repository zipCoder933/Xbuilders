package com.xbuilders.engine.settings;

import java.io.File;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;

public class EngineSettingsUtils {


    public EngineSettings load(boolean devMode) {
        File settingsFile = ResourceUtils.appDataResource("settings.json");
        if(EngineSettings.shouldReset()){
            System.out.println("Resetting settings!");
            save(new EngineSettings(devMode));
            return new EngineSettings(devMode);
        }

        if (settingsFile.exists()) {
            Gson gson = new Gson();
            try {
                String jsonString = Files.readString(settingsFile.toPath());
                return gson.fromJson(jsonString, EngineSettings.class);
            } catch (Exception e) {
                ErrorHandler.handleFatalError(e);
            }
        }
        return new EngineSettings(devMode);
    }

    public void save(EngineSettings settings) {
        File settingsFile = ResourceUtils.appDataResource("settings.json");
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
