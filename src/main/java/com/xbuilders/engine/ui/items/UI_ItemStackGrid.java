package com.xbuilders.engine.ui.items;

import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.StorageSpace;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetSizeMeasurement;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.nuklear.Nuklear.nk_group_end;

public class UI_ItemStackGrid {
    WidgetSizeMeasurement buttonSize;
    final String title;
    String hoveredItem;
    final StorageSpace storageSpace;
    UI_ItemWindow box;
    NKWindow window;
    int rowCount = 0;

    public UI_ItemStackGrid(NKWindow window, String title, StorageSpace storageSpace, UI_ItemWindow box) {
        this.title = title;
        this.storageSpace = storageSpace;
        this.box = box;
        this.window = window;
        buttonSize = new WidgetSizeMeasurement(0);
    }

    public void draw(MemoryStack stack, NkContext ctx, int maxColumns, int height) {
        hoveredItem = null;
        nk_layout_row_dynamic(ctx, height, 1);
        nk_style_set_font(ctx, Theme.font_10);


        if (nk_group_begin(ctx, title, NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR)) {

            nk_layout_row_dynamic(ctx, 20, GameScene.getGameMode() == GameMode.FREEPLAY ? 2 : 1);
            if (nk_button_label(ctx, "Sort")) {
                storageSpace.organize();
            } else if (GameScene.getGameMode() == GameMode.FREEPLAY && nk_button_label(ctx, "Clear")) {
                for (int i = 0; i < storageSpace.size(); i++) {
                    storageSpace.set(i, null);
                }
            }

            rowCount = 0;
            int index = 0;
            rows:
            while (true) {
                nk_layout_row_dynamic(ctx, buttonSize.width, maxColumns);
                rowCount++;
                cols:
                for (int i = 0; i < maxColumns; i++) {
                    if (index >= storageSpace.size()) {
                        break rows;
                    }
                    ItemStack item = storageSpace.get(index);
                    ctx.style().button().border_color().set(Theme.blue);


                    if (item != null) {
                        if (nk_widget_is_hovered(ctx)) {
                            hoveredItem = item.item.name;
                        }
                        if (UI_ItemWindow.drawItemStack(stack, ctx, item)) {
                            itemClickEvent(item, index);
                        }
                    } else if (nk_button_text(ctx, "")) {
                        itemClickEvent(item, index);
                    }
                    index++;
                    buttonSize.measure(ctx, stack);
                }
            }
            if (hoveredItem != null) Nuklear.nk_tooltip(ctx, " " + hoveredItem + ")");
//            if (Nuklear.nk_tooltip_begin(ctx, 300)) {
//                UI_Inventory.drawItemStack(stack, ctx, new ItemStack(Items.TOOL_ANIMAL_FEED,4));
//            }   Nuklear.nk_tooltip_end(ctx);
        }
        nk_group_end(ctx);

    }

    /**
     * When the box is clicked
     *
     * @param item
     * @param index
     */
    private void itemClickEvent(ItemStack item, int index) {
        if (box.draggingItem != null) {
            if (item != null && box.draggingItem.item.equals(item.item) && item.item.maxStackSize > 1) {
                ItemStack thisStack = storageSpace.get(index);
                thisStack.stackSize += box.draggingItem.stackSize;
                if (thisStack.stackSize > thisStack.item.maxStackSize) {
                    box.draggingItem.stackSize -= (byte) (thisStack.stackSize - thisStack.item.maxStackSize);
                    thisStack.stackSize = (byte) thisStack.item.maxStackSize;
                } else box.draggingItem = null;
            } else {
                ItemStack replaceStack = storageSpace.get(index);
                storageSpace.set(index, box.draggingItem);
                box.draggingItem = replaceStack;
            }
        } else if (item != null) {
            box.draggingItem = item;
            storageSpace.set(index, null);
        }
    }
}
