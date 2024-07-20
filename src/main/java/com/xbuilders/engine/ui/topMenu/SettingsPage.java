/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.settings.EngineSettings;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.game.Main;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.NKUtils;

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.*;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class SettingsPage implements MenuPage {

    public SettingsPage(NkContext ctx, NKWindow window, TopMenu menu) {
        this.ctx = ctx;
        this.window = window;
        this.menu = menu;
        boxWidth = Main.settings.video_largerUI ? 800 : 700;
    }

    NkContext ctx;
    TopMenu menu;
    NKWindow window;

    int boxWidth = 750;
    int boxHeight = 500;

    @Override
    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd) {
        nk_style_set_font(ctx, Theme.font_12);
        nk_rect((window.getWidth() / 2) - (boxWidth / 2), titleYEnd.get(0),
                boxWidth, boxHeight, windowDims);

        if (nk_begin(ctx, "Settings", windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
            nk_style_set_font(ctx, Theme.font_10);
            NKUtils.wrapText(ctx, "Note that these changes will only take efffect after the game is restarted", windowDims.w() - 20);
            nk_layout_row_static(ctx, 30, 1, 2);

            for (SettingsField field : fields) {
                field.layout(ctx, stack, windowDims);
            }

            //Back button
            nk_layout_row_static(ctx, 60, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "BACK")) {
                menu.goBack();
            }
        }
        nk_end(ctx);
    }

    ArrayList<SettingsField> fields = new ArrayList<>();

    @Override
    public void onOpen() {
        System.out.println("Settings opened");
        fields.clear();
        for (Field field : EngineSettings.class.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                fields.add(new SettingsField(field));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


}
