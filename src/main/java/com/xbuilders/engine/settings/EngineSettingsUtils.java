package com.xbuilders.engine.settings;

import java.io.File;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.utils.json.BoundedFloatTypeAdapter;
import com.xbuilders.engine.utils.json.BoundedIntTypeAdapter;

public class EngineSettingsUtils {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(BoundedInt.class, new BoundedIntTypeAdapter())
            .registerTypeAdapter(BoundedFloat.class, new BoundedFloatTypeAdapter())
            .create();

    public EngineSettings load(boolean devMode) {
        try {
            File settingsFile = ResourceUtils.appDataResource("settings.json");
            if (settingsFile.exists()) {
                String jsonString = Files.readString(settingsFile.toPath());
                return gson.fromJson(jsonString, EngineSettings.class).initVariables();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new EngineSettings().initVariables();
    }

    public void save(EngineSettings settings) {
        settings.initVariables();
        File settingsFile = ResourceUtils.appDataResource("settings.json");
        // Save to JSON
        try {
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
                settingsFile.setWritable(true);
                settingsFile.setReadable(true);
            }
            // Save to JSON
            Files.write(settingsFile.toPath(), gson.toJson(settings).getBytes());
        } catch (Exception e) {
            ErrorHandler.report(e);
        }
    }
}
