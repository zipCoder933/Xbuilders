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
import com.xbuilders.engine.client.settings.ClientSettings;
import com.xbuilders.engine.client.visuals.ui.Page;
import com.xbuilders.engine.client.visuals.ui.Theme;
import com.xbuilders.window.nuklear.NKUtils;

import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class SettingsPage implements MenuPage {

    public SettingsPage(NkContext ctx, MainWindow window, Runnable backCallback) {
        this.backCallback = backCallback;
        this.ctx = ctx;
        this.window = window;

        fields.clear();
        for (Field field : ClientSettings.class.getDeclaredFields()) {
            field.setAccessible(true);
            if (!MainWindow.devMode && field.getName().startsWith("internal_")) continue;
            try {
                fields.add(new SettingsField(window, field));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    Runnable backCallback;
    NkContext ctx;
    MainWindow window;

    final int BOX_DEFAULT_WIDTH = TopMenu.WIDTH_4;
    final int BOX_DEFAULT_HEIGHT = 500;

    @Override
    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd) {
        nk_style_set_font(ctx, Theme.font_12);
        int boxWidth = (int) (BOX_DEFAULT_WIDTH * Theme.getScale());
        int boxHeight = (int) (BOX_DEFAULT_HEIGHT * Theme.getScale());

        nk_rect((window.getWidth() / 2) - (boxWidth / 2),
                titleYEnd.get(0),
                boxWidth, boxHeight, windowDims);
        draw(stack, windowDims);
    }

    public void layout(MemoryStack stack, NkRect windowDims) {
        int boxWidth = (int) (BOX_DEFAULT_WIDTH * Theme.getScale());
        int boxHeight = (int) (BOX_DEFAULT_HEIGHT * Theme.getScale());

        nk_rect((window.getWidth() / 2) - (boxWidth / 2),
                (window.getHeight() / 2) - (boxHeight / 2),
                boxWidth, boxHeight, windowDims);
        draw(stack, windowDims);
    }

    private void draw(MemoryStack stack, NkRect windowDims) {
        if (nk_begin(ctx, "Settings", windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
            nk_style_set_font(ctx, Theme.font_10);
            NKUtils.wrapText(ctx, "Note that some changes will only take effect after the game is restarted", windowDims.w() - 20);
            nk_layout_row_static(ctx, 30, 1, 2);

            for (SettingsField field : fields) {
                field.layout(ctx, stack, windowDims);
            }

            //Back button
            nk_layout_row_static(ctx, 60, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 2);
            if (nk_button_label(ctx, "BACK")) {
                backCallback.run();
            }
            if (nk_button_label(ctx, "APPLY")) {
                window.saveAndApplySettings();
                backCallback.run();
            }
        }
        nk_end(ctx);
    }

    ArrayList<SettingsField> fields = new ArrayList<>();

    @Override
    public void onOpen(Page lastPage) {
    }


}