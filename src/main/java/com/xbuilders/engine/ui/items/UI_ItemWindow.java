package com.xbuilders.engine.ui.items;

import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.UI_GameMenu;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.nuklear.WidgetSizeMeasurement;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

public abstract class UI_ItemWindow extends UI_GameMenu {
    public UI_ItemWindow(NkContext ctx, NKWindow window, String title) {
        super(ctx, window);
        this.title = title;
    }

    public ItemStack draggingItem = null;
    public Vector2i menuDimensions = new Vector2i(645, 645);
    public final int maxColumns = 11;
    private boolean isOpen = false;
    public final int windowFlags = NK_WINDOW_TITLE | NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_CLOSABLE;
    public final String title;


    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        if (open) {
            nk_window_show(ctx, title, windowFlags);
            isOpen = true;
        } else {
            isOpen = false;
        }
    }

    @Override
    public final void draw(MemoryStack stack) {
        if (isOpen) {
            GLFW.glfwSetInputMode(window.getWindow(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
            NkRect windowDims2 = NkRect.malloc(stack);
            Theme.resetEntireButtonStyle(ctx);
            Theme.resetWindowColor(ctx);
            Theme.resetWindowPadding(ctx);
            nk_style_set_font(ctx, Theme.font_10);

            nk_rect(window.getWidth() / 2 - (menuDimensions.x / 2),
                    window.getHeight() / 2 - (menuDimensions.y / 2),
                    menuDimensions.x, menuDimensions.y, windowDims2);

            if (nk_begin(ctx, title, windowDims2, windowFlags)) {
                drawWindow(stack, windowDims2);
                if (draggingItem != null) drawItemAtCursor(window, stack, ctx, draggingItem);
            }
            nk_end(ctx);

            if (draggingItem != null) {
                drawOutOfBoundsStackAtCursor(window, stack, ctx, draggingItem);
                if (!inBounds(windowDims2) && window.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                    //Drop the item
                    draggingItem = null;
                }
            }
        }
        if (nk_window_is_hidden(ctx, title)) {
            isOpen = false;
        }
    }

    public abstract void drawWindow(MemoryStack stack, NkRect windowDims2);

    final static NkColor white = Theme.createColor(255, 255, 255, 255);
    final static NkColor green = Theme.createColor(0, 255, 0, 255);
    final static NkColor black = Theme.createColor(0, 0, 0, 255);
    final static int padding = 5;
    public final static WidgetSizeMeasurement itemWidth = new WidgetSizeMeasurement(0);

    public static boolean drawItemStack(MemoryStack stack, NkContext ctx, ItemStack itemStack) {
        ctx.style().window().padding().set(0, 0);
        ctx.style().window().group_padding().set(0, 0);
        ctx.style().window().border(0);


        NkImage bgImage = itemStack.item.getNKIcon();

        NkRect buttonBounds = NkRect.calloc(stack);
        Nuklear.nk_widget_bounds(ctx, buttonBounds);
        boolean pressed = nk_button_image(ctx, bgImage);

        drawItemStackOverlay(stack, ctx, itemStack, buttonBounds);
        return pressed;
    }

    private static void drawItemStackOverlay(MemoryStack stack, NkContext ctx, ItemStack itemStack, NkRect buttonBounds) {
        NkCommandBuffer canvas = Nuklear.nk_window_get_canvas(ctx); // Get the current drawing canvas
        NkImage bgImage = itemStack.item.getNKIcon();

        NkRect bounds = NkRect.calloc(stack).set(buttonBounds);
        bounds.x(buttonBounds.x() + padding).y(buttonBounds.y() + padding).w(buttonBounds.w() - padding - padding).h(buttonBounds.h() - padding - padding);
        nk_draw_image(canvas, bounds, bgImage, white);

        //draw quantity
        bounds.set(buttonBounds);
        bounds.x(buttonBounds.x() + 5).y(buttonBounds.y() + buttonBounds.w() - 16);


        Nuklear.nk_draw_text(canvas, bounds, "" + itemStack.stackSize, Theme.font_10, white, black);

        bounds.y(bounds.y() - 1).x(bounds.x() - 1);
        Nuklear.nk_draw_text(canvas, bounds, "" + itemStack.stackSize, Theme.font_10, black, white);

        //draw durability
        bounds.set(buttonBounds);
        bounds.y(bounds.y() + bounds.h() - 3 - 4);
        bounds.x(bounds.x() + 2);
        bounds.w(MathUtils.map(0.5f, 0, 1, 0, bounds.w() - 4));
        bounds.h(3);
        Nuklear.nk_fill_rect(canvas, bounds, 0.0f, green); // 0.0f for no rounding

    }

    public void drawItemAtCursor(NKWindow window, MemoryStack stack, NkContext ctx, ItemStack itemStack) {
        NkRect rect = NkRect.malloc(stack);
        Vector2d cursor = window.getCursorVector();
        rect.set((float) cursor.x - (itemWidth.width / 2), (float) cursor.y - (itemWidth.width / 2), itemWidth.width, itemWidth.width);
        nk_layout_row_dynamic(ctx, itemWidth.width, 1);
        drawItemStackOverlay(stack, ctx, itemStack, rect);
    }

    public void drawOutOfBoundsStackAtCursor(NKWindow window, MemoryStack stack, NkContext ctx, ItemStack itemStack) {
        NkRect rect = NkRect.malloc(stack);
        Vector2d cursor = window.getCursorVector();
        rect.set((float) cursor.x - (itemWidth.width / 2), (float) cursor.y - (itemWidth.width / 2), itemWidth.width, itemWidth.width);

        Theme.setWindowStyle(ctx, Theme.transparent, Theme.transparent);
        if (nk_begin(ctx, "cursor_stack", rect, NK_WINDOW_NO_INPUT | NK_WINDOW_BACKGROUND | NK_WINDOW_NO_SCROLLBAR)) {
            nk_layout_row_dynamic(ctx, itemWidth.width, 1);
            drawItemStackOverlay(stack, ctx, itemStack, rect);
        }
        Theme.resetWindowColor(ctx);
        nk_end(ctx);
    }
}
