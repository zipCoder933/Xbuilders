/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.world.chunk.Chunk;
import com.xbuilders.game.Main;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.components.NumberBox;

import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;

import static org.lwjgl.nuklear.Nuklear.NK_LEFT;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_BORDER;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_NO_SCROLLBAR;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_TITLE;
import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_button_label;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_static;
import static org.lwjgl.nuklear.Nuklear.nk_rect;
import static org.lwjgl.nuklear.Nuklear.nk_style_set_font;

import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
class GameMenu extends GameUIElement {

    final int menuWidth = 300;

    public GameMenu(NkContext ctx, NKWindow window, UIResources uires) {
        super(ctx, window, uires);
        chunkDist = new NumberBox(8, 0);
        chunkDist.setMinValue(World.VIEW_DIST_MIN);
        chunkDist.setMaxValue(World.VIEW_DIST_MAX);
        chunkDist.setValueAsNumber(GameScene.world.getViewDistance());

        chunkDist.setOnChangeEvent(() -> {
            GameScene.world.setViewDistance((int) chunkDist.getValueAsNumber());
          
        });
    }

    boolean settingsPage = false;
    NumberBox chunkDist;

    @Override
    public void draw(MemoryStack stack) {
        GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        NkRect windowDims = NkRect.malloc(stack);
        ctx.style().window().fixed_background().data().color().set(Theme.backgroundColor);
        nk_style_set_font(ctx, uires.font_10);

        if (settingsPage) {
            int menuHeight = 200;
            nk_rect(
                    window.getWidth() / 2 - (menuWidth / 2),
                    window.getHeight() / 2 - (menuHeight / 2),
                    menuWidth, menuHeight, windowDims);
            if (nk_begin(ctx, "Menu", windowDims, NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {//| NK_WINDOW_MINIMIZABLE
                nk_layout_row_static(ctx, 10, 1, 1);
                nk_layout_row_dynamic(ctx, 20, 1);

                Nuklear.nk_text(ctx, "Chunk Distance", NK_LEFT);
                nk_layout_row_dynamic(ctx, 30, 1);
                chunkDist.render(ctx);

                nk_layout_row_static(ctx, 10, 1, 1);
                nk_layout_row_dynamic(ctx, 40, 1);
                if (nk_button_label(ctx, "Back")) {
                    settingsPage = false;
                }
            }
            nk_end(ctx);
        } else {
            int menuHeight = 180;
            nk_rect(
                    window.getWidth() / 2 - (menuWidth / 2),
                    window.getHeight() / 2 - (menuHeight / 2),
                    menuWidth, menuHeight, windowDims);
            if (nk_begin(ctx, "Menu", windowDims, NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {//| NK_WINDOW_MINIMIZABLE
                nk_layout_row_static(ctx, 10, 1, 1);
                nk_layout_row_dynamic(ctx, 40, 1);
                if (nk_button_label(ctx, "Settings")) {
                    settingsPage = true;
                }
                nk_layout_row_dynamic(ctx, 40, 1);
                if (nk_button_label(ctx, "Save and Quit")) {
                    Main.goToMenuPage();
                }
            }
            nk_end(ctx);
        }
    }
}
