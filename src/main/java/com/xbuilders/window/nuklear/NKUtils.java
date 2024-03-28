/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.window.nuklear;

import org.lwjgl.nuklear.NkContext;
import static org.lwjgl.nuklear.Nuklear.nk_layout_row_dynamic;
import static org.lwjgl.nuklear.Nuklear.nk_text;

/**
 *
 * @author zipCoder933
 */
public class NKUtils {

    public static int text(NkContext ctx, String text, int lineHeight, int alignment) {
        int height = 0;
        nk_layout_row_dynamic(ctx, lineHeight, 1);
        String[] splitText = text.split("\n");
        for (int i = 0; i < splitText.length; i++) {
            nk_text(ctx, splitText[i], alignment);
            height += lineHeight;
        }

        return height;
    }

//    private static void drawTextWithShadow(NkContext ctx, String text,
//            float x, float y, float xOffset, float yOffset,
//            int textColorR, int textColorG, int textColorB,
//            int shadowColorR, int shadowColorG, int shadowColorB) {
//        // Draw text with white color (main text)
//        nk_nk_text_colored(ctx, text, x + xOffset, y + yOffset, nk_rgb(textColorR, textColorG, textColorB), nk_rgb(0, 0, 0));
//
//        // Draw text with black color at a slightly offset position (shadow)
//        nk_nk_text_colored(ctx, text, x, y, nk_rgb(shadowColorR, shadowColorG, shadowColorB), nk_rgb(0, 0, 0));
//    }
}
