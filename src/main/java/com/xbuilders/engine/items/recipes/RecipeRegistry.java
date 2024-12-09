package com.xbuilders.engine.items.recipes;

import com.xbuilders.engine.items.recipes.crafting.CraftingRecipes;
import com.xbuilders.engine.items.recipes.smelting.SmeltingRecipes;

public class RecipeRegistry {
    public static final CraftingRecipes craftingRecipes = new CraftingRecipes();
    public static final SmeltingRecipes smeltingRecipes = new SmeltingRecipes();
}
