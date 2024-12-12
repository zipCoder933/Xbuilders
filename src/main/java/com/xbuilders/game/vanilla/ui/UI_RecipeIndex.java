/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.gameScene.GameMode;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.item.Item;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemIndex;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.WindowEvents;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class UI_RecipeIndex extends UI_ItemWindow implements WindowEvents {

    public static final int KEY_OPEN_RECIPE_INDEX = GLFW.GLFW_KEY_R;

    public UI_RecipeIndex(NkContext ctx, Item[] itemList, NKWindow window) {
        super(ctx, window, "Recipe List");

        allItems = new UI_ItemIndex(this);
        allItems.setItemList(itemList);
        allItems.itemClickEvent = (item) -> {

        };
        nk_begin(ctx, title, NkRect.create(), windowFlags);
        nk_end(ctx);
        setOpen(false);
    }


    final int Allitems_Height = 250; //total item list window size
    final int recipeView_Height = 250; //player inventory window size
    UI_ItemIndex allItems;


    public void onOpenEvent() {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) setOpen(false);
        menuDimensions.y = Allitems_Height + recipeView_Height;
    }

    public void onCloseEvent() {
    }

    @Override
    public void drawWindow(MemoryStack stack, NkRect windowDims2) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) {
            setOpen(false);
        }

        menuDimensions.y = Allitems_Height + recipeView_Height;
        nk_layout_row_dynamic(ctx, recipeView_Height, 1);
        if (nk_group_begin(ctx, "Recipe view", NK_WINDOW_NO_SCROLLBAR)) {
            nk_layout_row_dynamic(ctx, 20, 3);
            nk_button_text(ctx, "Crafting");
            nk_button_text(ctx, "Smelting");
        }
        nk_group_end(ctx);

        allItems.draw(ctx, stack, Allitems_Height);

        Theme.resetEntireButtonStyle(ctx);
    }


    @Override
    public void windowResizeEvent(int width, int height) {

    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) return false;

        if (allItems.keyEvent(key, scancode, action, mods)) return true;
        if (action == GLFW.GLFW_RELEASE && key == KEY_OPEN_RECIPE_INDEX) {
            setOpen(!isOpen());
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) return false;
        allItems.mouseScrollEvent(scroll, xoffset, yoffset);
        return true;
    }

}
