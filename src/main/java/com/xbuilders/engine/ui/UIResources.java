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
 *
 * @author zipCoder933
 */
public class UIResources {

    public NkUserFont font_22,font_18, font_12, font_10, font_8;
    ByteBuffer fontBuffer;

    public UIResources(NKWindow window, NkContext ctx) throws IOException {
        String path = ResourceUtils.RESOURCE_DIR + "\\fonts\\Press_Start_2P\\PressStart2P-Regular.ttf";
        System.out.println("Font: " + path);
        fontBuffer = ioResourceToByteBuffer(path, 512 * 1024);

        font_22 = window.TTF_assignToNewTexture(fontBuffer, 22);
        font_18 = window.TTF_assignToNewTexture(fontBuffer, 18);
        font_12 = window.TTF_assignToNewTexture(fontBuffer, 12);
        font_10 = window.TTF_assignToNewTexture(fontBuffer, 10);
        font_8 = window.TTF_assignToNewTexture(fontBuffer, 8);

        //VERY IMPORTANT: a font must be specified before anything can be drawn
        nk_style_set_font(ctx, font_8);
        Theme.initialize(ctx);
    }
}
