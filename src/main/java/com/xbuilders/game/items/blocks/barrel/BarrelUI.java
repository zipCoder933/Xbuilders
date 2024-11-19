package com.xbuilders.game.items.blocks.barrel;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.StorageSpace;
import com.xbuilders.engine.ui.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.items.UI_ItemWindow;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

public class BarrelUI extends UI_ItemWindow {
    UI_ItemStackGrid barrelGrid, playerGrid;
    StorageSpace barrelStorage;

    public BarrelUI(NkContext ctx, NKWindow window) {
        super(ctx, window, "Barrel Grid");
        menuDimensions.y = 550;
        barrelStorage = new StorageSpace(33);
        barrelGrid = new UI_ItemStackGrid(window, "Barrel", barrelStorage, this);
        playerGrid = new UI_ItemStackGrid(window, "Player", GameScene.player.inventory, this);
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        barrelGrid.draw(stack, ctx, maxColumns, 250);
        playerGrid.draw(stack, ctx, maxColumns, 250);
    }
}
