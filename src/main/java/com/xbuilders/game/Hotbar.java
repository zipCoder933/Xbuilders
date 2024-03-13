/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game;

import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.items.Item;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_CENTERED;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_BORDER;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_NO_INPUT;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_NO_SCROLLBAR;
import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_button_image;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_rect;
import static org.lwjgl.nuklear.Nuklear.nk_style_set_font;
import static org.lwjgl.nuklear.Nuklear.nk_text;

import org.lwjgl.system.MemoryStack;

/**
 *
 * @author zipCoder933
 */
public class Hotbar extends GameUIElement {

    /**
     * @param playerBackpack the playerBackpack to set
     */
    public void setPlayerBackpack(Item[] playerBackpack) {
        this.playerBackpack = playerBackpack;
    }

    public Hotbar(NkContext ctx, NKWindow window, UIResources uires) {
        super(ctx, window, uires);
        buttonHeight = new WidgetWidthMeasurement(0);
    }

    int menuWidth = 650;
    int menuHeight = 65;
    final int ELEMENTS = 11;
    private Item[] playerBackpack;
    WidgetWidthMeasurement buttonHeight;
    int selectedItemIndex;
    int pushValue;

    @Override
    public void draw(MemoryStack stack) {
        NkRect windowDims2 = NkRect.malloc(stack);

        ctx.style().window().fixed_background().data().color().set(Theme.transparent);
        ctx.style().button().normal().data().color().set(Theme.transparent);
        ctx.style().window().border_color().set(Theme.transparent);
        ctx.style().button().padding().set(0, 0);
        nk_style_set_font(ctx, uires.font_8);

//<editor-fold defaultstate="collapsed" desc="Draw title text">
        nk_rect(
                window.getWidth() / 2 - (menuWidth / 2),
                window.getHeight() - menuHeight - 20 - 20,
                menuWidth, 20, windowDims2);
        ctx.style().window().padding().set(0, 0);
        if (nk_begin(ctx, "hotbarA", windowDims2, NK_WINDOW_NO_INPUT | NK_WINDOW_NO_SCROLLBAR)) {
            nk_layout_row_dynamic(ctx, 40, 1);
            if (playerBackpack[selectedItemIndex] != null) {
                nk_text(ctx, playerBackpack[selectedItemIndex].name, NK_TEXT_ALIGN_CENTERED);
            }
        }
        nk_end(ctx);
//</editor-fold>

        nk_rect(
                window.getWidth() / 2 - (menuWidth / 2),
                window.getHeight() - menuHeight - 20,
                menuWidth, menuHeight + 2, windowDims2);
        ctx.style().window().fixed_background().data().color().set(Theme.backgroundColor);
        ctx.style().window().padding().set(4, 4);
        if (nk_begin(ctx, "HotbarB", windowDims2, NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {
            nk_layout_row_dynamic(ctx, buttonHeight.width, ELEMENTS);

            int i = 0;
            for (int j = 0; j < ELEMENTS; j++) {
                i = j + pushValue;

                if (i >= playerBackpack.length) {
                    break;
                }
                Item item = playerBackpack[i];

                if (buttonHeight.isCalibrated()) {
                    if (i == selectedItemIndex) {
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

    protected void changeSelectedIndex(float increment) {
        selectedItemIndex += increment;
        selectedItemIndex = MathUtils.clamp(selectedItemIndex, 0, playerBackpack.length - 1);

        if (selectedItemIndex >= ELEMENTS + pushValue) {
            pushValue++;
        } else if (selectedItemIndex < pushValue) {
            pushValue--;
        }
        pushValue = MathUtils.clamp(pushValue, 0, playerBackpack.length - ELEMENTS);
    }

    void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        changeSelectedIndex(scroll.y());
    }

    void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_COMMA) {
                changeSelectedIndex(-1);
            } else if (key == GLFW.GLFW_KEY_PERIOD) {
                changeSelectedIndex(1);
            }
        }
    }

}
