/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui;

import com.xbuilders.engine.utils.ResourceUtils;
import com.xbuilders.window.NKWindow;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.xbuilders.window.nuklear.NKFontUtils;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkUserFont;

import static org.lwjgl.nuklear.Nuklear.nk_style_set_font;

/**
 * @author zipCoder933
 */
public class UIResources {

    public NkUserFont font_24, font_22, font_18, font_12, font_10, font_9;

    /**
     * crash from nuklear: https://github.com/LWJGL/lwjgl3/issues/986
     * <b>the byteBuffer pased to stbtt_initFont() is Garbage collected. the contents of the
     * buffer are not copied, instead a pointer to the font is passed and when the source of the pointer no longer exists,
     * the game crashes</b>
     * "You need to ensure that the ByteBuffer passed to stbtt_InitFont is not garbage collected (or not freed when
     * using explicit memory management APIs) while the font is in active use. This is a common mistake when dealing
     * with stb_truetype."
     */
    //Storing the byteBuffer keeps it from being garbage collected, this preventing a possible crash
    public final ByteBuffer fontBuffer;
    //----------------------------------------------------------------------------------------------

    public UIResources(NKWindow window, NkContext ctx, boolean largerFonts) throws IOException {
        fontBuffer = NKFontUtils.loadFontData(ResourceUtils.RESOURCE_DIR + "\\fonts\\Press_Start_2P\\PressStart2P-Regular.ttf");

        //Lets keep this font sizing. If we had a small enough screen size, we would still want small fonts
        //Minecraft has a UI scale setting
        font_24 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, largerFonts ? 26 : 24);
        font_22 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, largerFonts ? 24 : 22);
        font_18 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, largerFonts ? 20 : 18);
        font_12 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, largerFonts ? 14 : 12);
        font_10 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, largerFonts ? 12 : 10);
        font_9 = NKFontUtils.TTF_assignToNewTexture(fontBuffer, largerFonts ? 10 : 9);

        //VERY IMPORTANT: a font must be specified before anything can be drawn
        nk_style_set_font(ctx, font_9);
        Theme.initialize(ctx);
    }
}
