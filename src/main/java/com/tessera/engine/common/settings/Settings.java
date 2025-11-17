package com.tessera.engine.common.settings;

import com.tessera.engine.utils.ErrorHandler;
import com.tessera.engine.utils.option.BoundedFloat;
import com.tessera.engine.utils.option.BoundedInt;
import com.tessera.engine.utils.resource.ResourceUtils;
import com.tessera.engine.server.world.World;
import com.tessera.engine.server.world.chunk.Chunk;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;

import static com.tessera.engine.common.settings.EngineSettingsUtils.gson;

public class Settings {

    /**
     * NEVER declare a primitive typeReference as static. It will cause issues when reading the values.
     */
    public boolean game_switchMouseButtons = false;
    public boolean game_autoJump = false;

    public boolean video_fullscreen = true;
    public final BoundedFloat video_fullscreenSize = new BoundedFloat(1);
    public boolean video_vsync = true;

    //If there are settings we dont want acesssable in UI, we can prepend them with "internal"
    public boolean internal_smallWindow = false;
    public final BoundedInt viewDistance = new BoundedInt(Chunk.WIDTH * 5);
    public final BoundedInt internal_simulationDistance = new BoundedInt(Chunk.WIDTH * 3);
    public boolean internal_experimentalFeatures = false;
    public long internal_blockBoundaryAreaLimit = 1000000;
    public BoundedInt video_entityDistance = new BoundedInt(100);
    public boolean internal_allowOPCommands = false;


    public Settings initVariables() {
        viewDistance.setBounds(World.VIEW_DIST_MIN, World.VIEW_DIST_MAX);
        internal_simulationDistance.setBounds(World.VIEW_DIST_MIN / 2, World.VIEW_DIST_MAX);
        video_entityDistance.setBounds(20, 100);
        video_fullscreenSize.setBounds(0.5f, 1.0f);
        video_fullscreenSize.clamp();
        return this;
    }

    public static Settings load() {
        try {
            File settingsFile = ResourceUtils.appDataFile("settings.json");
            if (settingsFile.exists()) {
                String jsonString = Files.readString(settingsFile.toPath());
                return gson.fromJson(jsonString, Settings.class).initVariables();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Settings().initVariables();
    }

    public void save() {
        this.initVariables();
        File settingsFile = ResourceUtils.appDataFile("settings.json");
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
        for (Field field : Settings.class.getDeclaredFields()) {
            try {
                sb.append(field.getName()).append(": ").append(field.get(this)).append("\n");
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return sb.toString();
    }
}
