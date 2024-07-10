/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.ui.Page;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import com.xbuilders.game.Main;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.NKUtils;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MathUtil;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class PopupMessage {

    public PopupMessage(NkContext ctx, NKWindow window, UIResources uires) {
        this.ctx = ctx;
        this.uires = uires;
        windowDims = NkRect.create();
        this.window = window;
        windowDims.set(0, 0, boxWidth, boxHeight);
//        show("Title", "Body");
    }

    NkContext ctx;
    UIResources uires;
    NKWindow window;


    boolean visible = false;
    int boxHeight = 400;
    int boxWidth = 500;
    NkRect windowDims;
    String title, body;
    long shownTime;

    public void show(String title, String body) {
        this.title = title;
        this.body = body;
        shownTime = System.currentTimeMillis();
        visible = true;
    }

    private final String tag = "Popup_window";

    public void draw(MemoryStack stack) {
        if (!visible) return;
        nk_style_set_font(ctx, uires.font_12);


        nk_rect((window.getWidth() / 2) - (boxWidth / 2), (window.getHeight() / 2) - (boxHeight / 2),
                boxWidth, boxHeight, windowDims);

        if (nk_begin_titled(ctx, tag, title, windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {

            //Detect if the window is in focus
            if (!nk_window_has_focus(ctx)) {
//                nk_window_set_focus(ctx, tag);
                if (System.currentTimeMillis() - shownTime > 500) {
                    visible = false;
                }
            }
            nk_style_set_font(ctx, uires.font_8);
            nk_layout_row_dynamic(ctx, 5, 1);

            int height = NKUtils.text(ctx, body, 10, NK_TEXT_ALIGN_LEFT);
            boxHeight = MathUtils.clamp(height, 110, 300) + 50;

            nk_style_set_font(ctx, uires.font_12);
            nk_layout_row_static(ctx, 20, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "OK")) {
                if (System.currentTimeMillis() - shownTime > 500) {
                    visible = false;
                }
            }
        }
        nk_end(ctx);
    }
}
