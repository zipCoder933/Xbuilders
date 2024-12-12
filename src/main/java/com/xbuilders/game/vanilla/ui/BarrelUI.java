package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.StorageSpace;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.engine.world.chunk.BlockData;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.window.NKWindow;
import org.joml.Vector3f;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;

import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;

public class BarrelUI extends UI_ItemWindow {
    UI_ItemStackGrid barrelGrid, playerGrid;
    final StorageSpace barrelStorage;
    BlockData barrelBlockData;


    Chunk chunk;

    public BarrelUI(NkContext ctx, NKWindow window) {
        super(ctx, window, "Barrel Grid");
        barrelStorage = new StorageSpace(33);
        menuDimensions.y = 550;
        barrelGrid = new UI_ItemStackGrid(window, "Barrel", barrelStorage, this, true);
        playerGrid = new UI_ItemStackGrid(window, "Player", GameScene.player.inventory, this, true);
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        nk_layout_row_dynamic(ctx, 250, 1);
        barrelGrid.draw(stack, ctx, maxColumns);

        nk_layout_row_dynamic(ctx, 250, 1);
        playerGrid.draw(stack, ctx, maxColumns);
    }

    public void openUI(BlockData blockData, Chunk chunk) {
        barrelBlockData = blockData;
        barrelStorage.clear();
        this.chunk = chunk;
        if (blockData != null) {
            try {
                barrelStorage.loadFromJson(blockData.toByteArray());
            } catch (IOException e) {
                System.out.println("Error deserializing JSON, Making storage empty: " + e.getMessage());
            }
        }
        setOpen(true);
    }

    public void dropAllStorage(BlockData blockData, Vector3f targetPos) {
        if (blockData != null) {
            try {
                barrelStorage.loadFromJson(blockData.toByteArray());
                for (int i = 0; i < barrelStorage.size(); i++) {
                    if(barrelStorage.get(i) == null) continue;
                    GameScene.placeItemDrop(targetPos, barrelStorage.get(i), false);
                }
            } catch (IOException e) {
                System.out.println("Error deserializing JSON, Making storage empty: " + e.getMessage());
            }
        }
    }

    public void onCloseEvent() {
        try {
            barrelBlockData.setByteArray(barrelStorage.writeToJson());
            chunk.markAsModified();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        barrelStorage.clear();
    }
}
