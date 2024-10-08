/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.nuklear.NKFontUtils;
import org.joml.Vector2i;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Theme should be all static since it is just styling preferences, and because
 * we only need 1 theme per game.
 *
 * @author zipCoder933
 */
public class Theme {
    //<editor-fold desc="Nuklear Crash Fix, DO NOT REMOVE!" defaultstate="open">
    /**
     * crash from nuklear: https://github.com/LWJGL/lwjgl3/issues/986
     * "You need to ensure that the ByteBuffer passed to stbtt_InitFont is not garbage collected (or not freed when
     * using explicit memory management APIs) while the font is in active use. This is a common mistake when dealing
     * with stb_truetype."
     */
    public static ByteBuffer fontBuffer;
    //</editor-fold>


    public static NkColor transparent, darkTransparent,
            backgroundColor, buttonColor, buttonHover,
            gray, lightGray, blue, darkBlue, white, black;

    public static NkUserFont font_24, font_22, font_20, font_18, font_16, font_14, font_12, font_10, font_9, font_8;


    public static void initialize(NkContext context) throws IOException {
        fontBuffer = NKFontUtils.loadFontData(ResourceUtils.RESOURCE_DIR + "\\fonts\\Press_Start_2P\\PressStart2P-Regular.ttf");
        initFonts(context);
        nk_style_set_font(context, font_10);

        try (MemoryStack stack = stackPush()) {
            transparent = createColor(0, 0, 0, 0);
            darkTransparent = createColor(0, 0, 0, 70);
            backgroundColor = createColor(40, 40, 40, 255);

            buttonColor = createColor(20, 20, 20, 255);
            buttonHover = createColor(50, 50, 50, 255);

            gray = createColor(20, 20, 20, 255);
            lightGray = createColor(120, 120, 120, 255);
            blue = createColor(80, 80, 255, 255);
            darkBlue = createColor(40, 40, 230, 255);
            white = createColor(255, 255, 255, 255);
            black = createColor(0, 0, 0, 255);

            // This buffer acts like an array of NkColor structs
            int size = NkColor.SIZEOF * NK_COLOR_COUNT; // How much memory we need to store all the color data
            ByteBuffer buffer = stack.calloc(size);
            NkColor.Buffer colors = new NkColor.Buffer(buffer);
            colors.put(NK_COLOR_TEXT, white);
            colors.put(NK_COLOR_WINDOW, backgroundColor);
            colors.put(NK_COLOR_HEADER, black);
            colors.put(NK_COLOR_BORDER, backgroundColor);

            colors.put(NK_COLOR_BUTTON, buttonColor);
            colors.put(NK_COLOR_BUTTON_HOVER, buttonHover);
            colors.put(NK_COLOR_BUTTON_ACTIVE, blue);

            colors.put(NK_COLOR_TOGGLE, white);
            colors.put(NK_COLOR_TOGGLE_HOVER, blue);
            colors.put(NK_COLOR_TOGGLE_CURSOR, gray);
            colors.put(NK_COLOR_SELECT, gray);
            colors.put(NK_COLOR_SELECT_ACTIVE, white);
            colors.put(NK_COLOR_SLIDER, gray);
            colors.put(NK_COLOR_SLIDER_CURSOR, blue);
            colors.put(NK_COLOR_SLIDER_CURSOR_HOVER, blue);
            colors.put(NK_COLOR_SLIDER_CURSOR_ACTIVE, blue);
            colors.put(NK_COLOR_PROPERTY, gray);
            colors.put(NK_COLOR_EDIT, gray);
            colors.put(NK_COLOR_EDIT_CURSOR, black);
            colors.put(NK_COLOR_COMBO, gray);
            colors.put(NK_COLOR_CHART, gray);
            colors.put(NK_COLOR_CHART_COLOR, gray);
            colors.put(NK_COLOR_CHART_COLOR_HIGHLIGHT, blue);
            colors.put(NK_COLOR_SCROLLBAR, gray);
            colors.put(NK_COLOR_SCROLLBAR_CURSOR, gray);
            colors.put(NK_COLOR_SCROLLBAR_CURSOR_HOVER, gray);
            colors.put(NK_COLOR_SCROLLBAR_CURSOR_ACTIVE, gray);
            colors.put(NK_COLOR_TAB_HEADER, gray);
            nk_style_from_table(context, colors);

            //Set general styles
            context.style().window().rounding(5);
            resetEntireButtonStyle(context);
            resetWindowPadding(context);
        }
    }

    private static void initFonts(NkContext context) {
        //Lets keep this font sizing. If we had a small enough screen size, we would still want small fonts
        //Minecraft has a UI scale setting
        font_24 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 24);
        font_22 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 22);
        font_20 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 20);
        font_18 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 18);
        font_16 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 16);
        font_14 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 14);
        font_12 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 12);
        font_10 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 10);
        font_9 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 9);
        font_8 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, 8);
    }

    public static void resetWindowPadding(NkContext context) {
        context.style().window().padding().set(10, 10);
    }

    public static void resetWindowColor(NkContext context) {
        context.style().window().fixed_background().data().color().set(Theme.backgroundColor);
        context.style().window().border_color().set(Theme.blue);
    }

    public static void resetTextColor(NkContext context) {
        context.style().text().color().set(Theme.white);
    }

    public static void resetEntireButtonStyle(NkContext context) {
        context.style().button().padding().set(4, 4);
        context.style().button().border(2);
        context.style().button().normal().data().color().set(buttonColor);
        context.style().button().border_color().set(blue);
        context.style().button().text_alignment(Nuklear.NK_TEXT_ALIGN_CENTERED | Nuklear.NK_TEXT_ALIGN_MIDDLE);
    }

//    public static void buttonDisableStyle(NkContext context) {
//        context.style().button().text_normal().set(Theme.gray);
//        context.style().button().hover().data().set()
//        context.style().button().normal().data().color().set(buttonColor);
//        context.style().button().border_color().set(gray);
//        context.style().button().text_alignment(Nuklear.NK_TEXT_ALIGN_CENTERED | Nuklear.NK_TEXT_ALIGN_MIDDLE);
//    }

    public static NkColor createColor(MemoryStack stack, int r, int g, int b, int a) {
        return NkColor.mallocStack(stack).set((byte) r, (byte) g, (byte) b, (byte) a);
    }

    public static NkColor createColor(int r, int g, int b, int a) {
        return NkColor.create().set((byte) r, (byte) g, (byte) b, (byte) a);
    }


    /**
     * Font  and UI scaling
     * ===============================================================================================================
     */
    private static float scale = 1;

    public static void setScale(float scale) {
        Theme.scale = scale;
    }

    public static float getScale() {
        return scale;
    }

    public static void applyWindowScale(Vector2i windowSize, final int defaultWidth, final int defaultHeight) {
        windowSize.x = (int) (defaultWidth * scale);
        windowSize.y = (int) (defaultHeight * scale);
    }
}
