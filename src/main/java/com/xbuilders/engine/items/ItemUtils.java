package com.xbuilders.engine.items;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.EntitySupplier;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.utils.json.JsonManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class ItemUtils {

    public static ArrayList<Block> getAllJsonBlocks(File jsonDirectory) {
        System.out.println("Adding all json blocks from " + jsonDirectory.getAbsolutePath());
        if (!jsonDirectory.exists()) jsonDirectory.mkdirs();
        ArrayList<Block> allBlocks = new ArrayList<>();
        try {
            for (File file : jsonDirectory.listFiles()) {

                if (!file.getName().endsWith(".json")) continue;
                if (!MainWindow.devMode && file.getName().contains("devmode")) continue;

                String jsonString = Files.readString(file.toPath());
                Block[] jsonBlocks2 = JsonManager.gson_jsonBlock.fromJson(jsonString, Block[].class);
                if (jsonBlocks2 != null && jsonBlocks2.length > 0) {
                    // append to list
                    for (Block block : jsonBlocks2) {
                        allBlocks.add(block);
                    }
                }
            }
            StringBuilder blockClasses = new StringBuilder();
            StringBuilder blockIDs = new StringBuilder();
            for (Block block : allBlocks) {
                if (block == null) {
                    System.err.println("A block is null");
                    continue;
                }
                String nameTitle = block.name.toUpperCase();
                nameTitle = nameTitle
                        .replaceAll("hidden", "")
                        .replaceAll("[^A-Z0-9_]", "")
                        .replaceAll("\\s+", "_");

                blockClasses.append("public static Block BLOCK_" + nameTitle + " = ItemList.getBlock((short)(short)")
                        .append(block.id).append(");").append("\n");
                blockIDs.append("public static short BLOCK_" + nameTitle + " = ").append(block.id).append(";").append("\n");
            }
            Files.writeString(new File(jsonDirectory, "BlockClasses.java").toPath(), blockClasses.toString());
            Files.writeString(new File(jsonDirectory, "BlockIDs.java").toPath(), blockIDs.toString());
            return allBlocks;
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
        return new ArrayList<>();
    }

    public static ArrayList<Item> getItemsFromBlocks(ArrayList<Block> blocks, ArrayList<EntitySupplier> entities) {
        ArrayList<Item> items = new ArrayList<>();
        for (Block block : blocks) {
            if (block == null) continue;
            if(block.name.toLowerCase().contains("hidden")) continue;
            Item item = new Item(0, block.name);
            item.block = block;
            items.add(item);
        }
        for (EntitySupplier entity : entities) {
            if (entity == null) continue;
            if(entity.name.toLowerCase().contains("hidden")) continue;
            System.out.println(entity.name);
            Item item = new Item(0, entity.name);
            item.entity = entity;
            item.iconFilename = entity.iconFilename;
            items.add(item);
        }
        return items;
    }


}