/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.NKUtils;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

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

    Runnable confirmationCallback;
    boolean visible = false;
    int boxHeight = 400;
    int boxWidth = 500;
    final int maxCharactersPerLine = boxWidth / 12;
    NkRect windowDims;
    String title, body;
    long shownTime;

    private String maxCharsPerLine(String text, int maxCharactersPerLine) {
        String[] words = text.split("[\\s&&[^\\n]]+");
        String newText = "";
        int characterCount = 0;
        for (int i = 0; i < words.length; i++) {
            characterCount += words[i].length() + 1;
            if (words[i].endsWith("\n")) characterCount = 0;
            else if (characterCount > maxCharactersPerLine) {
                newText += "\n";
                characterCount = 0;
            }
            newText += words[i] + " ";
        }
        return newText;
    }

    public void message(String title, String body) {
        message(title, body, null);
    }

    public void message(String title, String body, Runnable confirmationCallback) {
        this.title = title;
        this.body = maxCharsPerLine(body, maxCharactersPerLine);
        shownTime = System.currentTimeMillis();
        this.confirmationCallback = confirmationCallback;
        visible = true;
    }

    private final String tag = "Popup_window";
    final int lineHeight = 10;

    public void draw(MemoryStack stack) {
        if (!visible) return;
        nk_style_set_font(ctx, uires.font_12);
        boxHeight = MathUtils.clamp(
                NKUtils.textHeight(body, lineHeight),
                110, 300) + 50;

        nk_rect((window.getWidth() / 2) - (boxWidth / 2), (window.getHeight() / 2) - (boxHeight / 2),
                boxWidth, boxHeight, windowDims);

        if (nk_begin_titled(ctx, tag, title, windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {

            //Detect if the window is in focus
            if (!nk_window_has_focus(ctx)) {
//                nk_window_set_focus(ctx, tag);
                if (canClose()) {
                    visible = false;
                }
            }


            nk_style_set_font(ctx, uires.font_9);
            nk_layout_row_dynamic(ctx, 5, 1);

            NKUtils.text(ctx, body, lineHeight, NK_TEXT_ALIGN_LEFT);

            nk_style_set_font(ctx, uires.font_12);
            nk_layout_row_static(ctx, 20, 1, 1);

            if (confirmationCallback != null) {
                nk_layout_row_dynamic(ctx, 40, 2);
                if (nk_button_label(ctx, "OK")) {
                    if (canClose()) {
                        confirmationCallback.run();
                        visible = false;
                    }
                }
                if (nk_button_label(ctx, "Cancel")) {
                    if (canClose()) {
                        visible = false;
                    }
                }
            } else {
                nk_layout_row_dynamic(ctx, 40, 1);
                if (nk_button_label(ctx, "OK")) {
                    if (canClose()) {
                        visible = false;
                    }
                }
            }
        }
        nk_end(ctx);
    }

    private boolean canClose() {
        return System.currentTimeMillis() - shownTime > 500;
    }
}
