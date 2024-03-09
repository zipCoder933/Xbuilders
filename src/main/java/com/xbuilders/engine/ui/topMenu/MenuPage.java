/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

import java.nio.IntBuffer;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Patron
 */
public interface MenuPage {

    public void onOpen();

    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd);
}
