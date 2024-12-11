package com.xbuilders.engine.items.recipes.crafting;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbuilders.engine.items.recipes.RecipeRegistry;

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
            if (recipe.shapeless) {
                if (inputMatchesShapeless(recipe.input, input)) return recipe;
            } else {
                if (inputMatchesShaped(recipe.input, input)) return recipe;
            }
        }
        return null;
    }

    private boolean inputMatchesShapeless(String[] recipe, String[] itemIDs) {
        for (int i = 0; i < recipe.length; i++) {
            String rItem = recipe[i];
            boolean foundPair = false;
            for (int j = 0; j < itemIDs.length; j++) {
                String item = itemIDs[j];
                if (RecipeRegistry.elementMatches(rItem, item)) {
                    foundPair = true;
                    break;
                }
            }
            if (!foundPair) return false;
        }
        return true;
    }


    public static boolean inputMatchesShaped(String[] recipe, String[] itemIDs) {
        if (Objects.deepEquals(itemIDs, recipe)) { //If the inputs are the same
            return true;
        } else {
            for (int i = 0; i < recipe.length; i++) {
                String rItem = recipe[i];
                String item = itemIDs[i];
                if (!RecipeRegistry.elementMatches(rItem, item)) return false;
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
        if (json.isBlank()) return;
        List<CraftingRecipe> recipeList = objectMapper.readValue(json, type_craftingRecipes);
        System.out.println("Loaded " + recipeList.size() + " crafting recipes from " + file);
        this.recipeList.addAll(recipeList);
    }


}
