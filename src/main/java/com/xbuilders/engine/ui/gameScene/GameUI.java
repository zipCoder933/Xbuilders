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
import com.xbuilders.window.NKWindow;

import static com.xbuilders.window.NKWindow.*;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * Java port of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class GameUI {

    public GameUI(Game game, NkContext ctx, NKWindow window, UIResources uires) throws IOException {
        this.ctx = ctx;
        this.window = window;
        this.uires = uires;
        this.game = game;
        crosshair = new Crosshair(window.getWidth(), window.getHeight());
        Theme.initialize(ctx);
        infoBox = new InfoText(ctx, window, uires);
        game.uiInit(ctx, window, uires, this);
    }

    public void init() {
        menu = new GameMenu(ctx, window, uires);
    }

    public void setInfoText(String text) {
        infoBox.setText(text);
    }

    private boolean gameMenuVisible = true;

    public boolean menusAreOpen() {
        return gameMenuVisible || game.menusAreOpen();
    }

    NkContext ctx;
    NKWindow window;
    UIResources uires;
    Game game;

    Crosshair crosshair;
    InfoText infoBox;
    GameMenu menu;
    boolean drawUI = true;

    public void windowResizeEvent(int width, int height) {
        crosshair.windowResizeEvent(width, height);
        infoBox.windowResizeEvent(width, height);
    }

    public void draw() {
        if (drawUI) {
            if (game.menusAreOpen()) {
                gameMenuVisible = false;
            }
            glDisable(GL_DEPTH_TEST);
            crosshair.draw();

            try (MemoryStack stack = stackPush()) {
                infoBox.draw(stack);
                if (gameMenuVisible) {
                    menu.draw(stack);
                } else {

                    game.uiDraw(stack);
                }
                //Add myGame.uiDraw right here
            }
            window.NKrender(NK_ANTI_ALIASING_ON, MAX_VERTEX_BUFFER, MAX_ELEMENT_BUFFER);
        }
    }

    public void mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        game.uiMouseScrollEvent(scroll, xoffset, yoffset);
    }

    public void keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            switch (key) {
                case GLFW.GLFW_KEY_M -> {
                    gameMenuVisible = !gameMenuVisible;
                }
                case GLFW.GLFW_KEY_J -> {
                    drawUI = !drawUI;
                }
            }
        }
        game.uiKeyEvent(key, scancode, action, mods);
    }

    public void mouseButtonEvent(int button, int action, int mods) {
        game.uiMouseButtonEvent(button, action, mods);
    }

}
