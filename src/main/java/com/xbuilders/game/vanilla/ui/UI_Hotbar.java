/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.vanilla.ui;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.items.item.StorageSpace;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.UI_GameMenu;
import com.xbuilders.engine.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.NKWindow;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class UI_Hotbar extends UI_GameMenu {


    public UI_Hotbar(NkContext ctx, NKWindow window) {
        super(ctx, window);
    }

    int menuWidth = 650;
    int menuHeight = 65 + 20;
    final int ELEMENTS = 11;
    private int selectedItemIndex;
    int pushValue;
    static final StorageSpace playerStorage = GameScene.player.inventory;

    @Override
    public void draw(MemoryStack stack) {
        NkRect windowDims2 = NkRect.malloc(stack);

        ctx.style().window().fixed_background().data().color().set(Theme.transparent);
        ctx.style().button().normal().data().color().set(Theme.transparent);
        ctx.style().window().border_color().set(Theme.transparent);
        ctx.style().button().padding().set(0, 0);
        nk_style_set_font(ctx, Theme.font_10);

        nk_rect(
                window.getWidth() / 2 - (menuWidth / 2),
                window.getHeight() - menuHeight - 20,
                menuWidth, menuHeight + 2, windowDims2);
        ctx.style().window().fixed_background().data().color().set(Theme.backgroundColor);
        ctx.style().window().padding().set(4, 4);
        if (nk_begin(ctx, "HotbarB", windowDims2, NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {
            playerStorage.deleteEmptyItems();
            // Draw the name of the item
            nk_layout_row_dynamic(ctx, 20, 1);
            if (playerStorage.get(getSelectedItemIndex()) != null) {
                nk_text(ctx, playerStorage.get(getSelectedItemIndex()).item.name, NK_TEXT_ALIGN_CENTERED);
            }

            nk_layout_row_dynamic(ctx, UI_ItemWindow.getItemSize(), ELEMENTS);
            int i = 0;
            for (int j = 0; j < ELEMENTS; j++) {
                i = j + pushValue;

                if (i >= playerStorage.size()) {
                    break;
                }
                ItemStack item = playerStorage.get(i);

                //if (buttonHeight.isCalibrated()) {
                if (i == getSelectedItemIndex()) {
                    ctx.style().button().border_color().set(Theme.white);
                } else {
                    ctx.style().button().border_color().set(Theme.blue);
                }
                //}
                if (item != null) {
                    UI_ItemWindow.drawItemStackButton(stack, ctx, item);
                } else {
                    Nuklear.nk_button_text(ctx, "");
                }
                //buttonHeight.measure(ctx, stack);
            }
        }
        nk_end(ctx);
        Theme.resetEntireButtonStyle(ctx);
        Theme.resetWindowPadding(ctx);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    protected void changeSelectedIndex(float increment) {
        selectedItemIndex += increment;
        selectedItemIndex = (MathUtils.clamp(getSelectedItemIndex(), 0, playerStorage.size() - 1));

        if (getSelectedItemIndex() >= ELEMENTS + pushValue) {
            pushValue++;
        } else if (getSelectedItemIndex() < pushValue) {
            pushValue--;
        }
        pushValue = MathUtils.clamp(pushValue, 0, playerStorage.size() - ELEMENTS);
    }

    public void setSelectedIndex(int index) {
        selectedItemIndex = MathUtils.clamp(index, 0, playerStorage.size() - 1);
        pushValue = selectedItemIndex - ELEMENTS / 2;
        pushValue = MathUtils.clamp(pushValue, 0, playerStorage.size() - ELEMENTS);
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        changeSelectedIndex(-scroll.y());
    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_COMMA) {
                changeSelectedIndex(-1);
            } else if (key == GLFW.GLFW_KEY_PERIOD) {
                changeSelectedIndex(1);
            } else if (key == GLFW.GLFW_KEY_Q) {
                GameScene.player.dropItem(getSelectedItem());
                playerStorage.set(getSelectedItemIndex(), null);
            }
        }
        return false;
    }

    public void pickItem(CursorRay ray, boolean allowAcquire) {
        ItemStack stack = null;
        StorageSpace storageSpace = playerStorage;
        if (ray.hitTarget()) {
            if (ray.getEntity() != null) {
                Entity entity = ray.getEntity();
                stack = new ItemStack(Registrys.getItem(entity), 1);
            } else {
                Block block = GameScene.world.getBlock(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z);
                stack = new ItemStack(Registrys.getItem(block), 1);
            }
        }
        for (int i = 0; i < storageSpace.size(); i++) {
            ItemStack item = storageSpace.get(i);
            if (item != null && item.item == stack.item) {
                setSelectedIndex(i);
                return;
            }
        }
        if (allowAcquire) setSelectedIndex(storageSpace.acquireItem(stack));
    }

    public ItemStack getSelectedItem() {
        return playerStorage.get(getSelectedItemIndex());
    }


}