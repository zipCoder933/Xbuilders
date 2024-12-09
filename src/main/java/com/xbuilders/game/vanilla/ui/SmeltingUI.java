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
import static org.lwjgl.nuklear.Nuklear.nk_prog;

public class SmeltingUI extends UI_ItemWindow {
    UI_ItemStackGrid inputGrid, fuelGrid, playerGrid, outputGrid;

    public SmeltingUI(NkContext ctx, NKWindow window) {
        super(ctx, window, "Smelting");
        menuDimensions.y = 500;
        inputGrid = new UI_ItemStackGrid(window, "Input", new StorageSpace(1), this, true);
        fuelGrid = new UI_ItemStackGrid(window, "Fuel", new StorageSpace(1), this, true);
        outputGrid = new UI_ItemStackGrid(window, "Output", new StorageSpace(1), this, true);
        outputGrid.showButtons = false;
        fuelGrid.showButtons = false;
        inputGrid.showButtons = false;

        outputGrid.itemFilter = (stack) -> false;
        fuelGrid.itemFilter = (stack) -> stack.item.tags.contains("flammable");


        playerGrid = new UI_ItemStackGrid(window, "Player", GameScene.player.inventory, this, true);
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        nk_layout_row_dynamic(ctx, 100, 3);
        inputGrid.draw(stack, ctx, 1);
        fuelGrid.draw(stack, ctx, 1);
        outputGrid.draw(stack, ctx, 1);

        nk_layout_row_dynamic(ctx, 20, 1);
        nk_prog(ctx, 10, 100, false);

        nk_layout_row_dynamic(ctx, 250, 1);
        playerGrid.draw(stack, ctx, maxColumns);
    }

    public void onCloseEvent() {
        //Give the items back to the player
        for (int i = 0; i < inputGrid.storageSpace.size(); i++) {
            if (inputGrid.storageSpace.get(i) != null) {
                GameScene.player.inventory.acquireItem(inputGrid.storageSpace.get(i));
            }
            inputGrid.storageSpace.set(i, null);
        }
    }
}
