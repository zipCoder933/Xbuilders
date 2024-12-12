package com.xbuilders.engine.items.recipes.crafting;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbuilders.engine.items.recipes.RecipeRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
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
        //Check for shaped recipes first
        for (CraftingRecipe recipe : recipeList) {
            if (!recipe.shapeless && inputMatchesShaped(recipe.input, input)) return recipe;
        }
        //Shapeless comes next
        for (CraftingRecipe recipe : recipeList) {
            if (recipe.shapeless && inputMatchesShapeless(recipe.input, input)) return recipe;
        }
        return null;
    }

    private boolean inputMatchesShapeless(String[] recipe, String[] itemIDs) {
        String[] itemIDsCopy = itemIDs.clone(); //We create a copy of the array and set already matched items to null

        for (int i = 0; i < recipe.length; i++) {
            String rItem = recipe[i];
            boolean foundPair = false;
            for (int j = 0; j < itemIDsCopy.length; j++) {
                String item = itemIDsCopy[j];
                if (item != null && RecipeRegistry.elementMatches(rItem, item)) {
                    foundPair = true;
                    itemIDsCopy[j] = null;
                    break;
                }
            }
            if (!foundPair) return false;
        }

        //If we have items left over, we can't match
        int nonNullElementsLeft = 0;
        for (int i = 0; i < itemIDsCopy.length; i++) {
            if (itemIDsCopy[i] != null) nonNullElementsLeft++;
        }
        if (nonNullElementsLeft > 0) return false;
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
