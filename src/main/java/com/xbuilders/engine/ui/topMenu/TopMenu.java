/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.ui.Page;

import static com.xbuilders.engine.ui.Page.HOME;
import static com.xbuilders.engine.ui.Page.HOST_MULTIPLAYER;
import static com.xbuilders.engine.ui.Page.JOIN_MULTIPLAYER;
import static com.xbuilders.engine.ui.Page.LOAD_WORLD;
import static com.xbuilders.engine.ui.Page.NEW_WORLD;
import static com.xbuilders.engine.ui.Page.PROGRESS;

import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.world.WorldsHandler;
import com.xbuilders.game.Main;
import com.xbuilders.window.NKWindow;

import static com.xbuilders.window.NKWindow.MAX_ELEMENT_BUFFER;
import static com.xbuilders.window.NKWindow.MAX_VERTEX_BUFFER;

import com.xbuilders.window.utils.texture.TextureUtils;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkRect;

import static org.lwjgl.nuklear.Nuklear.NK_ANTI_ALIASING_ON;
import static org.lwjgl.nuklear.Nuklear.NK_TEXT_CENTERED;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_BORDER;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_NO_INPUT;
import static org.lwjgl.nuklear.Nuklear.NK_WINDOW_NO_SCROLLBAR;
import static org.lwjgl.nuklear.Nuklear.nk_begin;
import static org.lwjgl.nuklear.Nuklear.nk_end;
import static org.lwjgl.nuklear.Nuklear.nk_label;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_rect;
import static org.lwjgl.nuklear.Nuklear.nk_style_set_font;

import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.glClearColor;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * @author Patron
 */
public class TopMenu {

    /**
     * @return the page
     */
    public Page getPage() {
        return page;
    }

    final boolean loadWorldOnStartup = false;

    NKWindow window;
    UIResources uires;
    public PopupMessage popupMessage;

    private MenuHome menuHome;
    private LoadWorld loadWorld;
    private NewWorld newWorld;
    private Multiplayer hostMultiplayer, joinMultiplayer;
    private Settings settings;
    public ProgressMenu progress;
    private Page page = Page.HOME;
    private Page lastPage = null;

    public void setPage(Page page) {
        switch (page) {
            case HOME -> menuHome.onOpen();
            case LOAD_WORLD -> loadWorld.onOpen();
            case NEW_WORLD -> newWorld.onOpen();
            case PROGRESS -> progress.onOpen();
            case HOST_MULTIPLAYER -> hostMultiplayer.onOpen();
            case JOIN_MULTIPLAYER -> joinMultiplayer.onOpen();
            case SETTINGS -> settings.onOpen();
        }
        this.lastPage = this.page;
        this.page = page;

    }

    public void goBack() {
        if (this.page != Page.HOME && lastPage != null) {
            setPage(lastPage);
        }
    }

    public TopMenu(NKWindow window) throws IOException {
        this.window = window;
    }

    public void init(UIResources uires, String ipAdress) throws IOException {
        this.uires = uires;
        menuHome = new MenuHome(window.ctx, window, this);
        loadWorld = new LoadWorld(window.ctx, window, this);
        newWorld = new NewWorld(window.ctx, window, this);
        progress = new ProgressMenu(window.ctx, window, this);
        hostMultiplayer = new Multiplayer(window.ctx, window, this, Main.gameScene.player, true, ipAdress, loadWorld);
        joinMultiplayer = new Multiplayer(window.ctx, window, this, Main.gameScene.player, false, ipAdress, loadWorld);
        settings = new Settings(window.ctx, window, this);
        popupMessage = new PopupMessage(window.ctx, window, uires);
    }

    boolean firsttime = true;

    final int titleHeight = 65;
    final int windowMaxHeight = 800;

    public void render() {
        GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        if (firsttime && loadWorldOnStartup && Main.devMode) {
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
                nk_style_set_font(window.ctx, uires.font_24);
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
            }
            popupMessage.draw(stack);
        }

        glClearColor(0, .5f, 1f, 1f);
        glClear(GL_COLOR_BUFFER_BIT);

        window.NKrender();
    }

    public void disposeEvent() {
        TextureUtils.deleteAllTextures();
        GL.setCapabilities(null);
    }

    private void loadWorldOnInit__Dev() {
        try {
            ArrayList<WorldInfo> worlds = new ArrayList<>();
            WorldsHandler.listWorlds(worlds);
            if (!worlds.isEmpty()) {
                loadWorld.loadWorld(worlds.get(0),null);
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
