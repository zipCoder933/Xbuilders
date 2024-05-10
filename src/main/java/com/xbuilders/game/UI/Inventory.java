/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.UI;

import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;

import java.io.IOException;
import java.util.HashSet;

import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class Inventory extends GameUIElement {

    /**
     * @param playerInfo the playerBackpack to set
     */
    public void setPlayerInfo(MyGame.GameInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public Inventory(NkContext ctx, NKWindow window, UIResources uires,
            Item[] itemList, Hotbar hotbar) throws IOException {
        super(ctx, window, uires);
        this.hotbar = hotbar;
        this.itemList = itemList;
        buttonWidth = new WidgetWidthMeasurement(0);
        searchBox = new TextBox(25);
        searchBox.setOnSelectEvent(() -> {
            searchBox.setValueAsString("");
        });

        // We have to create the window initially
        nk_begin(ctx, WINDOW_TITLE, NkRect.create(), windowFlags);
        nk_end(ctx);
        setOpen(false);
    }

    final int menuWidth = 700;
    final int menuHeight = 550;
    final int itemListHeight = 250;
    final int backpackMenuSize = menuHeight; // Its ok since this is the last row
    final int maxColumns = 11;
    Hotbar hotbar;
    Item[] itemList;
    private MyGame.GameInfo playerInfo;
    TextBox searchBox;

    WidgetWidthMeasurement buttonWidth;
    String hoveredItem = "";
    boolean isOpen = false;

    public boolean isOpen() {
        return isOpen;

    }

    public void setOpen(boolean open) {
        if (open) {
            nk_window_show(ctx, WINDOW_TITLE, windowFlags);
            isOpen = true;
        } else {
            isOpen = false;
        }
    }

    int windowFlags = Nuklear.NK_WINDOW_TITLE | Nuklear.NK_WINDOW_NO_SCROLLBAR | Nuklear.NK_WINDOW_CLOSABLE;
    final String WINDOW_TITLE = "Item List";

    public boolean canCloseWithKeyEvents() {
        return !searchBox.isFocused();
    }

    @Override
    public void draw(MemoryStack stack) {
        if (isOpen) {

            GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            NkRect windowDims2 = NkRect.malloc(stack);
            Theme.resetEntireButtonStyle(ctx);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            nk_style_set_font(ctx, uires.font_8);

            nk_rect(
                    window.getWidth() / 2 - (menuWidth / 2),
                    window.getHeight() / 2 - (menuHeight / 2),
                    menuWidth, menuHeight, windowDims2);

            if (nk_begin(ctx, WINDOW_TITLE, windowDims2, windowFlags)) {
                nk_layout_row_dynamic(ctx, 20, 1);
                ctx.style().text().color().set(Theme.lightGray);
                Nuklear.nk_text(ctx, hoveredItem, Nuklear.NK_TEXT_ALIGN_CENTERED);
                Theme.resetTextColor(ctx);

                // Draw a search bar
                nk_layout_row_dynamic(ctx, 20, 1);
                Nuklear.nk_label(ctx, "Search Item List", Nuklear.NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 25, 1);
                searchBox.render(ctx);

                ctx.style().button().padding().set(0, 0);
                inventoryGroup(stack);
                backpackGroup();

                Theme.resetEntireButtonStyle(ctx);
            }
            nk_end(ctx);
        }
        if (nk_window_is_hidden(ctx, WINDOW_TITLE)) {
            isOpen = false;
        }
    }

    private void inventoryGroup(MemoryStack stack) {
        nk_layout_row_dynamic(ctx, itemListHeight, 1);
        if (Nuklear.nk_group_begin(ctx, WINDOW_TITLE, Nuklear.NK_WINDOW_TITLE)) {
            String searchCriteria = searchBox.getValueAsString();
            if (searchCriteria.equals("") || searchCriteria.isBlank() || searchCriteria == null) {
                searchCriteria = null;
            } else
                searchCriteria = searchCriteria.toLowerCase();

            int itemID = 0;
            rows: while (true) {
                nk_layout_row_dynamic(ctx, buttonWidth.width, maxColumns);// row
                cols: for (int column = 0; column < maxColumns;) {
                    if (itemID >= itemList.length) {
                        break rows;
                    }

                    Item item = itemList[itemID];
                    if (isVisible(item) && matchesSearch(item, searchCriteria)) {
                        if (Nuklear.nk_widget_is_hovered(ctx)) {
                            hoveredItem = item.toString();
                        }
                        if (Nuklear.nk_button_image(ctx, item.getNKIcon())) {
                            addItemToBackpack(item);
                        }
                        buttonWidth.measure(ctx, stack);
                        column++;
                    }
                    itemID++;
                }
            }

        }
        Nuklear.nk_group_end(ctx);
    }

    private void backpackGroup() {
        nk_layout_row_dynamic(ctx, backpackMenuSize, 1);
        if (Nuklear.nk_group_begin(ctx, "My Items", Nuklear.NK_WINDOW_TITLE)) {
            nk_layout_row_dynamic(ctx, 20, 3);
            if (Nuklear.nk_button_label(ctx, "Organize")) {
                organizeBackpack();
            } else if (Nuklear.nk_button_label(ctx, "Remove")) {
                playerInfo.playerBackpack[hotbar.getSelectedItemIndex()] = null;
            } else if (Nuklear.nk_button_label(ctx, "Clear")) {
                for (int i = 0; i < playerInfo.playerBackpack.length; i++) {
                    playerInfo.playerBackpack[i] = null;
                }
            }

            int itemID = 0;
            rows: while (true) {
                nk_layout_row_dynamic(ctx, buttonWidth.width, maxColumns);
                cols: for (int i = 0; i < maxColumns; i++) {
                    if (itemID >= playerInfo.playerBackpack.length) {
                        break rows;
                    }
                    Item item = playerInfo.playerBackpack[itemID];

                    if (itemID == hotbar.getSelectedItemIndex()) {
                        ctx.style().button().border_color().set(Theme.white);
                    } else {
                        ctx.style().button().border_color().set(Theme.blue);
                    }

                    if (item != null) {
                        if (Nuklear.nk_widget_is_hovered(ctx)) {
                            hoveredItem = item.toString();
                        }
                        if (Nuklear.nk_button_image(ctx, item.getNKIcon())) {
                            hotbar.setSelectedIndex(itemID);
                        }
                    } else if (Nuklear.nk_button_text(ctx, "")) {
                        hotbar.setSelectedIndex(itemID);
                    }
                    itemID++;
                }
            }
        }
        Nuklear.nk_group_end(ctx);
    }

    private void organizeBackpack() {
        HashSet<Item> newBackpack = new HashSet();
        for (int i = 0; i < playerInfo.playerBackpack.length; i++) {
            if (playerInfo.playerBackpack[i] != null) {
                newBackpack.add(playerInfo.playerBackpack[i]);
            }
            playerInfo.playerBackpack[i] = null;
        }
        int index = 0;
        for (Item item : newBackpack) {
            playerInfo.playerBackpack[index] = item;
            index++;
        }
        hotbar.setSelectedIndex(0);
    }

    private boolean isVisible(Item item) {
        return item.name != null && !item.name.toLowerCase().contains("hidden");
    }

    private boolean matchesSearch(Item item, String searchCriteria) {
        return searchCriteria == null || item.name == null ||
                item.name.toLowerCase().contains(searchCriteria);
    }

    private void addItemToBackpack(Item item) {
        if (playerInfo.playerBackpack[hotbar.getSelectedItemIndex()] == item) {
            playerInfo.playerBackpack[hotbar.getSelectedItemIndex()] = null;
        } else {
            for (int i = 0; i < playerInfo.playerBackpack.length; i++) {
                if (playerInfo.playerBackpack[i] == null) {
                    playerInfo.playerBackpack[i] = item;
                    hotbar.setSelectedIndex(i);
                    return;
                }
            }
            playerInfo.playerBackpack[hotbar.getSelectedItemIndex()] = item;
            hotbar.changeSelectedIndex(1);
        }
    }

    public void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_I -> {
                    if (isOpen()) {
                        if (canCloseWithKeyEvents())
                            setOpen(false);
                    } else
                        setOpen(true);
                }
                case GLFW.GLFW_KEY_ESCAPE -> {
                    if (canCloseWithKeyEvents())
                        setOpen(false);
                }
            }
        }
    }
}
