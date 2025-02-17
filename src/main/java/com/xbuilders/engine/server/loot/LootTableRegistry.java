package com.xbuilders.engine.server.loot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xbuilders.engine.server.loot.animalFeed.AnimalFeedLootTables;
import com.xbuilders.engine.server.loot.block.BlockLootTables;
import com.xbuilders.engine.server.loot.output.Loot;
import com.xbuilders.engine.utils.json.fasterXML.loot.LootDeserializer;
import com.xbuilders.engine.utils.json.fasterXML.loot.LootSerializer;

public class LootTableRegistry {
    public static BlockLootTables blockLootTables = new BlockLootTables();
    public static AnimalFeedLootTables animalFeedLootTables = new AnimalFeedLootTables();

    //JSON serializer and deserializer
    public static final ObjectMapper lootMapper;

    static {
        lootMapper = new ObjectMapper();

        //Custom serializers and deserializers
        SimpleModule module = new SimpleModule();
        module.addSerializer(Loot.class, new LootSerializer());
        module.addDeserializer(Loot.class, new LootDeserializer());
        lootMapper.registerModule(module);

        //https://stackoverflow.com/questions/6371092/can-not-find-a-map-key-deserializer-for-type-simple-type-class
        //https://stackoverflow.com/questions/11246748/deserializing-non-string-map-keys-with-jackson
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addKeyDeserializer(AnimalFeedLoot.class, new KeyDeserializer());
//        lootMapper.registerModule(simpleModule);

//        SimpleModule module1 = new SimpleModule();
//        module1.addSerializer(AnimalFeedInput.class, new Serializer_AnimalFeedInput());
//        module1.addDeserializer(AnimalFeedInput.class, new Deserializer_AnimalFeedInput());
//        lootMapper.registerModule(module1);
    }
}
