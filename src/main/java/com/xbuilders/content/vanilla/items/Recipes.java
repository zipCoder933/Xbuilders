package com.xbuilders.content.vanilla.items;

import com.xbuilders.engine.server.items.block.Block;
import com.xbuilders.engine.server.items.block.BlockRegistry;
import com.xbuilders.engine.server.items.item.Item;
import com.xbuilders.engine.server.items.recipes.RecipeRegistry;
import com.xbuilders.engine.server.items.recipes.crafting.CraftingRecipe;
import com.xbuilders.engine.server.items.recipes.crafting.CraftingRecipes;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.content.vanilla.items.blocks.RenderType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;

public class Recipes {
    public static void loadRecipes() throws IOException {
        for (File jsonFile : Objects.requireNonNull(ResourceUtils.resource("items/recipes/crafting").listFiles())) {
            RecipeRegistry.craftingRecipes.loadFromFile(jsonFile);
        }
        for (File jsonFile : Objects.requireNonNull(ResourceUtils.resource("items/recipes/smelting").listFiles())) {
            RecipeRegistry.smeltingRecipes.loadFromFile(jsonFile);
        }
    }

    private void synthesizeBlockDyedRecipes(ArrayList<Item> itemList) throws IOException {
        System.out.println("Synthesizing block variants...");


        CraftingRecipes dyed = new CraftingRecipes();
        CraftingRecipes doorsTrapdoors = new CraftingRecipes();


        String[] colors = new String[]{
                "red",
                "orange",
                "yellow",
                "green",
                "lime",
                "cyan",
                "blue",
                "magenta",
                "purple",
                "gray",
                "light_gray",
                "brown",
                "black",
                "white",};

        for (Item item : itemList) {
            //If the item id has a color
            colorLoop:
            for (String color : colors) {
                if (item.id.contains(color)) {
                    String nonColorName = item.id
                            .replace(color, "")
                            .replace("xbuilders:", "").replace("_", " ").trim();
                    String dye = "xbuilders:" + color + "_dye";
                    Block block = item.getBlock();
                    if (block == null) continue;

                    //System.out.println("Synthesizing " + nonColorName + " " + color);

                    if (block.renderType == RenderType.BLOCK && block.alias.contains("glass")) {
                        String base = "xbuilders:glass";
                        dyed.add(new CraftingRecipe(
                                base, base, dye,
                                base, base, null,
                                null, null, null,
                                item.id, 4));
                    } else if (block.renderType == RenderType.BLOCK && nonColorName.contains("concrete")) {
                        String base = "xbuilders:concrete";
                        dyed.add(new CraftingRecipe(
                                base, base, dye,
                                base, base, null,
                                null, null, null,
                                item.id, 4));
                    } else if (block.renderType == RenderType.BLOCK && nonColorName.contains("wool")) {
                        String base = "xbuilders:wool";
                        dyed.add(new CraftingRecipe(
                                base, base, dye,
                                base, base, null,
                                null, null, null,
                                item.id, 4));
                    } else if (block.renderType == RenderType.BLOCK && nonColorName.contains("stained_wood")) {
                        String base = "#wood";
                        dyed.add(new CraftingRecipe(
                                base, base, dye,
                                base, base, null,
                                null, null, null,
                                item.id, 4));
                    } else if (block.renderType == RenderType.BLOCK && nonColorName.contains("siding")) {
                        String base = "xbuilders:tan_siding";
                        dyed.add(new CraftingRecipe(
                                base, base, dye,
                                base, base, null,
                                null, null, null,
                                item.id, 4));
                    }

                    break colorLoop;
                }
            }
        }
        dyed.writeToFile(ResourceUtils.resource("items/recipes/crafting/dyed.json"));
        System.out.println("Done synthesizing");
    }

