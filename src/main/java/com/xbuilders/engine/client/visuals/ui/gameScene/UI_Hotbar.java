/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.ui.gameScene;

import com.xbuilders.engine.game.model.GameMode;
import com.xbuilders.engine.game.model.GameScene;
import com.xbuilders.engine.game.model.items.Registrys;
import com.xbuilders.engine.game.model.items.block.Block;
import com.xbuilders.engine.game.model.items.entity.Entity;
import com.xbuilders.engine.game.model.items.item.Item;
import com.xbuilders.engine.game.model.items.item.ItemStack;
import com.xbuilders.engine.game.model.player.CursorRay;
import com.xbuilders.engine.game.model.items.item.StorageSpace;
import com.xbuilders.engine.client.visuals.ui.Theme;
import com.xbuilders.engine.client.visuals.ui.gameScene.items.UI_ItemWindow;
import com.xbuilders.engine.utils.math.MathUtils;
import com.xbuilders.window.NKWindow;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * @author zipCoder933
 */
public class UI_Hotbar extends UI_GameMenu {


    public UI_Hotbar(NkContext ctx, NKWindow window) {
        super(ctx, window);
    }

    int menuWidth = 650;
    int menuHeight = 65 + 20;
    final int ELEMENTS = 11;

    int pushValue;
    static final StorageSpace playerStorage = GameScene.player.inventory;

    @Override
    public void draw(MemoryStack stack) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) return;
        NkRect windowDims2 = NkRect.malloc(stack);

        ctx.style().window().fixed_background().data().color().set(Theme.color_transparent);
        ctx.style().button().normal().data().color().set(Theme.color_transparent);
        ctx.style().window().border_color().set(Theme.color_transparent);
        ctx.style().button().padding().set(0, 0);
        nk_style_set_font(ctx, Theme.font_10);

        int x = window.getWidth() / 2 - (menuWidth / 2);
        int y = window.getHeight() - menuHeight - 20;

        //Draw healthbars
        if (GameScene.getGameMode() == GameMode.ADVENTURE) {
            nk_rect(x, y - 60, menuWidth, menuHeight + 2, windowDims2);
            if (nk_begin(ctx, "health", windowDims2, NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {
                nk_layout_row_dynamic(ctx, 10, 3);
                nk_text(ctx, "Health: " + (int) (GameScene.player.getHealth()), NK_TEXT_ALIGN_LEFT);
                nk_text(ctx, "Hunger: " + (int) (GameScene.player.getHungerLevel()), NK_TEXT_ALIGN_LEFT);
                nk_text(ctx, "Air: " + (int) (GameScene.player.getOxygenLevel()), NK_TEXT_ALIGN_LEFT);
                nk_layout_row_dynamic(ctx, 20, 3);
//            ctx.style().progress().normal().data().color().set(Theme.color_red);
                nk_prog(ctx,
                        (long) (GameScene.player.getHealth() * 10),
                        (int) GameScene.player.MAX_HEALTH * 10, false);

                nk_prog(ctx,
                        (long) (GameScene.player.getHungerLevel() * 10),
                        (int) GameScene.player.MAX_HUNGER * 10, false);

                nk_prog(ctx,
                        (long) (GameScene.player.getOxygenLevel() * 10),
                        (int) GameScene.player.MAX_OXYGEN * 10, false);
//            Theme.resetProgressBar(ctx);
            }
            nk_end(ctx);
        }

        //Draw hotbar
        nk_rect(x, y, menuWidth, menuHeight + 2, windowDims2);
        ctx.style().window().fixed_background().data().color().set(Theme.color_backgroundColor);
        ctx.style().window().padding().set(4, 4);
        if (nk_begin(ctx, "HotbarB", windowDims2, NK_WINDOW_NO_SCROLLBAR | NK_WINDOW_BORDER)) {
            playerStorage.deleteEmptyItems();
            nk_layout_row_dynamic(ctx, 20, 1);
            if (GameScene.player.getSelectedItem() != null && GameScene.player.getSelectedItem().item != null)
                nk_text(ctx, GameScene.player.getSelectedItem().item.name, NK_TEXT_ALIGN_CENTERED);

            nk_layout_row_dynamic(ctx, UI_ItemWindow.getItemSize(), ELEMENTS);
            int i = 0;
            for (int j = 0; j < ELEMENTS; j++) {
                i = j + pushValue;

                if (i >= playerStorage.size()) {
                    break;
                }
                ItemStack item = playerStorage.get(i);

                //if (buttonHeight.isCalibrated()) {
                if (i == GameScene.player.getSelectedItemIndex()) {
                    ctx.style().button().border_color().set(Theme.color_white);
                } else {
                    ctx.style().button().border_color().set(Theme.color_blue);
                }
                //}
                if (item != null) {
                    UI_ItemWindow.drawItemStackButton(stack, ctx, item);
                } else {
                    Nuklear.nk_button_text(ctx, "");
                }
                //buttonHeight.measure(ctx, stack);
            }
        }
        nk_end(ctx);
        Theme.resetEntireButtonStyle(ctx);
        Theme.resetWindowPadding(ctx);
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    protected void changeSelectedIndex(float increment) {
        GameScene.player.changeSelectedIndex(increment);
        if (GameScene.player.getSelectedItemIndex() >= ELEMENTS + pushValue) {
            pushValue++;
        } else if (GameScene.player.getSelectedItemIndex() < pushValue) {
            pushValue--;
        }
        pushValue = MathUtils.clamp(pushValue, 0, playerStorage.size() - ELEMENTS);
    }

    public void setSelectedIndex(int index) {
        GameScene.player.setSelectedIndex(index);
        pushValue = GameScene.player.getSelectedItemIndex() - ELEMENTS / 2;
        pushValue = MathUtils.clamp(pushValue, 0, playerStorage.size() - ELEMENTS);
    }

    public boolean mouseScrollEvent(NkVec2 scroll, double xoffset, double yoffset) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) return false;

        changeSelectedIndex(-scroll.y());
        return true;
    }

    public boolean keyEvent(int key, int scancode, int action, int mods) {
        if (GameScene.getGameMode() == GameMode.SPECTATOR) return false;

        if (action == GLFW.GLFW_PRESS) {
            if (key == GLFW.GLFW_KEY_COMMA) {
                changeSelectedIndex(-1);
            } else if (key == GLFW.GLFW_KEY_PERIOD) {
                changeSelectedIndex(1);
            } else if (key == GLFW.GLFW_KEY_Q) {
                GameScene.player.dropItem(GameScene.player.getSelectedItem());
                playerStorage.set(GameScene.player.getSelectedItemIndex(), null);
            }
        }
        return false;
    }

    public void pickItem(CursorRay ray, boolean allowAcquire) {
        ItemStack stack = null;
        StorageSpace storageSpace = playerStorage;
        if (ray.hitTarget()) {
            if (ray.getEntity() != null) {
                Entity entity = ray.getEntity();
                stack = new ItemStack(Registrys.getItem(entity), 1);
            } else {
                Block block = GameScene.world.getBlock(ray.getHitPos().x, ray.getHitPos().y, ray.getHitPos().z);
                Item item = Registrys.getItem(block);
                if (item != null) stack = new ItemStack(item, 1);
            }
        }
        if (stack == null) return;
        for (int i = 0; i < storageSpace.size(); i++) {
            ItemStack item = storageSpace.get(i);
            if (item != null && item.item == stack.item) {
                setSelectedIndex(i);
                return;
            }
        }
        if (allowAcquire) setSelectedIndex(storageSpace.acquireItem(stack));
    }
}
