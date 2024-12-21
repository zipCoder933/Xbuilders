package com.xbuilders.engine.game.model.items.recipes.crafting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xbuilders.engine.game.model.items.Registrys;
import com.xbuilders.engine.game.model.items.item.Item;
import com.xbuilders.engine.game.model.items.recipes.RecipeList;
import com.xbuilders.engine.game.model.items.recipes.RecipeRegistry;
import com.xbuilders.engine.client.visuals.ui.gameScene.items.UI_ItemGrid;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class CraftingRecipes extends RecipeList<CraftingRecipe> {

    public CraftingRecipes() {
        super("Crafting");
    }

    static {
//        SimpleModule module = new SimpleModule();
//        module.addDeserializer(CraftingRecipe.class, new CraftingRecipeDeserializer());
//        module.addSerializer(CraftingRecipe.class, new CraftingRecipeSerializer());
//        objectMapper.registerModule(module);
    }

    public ArrayList<CraftingRecipe> getFromOutput(Item outputID) {
        ArrayList<CraftingRecipe> recipes = new ArrayList<>();
        for (CraftingRecipe recipe : recipeList) {
            if (recipe.output.equals(outputID.id)) {
                recipes.add(recipe); //Add the recipe to the recipes
            }
        }
        return recipes;
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

    public void loadFromFile(File file) throws IOException {
        System.out.println("Loading " + name + " recipes from " + file);
        String json = Files.readString(file.toPath());
        List<CraftingRecipe> recipeList = objectMapper.readValue(json, new TypeReference<List<CraftingRecipe>>() {
        });
        this.recipeList.addAll(recipeList);
    }

}
