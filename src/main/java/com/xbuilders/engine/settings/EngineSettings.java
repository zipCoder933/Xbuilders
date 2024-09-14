package com.xbuilders.engine.settings;

import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.Chunk;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;

import static com.xbuilders.engine.settings.EngineSettingsUtils.gson;

public class EngineSettings {

    /**
     * NEVER declare a primitive type as static. It will cause issues when reading the values.
     */
    public boolean game_switchMouseButtons = false;
    public int game_cursorRayDist = 128;
    public boolean video_fullscreen = true;
    public final BoundedFloat video_fullscreenSize = new BoundedFloat(1);
    public boolean video_vsync = true;
    public boolean video_largerUI = true;

    //If there are settings we dont want acesssable in UI, we can prepend them with "internal"
    public boolean internal_smallWindow = false;
    public final BoundedInt internal_viewDistance = new BoundedInt(Chunk.WIDTH * 5);
    public boolean internal_experimentalFeatures = false;

    public EngineSettings initVariables() {
        internal_viewDistance.setBounds(World.VIEW_DIST_MIN, World.VIEW_DIST_MAX);
        video_fullscreenSize.setBounds(0.5f, 1.0f);
        video_fullscreenSize.clamp();
        return this;
    }

    public static EngineSettings load() {
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

    public void save() {
        this.initVariables();
        File settingsFile = ResourceUtils.appDataResource("settings.json");
        // Save to JSON
        try {
            if (!settingsFile.exists()) {
                settingsFile.createNewFile();
                settingsFile.setWritable(true);
                settingsFile.setReadable(true);
            }
            // Save to JSON
            Files.write(settingsFile.toPath(), gson.toJson(this).getBytes());
        } catch (Exception e) {
            ErrorHandler.report(e);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Field field : EngineSettings.class.getDeclaredFields()) {
            try {
                sb.append(field.getName()).append(": ").append(field.get(this)).append("\n");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
}
