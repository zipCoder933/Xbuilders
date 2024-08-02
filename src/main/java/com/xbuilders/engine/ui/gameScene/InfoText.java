/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.gameScene;

import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.NKUtils;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkVec2;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;

import static org.lwjgl.nuklear.Nuklear.*;

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
    final int commandBoxHeight = 450;
    final int sidePadding = 50;

    public InfoText(NkContext ctx, NKWindow window) {
        super(ctx, window);
        box = new TextBox(100);
        box.setOnChangeEvent(() -> {
            submitCommand(box.getValueAsString());
            box.setValueAsString("");
        });
        infoTextRect = NkRect.create().x(0).y(40).w(window.getWidth()).h(400);
        commandRect = NkRect.create().x(0).y(40).w(window.getWidth()).h(commandBoxHeight);
    }

    private void submitCommand(String valueAsString) {
        addToHistory("< " + valueAsString);
        String str = GameScene.handleGameCommand(valueAsString);
        if (str != null) {
            addToHistory("> " + str);
        }
    }

    String infoPanelText = "info panel";
    String commandPanelText = "command panel";

    @Override
    public void draw(MemoryStack stack) {
        nk_style_set_font(ctx, Theme.font_9);

        if (commandMode) {
            commandRect.x(sidePadding);
            commandRect.w(window.getWidth() - (sidePadding * 2));
            commandRect.h(Math.min(commandBoxHeight, window.getHeight() - 150));
            ctx.style().window().fixed_background().data().color().set(Theme.darkTransparent);
            if (nk_begin(ctx, commandPanelText, commandRect, 0)) {
                Nuklear.nk_layout_row_dynamic(ctx, 30, 1);
                nk_text(ctx, "Enter command:", NK_LEFT);

                Nuklear.nk_layout_row_dynamic(ctx, 40, 1);
                box.render(ctx);

                Nuklear.nk_layout_row_static(ctx, 30, window.getWidth(), 1);
                drawChatHistory(ctx, true , 0);
            }
            nk_end(ctx);
        } else {
            infoTextRect.w(window.getWidth());
            infoTextRect.h(300);
            ctx.style().window().fixed_background().data().color().set(Theme.transparent);
            if (nk_begin(ctx, infoPanelText, infoTextRect, NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_NO_INPUT)) {
                if (text != null) {
                    Nuklear.nk_layout_row_dynamic(ctx, 10, 1);
                    NKUtils.text(ctx, text, 10, NK_LEFT);
                    Nuklear.nk_layout_row_static(ctx, 20, window.getWidth(), 1);
                }
                drawChatHistory(ctx, false, 10);
            }
            nk_end(ctx);
        }
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    ArrayList<ChatMessage> chatHistory = new ArrayList<>();

    public void addToHistory(String text) {
        System.out.println(text);
        chatHistory.add(0, new ChatMessage(text));
        if (chatHistory.size() > 30) {
            chatHistory.remove(chatHistory.size() - 1);
        }
    }

    private void drawChatHistory(NkContext ctx, boolean alwaysShow, int maxMessages) {
        for (int i = 0; i < chatHistory.size(); i ++) {

            if (maxMessages > 0 && i > maxMessages) {
                break;
            }

            String line = chatHistory.get(i).value;

            Nuklear.nk_layout_row_dynamic(ctx, 10, 1);
            if (alwaysShow || System.currentTimeMillis() - chatHistory.get(i).time < 10000) {
                if (line.startsWith("<")) {
                    NKUtils.text(ctx, line, 9, NK_TEXT_ALIGN_RIGHT);
                } else NKUtils.text(ctx, line, 9, NK_TEXT_ALIGN_LEFT);
            }
        }
    }

    void windowResizeEvent(int width, int height) {
        infoTextRect = NkRect.create().x(0).y(0).w(window.getWidth()).h(400);
    }

    boolean commandMode;

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (action == GLFW.GLFW_RELEASE) {
            if (key == GLFW.GLFW_KEY_SLASH) {//Toggle command mode
                commandMode = !commandMode;
                if (commandMode) {
                    box.setValueAsString("");
                    return true;
                }
            }
        }
        return commandMode;//the text box could press any key
    }


    public boolean releaseMouse() {
        return commandMode;
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
