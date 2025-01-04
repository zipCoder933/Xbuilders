package com.xbuilders.engine.server.items.loot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xbuilders.engine.server.items.Registrys;
import com.xbuilders.engine.server.items.loot.output.Loot;
import com.xbuilders.engine.server.items.loot.output.LootList;
import com.xbuilders.engine.utils.json.fasterXML.loot.LootDeserializer;
import com.xbuilders.engine.utils.json.fasterXML.loot.LootSerializer;

import java.util.HashMap;

public class LootTableRegistry {
    public static BlockLootTables blockLootTables = new BlockLootTables();

    //JSON serializer and deserializer
    protected static final ObjectMapper lootMapper;

    static {
        // Create an instance of ObjectMapper
        lootMapper = new ObjectMapper();
        // Create a module to register custom serializer and deserializer
        SimpleModule module = new SimpleModule();
        module.addSerializer(Loot.class, new LootSerializer()); // Register the custom serializer
        module.addDeserializer(Loot.class, new LootDeserializer(Registrys.items.idMap)); // Register the custom deserializer
        // Register the module with the ObjectMapper
        lootMapper.registerModule(module);
    }

    protected static final TypeReference<HashMap<String, LootList>> type_stringIDTable = new TypeReference<HashMap<String, LootList>>() {
    };
    protected static final TypeReference<HashMap<Short, LootList>> type_shortIDTable = new TypeReference<HashMap<Short, LootList>>() {
    };
}
