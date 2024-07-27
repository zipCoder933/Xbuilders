/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.game.blockTools;

import com.xbuilders.engine.ui.Theme;
import com.xbuilders.window.BaseWindow;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.Nuklear;
import org.lwjgl.system.MemoryStack;

import java.util.*;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class BlockToolPallete {

    BaseWindow window;
    NkContext ctx;
    WidgetWidthMeasurement buttonWidth;
    BlockTools tools;

    int palleteMaxColumns = 8;

    String toolDescription = "";

    int menuWidth = 400;
    int menuHeight = 100;

    List<BlockTool> toolsList;


    public BlockToolPallete(NkContext ctx, BaseWindow window, List<BlockTool> toolsList, BlockTools tools) {
        this.toolsList = toolsList;
        this.tools = tools;
        this.ctx = ctx;
        this.window = window;
        buttonWidth = new WidgetWidthMeasurement(0);
    }

    public void draw(MemoryStack stack) {
        if (isOpen()) {
            GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            NkRect windowSize = NkRect.malloc(stack);
            Theme.resetEntireButtonStyle(ctx);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            nk_style_set_font(ctx, Theme.font_9);


            nk_rect(window.getWidth() / 2 - (menuWidth / 2), window.getHeight() / 2 - (menuHeight / 2), menuWidth, menuHeight,
                    windowSize);

            //Set window background color
//            ctx.style().window().background().set(Theme.transparent);

            if (nk_begin(ctx, "Pallete", windowSize, Nuklear.NK_WINDOW_NO_SCROLLBAR)) {
                nk_layout_row_dynamic(ctx, 15, 1);
//                ctx.style().text().color().set(Theme.lightGray);

                Theme.resetTextColor(ctx);
                Nuklear.nk_text(ctx, toolDescription, Nuklear.NK_TEXT_ALIGN_CENTERED);

                ctx.style().button().padding().set(0, 0);

                int itemID = 0;
                int totalRows = 0;
                rows:
                while (true) {
                    nk_layout_row_dynamic(ctx, buttonWidth.width, palleteMaxColumns);
                    totalRows++;
                    cols:
                    for (int i = 0; i < palleteMaxColumns; i++) {
                        if (itemID >= toolsList.size()) {
                            break rows;
                        }
                        BlockTool tool = toolsList.get(itemID);

                        if (itemID == tools.selectedTool) {
                            ctx.style().button().border_color().set(Theme.white);

                        } else {
                            ctx.style().button().border_color().set(Theme.blue);
                        }

                        if (tool != null && tool.getNKIcon() != null) {
                            if (Nuklear.nk_widget_is_hovered(ctx)) {
                                toolDescription = tool.toolDescription();
                                tools.selectTool(itemID);
                            }
                            Nuklear.nk_button_image(ctx, tool.getNKIcon());
                        } else if (Nuklear.nk_button_text(ctx, "")) {
                            tools.selectTool(itemID);
                        }

                        itemID++;
                    }
                }
                buttonWidth.measure(ctx, stack);
                menuHeight = (int) ((totalRows * buttonWidth.width) - 1) + 20;

                Theme.resetEntireButtonStyle(ctx);
            }
            nk_end(ctx);
        }
    }


    public boolean isOpen() {
        return window.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT);
    }
}
