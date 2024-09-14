/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.Game;
import com.xbuilders.engine.ui.FileDialog;
import com.xbuilders.engine.ui.RectOverlay;
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

    public GameUI(Game game, NkContext ctx, MainWindow window) throws IOException {
        this.ctx = ctx;
        this.window = window;
        this.game = game;
        crosshair = new Crosshair(window.getWidth(), window.getHeight());
        infoBox = new InfoText(ctx, window);
    }

    public void init() {
        menu = new GameMenu(ctx, window);
        fileDialog = new FileDialog(ctx, window);
        overlay = new RectOverlay();
        overlay.setColor(0, 0, 0, 0);
    }

    public void setDevText(String text) {
        infoBox.setText(text);
    }



    public boolean menusAreOpen() {
        return menu.isOpen() || game.menusAreOpen() || infoBox.isActive() || fileDialog.isOpen();
    }

    public boolean baseMenusOpen() {
        return menu.isOpen() || infoBox.isActive();
    }

    NkContext ctx;
    MainWindow window;
    Game game;
    private RectOverlay overlay;
    public FileDialog fileDialog;
    Crosshair crosshair;
    public static InfoText infoBox;
    public static GameMenu menu;
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
                menu.setOpen(false);
            }
            initGLForUI();
            overlay.draw();
            crosshair.draw();

            try (MemoryStack stack = stackPush()) {
                if (menu.isOpen()) {
                    menu.draw(stack);
                } else if (fileDialog.isOpen()) {
                    fileDialog.draw(stack);
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
                    menu.setOpen(!menu.isOpen());
                    infoBox.escKey();
                }
                case GLFW.GLFW_KEY_F4 -> {
                    drawUI = !drawUI;
                }
            }
        }

        if (fileDialog.isOpen() && fileDialog.keyEvent(key, scancode, action, mods)) {
            return true;
        } else if (infoBox.keyEvent(key, scancode, action, mods)) {
            return true;
        } else if (baseMenusOpen()) return true;

        else return game.uiKeyEvent(key, scancode, action, mods);
    }

    public boolean mouseButtonEvent(int button, int action, int mods) {
        if (fileDialog.isOpen() && fileDialog.mouseButtonEvent(button, action, mods)) {
            return true;
        } else return game.uiMouseButtonEvent(button, action, mods);
    }

    public boolean releaseMouse() {
        if (menusAreOpen()) return true;
        else if (infoBox.releaseMouse()) return true;
        else if (fileDialog.isOpen() && fileDialog.releaseMouse) return false;
        else if (game.releaseMouse()) return true;

        return false;
    }
}
