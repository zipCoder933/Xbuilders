/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.UI;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Registrys;
import com.xbuilders.engine.items.block.Block;
import com.xbuilders.engine.items.entity.Entity;
import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.world.data.PlayerData;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.data.PlayerStuff;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class Hotbar extends GameUIElement {


    public Hotbar(NkContext ctx, NKWindow window) {
        super(ctx, window);
        buttonHeight = new WidgetWidthMeasurement(0);
    }

    int menuWidth = 650;
    int menuHeight = 65 + 20;
    final int ELEMENTS = 11;
    WidgetWidthMeasurement buttonHeight;
    private int selectedItemIndex;
    int pushValue;

    @Override
    public void draw(MemoryStack stack) {
        PlayerData playerInfo = GameScene.world.data.data.playerData;
        NkRect windowDims2 = NkRect.malloc(stack);

        ctx.style().window().fixed_background().data().color().set(Theme.transparent);
        ctx.style().button().normal().data().color().set(Theme.transparent);
        ctx.style().window().border_color().set(Theme.transparent);
        ctx.style().button().padding().set(0, 0);
        nk_style_set_font(ctx, Theme.font_10);
//
//        // <editor-fold defaultstate="collapsed" desc="Draw title text">
//        nk_rect(
//                window.getWidth() / 2 - (menuWidth / 2),
//                window.getHeight() - menuHeight - 20 - 20,
//                menuWidth, 20, windowDims2);
//        ctx.style().window().padding().set(0, 0);
//        if (nk_begin(ctx, "hotbarA", windowDims2, NK_WINDOW_NO_INPUT | NK_WINDOW_NO_SCROLLBAR)) {
//            nk_layout_row_dynamic(ctx, 40, 1);
//            if (playerInfo.playerBackpack[getSelectedItemIndex()] != null) {
//                nk_text(ctx, playerInfo.playerBackpack[getSelectedItemIndex()].name, NK_TEXT_ALIGN_CENTERED);
//            }
//        }
//        nk_end(ctx);
//        // </editor-fold>

        nk_rect(
                window.getWidth() / 2 - (menuWidth / 2),
                window.getHeight() - menuHeight - 20,
                menuWidth, menuHeight + 2, windowDims2);
        ctx.style().window().fixed_background().data().color().set(Theme.backgroundColor);
        ctx.style().window().padding().set(4, 4);
        if (nk_begin(ctx, "HotbarB", windowDims2, NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {
            // Draw the name of the item
            nk_layout_row_dynamic(ctx, 20, 1);
            if (playerInfo.playerStuff.get(getSelectedItemIndex()) != null) {
                nk_text(ctx, playerInfo.playerStuff.get(getSelectedItemIndex()).item.name, NK_TEXT_ALIGN_CENTERED);
            }

            nk_layout_row_dynamic(ctx, buttonHeight.width, ELEMENTS);
            int i = 0;
            for (int j = 0; j < ELEMENTS; j++) {
                i = j + pushValue;

                if (i >= playerInfo.playerStuff.size()) {
                    break;
                }
                ItemStack item = playerInfo.playerStuff.get(i);

                if (buttonHeight.isCalibrated()) {
                    if (i == getSelectedItemIndex()) {
                        ctx.style().button().border_color().set(Theme.white);
                    } else {
                        ctx.style().button().border_color().set(Theme.blue);
                    }
                }
                if (item != null) {
                    Inventory.drawItemStack(ctx, item);
                } else {
                    Nuklear.nk_button_text(ctx, "");
                }
                buttonHeight.measure(ctx, stack);
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
        PlayerData playerInfo = GameScene.world.data.data.playerData;
        selectedItemIndex += increment;
        selectedItemIndex = (MathUtils.clamp(getSelectedItemIndex(), 0, playerInfo.playerStuff.size() - 1));

        if (getSelectedItemIndex() >= ELEMENTS + pushValue) {
            pushValue++;
        } else if (getSelectedItemIndex() < pushValue) {
            pushValue--;
        }
        pushValue = MathUtils.clamp(pushValue, 0, playerInfo.playerStuff.size() - ELEMENTS);
    }

    public void setSelectedIndex(int index) {
        PlayerData playerInfo = GameScene.world.data.data.playerData;
        selectedItemIndex = MathUtils.clamp(index, 0, playerInfo.playerStuff.size() - 1);
        pushValue = MathUtils.clamp(selectedItemIndex, 0, playerInfo.playerStuff.size() - ELEMENTS);
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        changeSelectedIndex(scroll.y());
    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_COMMA) {
                changeSelectedIndex(-1);
            } else if (key == GLFW.GLFW_KEY_PERIOD) {
                changeSelectedIndex(1);
            }
        }
        return false;
    }

    public void pickItem(CursorRay ray) {
        if (ray.hitTarget()) {
            if (ray.getEntity() != null) {
                Entity entity = ray.getEntity();
                acquireItem(new ItemStack(Registrys.getItem(entity)));
            } else {
                Block block = GameScene.world.getBlock(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z);
                acquireItem(new ItemStack(Registrys.getItem(block)));
            }
        }
    }

    public ItemStack getSelectedItem() {
        PlayerData playerInfo = GameScene.world.data.data.playerData;
        return playerInfo.playerStuff.get(getSelectedItemIndex());
    }

    private void acquireItem(ItemStack item) {
        PlayerData playerInfo = GameScene.world.data.data.playerData;
        for (int i = 0; i < playerInfo.playerStuff.size(); i++) {
            if (playerInfo.playerStuff.get(i) != null && playerInfo.playerStuff.get(i).equals(item)) {
                setSelectedIndex(i);
                return;
            }
        }
        // otherwise add it
        for (int i = 0; i < playerInfo.playerStuff.size(); i++) {
            if (playerInfo.playerStuff.get(i) == null) {
                playerInfo.playerStuff.set(i, item);
                setSelectedIndex(i);
                return;
            }
        }
        // If there is no room, then remove the first item
        playerInfo.playerStuff.set(0, item);
        setSelectedIndex(0);
    }

}
