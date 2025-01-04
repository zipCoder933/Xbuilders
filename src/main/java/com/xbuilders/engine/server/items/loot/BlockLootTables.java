package com.xbuilders.engine.server.items.loot;

import com.xbuilders.engine.server.items.loot.output.LootList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import static com.xbuilders.engine.server.items.loot.LootTableRegistry.lootMapper;
import static com.xbuilders.engine.server.items.loot.LootTableRegistry.type_stringIDTable;

public class BlockLootTables extends HashMap<String, LootList> {

    public BlockLootTables() {
        super();
    }

    public void loadFromFile(File json) throws IOException {
        String jsonString = Files.readString(json.toPath());
        if (jsonString.isBlank()) return;
        putAll(lootMapper.readValue(jsonString, type_stringIDTable));
        System.out.println("Loaded " + size() + " loot tables from " + json.getAbsolutePath());
    }

    public void writeToFile(HashMap<String, LootList> table, File json) throws IOException {
        String jsonString = lootMapper.writeValueAsString(table);
        Files.writeString(json.toPath(), jsonString);
    }
}
