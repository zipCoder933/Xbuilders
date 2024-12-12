package com.xbuilders.engine.items.recipes.smelting;

import com.xbuilders.engine.items.recipes.Recipe;
import com.xbuilders.game.vanilla.ui.RecipeDrawingUtils;
import org.lwjgl.nuklear.NkContext;

import java.util.Objects;

public class SmeltingRecipe extends Recipe {
    public String input;
    public String output;
    public int amount = 1;

    public SmeltingRecipe() {
    }

    public SmeltingRecipe(String input, String output, int amount) {
        this.input = input;
        this.output = output;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SmeltingRecipe that = (SmeltingRecipe) o;
        return amount == that.amount && Objects.equals(input, that.input) && Objects.equals(output, that.output);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, amount);
    }

    public String toString() {
        return input + " -> " + output;
    }

    @Override
    public void drawRecipe(NkContext ctx, int groupHeight) {
        RecipeDrawingUtils.drawRecipe(ctx, this,groupHeight);
    }
}
