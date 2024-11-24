package com.xbuilders.game.UI;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.smile.SmileGenerator;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.item.StorageSpace;
import com.xbuilders.engine.items.recipes.CraftingRecipes;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.engine.utils.json.fasterXML.itemStack.ItemStackDeserializer;
import com.xbuilders.engine.utils.json.fasterXML.itemStack.ItemStackSerializer;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class CraftingUI extends UI_ItemWindow {
    UI_ItemStackGrid craftingGrid, playerGrid, outputGrid;

    public CraftingUI(NkContext ctx, NKWindow window) {
        super(ctx, window, "Crafting");
        menuDimensions.y = 550;
        craftingGrid = new UI_ItemStackGrid(window, "Grid", new StorageSpace(9), this, true);
        outputGrid = new UI_ItemStackGrid(window, "Output", new StorageSpace(1), this, true);
        outputGrid.showButtons = false;
        outputGrid.itemFilter = (stack) -> false;
        craftingGrid.storageSpace.changeEvent = () -> {
            craftingGrid.storageSpace.getAsList();
            System.out.println("Changed");
            String[] recipeMap = new String[9];
            for(int i = 0; i < craftingGrid.storageSpace.size(); i++) {
                recipeMap[i] = craftingGrid.storageSpace.get(i) == null ? null : craftingGrid.storageSpace.get(i).item.name;
            }
            String output = CraftingRecipes.recipeMap.get(recipeMap);
            System.out.println("Output: " + output);
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
        }
    }
}
