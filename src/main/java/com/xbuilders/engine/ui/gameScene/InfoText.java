/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.ui.Theme;

import static com.xbuilders.engine.ui.Theme.createColor;
import static com.xbuilders.engine.ui.Theme.gray;
import static org.lwjgl.nuklear.Nuklear.*;

import com.xbuilders.engine.ui.UIResources;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.NKUtils;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.nuklear.NkColor;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

/**
 * @author zipCoder933
 */
public class InfoText extends GameUIElement {

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    private NkRect infoTextRect;
    TextBox box;
    private String text;

    public InfoText(NkContext ctx, NKWindow window, UIResources uires) {
        super(ctx, window, uires);
        box = new TextBox(100);
        infoTextRect = NkRect.create().x(0).y(0).w(window.getWidth()).h(400);
    }

    @Override
    public void draw(MemoryStack stack) {
        ctx.style().window().fixed_background().data().color().set(Theme.transparent);
        nk_style_set_font(ctx, uires.font_8);
        if (nk_begin(ctx, "info text", infoTextRect, NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_NO_INPUT)) {
            Nuklear.nk_layout_row_dynamic(ctx, 10, 1);
//            nk_text(ctx, "Commands:", NK_LEFT);
//            Nuklear.nk_layout_row_dynamic(ctx, 20, 1);
//            box.render(ctx);
            if (text != null) {
                NKUtils.text(ctx, text, 10, NK_LEFT);
            }
        }
        nk_end(ctx);
    }

    void windowResizeEvent(int width, int height) {
        infoTextRect = NkRect.create().x(0).y(0).w(window.getWidth()).h(400);
    }

}
