/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.ui.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.client.visuals.ui.Theme;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.nuklear.NKUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class PopupMessage {

    public PopupMessage(NkContext ctx, MainWindow window) {
        this.ctx = ctx;
        windowDims = NkRect.create();
        this.window = window;
        windowDims.set(0, 0, boxWidth, boxHeight);
        //message("Title", "Body");
    }

    NkContext ctx;
    MainWindow window;

    Runnable confirmationCallback, closeCallback;
    boolean visible = false;
    float boxHeight = 400;
    float boxWidth = 500;

    NkRect windowDims;
    String title, body;
    long shownTime;
    boolean buttonsHovered = false;
    boolean buttonCanClose = false;
    boolean unfocusCanClose = false;

    public boolean isShown() {
        return visible;
    }

//    private String maxCharsPerLine(String text, int maxCharactersPerLine) {
//        String[] words = text.split("[\\s&&[^\\n]]+");
//        String newText = "";
//        int characterCount = 0;
//        for (int i = 0; i < words.length; i++) {
//            characterCount += words[i].length() + 1;
//            if (words[i].endsWith("\n")) characterCount = 0;
//            else if (characterCount > maxCharactersPerLine) {
//                newText += "\n";
//                characterCount = 0;
//            }
//            newText += words[i] + " ";
//        }
//        return newText;
//    }

    public void message(String title, String body) {
        this.title = title;
        this.body = body;
        shownTime = System.currentTimeMillis();
        this.confirmationCallback = null;
        this.closeCallback = null;
        visible = true;
    }

    public void message(String title, String body, Runnable closeCallback) {
        this.title = title;
        this.body = body;
        shownTime = System.currentTimeMillis();
        this.confirmationCallback = null;
        this.closeCallback = closeCallback;
        visible = true;
    }

    public void confirmation(String title, String body, Runnable confirmationCallback) {
        this.title = title;
        this.body = body;
        shownTime = System.currentTimeMillis();
        this.confirmationCallback = confirmationCallback;
        this.closeCallback = null;
        visible = true;
    }

    public void confirmation(String title, String body, Runnable confirmationCallback, Runnable closeCallback) {
        this.title = title;
        this.body = body;
        shownTime = System.currentTimeMillis();
        this.confirmationCallback = confirmationCallback;
        this.closeCallback = closeCallback;
        visible = true;
    }

    private final static String WINDOW_ID = "Popup_window";


    private void closeWindow() {
        if (closeCallback != null) {
            closeCallback.run();
        }
        closeCallback = null;
        confirmationCallback = null;
        visible = false;
    }


    public void draw() {
        buttonsHovered = false;
        if (!visible) {
            return;
        }
        float wrapWidth = boxWidth - 20;
        boxHeight = (int) (NKUtils.calculateWrappedTextHeight(Theme.font_10, body, wrapWidth) + 180);
        boxHeight = MathUtils.clamp(boxHeight, 160, 400);


        nk_style_set_font(ctx, Theme.font_12);
        nk_rect((window.getWidth() / 2) - (boxWidth / 2), (window.getHeight() / 2) - (boxHeight / 2), boxWidth, boxHeight, windowDims);
        if (nk_begin_titled(ctx, WINDOW_ID, title, windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {

            //Detect if the window is in focus
            if (!nk_window_has_focus(ctx)) {
                if (unfocusCanClose) {
                    closeWindow();
                } else nk_window_set_focus(ctx, WINDOW_ID);
            }


            nk_style_set_font(ctx, Theme.font_10);
            nk_layout_row_dynamic(ctx, 5, 1);

            NKUtils.wrapText(ctx, body, wrapWidth);

            nk_style_set_font(ctx, Theme.font_12);
            nk_layout_row_static(ctx, 20, 1, 1);

            if (confirmationCallback != null) {
                nk_layout_row_dynamic(ctx, 40, 2);

                if (nk_widget_is_hovered(ctx)) {
                    buttonsHovered = true;
                    //nk_tooltip(ctx, "This is a tooltip");
                }

                if (nk_button_label(ctx, "OK")) {
                    System.out.println("Closing popup...");
                    if (buttonCanClose) {
                        confirmationCallback.run();
                        confirmationCallback = null;
                        closeCallback = null;
                        visible = false;
                    }
                }

                if (nk_widget_is_hovered(ctx)) {
                    buttonsHovered = true;
                    //nk_tooltip(ctx, "This is a tooltip");
                }
                if (nk_button_label(ctx, "Cancel")) {
                    System.out.println("Closing popup...");
                    if (buttonCanClose) {
                        closeWindow();
                    }
                }
            } else {
                nk_layout_row_dynamic(ctx, 40, 1);

                if (nk_widget_is_hovered(ctx)) {
                    buttonsHovered = true;
                    //nk_tooltip(ctx, "This is a tooltip");
                }
                if (nk_button_label(ctx, "OK")) {
                    System.out.println("Closing popup...");
                    if (buttonCanClose) {
                        closeWindow();
                    }
                }
            }
        }
        nk_end(ctx);

        if (buttonsHovered) {
            //System.out.println("Hovered");
        }
        //Sometimes if the window is unfocused, it doesnt register the button press
        if (buttonCanClose && buttonsHovered && window.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
            closeWindow();
        }

        int timeSinceShown = (int) (System.currentTimeMillis() - shownTime);
        buttonCanClose = timeSinceShown > 500;
        unfocusCanClose = timeSinceShown > 1000;
    }


}
