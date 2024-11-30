package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.item.StorageSpace;
import com.xbuilders.engine.items.recipes.CraftingRecipe;
import com.xbuilders.engine.items.recipes.RecipeRegistry;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class CraftingUI extends UI_ItemWindow {
    UI_ItemStackGrid craftingGrid, playerGrid, outputGrid;

    public CraftingUI(NkContext ctx, NKWindow window) {
        super(ctx, window, "Crafting");
        menuDimensions.y = 500;
        craftingGrid = new UI_ItemStackGrid(window, "Grid", new StorageSpace(9), this, true);
        outputGrid = new UI_ItemStackGrid(window, "Output", new StorageSpace(1), this, true);
        outputGrid.showButtons = false;
        outputGrid.itemFilter = (stack) -> false;

        craftingGrid.storageSpace.changeEvent = () -> {
            craftingGrid.storageSpace.getList();
            String[] recipeMap = new String[9];
            for (int i = 0; i < craftingGrid.storageSpace.size(); i++) {
                recipeMap[i] = craftingGrid.storageSpace.get(i) == null ? null : craftingGrid.storageSpace.get(i).item.id;
            }
            //Print every entry in the recipeMap
            CraftingRecipe recipe = RecipeRegistry.craftingRecipes.getFromInput(recipeMap);
            if (recipe != null) {
                outputGrid.storageSpace.set(0, new ItemStack(recipe.output, recipe.amount));
            } else outputGrid.storageSpace.set(0, null);
        };

        outputGrid.storageSpace.changeEvent = () -> {
            if (outputGrid.storageSpace.get(0) == null && draggingItem != null) {
                outputGrid.storageSpace.getList()[0] = null; //We cant use set event otherwise we will get an infinite loop
                for (int i = 0; i < craftingGrid.storageSpace.size(); i++) { //We cant use clear() otherwise we will get an infinite loop
                    craftingGrid.storageSpace.getList()[i] = null;
                }
            }
        };

        craftingGrid.showButtons = false;
        playerGrid = new UI_ItemStackGrid(window, "Player", GameScene.player.inventory, this, true);
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        nk_layout_row_dynamic(ctx, 200, 2);
        craftingGrid.draw(stack, ctx, 3);
        outputGrid.draw(stack, ctx, 1);

        nk_layout_row_dynamic(ctx, 250, 1);
        playerGrid.draw(stack, ctx, maxColumns);
    }

    public void onCloseEvent() {
        //Give the items back to the player
        for (int i = 0; i < craftingGrid.storageSpace.size(); i++) {
            if (craftingGrid.storageSpace.get(i) != null) {
                GameScene.player.inventory.acquireItem(craftingGrid.storageSpace.get(i));
            }
            craftingGrid.storageSpace.set(i, null);
        }
    }
}
