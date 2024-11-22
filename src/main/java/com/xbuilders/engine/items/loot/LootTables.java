package com.xbuilders.engine.items.loot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xbuilders.engine.utils.json.fasterXML.loot.LootDeserializer;
import com.xbuilders.engine.utils.json.fasterXML.loot.LootSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class LootTables {
    public static final HashMap<String, LootList> blockLootTables = new HashMap<>();

    public static final ObjectMapper lootMapper;
    public static final TypeReference<HashMap<String, LootList>> stringIDTable = new TypeReference<HashMap<String, LootList>>() {
    };
    public static final TypeReference<HashMap<Short, LootList>> shortIDTable = new TypeReference<HashMap<Short, LootList>>() {
    };

    static {
        // Create an instance of ObjectMapper
        lootMapper = new ObjectMapper();
        // Create a module to register custom serializer and deserializer
        SimpleModule module = new SimpleModule();
        module.addSerializer(Loot.class, new LootSerializer()); // Register the custom serializer
        module.addDeserializer(Loot.class, new LootDeserializer()); // Register the custom deserializer
        // Register the module with the ObjectMapper
        lootMapper.registerModule(module);
    }

    public static void loadBlockLootTable(File json) throws IOException {
        String jsonString = Files.readString(json.toPath());
        blockLootTables.putAll(lootMapper.readValue(jsonString, stringIDTable));
    }

    public static void writeLootTableToJson(HashMap<String, LootList> table, File json) throws IOException {
        String jsonString = lootMapper.writeValueAsString(table);
        Files.writeString(json.toPath(), jsonString);
    }

//    public static void writeLootTableToJson(HashMap<String, LootList> table, File json) throws IOException {
//        String jsonString = lootMapper.writeValueAsString(table);
//        Files.writeString(json.toPath(), jsonString);
//    }

}
