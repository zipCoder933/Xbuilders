package com.xbuilders.engine.items.recipes;

import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.recipes.crafting.CraftingRecipes;
import com.xbuilders.engine.items.recipes.smelting.SmeltingRecipes;
import com.xbuilders.engine.utils.MiscUtils;

public class RecipeRegistry {
    public static final CraftingRecipes craftingRecipes = new CraftingRecipes();
    public static final SmeltingRecipes smeltingRecipes = new SmeltingRecipes();


    public static boolean elementMatches(String recipe, String item) {
        if (recipe != null && recipe.startsWith("#")) {
            String A_tag = recipe.substring(1);
            Item B_item = Registrys.items.getItem(item);
            if (B_item == null) return false;
            if (!B_item.getTags().contains(A_tag)) {
                return false;
            }
        } else if (!MiscUtils.equalOrNull(recipe, item)) { //if any input doesn't match
            return false;
        }
        return true;
    }

}
