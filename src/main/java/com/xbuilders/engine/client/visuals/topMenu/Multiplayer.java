/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.xbuilders.engine.client.visuals.topMenu;

/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import com.xbuilders.engine.client.Client;
import com.xbuilders.engine.client.ClientWindow;
import com.xbuilders.engine.server.multiplayer.NetworkJoinRequest;
import com.xbuilders.engine.client.player.UserControlledPlayer;
import com.xbuilders.engine.client.visuals.Theme;
import com.xbuilders.engine.client.visuals.Page;
import com.xbuilders.window.nuklear.NKUtils;
import com.xbuilders.window.nuklear.components.NumberBox;
import com.xbuilders.window.nuklear.components.TextBox;

import java.nio.IntBuffer;

import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import static com.xbuilders.engine.client.visuals.topMenu.TopMenu.row;
import static org.lwjgl.nuklear.Nuklear.*;

/**
 * Java portBox of
 * <a href="https://github.com/vurtun/nuklear/blob/master/demo/calculator.c">https://github.com/vurtun/nuklear/blob/master/demo/calculator.c</a>.
 */
public class Multiplayer implements MenuPage {


    final String ipAdress;
    LoadWorld loadWorld;
    Client localClient;

    public Multiplayer(NkContext ctx, Client localClient, TopMenu menu,
                       UserControlledPlayer player, boolean hosting,
                       String ipAdress, LoadWorld loadWorld) {
        this.loadWorld = loadWorld;
        this.ipAdress = ipAdress;
        this.ctx = ctx;
        this.localClient = localClient;
        this.window = localClient.window;
        this.menu = menu;
        portBox = new NumberBox(4, 0);
        fromPortBox = new NumberBox(4, 0);
        ipAdressBox = new TextBox(20);
        ipAdressBox.setValueAsString("");


        fromPortBox.setValueAsNumber(8080);
        portBox.setValueAsNumber(8080);

        if (Client.DEV_MODE) {
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
    ClientWindow window;
    NumberBox fromPortBox, portBox;
    TextBox ipAdressBox;
    int chosenSkin = 0;

    final int boxWidth = menu.WIDTH_3;
    final int boxHeight = 420;

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

            if (Client.DEV_MODE) {
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
                if (!Client.DEV_MODE) fromPortVal = portVal;
                String ipAdress = this.ipAdressBox.getValueAsString();
                NetworkJoinRequest req = new NetworkJoinRequest(hosting, fromPortVal, portVal, player.userInfo.name, ipAdress);
                System.out.println(req.toString());
                localClient.loadWorld(loadWorld.currentWorld, req);
            }
        }
        nk_end(ctx);
    }


    @Override
    public void onOpen(Page lastPage) {
        if (hosting) {
            ipAdressBox.setValueAsString(ipAdress);
        } else {
            if (Client.DEV_MODE) {
                ipAdressBox.setValueAsString(ipAdress);
            }
        }
    }


}
