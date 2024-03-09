/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import com.xbuilders.engine.ui.Page;
import com.xbuilders.game.Main;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.NKUtils;
import com.xbuilders.window.nuklear.components.NumberBox;
import com.xbuilders.window.nuklear.components.TextBox;
import java.io.IOException;
import java.nio.IntBuffer;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.*;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class Settings implements MenuPage {

    public Settings(NkContext ctx, NKWindow window, TopMenu menu) {
        this.ctx = ctx;
        this.window = window;
        this.menu = menu;
    }

    NkContext ctx;
    TopMenu menu;
    NKWindow window;

    final int boxWidth = 450;
    final int boxHeight = 450;

    @Override
    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd) {
        nk_style_set_font(ctx, menu.uires.font_12);
        nk_rect((window.getWidth() / 2) - (boxWidth / 2), titleYEnd.get(0),
                boxWidth, boxHeight, windowDims);

        if (nk_begin(ctx, "Settings", windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
            nk_style_set_font(ctx, menu.uires.font_10);
            nk_layout_row_static(ctx, 20, 1, 1);

//            row("IP Adress");
//            ipAdress.render(ctx);
//
//            row("Port:");
//            port.render(ctx);
//
//            row("My Name:");
//            name.render(ctx);
//
//            row("Player Type:");
//            nk_button_label(ctx, "Bob");
            nk_layout_row_static(ctx, 60, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "BACK")) {
                menu.goBack();
            }
        }
        nk_end(ctx);
    }

    @Override
    public void onOpen() {
    }

    public void row(String text) {
        nk_layout_row_dynamic(ctx, 30, 2);
        nk_label(ctx, text, NK_TEXT_ALIGN_LEFT);
    }

}
