package com.xbuilders.engine.items.recipes.smelting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SmeltingRecipes {
    //It has to be in this format to make sense when it is in JSON
    private final List<SmeltingRecipe> recipeList = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void add(SmeltingRecipe recipe) {
        this.recipeList.add(recipe);
    }

    public void remove(SmeltingRecipe recipe) {
        this.recipeList.remove(recipe);
    }

    public void clear() {
        recipeList.clear();
    }

    public SmeltingRecipe getFromInput(String input) {
        for (SmeltingRecipe recipe : recipeList) {
            if (recipe.input.equals(input)) return recipe;
        }
        return null;
    }

    private final TypeReference<List<SmeltingRecipe>> type_smeltingRecipe = new TypeReference<List<SmeltingRecipe>>() {
    };

    public void writeToFile(File file) throws IOException {
        String json = objectMapper.writeValueAsString(recipeList);
        Files.writeString(file.toPath(), json);
    }

    public void loadFromFile(File file) throws IOException {
        String json = Files.readString(file.toPath());
        List<SmeltingRecipe> recipeList = objectMapper.readValue(json, type_smeltingRecipe);
        System.out.println("Loaded " + recipeList.size() + " smelting recipes from " + file);
        this.recipeList.addAll(recipeList);
    }

}
