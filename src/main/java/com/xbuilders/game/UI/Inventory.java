/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.UI;

import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;

import java.io.IOException;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;

import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_rect;
import static org.lwjgl.nuklear.Nuklear.nk_style_set_font;

import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
public class Inventory extends GameUIElement {

    /**
     * @param playerBackpack the playerBackpack to set
     */
    public void setPlayerBackpack(Item[] playerBackpack) {
        this.playerBackpack = playerBackpack;
    }

    public Inventory(NkContext ctx, NKWindow window, UIResources uires,
                     Item[] itemList, Hotbar hotbar) throws IOException {
        super(ctx, window, uires);
        this.hotbar = hotbar;
        this.itemList = itemList;
        buttonWidth = new WidgetWidthMeasurement(0);
    }

    int menuWidth = 700;
    int menuHeight = 550;
    int itemListHeight = 300;
    int playerItemsHeight = 450;
    Hotbar hotbar;
    Item[] itemList;
    private Item[] playerBackpack;

    WidgetWidthMeasurement buttonWidth;
    String hoveredItem = "";

    @Override
    public void draw(MemoryStack stack) {
        NkRect windowDims2 = NkRect.malloc(stack);

        Theme.resetEntireButtonStyle(ctx);
        Theme.resetWindowColor(ctx);
        Theme.resetWindowPadding(ctx);
        nk_style_set_font(ctx, uires.font_8);

        nk_rect(
                window.getWidth() / 2 - (menuWidth / 2),
                window.getHeight() / 2 - (menuHeight / 2),
                menuWidth, menuHeight, windowDims2);

        if (nk_begin(ctx, "Item List", windowDims2, Nuklear.NK_WINDOW_TITLE | Nuklear.NK_WINDOW_NO_SCROLLBAR)) {
            ctx.style().button().padding().set(0, 0);

            nk_layout_row_dynamic(ctx, 30, 1);
            Nuklear.nk_text(ctx, hoveredItem, Nuklear.NK_TEXT_ALIGN_CENTERED);


            nk_layout_row_dynamic(ctx, itemListHeight, 1);
            if (Nuklear.nk_group_begin(ctx, "Item List", Nuklear.NK_WINDOW_TITLE)) {

                int itemID = 0;
                rows:
                while (true) {
                    nk_layout_row_dynamic(ctx, buttonWidth.width, 11);
                    cols:
                    for (int i = 0; i < 11; i++) {
                        if (itemID >= itemList.length) {
                            break rows;
                        }
                        Item item = itemList[itemID];

                        if (Nuklear.nk_widget_is_hovered(ctx)) {
                            hoveredItem = item.toString();
                        }

                        if (Nuklear.nk_button_image(ctx, item.getNKIcon())) {
                            System.out.println(item.toString());
                            addItemToBackpack(item);
                        }

                        buttonWidth.measure(ctx, stack);

                        itemID++;
                    }
                }

            }
            Nuklear.nk_group_end(ctx);

            nk_layout_row_dynamic(ctx, playerItemsHeight, 1);
            if (Nuklear.nk_group_begin(ctx, "My Items", Nuklear.NK_WINDOW_TITLE)) {

                playerItemsHeight = (int) ((buttonWidth.width * 2) + 50);

                int itemID = 0;
                rows:
                while (true) {
                    nk_layout_row_dynamic(ctx, buttonWidth.width, 11);
                    cols:
                    for (int i = 0; i < 11; i++) {
                        if (itemID >= playerBackpack.length) {
                            break rows;
                        }
                        Item item = playerBackpack[itemID];

                        if (itemID == hotbar.selectedItemIndex) {
                            ctx.style().button().border_color().set(Theme.white);
                        } else {
                            ctx.style().button().border_color().set(Theme.blue);
                        }

                        if (item != null) {
                            if (Nuklear.nk_widget_is_hovered(ctx)) {
                                hoveredItem = item.toString();
                            }
                            if (Nuklear.nk_button_image(ctx, item.getNKIcon())) {
                                hotbar.selectedItemIndex = itemID;
                            }
                        } else if (Nuklear.nk_button_text(ctx, "")) {
                            hotbar.selectedItemIndex = itemID;
                        }
                        itemID++;
                    }
                }

            }
            Nuklear.nk_group_end(ctx);
            Theme.resetEntireButtonStyle(ctx);
        }
        nk_end(ctx);
    }

    private void addItemToBackpack(Item item) {
        if (playerBackpack[hotbar.selectedItemIndex] == item) {
            playerBackpack[hotbar.selectedItemIndex] = null;
        } else {
            playerBackpack[hotbar.selectedItemIndex] = item;
            hotbar.changeSelectedIndex(1);
        }
    }

}
