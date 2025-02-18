package com.xbuilders.engine.server.loot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xbuilders.engine.server.loot.output.LootList;
import com.xbuilders.engine.utils.ResourceLoader;

import java.io.IOException;
import java.util.HashMap;

public abstract class LootTableRegistry {

    public final String name;

    public LootTableRegistry(String name) {
        this.name = name;
    }

    //    public void loadFromFile(File json) throws IOException {
//        String jsonString = Files.readString(json.toPath());
//        if (jsonString.isBlank()) return;
//        putAll(lootMapper.readValue(jsonString, typeReference));
//        System.out.println("Loaded " + size() + " loot tables from " + json.getAbsolutePath());
//    }
    public final ResourceLoader resourceLoader = new ResourceLoader();

    public abstract void registerFromResource(String path) throws IOException;

    public final void register(String path) throws IOException {
        for (String file : resourceLoader.getResourceFiles(path)) {
            registerFromResource(file);
        }
    }
}
