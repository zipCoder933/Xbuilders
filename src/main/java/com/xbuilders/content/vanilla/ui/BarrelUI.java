package com.xbuilders.content.vanilla.ui;

import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.server.LocalServer;
import com.xbuilders.engine.server.item.StorageSpace;
import com.xbuilders.engine.client.visuals.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.utils.ErrorHandler;
import com.xbuilders.window.NKWindow;
import org.joml.Vector3f;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class BarrelUI extends ContainerUI {
    UI_ItemStackGrid barrelGrid, playerGrid;
    final StorageSpace barrelStorage;

    public BarrelUI(NkContext ctx, NKWindow window) {
        super(ctx, window, "Barrel");
        barrelStorage = new StorageSpace(33);
        menuDimensions.y = 550;
        barrelGrid = new UI_ItemStackGrid(window, "Barrel", barrelStorage, this, true);
        playerGrid = new UI_ItemStackGrid(window, "Player", GameScene.userPlayer.inventory, this, true);

        barrelStorage.changeEvent = () -> {
            writeDataToWorld();
        };
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        super.drawWindow(stack, windowDims2);
        nk_layout_row_dynamic(ctx, 250, 1);
        barrelGrid.draw(stack, ctx, maxColumns);

        nk_layout_row_dynamic(ctx, 250, 1);
        playerGrid.draw(stack, ctx, maxColumns);
    }

    public void dropAllStorage(int x, int y, int z) {
        for (int i = 0; i < barrelStorage.size(); i++) {
            if (barrelStorage.get(i) == null) continue;
            LocalServer.placeItemDrop(new Vector3f(x, y, z), barrelStorage.get(i), false);
        }
    }

    public void readContainerData(byte[] bytes) {
        barrelStorage.clear();
        if (bytes != null) {
            try {
                barrelStorage.loadFromJson(bytes);
            } catch (IOException e) {
                System.out.println("Error deserializing JSON, Making storage empty: " + e.getMessage());
            }
        }
    }

    public byte[] writeContainerData() {
        try {
            return (barrelStorage.writeToJson());
        } catch (IOException e) {
            ErrorHandler.report(e);
            return new byte[0];
        }
    }

}
