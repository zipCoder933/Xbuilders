package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.item.StorageSpace;
import com.xbuilders.engine.items.recipes.RecipeRegistry;
import com.xbuilders.engine.items.recipes.smelting.SmeltingRecipe;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.window.NKWindow;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_prog;

public class SmeltingUI extends UI_ItemWindow {
    UI_ItemStackGrid inputGrid, fuelGrid, playerGrid, outputGrid;
    long timeSinceLastConsumption = 0;

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

        fuelGrid.storageSpace.changeEvent = () -> {
            consumeFuel();
        };
        inputGrid.storageSpace.changeEvent = () -> {
            consumeFuel();
        };
        outputGrid.storageSpace.changeEvent = () -> {
            consumeFuel();
        };


        playerGrid = new UI_ItemStackGrid(window, "Player", GameScene.player.inventory, this, true);
    }

    private void consumeFuel() {
        if (timeSinceLastConsumption == 0) {
            timeSinceLastConsumption = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - timeSinceLastConsumption > SMELT_TIME_MS) {
            if (smeltItem()) {///If we smelted something successfully
                timeSinceLastConsumption = System.currentTimeMillis();
            } else timeSinceLastConsumption = 0; //Otherwise we reset the timer
        }
    }

    private boolean smeltItem() {
        ItemStack input = inputGrid.storageSpace.get(0);
        ItemStack fuel = fuelGrid.storageSpace.get(0);

        if (input == null || input.stackSize == 0) return false; //Nothing to smelt
        if (fuel == null || fuel.stackSize == 0) return false; //No fuel

        SmeltingRecipe recipe = RecipeRegistry.smeltingRecipes.getFromInput(input.item.id);
        if (recipe == null) return false; //No recipe
        Item outputItem = Registrys.getItem(recipe.output);
        if (outputItem == null) return false; //No recipe

        ItemStack outputStack = outputGrid.storageSpace.get(0);
        if (outputStack == null) {
            ItemStack output = new ItemStack(outputItem, recipe.amount);
            outputGrid.storageSpace.set(0, output);
        } else if (outputStack.item.id.equals(outputItem.id)) {
            outputGrid.storageSpace.get(0).stackSize += recipe.amount;
        } else { //Wrong item
            return false;
        } //We already have an item in the output grid and its not the one we want

        //Reduce fuel
        fuelGrid.storageSpace.get(0).stackSize--;
        if (fuelGrid.storageSpace.get(0).stackSize == 0) {
            fuelGrid.storageSpace.set(0, null);
        }

        //Reduce input
        input.stackSize--;
        if (input.stackSize <= 0) {
            inputGrid.storageSpace.set(0, null);
        }

        return true;
    }

    long SMELT_TIME_MS = 5000;

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        nk_layout_row_dynamic(ctx, 100, 3);
        inputGrid.draw(stack, ctx, 1);
        fuelGrid.draw(stack, ctx, 1);
        outputGrid.draw(stack, ctx, 1);

        nk_layout_row_dynamic(ctx, 20, 1);
        if (timeSinceLastConsumption > 0) {
            nk_prog(ctx, System.currentTimeMillis() - timeSinceLastConsumption, SMELT_TIME_MS, false);
            consumeFuel();
        } else nk_prog(ctx, 0, SMELT_TIME_MS, false);

        nk_layout_row_dynamic(ctx, 250, 1);
        playerGrid.draw(stack, ctx, maxColumns);
    }

    Vector3i targetPos = new Vector3i();

    public void openUI(BlockData blockData, Vector3i targetPos) {
//        this.targetPos = targetPos;
//
//        if (blockData != null) {
//            try {
//                ItemStack[]
//                .loadFromJson(blockData.toByteArray());
//            } catch (IOException e) {
//                System.out.println("Error deserializing JSON, Making storage empty: " + e.getMessage());
//            }
//        }
//        setOpen(true);
    }

    public void onCloseEvent() {//TODO: Save contents of furnace
    }

    public void onOpenEvent() {
        if (timeSinceLastConsumption == 0) return;
        int coalAmt = fuelGrid.storageSpace.get(0) == null ? 0 : fuelGrid.storageSpace.get(0).stackSize;
        int coalToBurn = (int) ((System.currentTimeMillis() - timeSinceLastConsumption) / SMELT_TIME_MS);
        coalToBurn = Math.min(coalAmt, coalToBurn);

        System.out.println("Time since left: " + timeSinceLastConsumption + " Coal to burn: " + coalToBurn);
        for (int i = 0; i < coalToBurn; i++) {
            smeltItem();
        }
    }
}
