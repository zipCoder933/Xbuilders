/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.ui.gameScene;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.server.model.Game;
import com.xbuilders.engine.client.visuals.ui.FileDialog;
import com.xbuilders.engine.client.visuals.ui.RectOverlay;
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

    NkContext ctx;
    MainWindow window;
    Game game;
    private RectOverlay overlay;
    public FileDialog fileDialog;
    Crosshair crosshair;
    public static InfoText infoBox;
    public static UI_Hotbar hotbar;
    public static GameMenu baseMenu;
    boolean drawUI = true;

    public GameUI(Game game, NkContext ctx, MainWindow window) throws IOException {
        this.ctx = ctx;
        this.window = window;
        this.game = game;
        crosshair = new Crosshair(window.getWidth(), window.getHeight());
        infoBox = new InfoText(ctx, window);
        hotbar = new UI_Hotbar(ctx, window);
    }

    public void init() {
        baseMenu = new GameMenu(ctx, window);
        fileDialog = new FileDialog(ctx, window);
        overlay = new RectOverlay();
        overlay.setColor(0, 0, 0, 0);
    }

    public void setDevText(String text) {
        infoBox.setText(text);
    }


    public boolean anyMenuOpen() {
        return baseMenu.isOpen() || game.menusAreOpen() || infoBox.isActive() || fileDialog.isOpen();
    }

    public boolean baseMenusOpen() {
        return baseMenu.isOpen() || infoBox.isActive();
    }


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
                baseMenu.setOpen(false);
            }
            initGLForUI();
            overlay.draw();
            crosshair.draw();

            try (MemoryStack stack = stackPush()) {
                if (baseMenu.isOpen()) {
                    baseMenu.draw(stack);
                } else if (fileDialog.isOpen()) {
                    fileDialog.draw(stack);
                } else if (game.uiDraw(stack)) {
                } else {
                    infoBox.draw(stack);
                    hotbar.draw(stack);
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

    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {//TODO: nuklear already has builtin scroll event, Learn to detect that and take advantage of it.
        if (infoBox.mouseScrollEvent(scroll, xoffset, yoffset)) {
            return true;
        }
        return false;
    }


    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_ESCAPE -> {  //When we hit ESC, we ALWAYS open the menu.
                    baseMenu.setOpen(!baseMenu.isOpen());
                    infoBox.escKey();
                    return true;
                }
                case GLFW.GLFW_KEY_F4 -> {
                    drawUI = !drawUI;
                    return true;
                }
            }
        }
        if (fileDialog.isOpen() && fileDialog.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(fileDialog.getClass());
            return true;
        } else if (infoBox.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(infoBox.getClass());
            return true;
        } else if (hotbar.keyEvent(key, scancode, action, mods)) {
            printKeyConsumption(hotbar.getClass());
            return true;
        } else if (baseMenusOpen()) {
            printKeyConsumption(baseMenu.getClass());
            return true;
        }
        return false;
    }

    public static void printKeyConsumption(Class aClass) {
        //System.out.println("keyEvent consumed by: " + aClass.getSimpleName());
    }

    public boolean mouseButtonEvent(int button, int action, int mods) {
        if (fileDialog.isOpen() && fileDialog.mouseButtonEvent(button, action, mods)) {
            return true;
        } else return game.uiMouseButtonEvent(button, action, mods);
    }

    public boolean releaseMouse() {
        if (anyMenuOpen()) return true;
        else if (MainWindow.popupMessage.isShown()) return true;
        else if (infoBox.releaseMouse()) return true;
        else if (fileDialog.isOpen() && fileDialog.releaseMouse) return false;
        else return game.releaseMouse();
    }
}