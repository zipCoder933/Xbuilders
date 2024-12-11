package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.item.StorageSpace;
import com.xbuilders.engine.items.recipes.RecipeRegistry;
import com.xbuilders.engine.items.recipes.crafting.CraftingRecipe;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class CraftingUI_Base {
    public UI_ItemStackGrid inputGrid, outputGrid;
    public StorageSpace playerStorage;
    private int output_quantity = 0;
    private boolean output_recipeMatched = false;

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
            CraftingRecipe recipe = RecipeRegistry.craftingRecipes.getFromInput(recipeMap);
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
                output_recipeMatched = true;
//                System.out.println("multiplier: " + multiplier);
                outputGrid.storageSpace.set(0, new ItemStack(recipe.output, recipe.amount * multiplier));
            } else outputGrid.storageSpace.set(0, null);
        };

        outputGrid.storageSpace.changeEvent = () -> {
            if (outputGrid.storageSpace.get(0) == null && itemWindow.draggingItem != null && output_recipeMatched) {
                output_recipeMatched = false;
//                System.out.println("Removing items " + output_quantity);
                for (int i = 0; i < inputGrid.storageSpace.size(); i++) {
                    if (inputGrid.storageSpace.get(i) != null) {
//                        System.out.println("\tstack size: " + (inputGrid.storageSpace.get(i).stackSize));
                        inputGrid.storageSpace.get(i).stackSize -= output_quantity;
                    }
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
