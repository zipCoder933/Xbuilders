/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.gameScene;

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.GameMode;
import com.xbuilders.engine.server.Server;
import com.xbuilders.engine.client.visuals.Theme;
import com.xbuilders.engine.client.visuals.topMenu.SettingsPage;
import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.nuklear.components.NumberBox;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class GameMenu extends UI_GameMenu {

    final int menuWidth = 340;
    final int menuHeight = 280;
    final int BUTTON_HEIGHT = 35;

    public GameMenu(NkContext ctx, ClientWindow window) {
        super(ctx, window);
        chunkDist = new NumberBox(8, 0);
        chunkDist.setMinValue(ClientWindow.settings.internal_viewDistance.min);
        chunkDist.setMaxValue(ClientWindow.settings.internal_viewDistance.max);
        chunkDist.setValueAsNumber(Server.world.getViewDistance());
        simDist = new NumberBox(8, 0);

        simDist.setMinValue(ClientWindow.settings.internal_simulationDistance.min);
        simDist.setMaxValue(ClientWindow.settings.internal_simulationDistance.max);
        simDist.setValueAsNumber(ClientWindow.settings.internal_simulationDistance.value);

        chunkDist.setOnChangeEvent(() -> {
            Server.world.setViewDistance(window.settings, (int) chunkDist.getValueAsNumber());
            chunkDist.setValueAsNumber(Server.world.getViewDistance());
        });
        simDist.setOnChangeEvent(() -> {
            ClientWindow.settings.internal_simulationDistance.value = (int) simDist.getValueAsNumber();
            ClientWindow.settings.save();
            simDist.setValueAsNumber(ClientWindow.settings.internal_simulationDistance.value);
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
    NumberBox chunkDist, simDist;
    SettingsPage allSettings;

    @Override
    public void draw(MemoryStack stack) {
        GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        NkRect windowDims = NkRect.malloc(stack);
        ctx.style().window().fixed_background().data().color().set(Theme.color_backgroundColor);
        nk_style_set_font(ctx, Theme.font_10);

        switch (page) {
            case SETTINGS -> drawSettingsPage(windowDims);
            case ALL_SETTINGS -> allSettings.layout(stack, windowDims);
            case HOME -> drawHomePage(windowDims);
        }
    }

    private boolean gameMenuVisible = true;

    @Override
    public boolean isOpen() {
        return gameMenuVisible;
    }

    public void setOpen(boolean open) {
        gameMenuVisible = open;
    }

    private void openHelpPage() {
        Server.pauseGame();
        File helpHtmlPage = ResourceUtils.file("help-menu/help.html");
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

            nk_layout_row_dynamic(ctx, BUTTON_HEIGHT, 1);
            if (nk_button_label(ctx, "Settings")) {
                page = GameMenuPage.SETTINGS;
            }
            nk_layout_row_dynamic(ctx, BUTTON_HEIGHT, 1);
            if (nk_button_label(ctx, "Load Waypoint")) {
                waypoint(false);
            }
            nk_layout_row_dynamic(ctx, BUTTON_HEIGHT, 1);
            if (nk_button_label(ctx, "Save Waypoint")) {
                waypoint(true);
            }
            nk_layout_row_dynamic(ctx, BUTTON_HEIGHT, 1);
            if (nk_button_label(ctx, "Help")) {
                openHelpPage();
            }
            nk_layout_row_dynamic(ctx, BUTTON_HEIGHT, 1);
            if (nk_button_label(ctx, "Save and Quit")) {
                ClientWindow.goToMenuPage();
            }
        }
        nk_end(ctx);
    }

    private void waypoint(boolean save) {
        File waypointDir = new File(Server.world.data.getDirectory(), "waypoints");
        if (!waypointDir.exists()) {
            waypointDir.mkdirs();
        }
        //Close game menu
        setOpen(false);
        ClientWindow.gameScene.ui.fileDialog.show(
                waypointDir,
                save, "wp", (file) -> {
                    if (file != null) {
                        if (save) {
                            String waypoint = GameScene.userPlayer.worldPosition.x
                                    + "," + GameScene.userPlayer.worldPosition.y
                                    + "," + GameScene.userPlayer.worldPosition.z;
                            try {
                                Files.write(file.toPath(), waypoint.getBytes());
                            } catch (IOException e) {
                                ClientWindow.popupMessage.message("Error saving waypoint: ", e.getMessage());
                            }
                        } else {
                            Vector3f originalPosition = GameScene.userPlayer.worldPosition;
                            try {
                                String waypoint = new String(Files.readAllBytes(file.toPath()));
                                String[] split = waypoint.split(",");
                                goTo(Float.parseFloat(split[0]),
                                        Float.parseFloat(split[1]),
                                        Float.parseFloat(split[2]));
                            } catch (IOException e) {
                                ClientWindow.popupMessage.message("Error loading waypoint: ", e.getMessage());
                                goTo(originalPosition.x,
                                        originalPosition.y,
                                        originalPosition.z);
                            }
                        }
                    }
                });
    }

    private void goTo(float x, float y, float z) {
        if (Server.getGameMode() == GameMode.ADVENTURE) {
            Server.alertClient("You cannot teleport here, but the waypoint is: " + x + ",   " + y + ",   " + z);
            GameScene.client_hudText("Waypoint: " + x + ", " + y + ", " + z);
        } else {
            GameScene.userPlayer.teleport(x, y, z);
        }
    }

    private void drawSettingsPage(NkRect windowDims) {
        int menuHeight = 300;
        nk_rect(
                window.getWidth() / 2 - (menuWidth / 2),
                window.getHeight() / 2 - (menuHeight / 2),
                menuWidth, menuHeight, windowDims);
        if (nk_begin(ctx, "Menu", windowDims, NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {//| NK_WINDOW_MINIMIZABLE
            nk_layout_row_static(ctx, 10, 1, 1);

            nk_layout_row_dynamic(ctx, 15, 1);
            Nuklear.nk_text(ctx, "Chunk Distance", NK_LEFT);

            nk_layout_row_dynamic(ctx, 30, 1);
            chunkDist.render(ctx);

            nk_layout_row_dynamic(ctx, 15, 1);
            Nuklear.nk_text(ctx, "Simulation Distance", NK_LEFT);

            nk_layout_row_dynamic(ctx, 30, 1);
            simDist.render(ctx);

            nk_layout_row_static(ctx, 10, 1, 1);
            nk_layout_row_dynamic(ctx, 35, 1);
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
