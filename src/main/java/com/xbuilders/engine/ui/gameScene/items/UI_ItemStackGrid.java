package com.xbuilders.engine.ui.gameScene.items;

import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.items.StorageSpace;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetSizeMeasurement;
import org.lwjgl.glfw.GLFW;
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
                            hoveredItem = itemTooltip(item);
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

    private String itemTooltip(ItemStack item) {
        String str = item.item.name;
        if (item.item.maxDurability > 0 && item.durability < item.item.maxDurability) {
            str += "\n " + ((int) item.durability) + " / " + item.item.maxDurability;
        }
        return str;
    }

    /**
     * When the box is clicked
     *
     * @param clickedItem
     * @param index
     */
    private void itemClickEvent(ItemStack clickedItem, int index) {
        if (box.draggingItem != null) {
            if (window.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_RIGHT)) {
                System.out.println("Right click");
                if (box.draggingItem.stackSize > 1) {
                    if (clickedItem == null) {
                        box.draggingItem.stackSize--;
                        storageSpace.set(index, new ItemStack(box.draggingItem.item, 1));
                    } else if (clickedItem.item.equals(box.draggingItem.item) && clickedItem.stackSize < clickedItem.item.maxStackSize) {
                        box.draggingItem.stackSize--;
                        clickedItem.stackSize++;
                    }
                }
            } else if (clickedItem != null && box.draggingItem.item.equals(clickedItem.item)
                    && clickedItem.item.maxStackSize > 1 && box.draggingItem.stackSize < clickedItem.item.maxStackSize) {
                ItemStack thisStack = storageSpace.get(index);

                int totalStackSize = thisStack.stackSize + box.draggingItem.stackSize;
                if (totalStackSize > thisStack.item.maxStackSize) {
                    box.draggingItem.stackSize = (totalStackSize - thisStack.item.maxStackSize);
                    thisStack.stackSize = thisStack.item.maxStackSize;
                } else {
                    thisStack.stackSize += box.draggingItem.stackSize;
                    box.draggingItem = null;
                }
            } else {
                ItemStack replaceStack = storageSpace.get(index);
                storageSpace.set(index, box.draggingItem);
                box.draggingItem = replaceStack;
            }
        } else if (clickedItem != null) {
            box.draggingItem = clickedItem;
            storageSpace.set(index, null);
        }
    }
}
