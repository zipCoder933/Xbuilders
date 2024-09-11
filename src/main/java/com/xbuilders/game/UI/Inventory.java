/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.UI;

import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.items.ItemType;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.WindowEvents;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.util.*;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class Inventory extends GameUIElement implements WindowEvents {

    public static final int KEY_OPEN_INVENTORY = GLFW.GLFW_KEY_E;

    /**
     * @param playerInfo the playerBackpack to set
     */
    public void setPlayerInfo(MyGame.GameInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public Inventory(NkContext ctx, Item[] itemList, NKWindow window, Hotbar hotbar) throws IOException {
        super(ctx, window);
        this.hotbar = hotbar;
        setItemList(itemList);
        buttonWidth = new WidgetWidthMeasurement(0);
        searchBox = new TextBox(25);
        searchBox.setOnSelectEvent(() -> {
            searchBox.setValueAsString("");
            scrollValue = 0;
        });
//        searchBox.setOnChangeEvent(() -> {
//            scrollValue = 0;
//        });

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
                if (o1.itemType.equals(o2.itemType)) { // Else if 2 items have the same type
                    if (o1.itemType == ItemType.BLOCK) {
                        Block b1 = (Block) o1;
                        Block b2 = (Block) o2;
                        if (b1.renderType == b2.renderType) {
                            // System.out.println("Same block: " + b1.type);
                            return 0;
                        } else return b1.renderType > b2.renderType ? 1 : -1;
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


    int scrollValue = 0;
    final int menuWidth = 700;
    final int menuHeight = 600;
    int itemListHeight = 300;
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

            GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            NkRect windowDims2 = NkRect.malloc(stack);
            Theme.resetEntireButtonStyle(ctx);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            nk_style_set_font(ctx, Theme.getFont_9());

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
                backpackGroup();

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
                        addItemToBackpack(item);
                    }
                    buttonWidth.measure(ctx, stack);
                    column++;
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
        if (searchCriteria == null || searchCriteria.isBlank() || item.name == null) return true;
        for (String tag : item.tags) {
            if (tag != null && tag.toLowerCase().contains(
                    searchCriteria.toLowerCase().trim())) {
                return true;
            }
        }
        return item.name.toLowerCase().contains(searchCriteria);
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

    @Override
    public void windowResizeEvent(int width, int height) {

    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (isOpen() && action == GLFW.GLFW_RELEASE && key == GLFW.GLFW_KEY_ESCAPE) {
            setOpen(false);
            return true;
        } else if (searchBox.isFocused()) return true;
        else if (action == GLFW.GLFW_RELEASE && key == KEY_OPEN_INVENTORY) {
            if (isOpen()) {
                if (canCloseWithKeyEvents()) setOpen(false);
            } else setOpen(true);
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
