/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.ui.Page;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * @author Patron
 */
public class TopMenu {

    public static final int WIDTH_1 = 350;
    public static final int WIDTH_2 = 450;
    public static final int WIDTH_3 = 550;
    public static final int WIDTH_4 = 750;

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }


    public static void divider(NkContext ctx) {
        nk_layout_row_static(ctx, 25, 1, 1);
    }

    public static void row(NkContext ctx, String text, int columns) {
        nk_layout_row_dynamic(ctx, columns == 1 ? 20 : 30, columns);
        nk_label(ctx, text, NK_TEXT_ALIGN_LEFT);
    }

    MainWindow window;


    private MenuHome menuHome;
    private LoadWorld loadWorld;
    private NewWorld newWorld;
    private Multiplayer hostMultiplayer, joinMultiplayer;
    private SettingsPage settings;
    public ProgressMenu progress;
    public CustomizePlayer customizePlayer;
    private Page page = Page.HOME;
    private Page lastPage = null;

    public void setPage(Page page) {
        this.lastPage = this.page;
        this.page = page;

        switch (page) {
            case HOME -> menuHome.onOpen(lastPage);
            case LOAD_WORLD -> loadWorld.onOpen(lastPage);
            case NEW_WORLD -> newWorld.onOpen(lastPage);
            case PROGRESS -> progress.onOpen(lastPage);
            case HOST_MULTIPLAYER -> hostMultiplayer.onOpen(lastPage);
            case JOIN_MULTIPLAYER -> joinMultiplayer.onOpen(lastPage);
            case SETTINGS -> settings.onOpen(lastPage);
            case CUSTOMIZE_PLAYER -> customizePlayer.onOpen(lastPage);
        }
    }

    public void goBack() {
        if (this.page != Page.HOME && lastPage != null) {
            setPage(lastPage);
        }
    }


    public TopMenu(MainWindow window) throws IOException {
        this.window = window;
    }


    public void init(String ipAdress) throws IOException {
        menuHome = new MenuHome(window.ctx, window, this);
        loadWorld = new LoadWorld(window.ctx, window, this);
        newWorld = new NewWorld(window.ctx, window, this);
        progress = new ProgressMenu(window.ctx, window, this);
        hostMultiplayer = new Multiplayer(window.ctx, window, this, MainWindow.gameScene.player, true, ipAdress, loadWorld);
        joinMultiplayer = new Multiplayer(window.ctx, window, this, MainWindow.gameScene.player, false, ipAdress, loadWorld);
        customizePlayer = new CustomizePlayer(window.ctx, window, this, MainWindow.gameScene.player);
        settings = new SettingsPage(window.ctx, window, () -> {
            goBack();
        });

        VersionInfo versionInfo = new VersionInfo(window);
        versionInfo.createUpdatePrompt(MainWindow.popupMessage);
    }

    boolean firsttime = true;

    final int titleHeight = 65;
    final int windowMaxHeight = 800;

    public void render() {
        GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        if (firsttime && MainWindow.loadWorldOnStartup && MainWindow.devMode) {
            loadWorldOnInit__Dev();
            firsttime = false;
        }

        try (MemoryStack stack = stackPush()) {
            NkRect windowDims = NkRect.malloc(stack);
            IntBuffer titleYEnd = stack.mallocInt(1);
            if (window.getHeight() < windowMaxHeight) {
                titleYEnd.put(0, 50);
            } else {
                titleYEnd.put(0, (int) (window.getHeight() * 0.19));
            }

            //Draw the XBUILDERS title
            window.ctx.style().window().fixed_background().data().color().set(Theme.transparent);
            nk_rect(0, titleYEnd.get(0), window.getWidth(), titleHeight, windowDims);
            titleYEnd.put(0, titleYEnd.get(0) + titleHeight);
            if (nk_begin(window.ctx, "title", windowDims, NK_WINDOW_NO_INPUT | NK_WINDOW_NO_SCROLLBAR)) {
                nk_style_set_font(window.ctx, Theme.font_24);
                nk_layout_row_dynamic(window.ctx, 40, 1);
                nk_label(window.ctx, "X-Builders 3", NK_TEXT_CENTERED);
            }
            nk_end(window.ctx);
            Theme.resetWindowColor(window.ctx);

            switch (getPage()) {
                case HOME -> menuHome.layout(stack, windowDims, titleYEnd);
                case LOAD_WORLD -> loadWorld.layout(stack, windowDims, titleYEnd);
                case NEW_WORLD -> newWorld.layout(stack, windowDims, titleYEnd);
                case PROGRESS -> progress.layout(stack, windowDims, titleYEnd);
                case HOST_MULTIPLAYER -> hostMultiplayer.layout(stack, windowDims, titleYEnd);
                case JOIN_MULTIPLAYER -> joinMultiplayer.layout(stack, windowDims, titleYEnd);
                case SETTINGS -> settings.layout(stack, windowDims, titleYEnd);
                case CUSTOMIZE_PLAYER -> customizePlayer.layout(stack, windowDims, titleYEnd);
            }

        }

        glClearColor(0, .5f, 1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        window.NKrender();
    }

    private void loadWorldOnInit__Dev() {
        try {
            ArrayList<WorldInfo> worlds = new ArrayList<>();
            WorldsHandler.listWorlds(worlds);
            if (!worlds.isEmpty()) {
                loadWorld.loadWorld(worlds.get(0), null);
            }
        } catch (IOException ex) {
            Logger.getLogger(TopMenu.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void keyEvent() {
        if (window.isKeyPressed(GLFW.GLFW_KEY_Q)) {
            loadWorldOnInit__Dev();
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
            System.exit(0);
        }
    }

}
