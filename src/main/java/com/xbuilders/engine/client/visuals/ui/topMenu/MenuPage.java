/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.xbuilders.engine.client.visuals.ui.topMenu;

import java.nio.IntBuffer;

import com.xbuilders.engine.client.visuals.ui.Page;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Patron
 */
public interface MenuPage {

    public void onOpen(Page lastPage);

    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd);
}
