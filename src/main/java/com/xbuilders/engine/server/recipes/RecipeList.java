package com.xbuilders.engine.server.recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xbuilders.engine.client.visuals.RecipeDisplay;
import com.xbuilders.engine.server.item.Item;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class RecipeList<T extends Recipe> {
    public final String name;
    public final List<T> recipeList = new ArrayList<>();
    private final TypeReference<List<T>> typeReference = new TypeReference<List<T>>() {
    };

    public RecipeList(String name) {
        this.name = name;
    }

    public void add(T recipe) {
        this.recipeList.add(recipe);
    }

    public void remove(T recipe) {
        this.recipeList.remove(recipe);
    }

    public void clear() {
        recipeList.clear();
    }


    public abstract ArrayList<T> getFromOutput(Item output);

    @JsonIgnore
    public ArrayList<RecipeDisplay> getDisplayRecipesFromOutput(Item output) {
        ArrayList<T> recipes = getFromOutput(output);
        ArrayList<RecipeDisplay> formattedRecipes = new ArrayList<>();
        for (T recipe : recipes) {
            formattedRecipes.add(recipe.getDisplayRecipe());
        }
        return formattedRecipes;
    }

    public boolean hasRecipeWithOutput(Item output) {
        return getFromOutput(output) != null;
    }

    public final ObjectMapper objectMapper = new ObjectMapper();

    public void writeToFile(File file) throws IOException {
        System.out.println("Writing " + recipeList.size() + " " + name + " recipes to " + file);
        String json = objectMapper.writeValueAsString(recipeList);

        Files.writeString(file.toPath(), json);
    }

    public void loadFromFile(File file) throws IOException {
        String json = Files.readString(file.toPath());
        List<T> recipeList = objectMapper.readValue(json, typeReference);
        System.out.println("Loaded " + recipeList.size() + " " + name + " recipes from " + file);
        this.recipeList.addAll(recipeList);
    }

    public String toString() {
        return name + " recipes";// (x" + recipeList.size()+")";
    }

}
