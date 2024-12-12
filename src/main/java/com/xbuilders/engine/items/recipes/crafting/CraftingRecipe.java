package com.xbuilders.engine.items.recipes.crafting;

import com.xbuilders.engine.items.recipes.Recipe;
import com.xbuilders.game.vanilla.ui.RecipeDrawingUtils;
import org.lwjgl.nuklear.NkContext;

import java.util.Arrays;
import java.util.Objects;

public class CraftingRecipe extends Recipe {

    /**
     * There are a few ways to define a crafting recipe input:
     * <p>
     * #tag             Specific to a tag
     * xbuilders:item   Specific to an item
     */

    public boolean shapeless = false;
    public String[] input;
    public String output;
    public int amount = 1;


    public CraftingRecipe() {
    }

    public CraftingRecipe(String a, String b, String c,
                          String d, String e, String f,
                          String g, String h, String i,
                          String output, int amount) {
        shapeless = false;
        this.input[0] = a;
        this.input[1] = b;
        this.input[2] = c;
        this.input[3] = d;
        this.input[4] = e;
        this.input[5] = f;
        this.input[6] = g;
        this.input[7] = h;
        this.input[8] = i;

        this.output = output;
        this.amount = amount;
    }

    public CraftingRecipe(boolean shapeless, String[] input, String output, int amount) {
        this.shapeless = shapeless;
        this.input = input;
        this.output = output;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CraftingRecipe that)) return false;
        return Objects.deepEquals(input, that.input);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(input);
    }

    public String toString() {
        return Arrays.toString(input) + " -> " + output;
    }

    @Override
    public void drawRecipe(NkContext ctx) {
        RecipeDrawingUtils.drawRecipe(ctx, this);
    }
}
