package com.xbuilders.engine.items.recipes.crafting;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.recipes.crafting.json.CraftingRecipeDeserializer;
import com.xbuilders.engine.items.recipes.crafting.json.CraftingRecipeSerializer;
import com.xbuilders.engine.utils.MiscUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CraftingRecipes {
    //It has to be in this format to make sense when it is in JSON
    private final List<CraftingRecipe> recipeList = new ArrayList<>();


    private static final TypeReference<List<CraftingRecipe>> type_craftingRecipes = new TypeReference<List<CraftingRecipe>>() {
    };
    private final static ObjectMapper objectMapper = new ObjectMapper();

    static {
//        SimpleModule module = new SimpleModule();
//        module.addDeserializer(CraftingRecipe.class, new CraftingRecipeDeserializer());
//        module.addSerializer(CraftingRecipe.class, new CraftingRecipeSerializer());
//        objectMapper.registerModule(module);
    }

    public void add(CraftingRecipe recipe) {
        recipeList.add(recipe);
    }

    public void remove(CraftingRecipe recipe) {
        recipeList.remove(recipe);
    }

    public void clear() {
        recipeList.clear();
    }

    public CraftingRecipe getFromInput(String[] input) {
        for (CraftingRecipe recipe : recipeList) {
            if (inputMatches(recipe.input, input)) {
                return recipe;
            }
        }
        return null;
    }


    public static boolean inputMatches(String[] recipe, String[] itemIDs) {
        if (Objects.deepEquals(itemIDs, recipe)) { //If the inputs are the same
            return true;
        } else {
            for (int i = 0; i < recipe.length; i++) {
                String rItem = recipe[i];
                String item = itemIDs[i];

                if (rItem != null && rItem.startsWith("#")) {
                    String A_tag = rItem.substring(1);
                    Item B_item = Registrys.items.getItem(item);
                    if (B_item == null) return false;
//                    System.out.println("\tTag match: " + A_tag + " vs  item: " + B_item.id + " tags: " + B_item.tags.toString());

                    if (!B_item.getTags().contains(A_tag)) {
                        return false;
                    }
                } else if (!MiscUtils.equalOrNull(rItem, item)) { //if any input doesn't match
                    return false;
                }
            }
            return true;
        }
    }


    public CraftingRecipe getFromOutput(String outputID) {//TODO: Add tag recipes to this too
        for (CraftingRecipe recipe : recipeList) {
            if (recipe.output.equals(outputID)) {
                return recipe;
            }
        }
        return null;
    }


    public void writeToFile(File file) throws IOException {
        String json = objectMapper.writeValueAsString(recipeList);
        Files.writeString(file.toPath(), json);
    }

    public void loadFromFile(File file) throws IOException {
        String json = Files.readString(file.toPath());
        List<CraftingRecipe> recipeList = objectMapper.readValue(json, type_craftingRecipes);
        System.out.println("Loaded " + recipeList.size() + " crafting recipes from " + file);
        this.recipeList.addAll(recipeList);
    }


}
