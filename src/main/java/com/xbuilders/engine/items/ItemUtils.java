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
import java.util.List;

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
                Block[] jsonBlocks2 = JsonManager.gson_blockAdapter.fromJson(jsonString, Block[].class);
                if (jsonBlocks2 != null && jsonBlocks2.length > 0) {
                    // append to list
                    for (Block block : jsonBlocks2) {
                        allBlocks.add(block);
                    }
                }
            }

            return allBlocks;
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
        return new ArrayList<>();
    }

    public static ArrayList<Item> getAllJsonItems(File jsonDirectory) {
        System.out.println("Adding all json items from " + jsonDirectory.getAbsolutePath());
        if (!jsonDirectory.exists()) jsonDirectory.mkdirs();
        ArrayList<Item> allItems = new ArrayList<>();
        try {
            for (File file : jsonDirectory.listFiles()) {
                if (!file.getName().endsWith(".json")) continue;
                if (!MainWindow.devMode && file.getName().contains("devmode")) continue;
                String jsonString = Files.readString(file.toPath());
                Item[] jsonBlocks2 = JsonManager.gson_itemAdapter.fromJson(jsonString, Item[].class);
                if (jsonBlocks2 != null && jsonBlocks2.length > 0) {
                    // append to list
                    for (Item block : jsonBlocks2) {
                        allItems.add(block);
                    }
                }
            }
            return allItems;
        } catch (IOException e) {
            ErrorHandler.report(e);
        }
        return new ArrayList<>();
    }

    public static void block_makeClassJavaFiles(ArrayList<Block> blocks, File directory) throws IOException {
        StringBuilder blockClasses = new StringBuilder();
        StringBuilder blockIDs = new StringBuilder();
        for (Block block : blocks) {
            if (block == null) {
                System.err.println("A block is null");
                continue;
            }


            blockClasses.append("public static Block BLOCK_" +
                            nameToJavaName("block", block.name) + " = ItemList.getBlock((short)")
                    .append(block.id).append(");").append("\n");
            blockIDs.append("public static short BLOCK_" +
                    nameToJavaName("block", block.name) + " = ").append(block.id).append(";").append("\n");
        }
        Files.writeString(new File(directory, "BlockClasses.java").toPath(), blockClasses.toString());
        Files.writeString(new File(directory, "BlockIDs.java").toPath(), blockIDs.toString());
    }

    private static String nameToID(String name) {
        String id = name.toLowerCase().replaceAll(" ", "-");
        return "xbuilders:" + id;
    }

    public static void synthesizeItems(ArrayList<Block> blocks,
                                       ArrayList<EntitySupplier> entities, File outputFile) throws IOException {
        ArrayList<Item> items = new ArrayList<>();
        for (Block block : blocks) {
            if (block == null) continue;
            if (block.name.toLowerCase().contains("hidden")) continue;
            Item item = new Item(nameToID(block.name), block.name);
            item.setBlock(block.id);
            items.add(item);
        }
        for (EntitySupplier entity : entities) {
            if (entity == null) continue;
            if (entity.name.toLowerCase().contains("hidden")) continue;
            System.out.println(entity.name);
            Item item = new Item(nameToID(entity.name), entity.name);
            item.setEntity(entity.id);
            items.add(item);
        }
        String json = JsonManager.gson_itemAdapter.toJson(items);
        Files.writeString(outputFile.toPath(), json);
    }

    private static String nameToJavaName(String prefix, String name) {
        return prefix.toUpperCase().replaceAll("_", "") +
                "_" +
                name.toUpperCase()
                        .replaceAll("hidden", "")
                        .replaceAll("[^A-Z0-9_]", "")
                        .replaceAll("\\s+", "_");
    }

    public static void exportBlocksToJson(List<Block> list, File out) {
        //Save list as json
        try {
            String jsonString = JsonManager.gson_blockAdapter.toJson(list);
            Files.writeString(out.toPath(), jsonString);
            System.out.println("Saved " + list.size() + " blocks to " + out.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void item_makeClassJavaFiles(ArrayList<Item> items, File directory) throws IOException {
        StringBuilder itemClasses = new StringBuilder();
        for (Item item : items) {
            String entry = "public static final Item " + nameToJavaName("item", item.name) +
                    " = new Item(\"" + item.id + "\", \"" + item.name + "\");\n";
            if (item.getBlock() != null) {

            }
            itemClasses.append(entry);
        }
        itemClasses.append("""
                
                    public static void initItems(
                            IntMap<Block> blocks,
                            IntMap<EntitySupplier> entities) {
                """);
        for (Item item : items) {
            if (item.getBlock() != null) {
                itemClasses.append(nameToJavaName("item", item.name) + ".block = blocks.get(" + item.getBlock().id + ");\n");
            } else if (item.getEntity() != null) {
                itemClasses.append(nameToJavaName("item", item.name) + ".entity = entities.get(" + item.getEntity().id + ");\n");
            }
        }

        itemClasses.append("\n\n}");
        Files.writeString(new File(directory, "ItemClasses.java").toPath(), itemClasses.toString());
    }


}
