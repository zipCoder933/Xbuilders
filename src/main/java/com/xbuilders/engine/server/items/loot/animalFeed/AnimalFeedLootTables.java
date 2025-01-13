package com.xbuilders.engine.server.items.loot.animalFeed;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xbuilders.engine.server.items.loot.output.LootList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static com.xbuilders.engine.server.items.loot.LootTableRegistry.lootMapper;


public class AnimalFeedLootTables {
    public ArrayList<AnimalFeedLoot> list;

    protected static final TypeReference<ArrayList<AnimalFeedLoot>> type = new TypeReference<ArrayList<AnimalFeedLoot>>() {
    };

    public AnimalFeedLootTables() {
        super();
        list = new ArrayList<>();
    }

    public LootList getLoot(String animalId, String foodId) {
        for (int i = 0; i < list.size(); i++) {
            AnimalFeedLoot entry = list.get(i);
            if (entry.animalID.equals(animalId) && entry.foodID.equals(foodId)) {
                return entry.output;
            }
        }
        return new LootList();
    }

    public void loadFromFile(File json) throws IOException {
        String jsonString = Files.readString(json.toPath());
        if (jsonString.isBlank()) return;
        list.addAll(lootMapper.readValue(jsonString, type));
        System.out.println("Loaded " + list.size() + " loot tables from " + json.getAbsolutePath());
    }

    public static void writeToFile(ArrayList<AnimalFeedLoot> table, File json) throws IOException {
        String jsonString = lootMapper.writeValueAsString(table);
        Files.writeString(json.toPath(), jsonString);
    }
}
