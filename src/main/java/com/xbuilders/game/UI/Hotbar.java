/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.UI;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.player.CursorRay;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.game.MyGame;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;
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
public class Hotbar extends GameUIElement {

    /**
     * @param playerInfo the playerBackpack to set
     */
    public void setPlayerInfo(MyGame.GameInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public Hotbar(NkContext ctx, NKWindow window) {
        super(ctx, window);
        buttonHeight = new WidgetWidthMeasurement(0);
    }

    int menuWidth = 650;
    int menuHeight = 65 + 20;
    final int ELEMENTS = 11;
    private MyGame.GameInfo playerInfo;
    WidgetWidthMeasurement buttonHeight;
    private int selectedItemIndex;
    int pushValue;

    @Override
    public void draw(MemoryStack stack) {
        NkRect windowDims2 = NkRect.malloc(stack);

        ctx.style().window().fixed_background().data().color().set(Theme.transparent);
        ctx.style().button().normal().data().color().set(Theme.transparent);
        ctx.style().window().border_color().set(Theme.transparent);
        ctx.style().button().padding().set(0, 0);
        nk_style_set_font(ctx, Theme.font_9);
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
            if (playerInfo.playerBackpack[getSelectedItemIndex()] != null) {
                nk_text(ctx, playerInfo.playerBackpack[getSelectedItemIndex()].name, NK_TEXT_ALIGN_CENTERED);
            }

            nk_layout_row_dynamic(ctx, buttonHeight.width, ELEMENTS);
            int i = 0;
            for (int j = 0; j < ELEMENTS; j++) {
                i = j + pushValue;

                if (i >= playerInfo.playerBackpack.length) {
                    break;
                }
                Item item = playerInfo.playerBackpack[i];

                if (buttonHeight.isCalibrated()) {
                    if (i == getSelectedItemIndex()) {
                        ctx.style().button().border_color().set(Theme.white);
                    } else {
                        ctx.style().button().border_color().set(Theme.blue);
                    }
                }
                if (item != null) {
                    nk_button_image(ctx, item.getNKIcon());
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
        selectedItemIndex += increment;
        selectedItemIndex = (MathUtils.clamp(getSelectedItemIndex(), 0, playerInfo.playerBackpack.length - 1));

        if (getSelectedItemIndex() >= ELEMENTS + pushValue) {
            pushValue++;
        } else if (getSelectedItemIndex() < pushValue) {
            pushValue--;
        }
        pushValue = MathUtils.clamp(pushValue, 0, playerInfo.playerBackpack.length - ELEMENTS);
    }

    public void setSelectedIndex(int index) {
        selectedItemIndex = MathUtils.clamp(index, 0, playerInfo.playerBackpack.length - 1);
        pushValue = MathUtils.clamp(selectedItemIndex, 0, playerInfo.playerBackpack.length - ELEMENTS);
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        changeSelectedIndex(scroll.y());
    }

    final int PICK_KEY = GLFW.GLFW_KEY_0;

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_COMMA) {
                changeSelectedIndex(-1);
            } else if (key == GLFW.GLFW_KEY_PERIOD) {
                changeSelectedIndex(1);
            } else if (key == PICK_KEY) {
                pickItem();
            }
        }
        return false;
    }

    public boolean mouseButtonEvent(int button, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE && button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            pickItem();
            return true;
        }
        return false;
    }

    private void pickItem() {
        CursorRay ray = GameScene.player.camera.cursorRay;
        if (ray.hitTarget()) {
            if (ray.getEntity() != null) {
                acquireItem(ray.getEntity().link);
            } else acquireItem(GameScene.world.getBlock(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z));
        }
    }

    public Item getSelectedItem(){
        return playerInfo.playerBackpack[getSelectedItemIndex()];
    }

    private void acquireItem(Item item) {
        if (item.name.toLowerCase().contains("hidden") || item.getTags().contains("hidden"))
            return;
        // First check if the player already has the item
        for (int i = 0; i < playerInfo.playerBackpack.length; i++) {
            if (playerInfo.playerBackpack[i] != null && playerInfo.playerBackpack[i].equals(item)) {
                setSelectedIndex(i);
                return;
            }
        }
        // otherwise add it
        for (int i = 0; i < playerInfo.playerBackpack.length; i++) {
            if (playerInfo.playerBackpack[i] == null) {
                playerInfo.playerBackpack[i] = item;
                setSelectedIndex(i);
                return;
            }
        }
        // If there is no room, then remove the first item
        playerInfo.playerBackpack[0] = item;
        setSelectedIndex(0);
    }

}
