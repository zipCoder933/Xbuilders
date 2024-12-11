/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.UI_Hotbar;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemStackGrid;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.WindowEvents;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.util.*;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class UI_Inventory extends UI_ItemWindow implements WindowEvents {

    public static final int KEY_OPEN_INVENTORY = GLFW.GLFW_KEY_E;

    public UI_Inventory(NkContext ctx, Item[] itemList, NKWindow window, UI_Hotbar hotbar) {
        super(ctx, window, "Item List");
        this.hotbar = hotbar;
        setItemList(itemList);
        searchBox = new TextBox(25);
        searchBox.setOnSelectEvent(() -> clearSearch());
        searchBox.setOnChangeEvent(() -> searchQueryEvent());

        playerInventory = new UI_ItemStackGrid(window, "Inventory", GameScene.player.inventory, this, true);
        // We have to create the window initially
        nk_begin(ctx, title, NkRect.create(), windowFlags);
        nk_end(ctx);
        setOpen(false);
    }

    private void clearSearch() {
        searchBox.setValueAsString("");
        scrollValue = 0;
        filteredItems.clear();
        filteredItems.addAll(allItems);
    }

    private void searchQueryEvent() {
        filteredItems.clear();
        for (Item item : allItems) {
            if (matchesSearch(item, searchBox.getValueAsString())) {
                filteredItems.add(item);
            }
        }
    }

    public void setItemList(Item[] itemList) {
        allItems.clear();
        allItems.addAll(Arrays.asList(itemList));
        Collections.sort(allItems, comparator);
        filteredItems.addAll(allItems);
    }

    final Comparator<Item> comparator = (o1, o2) -> {
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
            return 1;
        }
    };

    UI_ItemStackGrid playerInventory;
    int scrollValue = 0;
    int Allitems_Height = 270; //total item list window size
    final int playerInv_height = 350; //player inventory window size
    UI_Hotbar hotbar;
    final List<Item> allItems = new ArrayList<>();
    final List<Item> filteredItems = new ArrayList<>(); //filtered items>
    TextBox searchBox;
    String hoveredItem;


    public void onOpenEvent() {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) setOpen(false);

        if (drawAllInventory()) menuDimensions.y = Allitems_Height + playerInv_height;
        else menuDimensions.y = playerInv_height;
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) {
            setOpen(false);
        }

        hoveredItem = null;
        if (drawAllInventory()) {
            menuDimensions.y = Allitems_Height + playerInv_height;
            //Search box
            nk_layout_row_dynamic(ctx, 20, 1);
            nk_label(ctx, "Search Item List", NK_TEXT_LEFT);
            nk_layout_row_dynamic(ctx, 25, 1);
            searchBox.render(ctx);
            ctx.style().button().padding().set(0, 0);
            inventoryGroup(stack, filteredItems);
        } else menuDimensions.y = playerInv_height;

        nk_layout_row_dynamic(ctx, playerInv_height, 1);
        playerInventory.draw(stack, ctx, maxColumns);

        Theme.resetEntireButtonStyle(ctx);
        if (hoveredItem != null)
            Nuklear.nk_tooltip(ctx, " " + hoveredItem + ")"); //ending character is important
    }

    private boolean drawAllInventory() {
        return GameScene.getGameMode() == GameMode.FREEPLAY || MainWindow.devMode;
    }


    private void inventoryGroup(MemoryStack stack, List<Item> items) {
        nk_layout_row_dynamic(ctx, Allitems_Height, 1);
        if (nk_group_begin(ctx, title, NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR)) {
            int maxRows = (int) (Math.floor(Allitems_Height / getItemSize()) - 1);

            scrollValue = MathUtils.clamp(scrollValue, 0, Math.max(0, (items.size() / maxColumns) - 1));
            int itemID = scrollValue * maxColumns;
            int rows = 0;


            rows:
            while (rows < maxRows) {
                //  nk_layout_row_dynamic(ctx, getItemSize(), maxColumns);// row
                nk_layout_row_static(ctx, getItemSize(), getItemSize(), maxColumns);
                rows++;

                for (int column = 0; column < maxColumns; ) {
                    if (itemID >= items.size()) {
                        break rows;
                    }

                    Item item = items.get(itemID);
                    if (nk_widget_is_hovered(ctx)) {
                        hoveredItem = item.name;
                    }
                    if (nk_button_image(ctx, item.getNKIcon())) {
                        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
                            draggingItem = new ItemStack(item, item.maxStackSize);
                        } else draggingItem = new ItemStack(item, 1);
                    }
                    column++;
                    itemID++;
                }
            }
        }
        nk_group_end(ctx);
    }

    private boolean matchesSearch(Item item, String searchCriteria) {
        if (searchCriteria == null || searchCriteria.isBlank() || item.name == null) return true;
        for (String tag : item.tags) {
            if (tag != null && tag.toLowerCase().contains(
                    searchCriteria.toLowerCase().trim())) {
                return true;
            }
        }
        return item.name.toLowerCase().contains(searchCriteria);
    }


    @Override
    public void windowResizeEvent(int width, int height) {

    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) return false;

        if (isOpen() && searchBox.isFocused()) {
            searchQueryEvent();
            return true;
        } else if (action == GLFW.GLFW_RELEASE && key == KEY_OPEN_INVENTORY) {
            setOpen(!isOpen());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) return false;

        scrollValue -= yoffset;
        return true;
    }

}
