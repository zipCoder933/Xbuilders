package com.xbuilders.engine.items.loot;

import com.xbuilders.engine.items.loot.output.LootList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

import static com.xbuilders.engine.items.loot.LootTableRegistry.lootMapper;
import static com.xbuilders.engine.items.loot.LootTableRegistry.type_stringIDTable;

public class BlockLootTables extends HashMap<String, LootList> {

    public BlockLootTables() {
        super();
    }

    public void loadFromFile(File json) throws IOException {
        String jsonString = Files.readString(json.toPath());
        putAll(lootMapper.readValue(jsonString, type_stringIDTable));
    }

    public void writeToFile(HashMap<String, LootList> table, File json) throws IOException {
        String jsonString = lootMapper.writeValueAsString(table);
        Files.writeString(json.toPath(), jsonString);
    }
}