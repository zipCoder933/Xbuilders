/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui;

import java.nio.ByteBuffer;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.Nuklear;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_BORDER;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_BUTTON;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_BUTTON_ACTIVE;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_BUTTON_HOVER;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_CHART;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_CHART_COLOR;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_CHART_COLOR_HIGHLIGHT;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_COMBO;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_COUNT;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_EDIT;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_EDIT_CURSOR;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_HEADER;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_PROPERTY;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SCROLLBAR;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SCROLLBAR_CURSOR;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SCROLLBAR_CURSOR_ACTIVE;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SCROLLBAR_CURSOR_HOVER;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SELECT;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SELECT_ACTIVE;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SLIDER;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SLIDER_CURSOR;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SLIDER_CURSOR_ACTIVE;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_SLIDER_CURSOR_HOVER;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_TAB_HEADER;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_TEXT;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_TOGGLE;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_TOGGLE_CURSOR;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_TOGGLE_HOVER;
import static org.lwjgl.nuklear.Nuklear.NK_COLOR_WINDOW;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_ALIGN_LEFT;
import static org.lwjgl.nuklear.Nuklear.nk_style_from_table;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Theme should be all static since it is just styling preferences, and because
 * we only need 1 theme per game.
 *
 * @author zipCoder933
 */
public class Theme {

    public static NkColor transparent, backgroundColor, buttonColor, buttonHover,
            gray, blue, darkBlue, white, black;

    public static void initialize(NkContext context) {
        try (MemoryStack stack = stackPush()) {
            transparent = createColor(0, 0, 0, 0);
            backgroundColor = createColor(40, 40, 40, 255);

            buttonColor = createColor(20, 20, 20, 255);
            buttonHover = createColor(50, 50, 50, 255);

            gray = createColor(20, 20, 20, 255);
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

            colors.put(NK_COLOR_TOGGLE, gray);
            colors.put(NK_COLOR_TOGGLE_HOVER, gray);
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

    public static void resetWindowPadding(NkContext context) {
        context.style().window().padding().set(10, 10);
    }

    public static void resetWindowColor(NkContext context) {
        context.style().window().fixed_background().data().color().set(Theme.backgroundColor);
        context.style().window().border_color().set(Theme.blue);
    }

    public static void resetEntireButtonStyle(NkContext context) {
        context.style().button().padding().set(4, 4);
        context.style().button().border(2);
        context.style().button().normal().data().color().set(buttonColor);
        context.style().button().border_color().set(blue);
        context.style().button().text_alignment(Nuklear.NK_TEXT_ALIGN_CENTERED | Nuklear.NK_TEXT_ALIGN_MIDDLE);
    }

    public static NkColor createColor(MemoryStack stack, int r, int g, int b, int a) {
        return NkColor.mallocStack(stack).set((byte) r, (byte) g, (byte) b, (byte) a);
    }

    public static NkColor createColor(int r, int g, int b, int a) {
        return NkColor.create().set((byte) r, (byte) g, (byte) b, (byte) a);
    }
}
