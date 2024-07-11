/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.engine.ui.RectOverlay;
import com.xbuilders.window.NKWindow;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL14C.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14C.glBlendEquation;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class GameUI {

    public GameUI(Game game, NkContext ctx, NKWindow window, UIResources uires) throws IOException {
        Theme.initialize(ctx);
        this.ctx = ctx;
        this.window = window;
        this.uires = uires;
        this.game = game;
        crosshair = new Crosshair(window.getWidth(), window.getHeight());
        infoBox = new InfoText(ctx, window, uires);
    }

    public void init() {
        menu = new GameMenu(ctx, window, uires);
        overlay = new RectOverlay();
        overlay.setColor(0, 0, 0, 0);
    }

    public void setDevText(String text) {
        infoBox.setText(text);
    }

    private boolean gameMenuVisible = true;

    public boolean allMenusAreOpen() {
        return gameMenuVisible || game.menusAreOpen() || infoBox.isActive();
    }

    public boolean baseMenusOpen() {
        return gameMenuVisible || infoBox.isActive();
    }

    NkContext ctx;
    NKWindow window;
    UIResources uires;
    Game game;
    private RectOverlay overlay;

    Crosshair crosshair;
    public InfoText infoBox;
    GameMenu menu;
    boolean drawUI = true;

    public void windowResizeEvent(int width, int height) {
        crosshair.windowResizeEvent(width, height);
        infoBox.windowResizeEvent(width, height);
    }

    public void setOverlayColor(float r, float g, float b, float a) {
        overlay.setColor(r, g, b, a);
    }

    public void draw() {
        if (drawUI) {
            GL30.glDepthMask(false);
            if (game.menusAreOpen()) {
                gameMenuVisible = false;
            }
            initGLForUI();
            overlay.draw();
            crosshair.draw();

            try (MemoryStack stack = stackPush()) {
                if (gameMenuVisible) {
                    menu.draw(stack);
                } else {
                    game.uiDraw(stack);
                    infoBox.draw(stack);
                }
                //Add myGame.uiDraw right here
            }
            window.NKrender();
            GL30.glDepthMask(true);
        }
    }

    private void initGLForUI() {
        //Some of these parameters allow the overlay to be seen
        // setup global state
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11C.glDisable(GL_CULL_FACE); //Disable backface culling
        glEnable(GL_SCISSOR_TEST);
        glBlendFunc(GL_ONE, GL_ZERO);
        GL11C.glDisable(GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST); //Disable depth test
        //enable transparency
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (infoBox.mouseScrollEvent(scroll, xoffset, yoffset)) {
        } else game.uiMouseScrollEvent(scroll, xoffset, yoffset);
    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE -> {
                    gameMenuVisible = !gameMenuVisible;
                    infoBox.escKey();
                }
                case GLFW.GLFW_KEY_F4 -> {
                    drawUI = !drawUI;
                }
            }
        }

        if (infoBox.keyEvent(key, scancode, action, mods)) {
            return true;
        } else if (baseMenusOpen()) return true;

        else return game.uiKeyEvent(key, scancode, action, mods);
    }

    public boolean mouseButtonEvent(int button, int action, int mods) {
        return game.uiMouseButtonEvent(button, action, mods);
    }

    public boolean canHoldMouse() {
        if (allMenusAreOpen()) return false;
        else if (!infoBox.canHoldMouse()) return false;
        return true;
    }
}
