/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.ui.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.MainWindow;
import com.xbuilders.engine.gameScene.GameScene;
import com.xbuilders.engine.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.player.UserControlledPlayer;
import com.xbuilders.engine.ui.Theme;
import com.xbuilders.engine.world.WorldInfo;
import com.xbuilders.engine.ui.Page;
import com.xbuilders.window.nuklear.NKUtils;
import com.xbuilders.window.nuklear.components.NumberBox;
import com.xbuilders.window.nuklear.components.TextBox;

import java.nio.IntBuffer;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import static com.xbuilders.engine.ui.topMenu.TopMenu.row;
import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java portBox of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class Multiplayer implements MenuPage {

    /**
     * @param world the world to set
     */
    public void setWorld(WorldInfo world) {
        this.world = world;
    }

    final String ipAdress;
    LoadWorld loadWorld;

    public Multiplayer(NkContext ctx, MainWindow window, TopMenu menu,
                       UserControlledPlayer player, boolean hosting,
                       String ipAdress, LoadWorld loadWorld) {
        this.loadWorld = loadWorld;
        this.ipAdress = ipAdress;
        this.ctx = ctx;
        this.window = window;
        this.menu = menu;
        portBox = new NumberBox(4, 0);
        fromPortBox = new NumberBox(4, 0);
        ipAdressBox = new TextBox(20);


        fromPortBox.setValueAsNumber(8080);
        portBox.setValueAsNumber(8080);

        if (MainWindow.devMode) {
            if (hosting) {
                fromPortBox.setValueAsNumber(8081);
            } else {
                portBox.setValueAsNumber(8081);
            }
        }

        this.player = player;
        this.hosting = hosting;
    }

    boolean hosting;
    UserControlledPlayer player;
    NkContext ctx;
    TopMenu menu;
    MainWindow window;
    NumberBox fromPortBox, portBox;
    TextBox ipAdressBox;
    private WorldInfo world;
    int chosenSkin = 0;

    final int boxWidth = menu.WIDTH_3;
    final int boxHeight = 450;

    @Override
    public void layout(MemoryStack stack, NkRect windowDims, IntBuffer titleYEnd) {
        nk_style_set_font(ctx, Theme.font_12);
        nk_rect((window.getWidth() / 2) - (boxWidth / 2), titleYEnd.get(0),
                boxWidth, boxHeight, windowDims);

        if (nk_begin(ctx, (hosting ? "Host" : "Join") + " Multiplayer World", windowDims, NK_WINDOW_BORDER | NK_WINDOW_TITLE)) {
            nk_style_set_font(ctx, Theme.font_10);
            nk_layout_row_dynamic(ctx, 20, 1);
            NKUtils.text(ctx, "All computers wanting to join must enter " + (hosting ? "this" : "the host's") + "\n"
                    + "IP adress and port to proceed:", 10, NK_TEXT_ALIGN_LEFT);
            nk_layout_row_static(ctx, 40, 1, 1);

            row(ctx, "IP Adress:", 2);
            if (hosting) {
                nk_label(ctx, ipAdressBox.getValueAsString(), NK_TEXT_ALIGN_LEFT);
            } else {
                ipAdressBox.render(ctx);
            }

            if (MainWindow.devMode) {
                row(ctx, "From Port:", 2);
                fromPortBox.render(ctx);
            }

            row(ctx, "Port:", 2);
            portBox.render(ctx);

            TopMenu.divider(ctx);
            nk_layout_row_dynamic(ctx, 40, 1);
            if (nk_button_label(ctx, "Customize Player")) {
                menu.setPage(Page.CUSTOMIZE_PLAYER);
            }

            TopMenu.divider(ctx);
            nk_layout_row_dynamic(ctx, 40, 2);

            if (nk_button_label(ctx, "BACK")) {
                if (hosting) {
                    menu.setPage(Page.LOAD_WORLD);
                } else menu.setPage(Page.HOME);
            }
            if (nk_button_label(ctx, "CONTINUE")) {
                int fromPortVal = (int) fromPortBox.getValueAsNumber();
                int portVal = (int) portBox.getValueAsNumber();
                if (!MainWindow.devMode) fromPortVal = portVal;
                String ipAdress = this.ipAdressBox.getValueAsString();
                NetworkJoinRequest req = new NetworkJoinRequest(hosting, fromPortVal, portVal, player.name, ipAdress);
                System.out.println(req.toString());
                loadWorld.loadWorld(loadWorld.currentWorld, req);
            }
        }
        nk_end(ctx);
    }


    private void goToNextSkin() {
        chosenSkin++;
        //Go to the next skin
        GameScene.player.setSkin(chosenSkin % MainWindow.game.availableSkins.size());
        GameScene.player.save();
    }

    @Override
    public void onOpen(Page lastPage) {
        if (hosting) {
            ipAdressBox.setValueAsString(ipAdress);
        } else {
            if (MainWindow.devMode) {
                ipAdressBox.setValueAsString(ipAdress);
            }
        }
    }


}
