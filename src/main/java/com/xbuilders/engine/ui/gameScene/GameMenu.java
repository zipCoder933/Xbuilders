/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.topMenu.SettingsPage;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.engine.world.World;
import com.xbuilders.engine.MainWindow;
import com.xbuilders.window.nuklear.components.NumberBox;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
class GameMenu extends GameUIElement {

    final int menuWidth = 340;
    final int menuHeight = 220;

    public GameMenu(NkContext ctx, MainWindow window) {
        super(ctx, window);
        chunkDist = new NumberBox(8, 0);
        chunkDist.setMinValue(World.VIEW_DIST_MIN);
        chunkDist.setMaxValue(World.VIEW_DIST_MAX);
        chunkDist.setValueAsNumber(GameScene.world.getViewDistance());

        chunkDist.setOnChangeEvent(() -> {
            GameScene.world.setViewDistance((int) chunkDist.getValueAsNumber());
        });
        allSettings = new SettingsPage(ctx, window, () -> {
            page = GameMenuPage.SETTINGS;
        });
    }

    enum GameMenuPage {
        SETTINGS,
        ALL_SETTINGS,
        HOME
    }

    GameMenuPage page = GameMenuPage.HOME;
    NumberBox chunkDist;
    SettingsPage allSettings;

    @Override
    public void draw(MemoryStack stack) {
        GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        NkRect windowDims = NkRect.malloc(stack);
        ctx.style().window().fixed_background().data().color().set(Theme.backgroundColor);
        nk_style_set_font(ctx, Theme.font_10);

        switch (page) {
            case SETTINGS -> drawSettingsPage(windowDims);
            case ALL_SETTINGS -> allSettings.layout(stack, windowDims);
            case HOME -> drawHomePage(windowDims);
        }
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    private void openHelpPage() {
        GameScene.pauseGame();
        File helpHtmlPage = ResourceUtils.resource("help-menu/help.html");
        if (helpHtmlPage.exists()) {
            try {
                Desktop.getDesktop().open(helpHtmlPage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawHomePage(NkRect windowDims) {
        nk_rect(
                window.getWidth() / 2 - (menuWidth / 2),
                window.getHeight() / 2 - (menuHeight / 2),
                menuWidth, menuHeight, windowDims);
        if (nk_begin(ctx, "Menu", windowDims, NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {//| NK_WINDOW_MINIMIZABLE
            nk_layout_row_static(ctx, 10, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "Settings")) {
                page = GameMenuPage.SETTINGS;
            }
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "Help")) {
                openHelpPage();
            }
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "Save and Quit")) {
                MainWindow.goToMenuPage();
            }
        }
        nk_end(ctx);
    }

    private void drawSettingsPage(NkRect windowDims) {
        int menuHeight = 300;
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
            if (nk_button_label(ctx, "All Settings")) {
                page = GameMenuPage.ALL_SETTINGS;
            }

            nk_layout_row_static(ctx, 10, 1, 1);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "Back")) {
                page = GameMenuPage.HOME;
            }
        }
        nk_end(ctx);
    }
}
