package com.xbuilders.engine.server.loot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xbuilders.engine.server.loot.output.LootList;
import com.xbuilders.engine.utils.ResourceLoader;

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
}
