package com.xbuilders.game.UI;

import com.xbuilders.engine.items.item.ItemStack;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.ui.gameScene.GameUIElement;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.NKWindow;
import com.xbuilders.window.WindowEvents;
import com.xbuilders.window.nuklear.WidgetWidthMeasurement;
import org.joml.Vector2d;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

public abstract class UI_ItemWindow extends GameUIElement {
    public UI_ItemWindow(NkContext ctx, NKWindow window) {
        super(ctx, window);
    }

    ItemStack draggingItem = null;


    final static NkColor white = Theme.createColor(255, 255, 255, 255);
    final static NkColor green = Theme.createColor(0, 255, 0, 255);
    final static NkColor black = Theme.createColor(0, 0, 0, 255);
    final static int padding = 5;
    public final static WidgetWidthMeasurement itemWidth = new WidgetWidthMeasurement(0);

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

    private static  void drawItemStackOverlay(MemoryStack stack, NkContext ctx, ItemStack itemStack, NkRect buttonBounds) {
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

    public  void drawItemAtCursor(NKWindow window, MemoryStack stack, NkContext ctx, ItemStack itemStack) {
        NkRect rect = NkRect.malloc(stack);
        Vector2d cursor = window.getCursorVector();
        rect.set((float) cursor.x - (itemWidth.width / 2), (float) cursor.y - (itemWidth.width / 2), itemWidth.width, itemWidth.width);
        nk_layout_row_dynamic(ctx, itemWidth.width, 1);
        drawItemStackOverlay(stack, ctx, itemStack, rect);
    }

    public  void drawOutOfBoundsStackAtCursor(NKWindow window, MemoryStack stack, NkContext ctx, ItemStack itemStack) {
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
