package com.xbuilders.engine.items.recipes.smelting;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.recipes.DisplayRecipe;
import com.xbuilders.engine.items.recipes.Recipe;
import com.xbuilders.engine.items.recipes.RecipeRegistry;
import com.xbuilders.engine.items.recipes.TagPossibilities;
import com.xbuilders.game.vanilla.ui.RecipeDrawingUtils;
import org.lwjgl.nuklear.NkContext;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class SmeltingRecipe extends Recipe {
    public String input;
    public String output;
    public int amount = 1;

    public SmeltingRecipe() {
    }

    public SmeltingRecipe(SmeltingRecipe recipe) {
        this.input = recipe.input;
        this.output = recipe.output;
        this.amount = recipe.amount;
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
        RecipeDrawingUtils.drawRecipe(ctx, this, groupHeight);
    }


    @Override
    public DisplayRecipe getDisplayRecipe() {
        //Make the tag possibilities from ALL inputs
        TagPossibilities tagPossibilities = new TagPossibilities(input);
        HashSet<String> exploredTags = new HashSet<>();

        //Make the formatted recipe
        DisplayRecipe formattedRecipe = new DisplayRecipe();

        if (tagPossibilities.isEmpty()) {
            System.out.println("No tags in formatted recipe");
            formattedRecipe.add(new SmeltingRecipe(this));
            return formattedRecipe;
        }

        //If the input does have tags
        //20 possible combinations at the most!
        for (int x = 0; x < 30; x++) {
            System.out.println("recipe " + x);
            SmeltingRecipe recipe = new SmeltingRecipe(this); //Make a copy of Our crafting recipe
            exploredTags.clear();

            //Fill ALL the inputs that have tags with the first possible input
            if (RecipeRegistry.isTag(recipe.input)) {
                String tag = recipe.input;
                List<Item> possibleInputs = tagPossibilities.get(tag); //Set the element to the first possible input
                exploredTags.add(tag); //Add the tag to the explored list so we can remove the first element

                if (possibleInputs.isEmpty()) { //If we can't find any possible inputs, return what we have so far
                    System.out.println("Could not find any more possible inputs for tag: " + recipe.input);
                    return formattedRecipe;
                }
                recipe.input = possibleInputs.get(0).id; //Get the first element
            }

            formattedRecipe.add(recipe); //Add the recipe to the formatted recipe list
            tagPossibilities.removeElementOfTags(exploredTags, 0);  //Remove the first element from all explored tags so we dont use them again
        }
        return formattedRecipe;
    }

}