    private void synthesizeBlockVariantRecipes(ArrayList<Item> itemList) throws IOException {
        System.out.println("Synthesizing block variants...");


        CraftingRecipes variants = new CraftingRecipes();
        CraftingRecipes doorsTrapdoors = new CraftingRecipes();

        Predicate<Block> matchPredicate = (b) -> !b.alias.contains("log") && !b.alias.contains("leaves");

        for (Item item : itemList) {

            if (item.getBlock() != null) {
                Block block = item.getBlock();
                //MatchTexture
                Item baseBlock = Items.getBlockWithSharedTexture(
                        matchPredicate,
                        item, //item
                        BlockRegistry.DEFAULT_BLOCK_TYPE_ID, RenderType.ORIENTABLE_BLOCK);
                //Match name
                if (baseBlock == null) baseBlock = Items.getBlockWithSharedName(
                        matchPredicate,
                        item, //item
                        new String[]{"door", "trapdoor"}, //invalid matches
                        BlockRegistry.DEFAULT_BLOCK_TYPE_ID, RenderType.ORIENTABLE_BLOCK);

                if (baseBlock == null) continue;

                if (block.renderType == RenderType.SLAB) {
                    variants.add(new CraftingRecipe(
                            null, null, null,
                            null, null, null,
                            baseBlock.id, baseBlock.id, baseBlock.id,
                            item.id, 6));
                } else if (block.renderType == RenderType.STAIRS) {
                    variants.add(new CraftingRecipe(
                            null, null, baseBlock.id,
                            null, baseBlock.id, baseBlock.id,
                            baseBlock.id, baseBlock.id, baseBlock.id,
                            item.id, 6));
                } else if (block.renderType == RenderType.FENCE) {
                    variants.add(new CraftingRecipe(
                            null, null, null,
                            baseBlock.id, "xbuilders:stick", baseBlock.id,
                            baseBlock.id, "xbuilders:stick", baseBlock.id,
                            item.id, 10));
                } else if (block.renderType == RenderType.FENCE_GATE) {
                    variants.add(new CraftingRecipe(
                            null, null, null,
                            "xbuilders:stick", baseBlock.id, "xbuilders:stick",
                            "xbuilders:stick", baseBlock.id, "xbuilders:stick",
                            item.id, 4));
                } else if (block.renderType == RenderType.PILLAR) {
                    variants.add(new CraftingRecipe(
                            null, baseBlock.id, null,
                            null, baseBlock.id, null,
                            null, baseBlock.id, null,
                            item.id, 4));
                } else if (block.renderType == RenderType.DOOR_HALF) {
                    doorsTrapdoors.add(new CraftingRecipe(
                            baseBlock.id, baseBlock.id, null,
                            baseBlock.id, baseBlock.id, null,
                            baseBlock.id, baseBlock.id, null,
                            item.id, 3));
                } else if (block.renderType == RenderType.TRAPDOOR) {
                    doorsTrapdoors.add(new CraftingRecipe(
                            null, null, null,
                            baseBlock.id, baseBlock.id, baseBlock.id,
                            baseBlock.id, baseBlock.id, baseBlock.id,
                            item.id, 6));
                } else if (block.renderType == RenderType.PANE) {
                    variants.add(new CraftingRecipe(
                            baseBlock.id, null, baseBlock.id,
                            baseBlock.id, null, baseBlock.id,
                            baseBlock.id, null, baseBlock.id,
                            item.id, 12));
                }
            } else {
                Item baseBlock = Items.getBlockWithSharedName(
                        matchPredicate,
                        item,
                        new String[]{"door", "trapdoor"},
                        BlockRegistry.DEFAULT_BLOCK_TYPE_ID, RenderType.ORIENTABLE_BLOCK);
                if (baseBlock == null) continue;
                if (item.id.contains("boat")) {
                    System.out.println(item.id);
                    variants.add(new CraftingRecipe(
                            null, null, null,
                            baseBlock.id, null, baseBlock.id,
                            baseBlock.id, baseBlock.id, baseBlock.id,
                            item.id, 1));
                }
            }
        }
        System.out.println("Writing recipes...");
        variants.writeToFile(ResourceUtils.resource("items/recipes/crafting/variants.json"));
        doorsTrapdoors.writeToFile(ResourceUtils.resource("items/recipes/crafting/doorsTrapdoors.json"));
        System.out.println("Done synthesizing");
    }

}
