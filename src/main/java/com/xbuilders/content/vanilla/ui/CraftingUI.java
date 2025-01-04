package com.xbuilders.content.vanilla.ui;

import com.xbuilders.engine.server.model.Server;
import com.xbuilders.engine.client.visuals.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.client.visuals.gameScene.items.UI_ItemWindow;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class CraftingUI extends UI_ItemWindow {
    UI_ItemStackGrid playerGrid;
    public CraftingUI_Base craftingGrid;


    public CraftingUI(NkContext ctx, NKWindow window) {
        super(ctx, window, "Crafting");
        menuDimensions.y = 500;
        craftingGrid = new CraftingUI_Base(ctx, window, this, Server.userPlayer.inventory, 9);
        playerGrid = new UI_ItemStackGrid(window, "Player", Server.userPlayer.inventory, this, true);
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        craftingGrid.draw(stack, 3);

        nk_layout_row_dynamic(ctx, 250, 1);
        playerGrid.draw(stack, ctx, maxColumns);
    }

    public void onCloseEvent() {
        craftingGrid.onCloseEvent();
    }
}
