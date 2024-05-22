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
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.game.Main;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.text.*;
import org.lwjgl.BufferUtils;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class MenuHome implements MenuPage {

    public MenuHome(NkContext ctx, NKWindow window, TopMenu menu) {
        this.ctx = ctx;
        this.window = window;
        this.menu = menu;
    }

    NkContext ctx;
    TopMenu menu;
    NKWindow window;
    final int boxWidth = 350;
    final int boxHeight = 400;
    final int titleHeight = 50;

    @Override
    public void layout(MemoryStack stack, NkRect rect, IntBuffer titleYEnd) {
        nk_style_set_font(ctx, menu.uires.font_12);
        nk_rect((window.getWidth() / 2) - (boxWidth / 2),
                titleYEnd.get(0),
                boxWidth, boxHeight, rect);

        if (nk_begin(ctx, "Home", rect, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
            nk_style_set_font(ctx, menu.uires.font_8);
            nk_layout_row_dynamic(ctx, 40, 1);
            nk_label(ctx, "Devmode: "+ Main.devMode, NK_TEXT_CENTERED);

            nk_style_set_font(ctx, menu.uires.font_12);

            nk_layout_row_static(ctx, 10, 1, 1);//Row static is just spacing
            nk_layout_row_dynamic(ctx, 40, 1); //Row dynamic affects the next components
            if (nk_button_label(ctx, "NEW WORLD")) {
                menu.setPage(Page.NEW_WORLD);
            }

            nk_layout_row_static(ctx, 10, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "LOAD WORLD")) {
                menu.setPage(Page.LOAD_WORLD);
            }

//            nk_layout_row_static(ctx, 10, 1, 1);
//            nk_layout_row_dynamic(ctx, 40, 1);
//            if (nk_button_label(ctx, "JOIN MULTIPLAYER")) {
//                menu.setPage(Page.JOIN_MULTIPLAYER);
//            }

            nk_layout_row_static(ctx, 40, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "SETTINGS")) {
                menu.setPage(Page.SETTINGS);
            }
        }
        nk_end(ctx);
    }

    @Override
    public void onOpen() {
    }

}
