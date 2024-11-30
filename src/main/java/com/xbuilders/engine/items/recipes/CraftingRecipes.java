package com.xbuilders.engine.items.recipes;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CraftingRecipes {
    //It has to be in this format to make sense when it is in JSON
    private final List<CraftingRecipe> craftingRecipes = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void add(CraftingRecipe recipe) {
        craftingRecipes.add(recipe);
    }

    public void remove(CraftingRecipe recipe) {
        craftingRecipes.remove(recipe);
    }

    public void clear() {
        craftingRecipes.clear();
    }

    public CraftingRecipe getFromInput(String[] input) {
        for (CraftingRecipe recipe : craftingRecipes) {
            if (Objects.deepEquals(recipe.input, input)) {
                return recipe;
            }
        }
        return null;
    }

    public CraftingRecipe getFromOutput(String outputID) {
        for (CraftingRecipe recipe : craftingRecipes) {
            if (recipe.output.equals(outputID)) {
                return recipe;
            }
        }
        return null;
    }

    private final TypeReference<List<CraftingRecipe>> type_craftingRecipes = new TypeReference<List<CraftingRecipe>>() {
    };

    public void writeToFile(File file) throws IOException {
        String json = objectMapper.writeValueAsString(craftingRecipes);
        Files.writeString(file.toPath(), json);
    }

    public void loadFromFile(File file) throws IOException {
        String json = Files.readString(file.toPath());
        craftingRecipes.addAll(objectMapper.readValue(json, type_craftingRecipes));
    }


}
