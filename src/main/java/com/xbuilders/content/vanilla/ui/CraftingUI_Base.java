package com.xbuilders.content.vanilla.ui;

import com.xbuilders.engine.server.items.Registrys;
import com.xbuilders.engine.server.items.item.Item;
import com.xbuilders.engine.server.items.item.ItemStack;
import com.xbuilders.engine.server.items.item.StorageSpace;
import com.xbuilders.engine.server.items.recipes.RecipeRegistry;
import com.xbuilders.engine.server.items.recipes.crafting.CraftingRecipe;
import com.xbuilders.engine.client.visuals.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.client.visuals.gameScene.items.UI_ItemWindow;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class CraftingUI_Base {
    public UI_ItemStackGrid inputGrid, outputGrid;

    public StorageSpace playerStorage;
    private int output_quantity = 0;
    private CraftingRecipe recipe;
    UI_ItemWindow itemWindow;
    NkContext ctx;

    public CraftingUI_Base(NkContext ctx, NKWindow window, UI_ItemWindow itemWindow, StorageSpace playerStorage, int inputSize) {
        this.ctx = ctx;
        this.itemWindow = itemWindow;
        this.playerStorage = playerStorage;


        inputGrid = new UI_ItemStackGrid(window, "Grid", new StorageSpace(inputSize), itemWindow, true);
        outputGrid = new UI_ItemStackGrid(window, "Output", new StorageSpace(1), itemWindow, true);
        outputGrid.showButtons = false;
        outputGrid.itemFilter = (stack) -> false;

        inputGrid.dragFromEvent = (stack, index, rightClick) -> {
            System.out.println("dragFromEvent stack: " + stack + " index: " + index + " rightClick: " + rightClick);
        };

        inputGrid.dragToEvent = (stack, index, rightClick) -> {
            System.out.println("dragToEvent stack: " + stack + " index: " + index + " rightClick: " + rightClick);
        };

        inputGrid.storageSpace.changeEvent = () -> {
            inputGrid.storageSpace.getList();
            String[] recipeMap = new String[9];
            for (int i = 0; i < inputGrid.storageSpace.size(); i++) {
                recipeMap[i] = inputGrid.storageSpace.get(i) == null ? null : inputGrid.storageSpace.get(i).item.id;
            }
            //Print every entry in the recipeMap
            recipe = RecipeRegistry.craftingRecipes.getFromInput(recipeMap);
            if (recipe != null && recipe.output != null) {

                //Calculate how many of the output we can craft
                int multiplier = Integer.MAX_VALUE;
                for (int i = 0; i < inputGrid.storageSpace.size(); i++) {
                    if (inputGrid.storageSpace.get(i) != null) {
                        multiplier = Math.min(multiplier, inputGrid.storageSpace.get(i).stackSize);
                    }
                }
                if (multiplier == Integer.MAX_VALUE || multiplier == 0) multiplier = 1;
                output_quantity = multiplier;

                Item item = Registrys.getItem(recipe.output);
                if (item == null) {
                    System.err.println("Recipe output not found: " + recipe.output);
                    return;
                }
                outputGrid.storageSpace.set(0, new ItemStack(item, recipe.amount * multiplier));
            } else outputGrid.storageSpace.set(0, null);
        };

        outputGrid.dragFromEvent = (stack, index, rightClick) -> {
            int take;
            if (!rightClick && outputGrid.storageSpace.get(0) == null) {
                outputGrid.storageSpace.set(index, new ItemStack(stack.item, stack.stackSize - recipe.amount));
                stack.stackSize = recipe.amount;
                take = 1;
            } else take = output_quantity;

            for (int i = 0; i < inputGrid.storageSpace.size(); i++) {
                if (inputGrid.storageSpace.get(i) != null) {
                    inputGrid.storageSpace.get(i).stackSize -= take;
                }
            }
        };
        inputGrid.showButtons = false;
    }

    public void draw(MemoryStack stack, int inputColumns) {
        nk_layout_row_dynamic(ctx, 200, 2);
        inputGrid.draw(stack, ctx, inputColumns);
        outputGrid.draw(stack, ctx, 1);
    }


    public void onCloseEvent() {
        //Give the items back to the player
        for (int i = 0; i < inputGrid.storageSpace.size(); i++) {
            if (inputGrid.storageSpace.get(i) != null) {
                playerStorage.acquireItem(inputGrid.storageSpace.get(i));
            }
            inputGrid.storageSpace.set(i, null);
        }
    }
}
