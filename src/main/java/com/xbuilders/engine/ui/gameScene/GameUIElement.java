/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.window.NKWindow;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author zipCoder933
 */
public abstract class GameUIElement {

    public GameUIElement(NkContext ctx, NKWindow window, UIResources uires) {
        this.ctx = ctx;
        this.window = window;
        this.uires = uires;
    }
    public final NkContext ctx;
    public final NKWindow window;
    public final UIResources uires;

    public abstract void draw(MemoryStack stack);
}
