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
//    BlockTool hoveredTool;
    boolean wasOpen = false;

    int palleteMaxColumns = 8;

    int menuWidth = 400;
    int menu1Height = 100;
    int menu2Height = 100;

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
            if (!wasOpen) {
                onOpenEvent();
            }
            GLFW.glfwSetInputMode(window.getId(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            NkRect windowSize = NkRect.malloc(stack);
            Theme.resetEntireButtonStyle(ctx);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            nk_style_set_font(ctx, Theme.font_9);


            BlockTool selectedTool = tools.getSelectedTool();
            boolean showOptions = selectedTool.hasOptions;

            float menu1X = window.getWidth() / 2 - (menuWidth / 2);
            float menu1Y = window.getHeight() / 2 - (menu1Height / 2) - menu1Height;
            float menuHeight = menu1Height;
            if (showOptions) menuHeight += menu2Height;

            nk_rect(menu1X, menu1Y,
                    menuWidth, menuHeight,
                    windowSize);

            //Set window background color
//            ctx.style().window().background().set(Theme.transparent);

            if (nk_begin(ctx, "Pallete", windowSize, Nuklear.NK_WINDOW_NO_SCROLLBAR)) {
                nk_layout_row_dynamic(ctx, 15, 1);

                Theme.resetTextColor(ctx);
                Nuklear.nk_text(ctx, selectedTool.toolDescription(), Nuklear.NK_TEXT_ALIGN_CENTERED);

                ctx.style().button().padding().set(0, 0);

                int itemID = 0;
                int totalRows = 0;
                rows:
                while (true) {
                    boolean firstColunm = true;
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
                            if (firstColunm) {
                                nk_layout_row_dynamic(ctx, buttonWidth.width, palleteMaxColumns);
                                firstColunm = false;
                            }
//                            if (Nuklear.nk_widget_is_hovered(ctx)) {
//                                hoveredTool = tool;
//                            }
                            if (Nuklear.nk_button_image(ctx, tool.getNKIcon())) {
                                tools.selectTool(itemID);
                            }
                        }

                        itemID++;
                    }
                }
                buttonWidth.measure(ctx, stack);
                menu1Height = (int) ((totalRows * buttonWidth.width) - 1) + 20;
                Theme.resetEntireButtonStyle(ctx);

                if (showOptions) optionsGroup(stack, windowSize, selectedTool);
            }
            nk_end(ctx);
        } else if (wasOpen) {
            onCloseEvent();
        }
        wasOpen = false;
    }

    private void onCloseEvent() {
    }

    private void onOpenEvent() {
    }

    private void optionsGroup(MemoryStack stack, NkRect windowSize, BlockTool tool) {
        nk_layout_row_dynamic(ctx, menu2Height + 20, 1);//Super important
        if (Nuklear.nk_group_begin(ctx, "Options", Nuklear.NK_WINDOW_TITLE)) {
            tool.drawOptionsUI(stack, ctx, windowSize);
            Nuklear.nk_group_end(ctx);
        }
    }

    public boolean isOpen() {
        return window.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT);
    }
}
