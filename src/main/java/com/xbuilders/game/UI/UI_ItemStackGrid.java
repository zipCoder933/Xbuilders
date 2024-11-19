package com.xbuilders.game.UI;

import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.ItemRegistry;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.player.data.StorageSpace;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.game.items.Items;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.nuklear.Nuklear.nk_group_end;

public class UI_ItemStackGrid {
    WidgetWidthMeasurement buttonWidth;
    final String title;
    String hoveredItem;
    final StorageSpace storageSpace;

    public UI_ItemStackGrid(String title, StorageSpace storageSpace) {
        this.title = title;
        this.storageSpace = storageSpace;
        buttonWidth = new WidgetWidthMeasurement(0);
    }

    protected void draw(MemoryStack stack, NkContext ctx, int maxColumns, int height) {
        hoveredItem = null;
        nk_layout_row_dynamic(ctx, height, 1);
        nk_style_set_font(ctx, Theme.font_10);

        if (nk_group_begin(ctx, title, NK_WINDOW_TITLE)) {
            nk_layout_row_dynamic(ctx, 15, GameScene.getGameMode() == GameMode.FREEPLAY ? 3 : 2);
            if (nk_button_label(ctx, "Sort")) {
                storageSpace.organize();
            } else if (nk_button_label(ctx, "Remove")) {
//                storageSpace.set(hotbar.getSelectedItemIndex(), null);
            } else if (GameScene.getGameMode() == GameMode.FREEPLAY && nk_button_label(ctx, "Clear")) {
                for (int i = 0; i < storageSpace.size(); i++) {
                    storageSpace.set(i, null);
                }
            }

            int itemID = 0;
            rows:
            while (true) {
                nk_layout_row_dynamic(ctx, buttonWidth.width, maxColumns);
                cols:
                for (int i = 0; i < maxColumns; i++) {
                    if (itemID >= storageSpace.size()) {
                        break rows;
                    }
                    ItemStack item = storageSpace.get(itemID);
                    ctx.style().button().border_color().set(Theme.blue);


                    if (item != null) {
                        if (nk_widget_is_hovered(ctx)) hoveredItem = item.item.name;
                        if (UI_Inventory.drawItemStack(stack, ctx, item)) {
                            //hotbar.setSelectedIndex(itemID);
                        }


                    } else if (nk_button_text(ctx, "")) {
                        //hotbar.setSelectedIndex(itemID);
                    }
                    itemID++;
                    buttonWidth.measure(ctx, stack);
                }
            }
            if (hoveredItem != null) Nuklear.nk_tooltip(ctx, " " + hoveredItem + ")");

//            if (Nuklear.nk_tooltip_begin(ctx, 300)) {
//                UI_Inventory.drawItemStack(stack, ctx, new ItemStack(Items.TOOL_ANIMAL_FEED,4));
//            }   Nuklear.nk_tooltip_end(ctx);
        }
        nk_group_end(ctx);

    }
}
