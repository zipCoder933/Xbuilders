package com.xbuilders.engine.server.model.items.recipes.smelting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xbuilders.engine.server.model.items.Registrys;
import com.xbuilders.engine.server.model.items.item.Item;
import com.xbuilders.engine.server.model.items.recipes.RecipeList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SmeltingRecipes extends RecipeList<SmeltingRecipe> {

    public SmeltingRecipes() {
        super("Smelting");

    }

    public SmeltingRecipe getFromInput(String input) {
        for (SmeltingRecipe recipe : recipeList) {
            if (recipe.input.equals(input)) return recipe;
            else if (recipe.input.startsWith("#")) {
                String tag = recipe.input.substring(1);
                Item item = Registrys.items.getItem(input);
                if (item != null && item.tags.contains(tag)) return recipe;
            }
        }
        return null;
    }

    public ArrayList<SmeltingRecipe> getFromOutput(Item output) {
        ArrayList<SmeltingRecipe> recipes = new ArrayList<>();
        for (SmeltingRecipe recipe : recipeList) {
            if (recipe.output.equals(output.id)) recipes.add(recipe);
        }
        return recipes;
    }

    //TODO: For somre reason the super class loadFrom file doesnt work so well with abstract types
    public void loadFromFile(File file) throws IOException {
        String json = Files.readString(file.toPath());
        List<SmeltingRecipe> recipeList = objectMapper.readValue(json, new TypeReference<List<SmeltingRecipe>>() {
        });
        System.out.println("Loaded " + recipeList.size() + " " + name + " recipes from " + file);
        this.recipeList.addAll(recipeList);
    }

}
