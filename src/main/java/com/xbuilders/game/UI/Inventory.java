/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.UI;

import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.player.data.PlayerData;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.game.XbuildersGame;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.WindowEvents;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.util.*;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class Inventory extends GameUIElement implements WindowEvents {

    public static final int KEY_OPEN_INVENTORY = GLFW.GLFW_KEY_E;

    public Inventory(NkContext ctx, Item[] itemList, NKWindow window, Hotbar hotbar, PlayerData playerData) throws IOException {
        super(ctx, window);
        this.hotbar = hotbar;
        this.gameInfo = playerData;
        setItemList(itemList);
        buttonWidth = new WidgetWidthMeasurement(0);

        searchBox = new TextBox(25);
        searchBox.setOnSelectEvent(() -> {
            searchBox.setValueAsString("");
            scrollValue = 0;
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
            if (itemList[i] == null || !isVisible(itemList[i])) continue;
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
                return 1;
            }
        }
    };


    int scrollValue = 0;
    final int menuWidth = 680;  //Menu window size
    final int menuHeight = 645; //Menu window size
    int itemListHeight = 285; //iten list window size
    final int backpackMenuSize = menuHeight; // Its ok since this is the last row
    final int maxColumns = 11;
    Hotbar hotbar;
    Item[] itemList;
    private PlayerData gameInfo;
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


    @Override
    public void draw(MemoryStack stack) {
        if (isOpen) {

            GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            NkRect windowDims2 = NkRect.malloc(stack);
            Theme.resetEntireButtonStyle(ctx);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            nk_style_set_font(ctx, Theme.font_10);

            nk_rect(window.getWidth() / 2 - (menuWidth / 2), window.getHeight() / 2 - (menuHeight / 2), menuWidth, menuHeight, windowDims2);

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
                drawPlayerStuff();

                Theme.resetEntireButtonStyle(ctx);
            }
            nk_end(ctx);
        }
        if (nk_window_is_hidden(ctx, WINDOW_TITLE)) {
            isOpen = false;
        }
    }

    final ArrayList<Item> visibleEntries = new ArrayList<>();

    private void updateVisibleEntries() {
        String searchCriteria = searchBox.getValueAsString();
        if (searchCriteria.equals("") || searchCriteria.isBlank() || searchCriteria == null) {
            searchCriteria = null;
        } else searchCriteria = searchCriteria.toLowerCase();
        visibleEntries.clear();
        for (Item item : itemList) {
            if (isVisible(item) && matchesSearch(item, searchCriteria)) {
                visibleEntries.add(item);
            }
        }
    }

    private void inventoryGroup(MemoryStack stack) {
        nk_layout_row_dynamic(ctx, itemListHeight, 1);
        if (Nuklear.nk_group_begin(ctx, WINDOW_TITLE, Nuklear.NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR)) {
            int maxRows = (int) (Math.floor(itemListHeight / buttonWidth.width) - 1);
//            Nuklear.nk_slider_float(ctx, 0.0f, floatValue, 1.0f, 0.1f);
//            System.out.println(floatValue[0]);
//            scrollValue = (int) (Math.sin(System.currentTimeMillis()*0.01) * 10)+10;
//            clampScroll();
//            Nuklear.nk_group_set_scroll(ctx, WINDOW_TITLE, 0, scrollValue);


            updateVisibleEntries();
            scrollValue = MathUtils.clamp(scrollValue, 0, Math.max(0, (visibleEntries.size() / maxColumns) - 1));
            int itemID = scrollValue * maxColumns;
            int rows = 0;


            rows:
            while (rows < maxRows) {
                nk_layout_row_dynamic(ctx, buttonWidth.width, maxColumns);// row
                rows++;

                for (int column = 0; column < maxColumns; ) {
                    if (itemID >= visibleEntries.size()) {
                        break rows;
                    }

                    Item item = visibleEntries.get(itemID);
                    if (Nuklear.nk_widget_is_hovered(ctx)) {
                        hoveredItem = item.toString();
                    }
                    if (Nuklear.nk_button_image(ctx, item.getNKIcon())) {
                        gameInfo.playerStuff.freeplay_getItem(item);
                    }
                    buttonWidth.measure(ctx, stack);
                    column++;
                    itemID++;
                }
            }
        }
        Nuklear.nk_group_end(ctx);
    }

    protected void drawPlayerStuff() {
        nk_layout_row_dynamic(ctx, backpackMenuSize, 1);
        if (Nuklear.nk_group_begin(ctx, "My Items", Nuklear.NK_WINDOW_TITLE)) {
            nk_layout_row_dynamic(ctx, 20, 3);
            if (Nuklear.nk_button_label(ctx, "Organize")) {
                gameInfo.playerStuff.organize();
            } else if (Nuklear.nk_button_label(ctx, "Remove")) {
                gameInfo.playerStuff.set(hotbar.getSelectedItemIndex(), null);
            } else if (Nuklear.nk_button_label(ctx, "Clear")) {
                for (int i = 0; i < gameInfo.playerStuff.size(); i++) {
                    gameInfo.playerStuff.set(i, null);
                }
            }

            int itemID = 0;
            rows:
            while (true) {
                nk_layout_row_dynamic(ctx, buttonWidth.width, maxColumns);
                cols:
                for (int i = 0; i < maxColumns; i++) {
                    if (itemID >= gameInfo.playerStuff.size()) {
                        break rows;
                    }
                    ItemStack item = gameInfo.playerStuff.get(itemID);

                    if (itemID == hotbar.getSelectedItemIndex()) {
                        ctx.style().button().border_color().set(Theme.white);

                    } else {
                        ctx.style().button().border_color().set(Theme.blue);
                    }

                    if (item != null) {
                        if (Nuklear.nk_widget_is_hovered(ctx)) {
//                            hoveredItem = item.toString();
                        }
                        if (drawItemStack(ctx, item)) {
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

    public static boolean drawItemStack(NkContext ctx, ItemStack itemStack) {
        return Nuklear.nk_button_image(ctx, itemStack.item.getNKIcon());
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
        if (isOpen() && searchBox.isFocused()) {
            return true;
        } else if (action == GLFW.GLFW_RELEASE && key == KEY_OPEN_INVENTORY) {
            setOpen(!isOpen());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseButtonEvent(int button, int action, int mods) {
        return false;
    }

    @Override
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        scrollValue -= yoffset;
        return true;
    }

    private float buttonWidthPlusPadding() {
        NkVec2 padding = ctx.style().button().padding();
        return buttonWidth.width + (padding.y()) + 0.02f;
    }


}
