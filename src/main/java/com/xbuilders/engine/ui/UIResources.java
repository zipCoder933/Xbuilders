/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui;

import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.NKWindow;

import static com.xbuilders.window.utils.IOUtil.ioResourceToByteBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkUserFont;

import static org.lwjgl.nuklear.Nuklear.nk_style_set_font;

/**
 * @author zipCoder933
 */
public class UIResources {

    public NkUserFont font_24, font_22, font_18, font_12, font_10, font_8, font_6;
    ByteBuffer fontBuffer;

    public UIResources(NKWindow window, NkContext ctx, boolean largerFonts) throws IOException {
        String path = ResourceUtils.RESOURCE_DIR + "\\fonts\\Press_Start_2P\\PressStart2P-Regular.ttf";
        System.out.println("Font: " + path);
        fontBuffer = ioResourceToByteBuffer(path, 512 * 1024);

        //Lets keep this font sizing. If we had a small enough screen size, we would still want small fonts
        //Minecraft has a UI scale setting
        font_24 = window.TTF_assignToNewTexture(fontBuffer, largerFonts ? 26 : 24);
        font_22 = window.TTF_assignToNewTexture(fontBuffer, largerFonts ? 24 : 22);
        font_18 = window.TTF_assignToNewTexture(fontBuffer, largerFonts ? 20 : 18);
        font_12 = window.TTF_assignToNewTexture(fontBuffer, largerFonts ? 14 : 12);
        font_10 = window.TTF_assignToNewTexture(fontBuffer, largerFonts ? 12 : 10);
        font_8 = window.TTF_assignToNewTexture(fontBuffer, largerFonts ? 10 : 8);
        font_6 = window.TTF_assignToNewTexture(fontBuffer, largerFonts ? 10 : 6);

        //VERY IMPORTANT: a font must be specified before anything can be drawn
        nk_style_set_font(ctx, font_8);
        Theme.initialize(ctx);
    }
}
