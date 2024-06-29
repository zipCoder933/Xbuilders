/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.UI;

import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

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

    public static final int KEY_OPEN_INVENTORY = GLFW.GLFW_KEY_E;

    /**
     * @param playerInfo the playerBackpack to set
     */
    public void setPlayerInfo(MyGame.GameInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public Inventory(NkContext ctx, Item[] itemList, NKWindow window, UIResources uires,
                     Hotbar hotbar) throws IOException {
        super(ctx, window, uires);
        this.hotbar = hotbar;
        setItemList(itemList);
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

    public void setItemList(Item[] itemList) {
        List<Item> items = new ArrayList<>();

        // Only keep items that are visible
        for (int i = 0; i < itemList.length; i++) {
            if (itemList[i] == null || !isVisible(itemList[i]))
                continue;
            items.add(itemList[i]);
        }

        // TODO: Figure out how to effectively sort items
        // Sort items by value in ascending order
        Collections.sort(items, comparator);

        // Convert arraylist to array
        this.itemList = new Item[items.size()];
        for (int i = 0; i < items.size(); i++) {
            this.itemList[i] = items.get(i);
        }
    }

    private boolean isVisible(Item item) {
        return item.name != null && !item.name.toLowerCase().contains("hidden");
    }

    Comparator<Item> comparator = new Comparator<Item>() {
        @Override
        public int compare(Item o1, Item o2) {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            } else {


                for (String tag : o1.getTags()) {  // If 2 items have a shared tag
                    if (o2.getTags().equals(tag)) {
                        return 0;
                    }
                }
                if (o1.itemType.equals(o2.itemType)) { // Else if 2 items have the same type
                    if (o1.itemType == ItemType.BLOCK) {
                        Block b1 = (Block) o1;
                        Block b2 = (Block) o2;
                        if (b1.type == b2.type) {
                            // System.out.println("Same block: " + b1.type);
                            return 0;
                        } else
                            return b1.type > b2.type ? 1 : -1;
                    }
                }


                //Tools come before blocks and Blocks come before entities
                if (o1.itemType == ItemType.TOOL && o2.itemType != ItemType.TOOL) {
                    return -1;
                } else if (o1.itemType != ItemType.TOOL && o2.itemType == ItemType.TOOL) {
                    return 1;
                } else if (o1.itemType == ItemType.ENTITY_LINK && o2.itemType != ItemType.ENTITY_LINK) {
                    return 1;
                } else if (o1.itemType != ItemType.ENTITY_LINK && o2.itemType == ItemType.ENTITY_LINK) {
                    return -1;
                }
                return 1;
            }
        }

        ;
    };

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


            drawScrollBar(stack);


            String searchCriteria = searchBox.getValueAsString();
            if (searchCriteria.equals("") || searchCriteria.isBlank() || searchCriteria == null) {
                searchCriteria = null;
            } else
                searchCriteria = searchCriteria.toLowerCase();

            int itemID = 0;
            rows:
            while (true) {
                nk_layout_row_dynamic(ctx, buttonWidth.width, maxColumns);// row
                cols:
                for (int column = 0; column < maxColumns; ) {
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

    private void drawScrollBar(MemoryStack stack) {
//        Nuklear.nnk_group_scrolled_offset_begin(ctx, scrollY); //TODO: Study how to increase scrolling speed or add a scrollbar
//
//        int contentHeight = itemListHeight;
//        int windowHeight = window.getHeight();
//        // Enable a scrolling region
//        Nuklear.nk_group_begin(ctx, "ScrollingRegion", NK_WINDOW_BORDER);
//        Nuklear.nk_layout_row_dynamic(ctx, contentHeight, 1);
//
//        // Render content that exceeds windowHeight here
//
//        // Example of a scrollbar (you need to implement this)
//        float scrollY = 0.0f; // Vertical scroll position (should be dynamic)
//        float scrollBarHeight = windowHeight; // Height of the scrollbar
//
//        Nuklear.nk_layout_row_begin(ctx, NK_STATIC, scrollBarHeight, 2);
//        Nuklear.nk_layout_row_push(ctx, 0.85f);
//
////        // Example of rendering a basic scrollbar
////        Nuklear.nk_style_set_color(ctx, Nuklear.NK_COLOR_SCROLLBAR, nk_rgb(150, 150, 150));
////        Nuklear.nk_style_set_color(ctx, Nuklear.NK_COLOR_SCROLLBAR_CURSOR, nk_rgb(180, 180, 180));
//
//        float[] value = new float[]{scrollY};
//
//        if (Nuklear.nk_slider_float(ctx, 0, value, 100, 0.1f)) {
//            scrollY = Nuklear.nk_slide_float(ctx, 0, scrollY, contentHeight - windowHeight, 1.0f);
//        }
//        Nuklear.nk_layout_row_end(ctx);
//
//        Nuklear.nk_group_end(ctx);
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
            rows:
            while (true) {
                nk_layout_row_dynamic(ctx, buttonWidth.width, maxColumns);
                cols:
                for (int i = 0; i < maxColumns; i++) {
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
                case KEY_OPEN_INVENTORY -> {
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
