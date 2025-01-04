/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.client.visuals.gameScene.GameScene;
import com.xbuilders.engine.client.player.Skin;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.client.visuals.Page;
import com.xbuilders.engine.client.visuals.Theme;
import com.xbuilders.window.nuklear.components.TextBox;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java portBox of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class CustomizePlayer implements MenuPage {


    public CustomizePlayer(NkContext ctx, ClientWindow window, TopMenu menu,
                           UserControlledPlayer player) {

        this.ctx = ctx;
        this.window = window;
        this.menu = menu;
        nameBox = new TextBox(20);
        nameBox.setOnChangeEvent(() -> {
            player.userInfo.name = nameBox.getValueAsString();
            player.userInfo.saveToDisk();
        });
        this.player = player;
    }

    UserControlledPlayer player;
    NkContext ctx;
    TopMenu menu;
    ClientWindow window;
    TextBox nameBox;
    int chosenSkin = 0;

    final int boxWidth = TopMenu.WIDTH_3;
    final int boxHeight = 300;

    @Override
    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd) {
        nk_style_set_font(ctx, Theme.font_12);
        nk_rect((window.getWidth() / 2) - (boxWidth / 2), titleYEnd.get(0),
                boxWidth, boxHeight, windowDims);

        if (nk_begin(ctx, "Customize Player", windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
            nk_style_set_font(ctx, Theme.font_10);

            nk_layout_row_static(ctx, 20, 1, 1);
            TopMenu.row(ctx, "My Name:", 2);
            nameBox.render(ctx);

            TopMenu.row(ctx, "Player Type:", 2);

            Skin playerSkin = GameScene.userPlayer.userInfo.getSkin();
            if (nk_button_label(ctx,
                    playerSkin == null ? "none" : playerSkin.name)) {
                goToNextSkin();
            }

            TopMenu.divider(ctx);
            TopMenu.divider(ctx);
            nk_layout_row_dynamic(ctx, 40, 1);

            if (nk_button_label(ctx, "BACK")) {
                menu.goBack();
            }
        }
        nk_end(ctx);
    }

    private void goToNextSkin() {
        chosenSkin++;
        //Go to the next skin
        GameScene.userPlayer.userInfo.setSkin(chosenSkin % ClientWindow.game.availableSkins.size());
        GameScene.userPlayer.userInfo.saveToDisk();
    }

    @Override
    public void onOpen(Page lastPage) {
        nameBox.setValueAsString(player.userInfo.name);
    }


}
