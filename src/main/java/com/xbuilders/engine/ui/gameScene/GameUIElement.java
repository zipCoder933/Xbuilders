/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author zipCoder933
 */
public abstract class GameUIElement {

    public GameUIElement(NkContext ctx, NKWindow window) {
        this.ctx = ctx;
        this.window = window;
    }

    public boolean releaseMouse = false;
    public final NkContext ctx;
    public final NKWindow window;

    public abstract void draw(MemoryStack stack);

    public abstract boolean isOpen();
}
