/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

import com.xbuilders.window.NKWindow;
import org.joml.Vector2d;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author zipCoder933
 */
public abstract class UI_GameMenu {

    public UI_GameMenu(NkContext ctx, NKWindow window) {
        this.ctx = ctx;
        this.window = window;
    }

    public boolean releaseMouse = false;
    public final NkContext ctx;
    public final NKWindow window;

    public boolean inBounds(NkRect bounds) {
        Vector2d cursor = window.getCursorVector();
        return cursor.x > bounds.x() && cursor.x < bounds.x() + bounds.w() && cursor.y > bounds.y() && cursor.y < bounds.y() + bounds.h();
    }

    public abstract void draw(MemoryStack stack);

    public abstract boolean isOpen();
}