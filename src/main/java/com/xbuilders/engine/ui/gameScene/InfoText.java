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
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
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
    private NkRect commandRect;
    TextBox box;
    private String text;
    final int commandBoxHeight = 400;

    public InfoText(NkContext ctx, NKWindow window, UIResources uires) {
        super(ctx, window, uires);
        box = new TextBox(100);
        box.setOnChangeEvent(() -> {
          submitCommand(box.getValueAsString());
          box.setValueAsString("");
        });
        infoTextRect = NkRect.create().x(0).y(0).w(window.getWidth()).h(400);
        commandRect = NkRect.create().x(0).y(0).w(window.getWidth()).h(commandBoxHeight);
    }

    private void submitCommand(String valueAsString) {
        System.out.println("COMMAND: "+valueAsString);
    }

    String infoPanelText = "info panel";
    String commandPanelText = "command panel";

    @Override
    public void draw(MemoryStack stack) {

        nk_style_set_font(ctx, uires.font_8);


        if (commandMode) {
            commandRect.w(window.getWidth());
            commandRect.h(Math.min(commandBoxHeight, window.getHeight() - 150));
            ctx.style().window().fixed_background().data().color().set(Theme.darkTransparent);
            if (nk_begin(ctx, commandPanelText, commandRect, 0)) {
                Nuklear.nk_layout_row_static(ctx, 30, window.getWidth(), 1);
                Nuklear.nk_layout_row_dynamic(ctx, 20, 1);
                nk_text(ctx, "Enter command:", NK_LEFT);
                Nuklear.nk_layout_row_dynamic(ctx, 20, 1);
                box.render(ctx);

//                Nuklear.nk_layout_row_static(ctx, 20, window.getWidth(), 1);
//                NKUtils.text(ctx, "Test1\nTest2\nTest3", 10, NK_LEFT);
            }
            nk_end(ctx);
        } else {
            ctx.style().window().fixed_background().data().color().set(Theme.transparent);
            if (nk_begin(ctx, infoPanelText, infoTextRect, NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_NO_INPUT)) {
                Nuklear.nk_layout_row_dynamic(ctx, 10, 1);
                if (text != null) {
                    NKUtils.text(ctx, text, 10, NK_LEFT);
                }
            }
            nk_end(ctx);
        }


    }

    void windowResizeEvent(int width, int height) {
        infoTextRect = NkRect.create().x(0).y(0).w(window.getWidth()).h(400);
    }

    boolean commandMode;

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            if (key == GLFW.GLFW_KEY_SLASH) {
                commandMode = !commandMode;
                if(commandMode){
                    box.setValueAsString("");
                }
            }
            return true;
        }
        return false;
    }


    public boolean canHoldMouse() {
        return !commandMode;
    }

    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        return commandMode;
    }

    public boolean isActive() {
        return commandMode;
    }

    public void escKey() {
        commandMode = false;
    }
}
