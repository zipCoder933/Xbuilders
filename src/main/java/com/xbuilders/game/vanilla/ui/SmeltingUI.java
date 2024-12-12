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
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.window.NKWindow;
import org.joml.Vector3i;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_prog;

public class SmeltingUI extends UI_ItemWindow {
    UI_ItemStackGrid inputGrid, fuelGrid, playerGrid, outputGrid;
    FurnaceData furnaceData;

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
            smeltWhenReady();
        };
        inputGrid.storageSpace.changeEvent = () -> {
            smeltWhenReady();
        };
        outputGrid.storageSpace.changeEvent = () -> {
            smeltWhenReady();
        };


        playerGrid = new UI_ItemStackGrid(window, "Player", GameScene.player.inventory, this, true);
    }

    private void smeltWhenReady() {
        if (furnaceData.lastSmeltTime == 0) {
            furnaceData.lastSmeltTime = System.currentTimeMillis();
        } else if (System.currentTimeMillis() - furnaceData.lastSmeltTime > SMELT_TIME_MS) {
            if (smelt()) {///If we smelted something successfully
                furnaceData.lastSmeltTime = System.currentTimeMillis();
            } else furnaceData.lastSmeltTime = 0; //Otherwise we reset the timer
        }
    }


    private boolean smelt() {
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
        if (furnaceData.lastSmeltTime > 0) {
            nk_prog(ctx, System.currentTimeMillis() - furnaceData.lastSmeltTime, SMELT_TIME_MS, false);
            smeltWhenReady();
        } else nk_prog(ctx, 0, SMELT_TIME_MS, false);

        nk_layout_row_dynamic(ctx, 250, 1);
        playerGrid.draw(stack, ctx, maxColumns);
    }

    Vector3i targetPos = new Vector3i();

    public void openUI(BlockData blockData, Vector3i targetPos) {
        this.targetPos = targetPos;

        //Load furnace data
        if (blockData != null) {
            try {
                byte[] json = blockData.toByteArray();
                System.out.println("Deserializing JSON: " + new String(json));
                furnaceData = StorageSpace.binaryJsonMapper.readValue(json, FurnaceData.class);
                System.out.println("\tLoaded furnace data: " + furnaceData);

            } catch (IOException e) {
                ErrorHandler.log(e);
            }
        }
        setOpen(true);
    }

    public void onCloseEvent() {
        //Furnace data is separate from the storage spaces so we have to save them separately
        furnaceData.inputGrid = inputGrid.storageSpace.get(0);
        furnaceData.fuelGrid = fuelGrid.storageSpace.get(0);
        furnaceData.outputGrid = outputGrid.storageSpace.get(0);


        //Save furnace data
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StorageSpace.binaryJsonMapper.writeValue(baos, furnaceData);
            BlockData bd = new BlockData(baos.toByteArray());
            System.out.println("Serializing JSON: " + new String(bd.toByteArray()));
            System.out.println("\tfurnace data: " + furnaceData);
            GameScene.setBlockData(bd, targetPos.x, targetPos.y, targetPos.z);
        } catch (IOException e) {
            ErrorHandler.log(e);
        }
    }

    public void onOpenEvent() {
        if (furnaceData == null) {
            System.err.println("Furnace data is null");
            furnaceData = new FurnaceData();
        }

        //Set the storage spaces
        inputGrid.storageSpace.set(0, furnaceData.inputGrid);
        fuelGrid.storageSpace.set(0, furnaceData.fuelGrid);
        outputGrid.storageSpace.set(0, furnaceData.outputGrid);


        if (furnaceData.lastSmeltTime == 0) return;
        int coalAmt = fuelGrid.storageSpace.get(0) == null ? 0 : fuelGrid.storageSpace.get(0).stackSize;
        int coalToBurn = (int) ((System.currentTimeMillis() - furnaceData.lastSmeltTime) / SMELT_TIME_MS);
        coalToBurn = Math.min(coalAmt, coalToBurn);
        for (int i = 0; i < coalToBurn; i++) {
            smelt();
        }
    }
}
