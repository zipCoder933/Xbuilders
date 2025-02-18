package com.xbuilders.engine.server.loot.block;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbuilders.engine.server.loot.LootTableRegistry;
import com.xbuilders.engine.server.loot.output.LootList;
import com.xbuilders.engine.utils.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import static com.xbuilders.engine.server.loot.AllLootTables.lootMapper;

public class BlockLootRegistry extends LootTableRegistry {
    HashMap<String, LootList> list;

    protected static final TypeReference<HashMap<String, LootList>> typeReference = new TypeReference<HashMap<String, LootList>>() {
    };

    public BlockLootRegistry() {
        super("block");
        list = new HashMap<>();
    }

//    public void loadFromFile(File json) throws IOException {
//        String jsonString = Files.readString(json.toPath());
//        if (jsonString.isBlank()) return;
//        putAll(lootMapper.readValue(jsonString, typeReference));
//        System.out.println("Loaded " + size() + " loot tables from " + json.getAbsolutePath());
//    }

    public void registerFromResource(String path) throws IOException {
        String json = new String(resourceLoader.readResource(path));
        System.out.println("Loading " + name + " loot from " + path);
        HashMap<String, LootList> loadedRecipes = lootMapper.readValue(json, typeReference);
        list.putAll(loadedRecipes);
    }

    public void writeToFile(HashMap<String, LootList> table, File json) throws IOException {
        String jsonString = lootMapper.writeValueAsString(table);
        Files.writeString(json.toPath(), jsonString);
    }

    public LootList getLoot(String blockId) {
        return list.get(blockId);
    }
}
